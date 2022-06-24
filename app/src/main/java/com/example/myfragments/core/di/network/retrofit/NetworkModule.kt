package com.example.myfragments.core.di.network.retrofit

import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.myfragments.core.util.connection.ConnectionLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
open class NetworkModule {

    @Provides
    @Singleton
    open fun getChuckerCollector(context: Application): ChuckerCollector = ChuckerCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.FOREVER
    )


//    @Provides
//    @Singleton
//    open fun getChuckerInterceptor(
//        context: Application,
//        chuckerCollector: ChuckerCollector,
//    ): ChuckerInterceptor {
//        return ChuckerInterceptor.Builder(context)
//            .collector(chuckerCollector)
//            .maxContentLength(250_000L)
//            .alwaysReadResponseBody(true)
//            .alwaysReadResponseBody(true)
//            .build()
//
//    }


    @Provides
    @Singleton
    open fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor {
            Timber.d("beepul ##  %s", it)
//            Timber.d("BNetwork ##  %s", it)
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }


    @Provides
    @Singleton
    open fun getGson(): Gson = GsonBuilder().setLenient().create()


    @Singleton
    @Provides
    open fun interceptor(
        context: Context?
    ): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val builder: Request.Builder = request.newBuilder()
            builder
                .header("Connection", "close")
                .addHeader("Content-Type", "application/json")
            val response = chain.proceed(builder.build())

            response
        }
    }


    @Singleton
    @Provides
    open fun getOkHttpClient(
        logging: HttpLoggingInterceptor,
//        chucker: ChuckerInterceptor,
        interceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(logging)
//            .addInterceptor(chucker)
            .addInterceptor(interceptor)
            .build()
    }


    @Singleton
    @Provides
    open fun getApiClient(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        return Retrofit
            .Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Singleton
    @Provides
    open fun getConncetionLiveDate(context: Context):ConnectionLiveData{
        return ConnectionLiveData(context)
    }

}