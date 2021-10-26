package com.tuval.barak.mycontacts

import android.app.Application
import com.tuval.barak.mycontacts.repos.RepoFactory

/**
 * Initializer contains necessary data in order to start the app (though in this case it's tiny, it's a good practice for a scalable solution).
 */
object Initializer {
    fun init(application: Application) {
        RepoFactory.context = application
    }
}