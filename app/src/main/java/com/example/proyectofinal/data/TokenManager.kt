package com.example.proyectofinal.data

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    var userId: Int
        get() = prefs.getInt(KEY_USER_ID, -1)
        set(value) {
            prefs.edit().putInt(KEY_USER_ID, value).apply()
        }

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) {
            prefs.edit().putString(KEY_USERNAME, value).apply()
        }

    var userRole: String?
        get() = prefs.getString(KEY_USER_ROLE, null)
        set(value) {
            prefs.edit().putString(KEY_USER_ROLE, value).apply()
        }

    var nombreCompleto: String?
        get() = prefs.getString(KEY_NOMBRE_COMPLETO, null)
        set(value) {
            prefs.edit().putString(KEY_NOMBRE_COMPLETO, value).apply()
        }

    fun isLoggedIn(): Boolean {
        return !token.isNullOrEmpty()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "hospital_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_NOMBRE_COMPLETO = "nombre_completo"
    }
}
