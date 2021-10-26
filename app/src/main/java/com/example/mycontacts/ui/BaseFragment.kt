package com.tuval.barak.mycontacts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {
    abstract val layoutRes: Int
    abstract val logTag: String
    abstract fun setupObservers()

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutRes, container, false)
}



