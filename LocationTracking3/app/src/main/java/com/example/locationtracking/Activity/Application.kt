package com.example.locationtracking.Activity

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.locationtracking.Service.BackgroundService
import java.util.*


class Application : Application() {


    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences(SHARED_KEY, MODE_PRIVATE)
        editor = preferences.edit()
    }

    companion object {
        var SHARED_KEY = "MusicShared"
        lateinit var preferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor

        fun putString(key: String, value: String) {
            editor.putString(key, value).apply()
        }

        fun getString(key: String): String {
            return preferences.getString(key, "").toString()
        }

        fun putBoolean(key: String, value: Boolean) {
            editor.putBoolean(key, value).apply()
        }

        fun getBoolean(key: String): Boolean {
            return preferences.getBoolean(key, false)
        }

        fun putInt(key: String, value: Int) {
            editor.putInt(key, value).apply()
        }

        fun getInt(key: String, i: Int): Int {
            return preferences.getInt(key, i)
        }

        fun Any?.log(): Unit = exc { Log.wtf("FATZ", "$this") }
        inline fun exc(block: () -> Unit) {
            try {
                block()
            } catch (e: Exception) {
                e.message
            }
        }
    }


}