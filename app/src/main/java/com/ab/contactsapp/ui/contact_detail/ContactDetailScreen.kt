package com.ab.contactsapp.ui.contact_detail


import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ab.contactsapp.R
import com.ab.contactsapp.domain.model.SocialMedia
import com.ab.contactsapp.ui.composables.RoundedBorderIcon
import com.ab.contactsapp.ui.composables.RoundedTabView
import com.ab.contactsapp.utils.Constants
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.utils.Route
import com.ab.contactsapp.rememberWindowInfo
import com.ab.contactsapp.ui.composables.DropDownMenu
import com.ab.contactsapp.ui.contact_list.ContactListViewModel
import com.ab.contactsapp.utils.Utils
import com.ab.contactsapp.utils.visible
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactDetailScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: ContactListViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onMenuClicked: (String) -> Unit
) {
    val context = LocalContext.current
    val contact by remember { viewModel.contactState }
    val windowType =  rememberWindowInfo()

    val writeContactPermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_CONTACTS)

    val writePermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted){
            Toast.makeText(context, context.resources.getString(R.string.write_permission), Toast.LENGTH_SHORT).show()
        }
    }

    // Detail UI


    Scaffold(
        modifier = modifier.fillMaxWidth()
    ){ padding->
        if(contact?.name.isNullOrBlank() && contact?.phoneNumber.isNullOrBlank()){
           Row( modifier = Modifier
               .fillMaxSize()
               .padding(padding),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center
           ) {
               Text(
                   modifier = Modifier
                       .weight(1f),
                   textAlign = TextAlign.Center,
                   color = MaterialTheme.colorScheme.onSurface,
                   text = context.resources.getString(R.string.select_contacts_to_display)
               )
           }

        }else{
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            val (clearImage, backButton, menuButton, nameText, numberText,contactActionRow, tabView) = createRefs()


                            Image(
                                painter = if(contact?.photo!=null || contact?.photoUrl != null) rememberAsyncImagePainter(contact?.photo?:contact?.photoUrl) else painterResource(id = R.drawable.ic_person), // your clear image
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .constrainAs(clearImage) {
                                        top.linkTo(parent.top,40.dp)
                                        centerHorizontallyTo(parent)
                                    }
                                    .clip(RoundedCornerShape(25))
                                ,
                                colorFilter = if(contact?.photo==null && contact?.photoUrl == null){
                                    ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                }else{
                                    null
                                },
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = onBackClicked,
                                modifier = Modifier
                                    .constrainAs(backButton) {
                                        top.linkTo(parent.top, margin = 16.dp)
                                        start.linkTo(parent.start, margin = 16.dp)
                                    }
                                    .visible(Utils.isCompactOrMedium(windowType))
                            ) {
                                RoundedBorderIcon(
                                    R.drawable.ic_arrow_back,
                                ){
                                    navController.popBackStack()
                                }
                            }

                            DropDownMenu(
                                modifier = Modifier
                                    .visible(!contact!!.isRandomContact)
                                    .constrainAs(menuButton) {
                                        top.linkTo(parent.top, margin = 16.dp)
                                        end.linkTo(parent.end, margin = 16.dp)
                                    },
                                items = Constants.OPTION_MENU_LIST
                            ) { menu ->
                                when(menu){
                                    Constants.EDIT_CONTACT -> {
                                        navController.navigate(
                                            "${Route.CONTACT_CREATE_SCREEN}/${
                                                Gson().toJson(
                                                    contact
                                                )
                                            }"
                                        ) {
                                            launchSingleTop = true
                                        }
                                    }
                                    Constants.CALL_LOGS -> onMenuClicked(Constants.CALL_LOGS)
                                    else -> {
                                        if(!writeContactPermissionState.status.isGranted){
                                            writePermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                                        }else{
                                            if(menu == Constants.MARK_AS_FAV){
                                                viewModel.markContactAsFav(context.contentResolver, contactId = contact!!.contactId.toLong())
                                                Toast.makeText(context, context.resources.getString(R.string.contact_fav),Toast.LENGTH_SHORT).show()
                                            }else{
                                                viewModel.deleteContact(context.contentResolver,contact!!.contactId.toLong())
                                                onMenuClicked(Constants.DELETE)
                                            }
                                        }

                                    }

                                }
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
                                viewModel
                            )

                            Box(modifier = Modifier
                                .constrainAs(tabView){
                                    top.linkTo(contactActionRow.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
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

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactActionsRow(modifier: Modifier, contact: Contact, viewModel: ContactListViewModel) {
    val context = LocalContext.current
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, context.resources.getString(R.string.call_permission), Toast.LENGTH_SHORT).show()
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
                shareContact(context, contact = contact)
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
fun SocialMediaAndRecentTab(modifier: Modifier = Modifier, contact: Contact, viewModel: ContactListViewModel){

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
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.CenterHorizontally)
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
fun SocialMediaAndRecentList(modifier: Modifier, isSocialMediaTab : Boolean, contact: Contact, viewModel: ContactListViewModel){
    val context = LocalContext.current
    val windowInfo =  rememberWindowInfo()
    val callLogGroups by viewModel.callLogEntries.collectAsState(initial = listOf())
    val callLogPermissionState = rememberPermissionState(permission = Manifest.permission.READ_CALL_LOG)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, context.resources.getString(R.string.call_log_permission), Toast.LENGTH_SHORT).show()
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
            var calls = callLogGroups
            if(calls.size > 1 && Utils.isCompact(windowInfo)){
                calls = calls.take(2)
            }
           CallLogList(calls)
        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SocialMediaList(modifier: Modifier = Modifier, socialMedia: SocialMedia) {
    val socialMediaLinks = mapOf(
        "Email" to socialMedia.email,
        "Facebook" to socialMedia.facebook,
        "Twitter" to socialMedia.twitter
    )
    val filteredSocialMediaLinks = socialMediaLinks.filter { !it.value.isNullOrBlank() }
    val pagerState = rememberPagerState(pageCount = { filteredSocialMediaLinks.size })

    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) {
        LazyColumn(modifier = modifier) {
            items(filteredSocialMediaLinks.size) { index ->
                val (platform, link) = filteredSocialMediaLinks.entries.toList()[index]
                SocialMediaListItem(platform = platform, link = link)
            }
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
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            RoundedBorderIcon(
                modifier = Modifier.padding(vertical = 10.dp),
                icon = if(platform == "Email"){
                    R.drawable.ic_email
                }else{
                    R.drawable.ic_message
                },
                onClick = {}
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = link?:"",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
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

