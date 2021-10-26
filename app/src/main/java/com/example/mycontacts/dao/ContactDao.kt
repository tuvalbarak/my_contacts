package com.tuval.barak.mycontacts.dao



import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Dao
import com.tuval.barak.mycontacts.models.Contact
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ContactDao {


    //If the contact is already exists in the DB, replace it with the new one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createOrUpdateContact(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    //Returns all contacts that contains the keyword (either first, last name or phone number), not case sensitive, ordered by first name and last name.
    @Query("SELECT * FROM contacts WHERE " +
            "(firstName || ' ' || lastName) LIKE :keyword OR " +
            "phoneNum LIKE :keyword " +
            "ORDER BY firstName COLLATE NOCASE, lastName COLLATE NOCASE ASC")
    fun getContactsByKeyword(keyword: String) : Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY firstName COLLATE NOCASE, lastName COLLATE NOCASE ASC")
    fun getContacts() : Flow<List<Contact>>
}
