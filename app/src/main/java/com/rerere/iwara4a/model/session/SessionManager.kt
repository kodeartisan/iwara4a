package com.rerere.iwara4a.model.session

import androidx.core.content.edit
import com.rerere.iwara4a.sharedPreferencesOf

class SessionManager {
    val session: Session by lazy {
        val sharedPreferences = sharedPreferencesOf("session")
        Session(sharedPreferences.getString("key","")!!, sharedPreferences.getString("value","")!!)
    }

    fun update(key: String, value: String) {
        session.key = key
        session.value = value
        sharedPreferencesOf("session").edit {
            putString("key", key)
            putString("value", value)
        }
    }
}