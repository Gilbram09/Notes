package com.gilbram.notes.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(activity: Activity){
    val inputMethotManager= activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus
    currentFocusedView.let {
        inputMethotManager.hideSoftInputFromWindow(
                currentFocusedView?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}