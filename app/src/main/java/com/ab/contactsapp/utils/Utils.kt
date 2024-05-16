package com.ab.contactsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.ab.contactsapp.WindowInfo
import com.ab.contactsapp.domain.model.CallLogEntry
import com.ab.contactsapp.domain.model.Contact
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

@Composable
fun Modifier.visible(visible: Boolean): Modifier {
    return if (visible) {
        this
    } else {
        this.alpha(0f) // Set alpha to 0 to make the composable invisible
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
