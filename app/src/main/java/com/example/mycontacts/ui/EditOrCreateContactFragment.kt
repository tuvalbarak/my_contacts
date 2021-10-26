package com.tuval.barak.mycontacts.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.tuval.barak.mycontacts.R
import com.tuval.barak.mycontacts.models.Contact
import com.tuval.barak.mycontacts.ui.extensions.gone
import com.tuval.barak.mycontacts.ui.extensions.hideKeyboard
import com.tuval.barak.mycontacts.ui.extensions.show
import com.tuval.barak.mycontacts.ui.extensions.value
import com.tuval.barak.mycontacts.viewmodels.ContactViewModel
import com.tuval.barak.mycontacts.viewmodels.States
import com.tuval.barak.mycontacts.viewmodels.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_add_or_update_contact.*


class EditOrCreateContactFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_add_or_update_contact
    override val logTag = "EditOrCreateContactFragment"

    private val contactViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.create(requireContext())).get(ContactViewModel::class.java)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onEditContact()
        setupAddButtons()
        setupObservers()
    }

    /**
     * Getting data from ContactFragment. If arguments isn't null => means we are editing an already existing contact =>
     * the contact's details will be inserted into the fields.
     * Else, creating a new one contact => keeping the fields empty.
     */
    private fun onEditContact() {
        arguments?.let {
            EditOrCreateContactFragmentArgs.fromBundle(it).contact?.apply {
                //Inserting contact's details to the fields
                fragment_add_contact_tid_first_name.value = firstName
                fragment_add_contact_tid_last_name.value = lastName
                fragment_add_contact_tid_phone.value = phoneNum
                //Changing the button text
                fragment_add_contact_btn_create.text = resources.getString(R.string.fragment_add_contact_btn_save_contact)
            }
        }
    }

    private fun setupAddButtons() {
        val contactToBeUpdated = EditOrCreateContactFragmentArgs.fromBundle(requireArguments()).contact

        fragment_add_contact_btn_create.setOnClickListener { //Create or Save
            onCreateClicked(contactToBeUpdated)
        }

        fragment_add_contact_btn_cancel.setOnClickListener { //Cancel
            onCancelClicked(contactToBeUpdated)
        }
    }

    private fun onCreateClicked(contactToBeUpdated: Contact?) {
        //Checking that fields aren't blank.
        if(validateField(fragment_add_contact_tid_first_name, fragment_add_contact_til_first_name) &&
            validateField(fragment_add_contact_tid_last_name, fragment_add_contact_til_last_name) &&
            validateField(fragment_add_contact_tid_phone, fragment_add_contact_til_phone)) {

            //contactToBeUpdated isn't null => update
            contactToBeUpdated?.apply {
                contactViewModel.updateContact( //Update existing contact.
                    id, //Keeping the same id.
                    fragment_add_contact_tid_first_name.value,
                    fragment_add_contact_tid_last_name.value,
                    fragment_add_contact_tid_phone.value,
                    backgroundColor //Keeping the same background color.
                )
            } ?: run {
                contactViewModel.createContact(
                    fragment_add_contact_tid_first_name.value,
                    fragment_add_contact_tid_last_name.value,
                    fragment_add_contact_tid_phone.value,
                    ColorGenerator.MATERIAL.randomColor //Generates a random color for the contact's background
                )
            }
        }
    }

    private fun onCancelClicked(contactToBeUpdated: Contact?) {
        //If contactToBeUpdated isn't null -> check if the contact details are equal to the current fields data.
        contactToBeUpdated?.apply {
            if(fragment_add_contact_tid_first_name.value != firstName ||
                fragment_add_contact_tid_last_name.value != lastName ||
                fragment_add_contact_tid_phone.value != phoneNum) { //If a change has been made ->

                AlertDialog.Builder(context).apply {
                    setMessage("Are you sure you want to discard the changes?")
                    setCancelable(false) //Click outside the dialog won't dismiss it.

                    setPositiveButton("Yes") { _, _ ->
                        view?.findNavController()?.popBackStack()
                    }
                    setNegativeButton("No") { _, _ ->
                        //No need to do anything. The user will stay in the current fragment.
                    }
                }.create().show()

            } else {
                view?.findNavController()?.popBackStack()
            }
        } ?: view?.findNavController()?.popBackStack()
    }

    override fun setupObservers() {
        setupState()
    }

    /**
     * Observing the current app state
     */
    private fun setupState() {
        contactViewModel.state.observe(viewLifecycleOwner, Observer { state ->

            when (state) {
                States.Idle -> {
                    Log.d(logTag, "Idle")
                    fragment_add_contact_pb_progress_bar.gone()
                }
                States.Loading -> {
                    Log.d(logTag, "Loading")
                    fragment_add_contact_pb_progress_bar.show()
                }
                States.Added -> {
                    Log.d(logTag, "Added")
                    fragment_add_contact_pb_progress_bar.gone()
                    hideKeyboard()
                    view?.findNavController()
                        ?.popBackStack() //Goes back to previous screen (ContactFragment)
                }
            }
        })
    }

    private fun validateField(
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout
    ) : Boolean =
        if (textInputEditText.value.isBlank()) {
            textInputLayout.error = " "
            false
        } else {
            textInputLayout.error = ""
            true
        }
    
}