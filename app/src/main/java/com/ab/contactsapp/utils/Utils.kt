package com.ab.contactsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import com.ab.contactsapp.WindowInfo
import com.ab.contactsapp.domain.contact.CallLogEntry
import com.ab.contactsapp.domain.contact.Contact
import com.google.gson.Gson

object Utils {
    internal fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =context
            ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = connectivityManager.activeNetworkInfo?.isConnected
        return isConnected ?: false
    }
    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\S+@\\S+\\.\\S+\$")
        return emailRegex.matches(email)
    }

    fun isCompactOrMedium(windowInfo: WindowInfo) : Boolean{
       return windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact || windowInfo.screenWidthInfo is WindowInfo.WindowType.Medium
    }

    fun isCompact(windowInfo: WindowInfo) : Boolean{
        return windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact
    }

    fun getCallTypeAndDuration(entry: CallLogEntry) : String{
        return if(entry.type.toLowerCase() == "missed"){
            "(${entry.type})"
        }else{
            "(${entry.type} - ${entry.duration / 60} min ${entry.duration % 60} sec)"
        }
    }
}

inline fun <reified T> List<T>.sortByCustomOrder(crossinline selector: (T) -> String): List<T> {
    return this.sortedWith(compareBy<T> {
        // Sort by whether the name is a letter, number, or other character
        when {
            selector(it).all { char -> char.isLetter() } -> 0 // Letters
            selector(it).all { char -> char.isDigit() } -> 1 // Numbers
            else -> 2 // Other characters
        }
    }.thenBy {
        // If the same type, sort alphabetically
        selector(it)
    })
}



class ContactInfoArgType : JsonNavType<Contact>() {
    override fun fromJsonParse(value: String): Contact = Gson().fromJson(value, Contact::class.java)
    override fun Contact.getJsonParse(): String =  Gson().toJson(this)
}
