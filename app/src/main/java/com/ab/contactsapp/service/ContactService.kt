package com.ab.contactsapp.service

import com.ab.contactsapp.domain.contact.ContactInfoResponse
import com.ab.contactsapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query


interface ContactService {
    @GET("/api/")
    suspend fun getContactsInfo(@Query("results") countryCode : Int = Constants.RESULT_SIZE, @Query("page") page : Int) : ContactInfoResponse
}