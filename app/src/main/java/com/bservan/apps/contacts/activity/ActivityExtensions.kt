package com.bservan.apps.contacts.activity

import android.app.Activity
import com.bservan.apps.contacts.domain.content.goToApplicationSettings

fun Activity.goToApplicationSettings() {
    goToApplicationSettings(this, this)
}
