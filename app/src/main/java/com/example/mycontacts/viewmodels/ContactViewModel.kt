package com.tuval.barak.mycontacts.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tuval.barak.mycontacts.models.Contact
import com.tuval.barak.mycontacts.repos.ContactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

//Enum class that holds the app state.
enum class States {
    Idle,
    Deleted,
    Loading,
    Added
}

class ContactViewModel(private val contactRepo: ContactRepo, app: Application) : AndroidViewModel(app) {

    val state = MutableLiveData<States>().apply {
        postValue(States.Idle)
    }
    //Fetching data from DB.
    val contactList = MutableLiveData<List<Contact>>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(States.Loading)
            contactRepo.getContacts().collect { contacts ->
                postValue(contacts)
                state.postValue(States.Idle)
            }
        }
    }
    //Fetching filtered data from DB using a keyword.
    fun onQueryChanged(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(States.Loading)
            contactRepo.getContactsByKeyword(text).collect {
                contactList.postValue(it)
                state.postValue(States.Idle)
            }
        }
    }
    //Creating a new Contact.
    fun createContact(firstName: String, lastName: String, phoneNumber: String, randomColor: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(States.Loading)
            val contact = Contact(
                    id = System.currentTimeMillis(),
                    firstName = firstName,
                    lastName = lastName,
                    backgroundColor = randomColor,
                    phoneNum = phoneNumber
            )
            contactRepo.createOrUpdateContact(contact)
            state.postValue(States.Added)
        }
    }
    //Updating current contact.
    fun updateContact(id: Long, firstName: String, lastName: String, phoneNumber: String, randomColor: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(States.Loading)
            val contact = Contact(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    backgroundColor = randomColor,
                    phoneNum = phoneNumber
            )
            contactRepo.createOrUpdateContact(contact)
            state.postValue(States.Added)
        }
    }
    //Deleting a contact.
    fun deleteContact(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(States.Loading)
            contactRepo.deleteContact(contact)
            state.postValue(States.Deleted)
        }
    }
}
