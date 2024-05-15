package com.ab.contactsapp.ui.contact_detail


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ab.contactsapp.R
import com.ab.contactsapp.domain.contact.SocialMedia
import com.ab.contactsapp.ui.composables.RoundedBorderIcon
import com.ab.contactsapp.ui.composables.RoundedTabView
import com.ab.contactsapp.ui.contact_list.ContactItem
import com.ab.contactsapp.utils.Constants
import com.skydoves.cloudy.Cloudy
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.Route
import com.ab.contactsapp.ui.contact_list.ContactListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun ContactDetailScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: ContactListViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    var contact by remember { viewModel.contactState }
    Scaffold(
        modifier = modifier.fillMaxWidth()){ padding->
        if(contact == null){
            Text(text = "Select contact to show detail")
        }else{
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val (backgroundImage, clearImage, backButton, menuButton, nameText, numberText,contactActionRow, tabView) = createRefs()

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .constrainAs(backgroundImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
//          Cloudy(radius = 25) {
                    if(contact?.photo !=null){
                        Image(
                            painter = rememberAsyncImagePainter(contact!!.photo),
                            contentDescription = "Photo",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }

//          }

                }


                Image(
                    painter = if(contact?.photo!=null)  rememberAsyncImagePainter(contact!!.photo) else painterResource(id = R.drawable.ic_bg_pic), // your clear image
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .constrainAs(clearImage) {
                            top.linkTo(backgroundImage.bottom, margin = -80.dp)
                            centerHorizontallyTo(parent)
                        }
                        .clip(RoundedCornerShape(25))
                    ,
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .constrainAs(backButton) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                ) {
                    RoundedBorderIcon(
                        R.drawable.ic_arrow_back,
                    ){
                        navController.popBackStack()
                    }
                }

                IconButton(
                    onClick = onMenuClicked,
                    modifier = Modifier
                        .constrainAs(menuButton) {
                            top.linkTo(parent.top, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.option_menu_icon), // your menu icon
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Menu"
                    )
                }

                Text(
                    text = contact?.name?:"", // contact name
                    style = MaterialTheme.typography.titleLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .constrainAs(nameText) {
                            top.linkTo(clearImage.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                )

                Text(
                    text = contact?.phoneNumber?:"", // contact number
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .constrainAs(numberText) {
                            top.linkTo(nameText.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .padding(horizontal = 16.dp)
                )

                ContactActionsRow(
                    modifier = Modifier
                        .constrainAs(contactActionRow){
                            top.linkTo(numberText.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    contact = contact!!,
                    navController
                )

                Box(modifier = Modifier
                    .constrainAs(tabView){
                        top.linkTo(contactActionRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }) {

                    SocialMediaAndRecentTab(
                        modifier = Modifier,
                        contact = contact!!,
                        viewModel
                    )
                }



            } 
        }
       
    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactActionsRow(modifier: Modifier, contact: Contact,navController: NavController) {
    val context = LocalContext.current
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(64.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedBorderIcon( R.drawable.ic_share ) {
                navController.navigate("${Route.CONTACT_CREATE_SCREEN}/${Gson().toJson(contact)}"){
                    launchSingleTop = true
                }
//                shareContact(context, contact = contact)
            }

            Spacer(modifier = Modifier.width(16.dp))

            RoundedBorderIcon(R.drawable.ic_message) {
                openMessageApp(context, contact)
            }

            Spacer(modifier = Modifier.width(16.dp))

            RoundedBorderIcon(R.drawable.ic_call) {
                if(callPermissionState.status.isGranted){
                    makePhoneCall(context,contact.phoneNumber?:"")
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        }
    }
}

@Composable
fun SocialMediaAndRecentTab(modifier: Modifier = Modifier,contact: Contact,viewModel: ContactListViewModel){

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    Column(modifier = modifier
        .padding(16.dp)) {
        RoundedTabView(
            selectedTabIndex = selectedTabIndex, onTabSelected = { tabIndex ->
                selectedTabIndex = tabIndex
            },
            tabs = listOf(Constants.SOCIAL_MEDIA,Constants.RECENTS),
            modifier = Modifier
        )
        
        Spacer(modifier = Modifier.height(10.dp))

        SocialMediaAndRecentList(
            modifier = Modifier.padding(8.dp),
            isSocialMediaTab = selectedTabIndex == 0,
            contact = contact,
            viewModel
        )

    }
    
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SocialMediaAndRecentList(modifier: Modifier, isSocialMediaTab : Boolean,contact: Contact,viewModel: ContactListViewModel){
    val context = LocalContext.current
    val callLogGroups by viewModel.callLogEntries.collectAsState(initial = listOf())
    val callLogPermissionState = rememberPermissionState(permission = Manifest.permission.READ_CALL_LOG)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }
    LaunchedEffect(!isSocialMediaTab) {
        if(callLogPermissionState.status.isGranted){
            viewModel.getCallLogForNumber(contentResolver = context.contentResolver,contact.phoneNumber?:"")
        }else{
            requestPermissionLauncher.launch( Manifest.permission.READ_CALL_LOG)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = isSocialMediaTab,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            SocialMediaList(
                modifier = Modifier,
                socialMedia =
                SocialMedia(
                    email = contact?.email?:"",
                    facebook = null,
                    twitter = null
                )
            )
        }


        AnimatedVisibility(
            visible = !isSocialMediaTab,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
//                Text(
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 25.dp),
//                    text = "No Recent Calls"
//                )

                LazyColumn {

                    var calls = callLogGroups
                    if(calls.size > 1){
                        calls = calls.take(1)
                    }
                    items(calls) { group ->
                        Text(
                            text = DateFormat.getDateInstance().format(Date(group.date)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Column {
                            group.callLogs.forEach { entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Display call icon based on call type
                                    val icon = when (entry.type) {
                                        "Incoming" -> Icons.Default.Call
                                        "Outgoing" -> Icons.Default.Call
                                        "Missed" -> Icons.Default.Call
                                        else -> Icons.Default.Call
                                    }
                                    Icon(icon, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    // Display call time
                                    Text(
                                        text = SimpleDateFormat.getTimeInstance().format(Date(entry.date)),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    // Display call type and duration
                                    Text(
                                        text = "${entry.type} - ${entry.duration / 60} min ${entry.duration % 60} sec",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }


    }
}

@Composable
fun SocialMediaList(modifier: Modifier = Modifier, socialMedia: SocialMedia) {
    val socialMediaLinks = mapOf(
        "Email" to socialMedia.email,
        "Facebook" to socialMedia.facebook,
        "Twitter" to socialMedia.twitter
    )
    val filteredSocialMediaLinks = socialMediaLinks.filter { !it.value.isNullOrBlank() }

    LazyColumn(modifier = modifier) {
        items(filteredSocialMediaLinks.size) { index ->
            val (platform, link) = filteredSocialMediaLinks.entries.toList()[index]
            SocialMediaListItem(platform = platform, link = link)
        }
    }
}

@Composable
fun SocialMediaListItem(platform : String, link : String?){
 
    Column {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_message), contentDescription = "platform image")
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = link?:"",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Divider(modifier = Modifier.padding(5.dp))
    }
}


private fun shareContact(context: Context, contact: Contact) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Name: ${contact.name}\nPhone: ${contact.phoneNumber}\nEmail: ${contact.email}")
    }
    context.startActivity(Intent.createChooser(intent, "Share Contact"))
}

private fun openMessageApp(context: Context, contact: Contact) {
    val phoneNumber = contact.phoneNumber
    val uri = Uri.parse("smsto:$phoneNumber")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun makePhoneCall(context: Context, phoneNumber: String) {
    val uri = Uri.parse("tel:$phoneNumber")
    val intent = Intent(Intent.ACTION_CALL, uri)
    context.startActivity(intent)

}

