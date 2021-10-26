package com.tuval.barak.mycontacts.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

interface IContact {
    val id: Long
    val firstName: String
    val lastName: String
    val backgroundColor: Int
    val phoneNum: String
}
//Parcelize lets me send a Contact between fragments (using SafeArgs).
@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    override val id: Long,
    override val firstName: String,
    override val lastName: String,
    override val backgroundColor: Int,
    override val phoneNum: String
) : IContact, Parcelable


