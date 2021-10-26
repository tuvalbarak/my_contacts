package com.tuval.barak.mycontacts

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tuval.barak.mycontacts.dao.ContactDao
import com.tuval.barak.mycontacts.models.Contact
import com.tuval.barak.mycontacts.repos.RepoFactory

private const val DB_NAME = "contacts"

/**
 * DB reference is implemented as a Singleton, gaining a single source of truth throughout the app.
 */
@Database(entities = [
    Contact::class
], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract val contactDao: ContactDao

    companion object {
         var dbInstance: AppDatabase? = null

        fun instance() = dbInstance ?: run {
            dbInstance = Room.databaseBuilder(RepoFactory.context, AppDatabase::class.java, DB_NAME).build()
            dbInstance!! //If it's null, there's no point to keep running.
        }
    }
}