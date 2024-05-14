package com.ab.contactsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ab.contactsapp.ui.contact_detail.ContactDetailScreen
import com.ab.contactsapp.ui.contact_list.ContactListScreen
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.Route
import com.ab.contactsapp.permission.ReadContactPermissionTextProvider
import com.ab.contactsapp.permission.PermissionDialog
import com.ab.contactsapp.permission.WriteContactPermissionTextProvider
import com.ab.contactsapp.ui.base.BaseViewModel
import com.ab.contactsapp.ui.composables.SearchBar
import com.ab.contactsapp.ui.contact_create.ContactCreateScreen
import com.ab.contactsapp.ui.contact_list.ContactListViewModel
import com.ab.contactsapp.utils.ContactInfoArgType
import com.example.compose.AppTheme
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson


const val PERMISSION_REQUEST_CODE = 101

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val permissionsToRequest = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
//                val viewModel = viewModel<BaseViewModel>()
//                val dialogQueue = viewModel.visiblePermissionDialogQueue
//
//
//                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.RequestMultiplePermissions(),
//                    onResult = { perms ->
//                        permissionsToRequest.forEach { permission ->
//                            viewModel.onPermissionResult(
//                                permission = permission,
//                                isGranted = perms[permission] == true
//                            )
//                        }
//                    }
//                )

               MainContent()

//                dialogQueue
//                    .reversed()
//                    .forEach { permission ->
//                        PermissionDialog(
//                            permissionTextProvider = when (permission) {
//                                Manifest.permission.READ_CONTACTS -> {
//                                    ReadContactPermissionTextProvider()
//                                }
//                                Manifest.permission.WRITE_CONTACTS -> {
//                                    WriteContactPermissionTextProvider()
//                                }
//
//                                else -> return@forEach
//                            },
//                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
//                                permission
//                            ),
//                            onDismiss = viewModel::dismissDialog,
//                            onOkClick = {
//                                viewModel.dismissDialog()
//                                multiplePermissionResultLauncher.launch(
//                                    arrayOf(permission)
//                                )
//                            },
//                            onGoToAppSettingsClick = ::openAppSettings
//                        )
//                    }
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
////                locationPermissionGranted.value = true
//            } else {
//                // Permission denied
//                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show()
//            }
//            return
//        }
//    }


    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        val viewModel: ContactListViewModel = hiltViewModel()
        val readContactPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                viewModel.onPermissionResult(
                    permission = Manifest.permission.READ_CONTACTS,
                    isGranted = isGranted
                )
            }
        )
//        val dialog = PermissionDialog(
//            permissionTextProvider = ReadContactPermissionTextProvider(),
//            isPermanentlyDeclined = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) ,
//            onDismiss = { viewModel.dismissDialog() },
//            onOkClick = {
//                viewModel.dismissDialog()
//                readContactPermissionResultLauncher.launch(Manifest.permission.READ_CONTACTS)
//            },
//            onGoToAppSettingsClick = ::openAppSettings)



        Box {
            NavHost(navController = navController, startDestination = Route.CONTACT_LIST_SCREEN) {
                composable(route = Route.CONTACT_LIST_SCREEN) {
                    ContactListScreen(navController = navController, viewModel){
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
                            ContactCreateScreen(it,navController)
                        }
                    } ?: run {
                        // Handle case when parameter is null
                    }

                }

                composable(Route.CONTACT_DETAIL_SCREEN) {
                    ContactDetailScreen(navController,viewModel, onBackClicked = {
                        navController.navigate(Route.CONTACT_LIST_SCREEN) {
                            popUpTo(Route.CONTACT_DETAIL_SCREEN) {
                                inclusive = true
                            }
                        }

                    }) {
                    }
                }


                composable(Route.CONTACT_SEARCH_SCREEN){
                    SearchBar(
                       viewModel
                    )
                }
            }

        }
    }


    @Composable
    private fun launchPermission(viewModel: BaseViewModel,permission : String): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = isGranted
                )
            }
        )
    }

//    private fun checkContactPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_CONTACTS
//        ) == PackageManager.PERMISSION_GRANTED
//    }

//    private fun requestPermission() {
//        if (!checkContactPermission()) {
//            // Permission is not granted, request it
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.READ_CONTACTS
//                )
//            ) {
//                Toast.makeText(this, "Contact Permission denied", Toast.LENGTH_SHORT).show()
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
//                    PERMISSION_REQUEST_CODE
//                )
//            }
//        } else {
////            locationPermissionGranted.value = true
//        }
//
//}

}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}



