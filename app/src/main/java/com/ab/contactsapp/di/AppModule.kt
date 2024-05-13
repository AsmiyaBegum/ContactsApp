package com.ab.contactsapp.di

import android.app.Application
import androidx.room.Room
import com.ab.contactsapp.BuildConfig
import com.ab.contactsapp.data.contact.ContactDataSourceImpl
import com.ab.contactsapp.data.data_source.ContactDatabase
import com.ab.contactsapp.domain.contact.ContactDataSource
import com.ab.contactsapp.service.ContactService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideContactDataSource(db : ContactDatabase) : ContactDataSource {
        return ContactDataSourceImpl(db)
    }

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
//            .addInterceptor {
//                val original = it.request()
//                val newRequestBuilder = original.newBuilder()
//                newRequestBuilder.addHeader("X-Api-Key", BuildConfig.API_KEY)
//                it.proceed(newRequestBuilder.build())
//            }
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }


    @Provides
    @Singleton
    fun provideContactDatabase(app: Application): ContactDatabase {
        return Room.databaseBuilder(app, ContactDatabase::class.java, ContactDatabase.DATABASE_NAME)
            .build()
    }

    @Provides
    fun provideContactService(retrofit: Retrofit): ContactService =
        retrofit.create(ContactService::class.java)

}



