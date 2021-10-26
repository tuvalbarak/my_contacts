package com.tuval.barak.mycontacts.ui.customviews

import android.widget.TextView
import com.tuval.barak.mycontacts.R
import com.tuval.barak.mycontacts.repos.RepoFactory.context
import com.google.android.material.snackbar.Snackbar

/**
 * @property snackbar - Already has been initialized in the Fragment/Activity (using make()).
 * @property backgroundColor - The Snackbar background color.
 * @property textColor - The Snackbar text color.
 * @property actionColor - The Snackbar action text color.
 * @property actionFun - After clicking on the action text, this function will be invoked.
 */
internal class CustomSnackbar(private val snackbar: Snackbar, actionText: String, actionFun: () -> Unit) {

    init {
        //Custom background
        snackbar.view.background = context.resources.getDrawable(R.drawable.rounded_snackbar, null)
        //Text color - white
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(
            context.resources.getColor(R.color.white, null)
        )
        //Action text color - primary color
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).setTextColor(
            context.resources.getColor(R.color.primary_color, null)
        )
        setAction(actionText, actionFun)
    }

    //Invoking the function received in the constructor
    private fun setAction(text: String, actionFunc: () -> Unit) {
        snackbar.setAction(text) {
            actionFunc()
        }
    }

    fun show() {
        snackbar.show()
    }
}