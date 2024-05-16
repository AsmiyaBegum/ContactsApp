package com.ab.contactsapp.ui.contact_create

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ab.contactsapp.R
import com.ab.contactsapp.contactHelper.editContact
import com.ab.contactsapp.contactHelper.uriToByteArray
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.rememberWindowInfo
import com.ab.contactsapp.ui.composables.OutlinedTextFieldWithIcon
import com.ab.contactsapp.ui.composables.RoundedBorderIcon
import com.ab.contactsapp.ui.composables.RoundedCornerButton
import com.ab.contactsapp.utils.Utils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactCreateScreen(contact: Contact?,navController : NavController, viewModel: ContactCreationViewmodel = hiltViewModel(),
                        editedContact: (Contact) -> Unit){
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
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Back button
                    RoundedBorderIcon(
                        icon = R.drawable.ic_arrow_back,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(16.dp)
                    ){
                        navController.popBackStack()
                    }


                    // "Add Contact" text
                    Text(
                        text = "Add Contact",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable {
                                    pickImageLauncher.launch("image/*")
                                }
                        ) {
                            if(imageUri == null) {
                                Image(
                                    painter =  if(contact?.photo!=null)  rememberAsyncImagePainter(contact.photo) else painterResource(id = R.drawable.ic_person), // your clear image
                                    contentDescription = null,
                                    modifier =  Modifier
                                        .size(180.dp)
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.FillBounds,
                                )
                            } else {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Crop
                                )
                            }

                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (5).dp, y = (-15).dp) // Adjust offset values as needed
                        ) {
                            Icon(
                                imageVector = if(imageUri == null && contact?.photo==null){
                                    Icons.Default.Add
                                }else{
                                    Icons.Default.Edit
                                },
                                contentDescription = "Add photo",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .align(Alignment.Center)
                                    .padding(8.dp)
                            )
                        }
                    }


                    CreateOrEditContact(navController = navController, viewModel = viewModel,permissionState.status.isGranted){
                        viewModel.contact?.let { editedContact(it) }
                    }


                }


            }
        }

    }

}


@Composable
fun CreateOrEditContact(navController : NavController, viewModel: ContactCreationViewmodel, isAllowed : Boolean,editedContact : () -> (Unit)){
    val state by viewModel.state.collectAsState()
    val hasContactBeenSaved by viewModel.hasContactBeenSaved.collectAsState()
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    LaunchedEffect(key1 = hasContactBeenSaved) {
        if(hasContactBeenSaved){
            if(viewModel.existingContactId!=-1L){
                editedContact()
            }else{
                navController.popBackStack()
            }
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
          fieldValue = state.phone,
          keyboardOptions =  KeyboardOptions.Default.copy(
              keyboardType = KeyboardType.Number
          )
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.email),
          imageVector = Icons.Default.Email,
          onValueChanged = viewModel::onEmailChanged,
          fieldValue = state.email,
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
             checkFields(state,context){
                 viewModel.saveContact(context = context)
             }
          }else{
              Toast.makeText(context, "Kindly provide write contact permission in app settings",Toast.LENGTH_SHORT).show()
          }
      }


      )
  }
}

fun checkFields(
    state: CreateContactState,
    context: Context,
    saveContact : () -> (Unit)
) {
    if ((state.firstName.isNotEmpty() || state.lastName.isNotEmpty()) && state.phone.isNotEmpty() ) {
        if (state.email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
        }else{
            saveContact()
        }
    } else {
        if(state.firstName.isBlank() && state.lastName.isBlank()){
            Toast.makeText(context, "Kindly provide name to continue", Toast.LENGTH_SHORT).show()
        }else if(state.phone.isBlank()){
            Toast.makeText(context, "Kindly provide phone number to continue", Toast.LENGTH_SHORT).show()
        }
    }


}