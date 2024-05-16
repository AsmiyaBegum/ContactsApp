package com.ab.contactsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ContactApp : Application() {

    companion object {
        private lateinit var instance: ContactApp

        fun applicationContext(): ContactApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}