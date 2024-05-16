package com.ab.contactsapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.ui.contact_create.ContactCreateScreen
import com.ab.contactsapp.ui.contact_detail.CallLogScreen
import com.ab.contactsapp.ui.contact_detail.ContactDetailScreen
import com.ab.contactsapp.ui.contact_list.AdaptiveContactScreen
import com.ab.contactsapp.ui.contact_list.ContactListViewModel
import com.ab.contactsapp.utils.Constants
import com.ab.contactsapp.utils.ContactInfoArgType
import com.ab.contactsapp.utils.Route
import com.example.compose.AppTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainContent()
            }
        }
    }


    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        val viewModel: ContactListViewModel = hiltViewModel()

        Box {
            NavHost(navController = navController, startDestination = Route.CONTACT_LIST_SCREEN) {
                composable(route = Route.CONTACT_LIST_SCREEN) {
                    AdaptiveContactScreen(navController = navController, viewModel){
                        openAppSettings()
                    }
                }
                composable(
                    route = "${Route.CONTACT_CREATE_SCREEN}/{contact}",
                    arguments = listOf(navArgument("contact"){
                        type = ContactInfoArgType() })) { backStackEntry ->
                    val contact = backStackEntry.arguments?.getString("contact")?.let { Gson().fromJson(it, Contact::class.java) }
                    contact?.let {
                        AppTheme {
                            ContactCreateScreen(it,navController){ contact ->
                                viewModel.updateSelectedContactAndRefreshContacts(contact)
                                navController.popBackStack()
                            }
                        }
                    } ?: run {
                        // Handle case when parameter is null
                    }

                }

                composable(Route.CONTACT_DETAIL_SCREEN) {
                    ContactDetailScreen(modifier = Modifier.fillMaxSize(),navController,viewModel, onBackClicked = {
                        navController.navigate(Route.CONTACT_LIST_SCREEN) {
                            popUpTo(Route.CONTACT_DETAIL_SCREEN) {
                                inclusive = true
                            }
                        }

                    }) { menu ->
                        if(menu == Constants.CALL_LOGS){
                            navController.navigate(Route.CALL_LOG_SCREEN)
                        }else{
                            navController.navigate(Route.CONTACT_LIST_SCREEN){
                                popUpTo(Route.CONTACT_LIST_SCREEN) {
                                    inclusive = false
                                }
                            }
                        }
                    }
                }


                composable(Route.CALL_LOG_SCREEN){
                    CallLogScreen(navController,viewModel )
                }
            }

        }
    }


}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}



