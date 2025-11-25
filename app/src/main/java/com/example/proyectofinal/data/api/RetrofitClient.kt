package com.example.proyectofinal.data.api

import android.content.Context
import com.example.proyectofinal.BuildConfig
import com.example.proyectofinal.data.TokenManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // BASE_URL configurable via gradle property BASE_URL (ver build.gradle.kts). Default: http://10.0.2.2:5169/
    private val baseUrl = BuildConfig.BASE_URL

    @Volatile
    private var tokenManager: TokenManager? = null

    fun initialize(context: Context) {
        if (tokenManager == null) {
            tokenManager = TokenManager(context.applicationContext)
        }
    }

    private fun requireTokenManager(): TokenManager =
        tokenManager ?: throw IllegalStateException("TokenManager not initialized; ensure HospitalApplication.onCreate called")

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor(requireTokenManager()))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // Solo relajar verificacion de host en dev cuando es https con cert no confiable
        if (baseUrl.startsWith("https://")) {
            builder.hostnameVerifier { _, _ -> true }
        }

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }

    val citasApi: CitasApi by lazy { retrofit.create(CitasApi::class.java) }

    val medicosApi: MedicosApi by lazy { retrofit.create(MedicosApi::class.java) }

    val pacientesApi: PacientesApi by lazy { retrofit.create(PacientesApi::class.java) }

    val triajeApi: TriajeApi by lazy { retrofit.create(TriajeApi::class.java) }

    val historiaClinicaApi: HistoriaClinicaApi by lazy { retrofit.create(HistoriaClinicaApi::class.java) }

    fun getTokenManager(): TokenManager = requireTokenManager()
}
