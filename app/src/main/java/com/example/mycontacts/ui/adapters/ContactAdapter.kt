package com.tuval.barak.mycontacts.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.tuval.barak.mycontacts.R
import com.tuval.barak.mycontacts.models.Contact
import kotlinx.android.synthetic.main.holder_row_contact.view.*
import java.util.Locale

/**
 * Using DiffUtils to compare between two contacts.
 * Two contacts are the same if they have the same id (auto generated at creation).
 * areContentsTheSame returns false, what causes the list to to be updated after every modification => gaining a single source of truth.
 */
class UserItemDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean = false
}

/**
 * @property itemView - current item in the recyclerview.
 * @property onClickListener - lambda function for click handling.
 */
class ContactsViewHolder(itemView: View, private val onClickListener: (contact: Contact) -> Unit) : RecyclerView.ViewHolder(itemView) {

    //prevContact can be null if currContact is at position 0
    fun bind(prevContact: Contact?, currContact: Contact) {

        val currFirstLetter = currContact.firstName[0].toString().toUpperCase(Locale.ROOT) //Get the first letter of the current contact first name
        val prevFirstLetter = prevContact?.firstName?.get(0)?.toString()?.toUpperCase(Locale.ROOT) //Same for previous contact

        itemView.apply {
            //Drawing a circle which contains the contact's first letter
            val drawable = TextDrawable.builder().buildRound(currFirstLetter, currContact.backgroundColor)
            //If the currContact first letter of his first name is different from the previous contact, then display the letter.
            holder_row_contact_tv_new_letter.text =
                if(prevFirstLetter != currFirstLetter) currFirstLetter.toUpperCase(Locale.ROOT) //New letter will be displayed
                else "" //There's no need to display a letter

            //Binding rest of the data.
            holder_row_contact_tv_first_name.text = currContact.firstName
            holder_row_contact_tv_last_name.text = currContact.lastName
            holder_row_contact_tv_phone.text = currContact.phoneNum
            holder_row_contact_iv_circle.setImageDrawable(drawable) //Draw a circle that contains the first letter of the contact

            this.setOnClickListener { //Setting click listener for each item.
                currContact.let { clickedContact -> onClickListener.invoke(clickedContact) }
            }
        }
    }
}

/**
 * @property onClickListener - lambda function for click handling .
 */
class ContactsAdapter(private val onClickListener: (contact: Contact) -> Unit) : ListAdapter<Contact, ContactsViewHolder>(UserItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ContactsViewHolder =
        ContactsViewHolder(
            LayoutInflater.from(parent.context).
        inflate(R.layout.holder_row_contact, parent, false), onClickListener
        )
    //Did binding inside the ViewHolder for simplicity.
    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        if(position == 0)
            holder.bind(null, getItem(position)) //preventing illegal index (-1).
        else
            holder.bind(getItem(position - 1), getItem(position))
    }
}
