package com.example.proyectofinal

import android.app.Application
import com.example.proyectofinal.data.api.RetrofitClient

class HospitalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}
