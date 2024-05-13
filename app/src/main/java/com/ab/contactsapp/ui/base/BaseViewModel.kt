package com.ab.contactsapp.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ab.contactsapp.domain.contact.Contact
import javax.inject.Singleton


data class Event<out T>(val content : T, val hasBeenHandled : Boolean = false)

open class BaseViewModel : ViewModel() {
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    val loading: LiveData<Boolean>
        get() = _loading

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()

    val showSnackBar: LiveData<Event<String>>
        get() = _showSnackBar


    private val _showSnackBar: MutableLiveData<Event<String>> = MutableLiveData()

    val unauthorized: LiveData<Boolean> get() = _unauthorized
    private val _unauthorized: MutableLiveData<Boolean> = MutableLiveData()

    private val _error: MutableLiveData<Event<String>> = MutableLiveData()
    val error: LiveData<Event<String>>
        get() = _error


    protected fun <T> call(
        apiOrDBCall: suspend () -> Result<T>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        handleLoading: Boolean = true,
        handleError: Boolean = true,
    ) = viewModelScope.launch(Dispatchers.IO) {
        // Show loading
        if (handleLoading) {
            _loading.postValue(true)
        }

        // Execute call
        val result = apiOrDBCall.invoke()

        // hide loading
        if (handleLoading) {
            _loading.postValue(false)
        }

        // Check for result
        result.getOrNull()?.let { value ->
            onSuccess?.invoke(value)
        }

        result.exceptionOrNull()?.let { error ->
            onError?.invoke(error)
            if (handleError) {
                onCallError(error)
            }
        }
    }

    protected fun onCallError(error: Throwable) {
        setError(error.message.orEmpty())
    }

    protected fun setLoading(isLoading: Boolean) = _loading.postValue(isLoading)

    protected fun setError(errorMessage: String) = _error.postValue(Event(errorMessage))

    fun showSnackBar(snackBarMsg: String) = _showSnackBar.postValue(Event(snackBarMsg))


}
