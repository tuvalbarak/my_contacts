package com.tuval.barak.mycontacts.ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tuval.barak.mycontacts.R
import com.tuval.barak.mycontacts.models.Contact
import com.tuval.barak.mycontacts.ui.adapters.ContactsAdapter
import com.tuval.barak.mycontacts.ui.customviews.CustomSnackbar
import com.tuval.barak.mycontacts.ui.extensions.gone
import com.tuval.barak.mycontacts.ui.extensions.show
import com.tuval.barak.mycontacts.ui.extensions.hideKeyboard
import com.tuval.barak.mycontacts.viewmodels.ContactViewModel
import com.tuval.barak.mycontacts.viewmodels.States
import com.tuval.barak.mycontacts.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_contact.*


class ContactFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_contact
    override val logTag = "ContactFragment"

    private val contactViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.create(requireContext())).get(ContactViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupObservers()
        swipeHandler()
    }

    private fun setupListeners() {
        setupFab()
        setupRecyclerView()
        setupSearchView()
    }

    /**
     * Clarification -> When the user type a query in the SearchView, and then he navigates to another fragment -> the SearchView
     * will still hold the query (UX decision).
     * When clicking on the FAB, go to EditOrCreateContactFragment
     */
    private fun setupFab() {
        fragment_contact_extended_fab_new_contact.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_action_add_contact_fragment)
        }
    }

    /**
     * Scroll listener is used to shrink and extend the FAB.
     * While scrolling down the FAB is extended.
     * While scrolling up or when reaching the top of the screen the FAB is shrinked.
     */
    private fun setupRecyclerView() {

        fragment_contact_rv_contact.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Scroll down -> shrink
                if (dy > resources.getInteger(R.integer.scroll_down_velocity) && fragment_contact_extended_fab_new_contact.isExtended)
                    fragment_contact_extended_fab_new_contact.shrink()
                // Scroll up -> extend
                if (dy < resources.getInteger(R.integer.scroll_up_velocity) && !fragment_contact_extended_fab_new_contact.isExtended)
                    fragment_contact_extended_fab_new_contact.extend()
                // At the top -> extend
                if (!recyclerView.canScrollVertically(resources.getInteger(R.integer.at_the_top)))
                    fragment_contact_extended_fab_new_contact.extend()

            }
        })
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL) //Divider between items
        getDrawable(fragment_contact_rv_contact.context, R.drawable.divider_line)?.let { itemDecoration.setDrawable(it) } //Custom divider
        fragment_contact_rv_contact.addItemDecoration(itemDecoration) //Adding the divider to the recyclerview

        //Lambda function that calls onOpenDialPad every time a contact is clicked.
        val onContactClicked: (contact: Contact) -> Unit = { onOpenDialPad(it.phoneNum) }
        fragment_contact_rv_contact.adapter = ContactsAdapter(onContactClicked) //Bind adapter and recyclerview
    }

    /**
     * Customizing the searchView.
     * Setting up a listener to the searchView
     */
    private fun setupSearchView() {
        //Customizing the searchView.
        fragment_contact_sv_search.findViewById<ImageView>(R.id.search_mag_icon).setColorFilter(resources.getColor(R.color.white, null))
        fragment_contact_sv_search.findViewById<ImageView>(R.id.search_close_btn).setColorFilter(resources.getColor(R.color.white, null))
        fragment_contact_sv_search.findViewById<TextView>(R.id.search_src_text).setTextColor(resources.getColor(R.color.white, null))
        fragment_contact_sv_search.findViewById<EditText>(R.id.search_src_text).setHintTextColor(resources.getColor(R.color.hint, null))

        //Whenever the user clicks on the magnifying glass icon, the keyboard will be closed.
        fragment_contact_sv_search.findViewById<ImageView>(R.id.search_mag_icon).setOnClickListener {
            hideKeyboard()
        }

        fragment_contact_sv_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { //When magnifying glass is clicked
                Log.d(logTag, "onQueryTextSubmit")
                fragment_contact_sv_search.clearFocus() //prevent onQueryTextSubmit from being called twice (SearchView known bug).
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean { //Real-time observer
                Log.d(logTag, "onQueryTextChange")
                contactViewModel.onQueryChanged(newText) //updating VM
                return true
            }
        })
    }

    override fun setupObservers() {
        setupState()
        setupContactsList()
    }

    /**
     * Observing the current app state.
     * Using View extension functions to change the visibility of the progress bar.
     */
    private fun setupState() {

        contactViewModel.state.observe(viewLifecycleOwner, Observer { state ->

            when (state) {
                States.Idle -> {
                    Log.d(logTag, "Idle")
                    fragment_contact_pb_progress_bar.gone()
                }
                States.Deleted -> {
                    Log.d(logTag, "Deleted")
                    fragment_contact_pb_progress_bar.gone()
                }
                States.Loading -> {
                    Log.d(logTag, "Loading")
                    fragment_contact_pb_progress_bar.show()
                }
                States.Added -> {
                    Log.d(logTag, "Added")
                    fragment_contact_pb_progress_bar.gone()
                }
            }
        })
    }

    /**
     * Observing contactList, which holds the current contacts list.
     * After every list modification - updating the entire list => this way I gain a single
     * source of truth, and prevent undefined behavior (even though it comes at the expense of efficiency, I preferred to use it this way).
     * When a contact has been clicked => the phone's dial pad will be open with the contact's phone number.
     * If the list is empty (either because it has no contacts or because the search text doesn't match any contact),
     * a message will be presented to the user.
     */
    private fun setupContactsList() {

        contactViewModel.contactList.observe(viewLifecycleOwner, Observer { contactsList ->
            (fragment_contact_rv_contact.adapter as ContactsAdapter).submitList(contactsList)
            fragment_contact_tv_empty_list.apply {
                if(contactsList.isEmpty()) show()
                else gone()
            }
        })
    }

    /**
     * @param contact - The current swiped contact.
     * A custom Snackbar class is used.
     * A Snackbar with the deleted contact's details is being presented to the user, if he clicks "undo" ->
     * The contact will be added back to the list, otherwise it will permanently deleted.
     */
    private fun showSnackbarUndo(contact: Contact) {

        val snackbar = Snackbar.make(requireView(),
            //Message to display
            "${contact.firstName} ${contact.lastName} " + resources.getString(R.string.contact_fragment_snackbar_message_text),
            Snackbar.LENGTH_LONG
        )

        //Creating a CustomSnackbar instance.
        CustomSnackbar(
            snackbar,
            resources.getString(R.string.contact_fragment_snackbar_action_text),
        ) { //Action function (will be called after the "undo" was clicked)
            contactViewModel.createContact( //Adding a contact to the list (the VM will generate new ID)
                contact.firstName,
                contact.lastName,
                contact.phoneNum,
                contact.backgroundColor,
            )
        }.show()
    }

    /**
     * Taking care of swipe events via ItemTouchHelper.SimpleCallback, allowing both left and right swipes.
     * Swipe left -> means delete => Calling VM to delete the contact and presenting a snackbar.
     * Swipe right -> means edit => Navigating to EditOrCreateContactFragment with the contact details.
     */
    private fun swipeHandler() {

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(resources.getInteger(R.integer.disable_drag_and_drop),
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                     actionState: Int, isCurrentlyActive: Boolean) {

                val itemView = viewHolder.itemView
                val colorDrawableBackground =
                    if(isSwipeRight(dX)) resources.getDrawable(R.drawable.swipe_edit, null).apply { //Delete
                        setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom) //Animation from right to left
                    }
                    else resources.getDrawable(R.drawable.swipe_delete, null).apply { //Edit
                        setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom) //Animation from left to right
                    }

                colorDrawableBackground.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                //currContact is the swiped item
                val currContact = (fragment_contact_rv_contact.adapter as ContactsAdapter).currentList[viewHolder.adapterPosition]

                when (direction) { //Swipe left or right
                    ItemTouchHelper.LEFT -> { //Swipe to the left
                        contactViewModel.deleteContact(currContact) //Calling VM function to delete the swiped contact.
                        showSnackbarUndo(currContact)
                    }

                    ItemTouchHelper.RIGHT -> { //Swipe to the right
                        // Using the navigation SafeArgs to pass data between fragments.
                        // Navigating to EditOrCreateContactFragment. Passing the current contact.
                        view?.findNavController()?.navigate(
                            ContactFragmentDirections.navActionAddContactFragment(currContact)
                        )
                    }
                }
            }
        }).attachToRecyclerView(fragment_contact_rv_contact)
    }

    /**
     * While swiping right, the value is greater than zero. While swiping left, the value is less than zero. If no swap has occurred,
     * the value is exactly zero.
     */
    private fun isSwipeRight(x: Float) = x > 0

    /**
     * @param phoneNumber - The contact's phone number that needs to be called.
     * This function will get his phoneNum, open the default phone dial app (using implicit intent),
     * and insert the contact's phone number.
     * There's no need for user permissions because the actual phone call won't happen, only the "setup" for the call(phone number).
     */
    private fun onOpenDialPad(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phoneNumber}")
        startActivity(intent)
    }

}