package com.ab.contactsapp.ui.contact_create

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.size.Size
import com.ab.contactsapp.R
import com.ab.contactsapp.contactHelper.uriToByteArray
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.ui.composables.OutlinedTextFieldWithIcon
import com.ab.contactsapp.ui.composables.RoundedCornerButton
import com.ab.contactsapp.ui.contact_list.visible
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactCreateScreen(contact: Contact?,navController : NavController, viewModel: ContactCreationViewmodel = hiltViewModel()){
    val permissionState = rememberPermissionState(permission = Manifest.permission.WRITE_CONTACTS)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<String?>(null) }
    val painter = rememberAsyncImagePainter(imageUri)

    LaunchedEffect(permissionState) {
        if(permissionState.status.isGranted){

        }else{
            permissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
        }
    }


    // Remember launcher for activity result
    val pickImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri.toString()
        viewModel.onPhotoChanged(uriToByteArray(contentResolver = context.contentResolver,uri))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                // Back button
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(16.dp)
                )


                // "Add Contact" text
                Text(
                    text = "Add Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Centered round box for image
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp)
                        .clip(CircleShape)
                        .clickable {
                            pickImageLauncher.launch("image/*")
                        }
                ) {

                    Image(
                        painter,
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add photo",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.secondary
                                )
                                // Align the add icon's center to the center of the rounded box
                                .align(Alignment.Center)
                        )
                    }

                }

                CreateOrEditContact(navController = navController, viewModel = viewModel,permissionState.status.isGranted)


            }


        }
    }

}


@Composable
fun CreateOrEditContact(navController : NavController, viewModel: ContactCreationViewmodel, isAllowed : Boolean){
    val state by viewModel.state.collectAsState()
    val hasNoteBeenSaved by viewModel.hasNotBeenSaved.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = hasNoteBeenSaved) {
        if(hasNoteBeenSaved){
            navController.popBackStack()
        }
    }

  Column {
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.first_name),
          imageVector = Icons.Default.Person,
          onValueChanged = viewModel::onFirstNameChanged,
          fieldValue = state.firstName
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.last_name),
          showIcon = false,
          imageVector = Icons.Default.Phone,
          onValueChanged = viewModel::onLastNameChanged,
          fieldValue = state.lastName
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.phone),
          imageVector = Icons.Default.Phone,
          onValueChanged = viewModel::onPhoneNumberChanged,
          fieldValue = state.phone
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.email),
          imageVector = Icons.Default.Email,
          onValueChanged = viewModel::onEmailChanged,
          fieldValue = state.email
      )

      Spacer(modifier = Modifier.weight(1f))

      RoundedCornerButton(
      buttonText = stringResource(id = R.string.save_changes),
      modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .align(Alignment.CenterHorizontally),
      onClick = {
          if(isAllowed){
              viewModel.saveContact(context = context)
          }else{
              Toast.makeText(context, "Kindly provide write contact permission in app settings",Toast.LENGTH_SHORT).show()
          }
      }


      )
  }
}