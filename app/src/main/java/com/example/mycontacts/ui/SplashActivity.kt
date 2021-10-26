package com.tuval.barak.mycontacts.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuval.barak.mycontacts.Initializer

/**
 * The first activity of the app (in this case it only initializes the app context).
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Initializer.init(application) //Initializing the app's context
        Intent(this, MainActivity::class.java).also { mainActivity ->
            startActivity(mainActivity)
        }
    }
}