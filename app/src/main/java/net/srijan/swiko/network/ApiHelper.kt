package net.srijan.swiko.network

import net.srijan.swiko.ui.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiHelper {

    private val builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    val userService: UserService by lazy {
        builder.create(UserService::class.java)
    }

    val resetPasswordService: ResetPasswordService by lazy {
        builder.create(ResetPasswordService::class.java)
    }

    val appVersionService: AppVersionService by lazy {
        builder.create(AppVersionService::class.java)
    }

}
