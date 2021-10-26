package com.tuval.barak.mycontacts.repos

import android.app.Application

object RepoFactory {

    lateinit var context: Application
    val contactRepo: ContactRepo = ContactRepoImpl

}