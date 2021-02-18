package com.decagon.avalanche.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.decagon.avalanche.R


fun Activity.showAlertDialog(
    message: String,
    title: String,
    dialogInterface: DialogInterface.OnClickListener
) {
    val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
    dialogBuilder.setMessage(message)
        .setPositiveButton("Yes", dialogInterface)
        .setNegativeButton("Cancel", null)


    val alert = dialogBuilder.create()
    alert.setTitle(title)
    alert.show()
}


fun showToast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

