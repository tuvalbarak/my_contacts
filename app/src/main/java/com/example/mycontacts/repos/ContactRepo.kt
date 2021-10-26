package com.tuval.barak.mycontacts.repos

import com.tuval.barak.mycontacts.AppDatabase
import com.tuval.barak.mycontacts.models.Contact
import kotlinx.coroutines.flow.Flow

/**
 * The functions are "suspended", this way enabling Async performance using Coroutines
 * (and better performance especially when using an external server).
 */
interface ContactRepo {
    suspend fun deleteContact(contact: Contact)
    suspend fun createOrUpdateContact(contact: Contact)
    suspend fun getContacts() : Flow<List<Contact>>
    suspend fun getContactsByKeyword(keyword: String) : Flow<List<Contact>>
}

internal object ContactRepoImpl : ContactRepo {

    override suspend fun deleteContact(contact: Contact) {
        AppDatabase.instance().contactDao.delete(contact)
    }

    override suspend fun createOrUpdateContact(contact: Contact) {
        AppDatabase.instance().contactDao.createOrUpdateContact(contact)
    }

    override suspend fun getContacts() : Flow<List<Contact>> =
            AppDatabase.instance().contactDao.getContacts()

    override suspend fun getContactsByKeyword(keyword: String) : Flow<List<Contact>> =
            AppDatabase.instance().contactDao.getContactsByKeyword("%$keyword%") //Already setting the '%' for the DB query.
}