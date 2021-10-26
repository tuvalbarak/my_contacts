package com.tuval.barak.mycontacts.ui.extensions

import android.view.View
import android.widget.EditText

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

//editText to String
var EditText.value
    get() = this.text.toString()
    set(value) { this.setText(value) }