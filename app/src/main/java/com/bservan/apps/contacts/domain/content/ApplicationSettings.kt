package com.bservan.apps.contacts.domain.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun goToApplicationSettings(context: Context, activity: Activity?) {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", activity?.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}
