package com.ab.contactsapp.ui.contact_list

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.ab.contactsapp.WindowInfo
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.Route
import com.ab.contactsapp.rememberWindowInfo
import com.ab.contactsapp.ui.composables.HideableSearchTextField
import com.ab.contactsapp.ui.composables.RoundedCornerButton
import com.ab.contactsapp.ui.composables.RoundedTabView
import com.ab.contactsapp.ui.contact_detail.ContactDetailScreen
import com.ab.contactsapp.utils.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import java.util.Locale

@Composable
fun ContactsScreen(
    viewModel: ContactListViewModel,
    state: ContactListState,
    onItemClick: (Contact) -> Unit,
    modifier: Modifier
) {
    val contacts = if (state.selectedTab == Constants.PHONE_CONTACTS) {
        state.contacts
    } else {
        state.randomContact
    }
    val groupedContacts = groupContacts(contacts)
    var targetGroup by remember { mutableStateOf<Pair<Char, Int>?>(null) }
    val listState = rememberLazyListState()
//    val randomContacts = viewModel.getBreakingNews().collectAsLazyPagingItems()


    LaunchedEffect(targetGroup) {
        // Find the index of the first contact in the target group
        val index = contacts.indexOfFirst { it.name?.firstOrNull()?.equals(targetGroup?.first) == true }
        // Scroll to the calculated position
        if (index != -1) {
            listState.scrollToItem(index + (targetGroup?.second ?: 0))
        }
    }


    Box(modifier = modifier) {
        if (state.selectedTab == Constants.PHONE_CONTACTS) {
            ContactList(groupedContacts, listState, modifier, onItemClick = onItemClick)
        }else{
//            RandomContactList(randomContacts)
        }
    }
}

@Composable
fun AToZList(
    groups: List<Char>,
    modifier: Modifier,
    targetGroup: (Char, Int) -> Unit
) {
    Column(modifier = modifier) {
        groups.forEachIndexed { index, group ->
            // Use a fixed height for each row containing the group label
            Box(
                modifier = Modifier
                    .clickable {
                        // Set the target group to scroll to
                        targetGroup(group, index)
                    }
            ) {
                Text(
                    text = group.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactList(
    groupedContacts: Map<Char, List<Contact>>,
    scrollState: LazyListState,
    modifier: Modifier,
    onItemClick: (Contact) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        groupedContacts.forEach { (group, contacts) ->
            stickyHeader {
                Text(
                    text = group.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                )
            }
            items(contacts) { contact ->
                ContactItem(contact, onItemClick)
            }
        }
    }
}








private fun calculateScrollPosition(
    index: Int,
    listState: LazyListState
): Int {
    val layoutInfo = listState.layoutInfo
    val headerOffset = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.offset ?: 0
    return headerOffset
}











//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun ContactList(groupedContacts: Map<Char, List<ContactInfo>>, scrollState: LazyListState, modifier: Modifier) {
//    LazyColumn(modifier = modifier,state = scrollState) {
//        groupedContacts.forEach { (group, contacts) ->
//            stickyHeader {
//                Text(text = group.toString())
//            }
//            items(contacts) { contact ->
//                    ContactItem(contact)
//            }
//        }
//    }
//
//}


@Composable
fun ContactItem(contact: Contact,onItemClick: (Contact) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(contact)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Profile image
        if(contact.photo!=null){
            Image(
                painter = rememberAsyncImagePainter(contact.photo),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }else {
            val nameLetters = contact.name?.split(" ")?: listOf()
            var contactName : String = ""
            if(nameLetters.isNotEmpty()){
                contactName = "${nameLetters[0].first()}${(nameLetters.getOrNull(1)?:"").firstOrNull()?:""}"
            }
           Box(
               modifier = Modifier
                   .size(50.dp)
                   .clip(CircleShape)
                   .background(MaterialTheme.colorScheme.inversePrimary)
           ){
               Text(
                   text = contactName.toUpperCase(Locale.ROOT),
                   modifier = Modifier.align(Alignment.Center),
                   textAlign = TextAlign.Center
               )
           }
        }


        Spacer(modifier = Modifier.width(16.dp))

        // Name, email, and phone number
        Column {
            Text(
                text = contact.name?:"",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contact.phoneNumber?:"",
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}


fun groupContacts(contacts: List<Contact>): Map<Char, List<Contact>> {
    return contacts.groupBy { it.name!!.first().toUpperCase() }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactListScreen(modifier: Modifier,navController: NavController,viewModel: ContactListViewModel = hiltViewModel(),openAppSettings :() -> (Unit)) {
    val readContactPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadcontacts(context)
        } else {

        }
    }

    LaunchedEffect(readContactPermissionState) {
        if (!readContactPermissionState.status.isGranted) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    var selectedTabIndex by remember {
        mutableStateOf(Constants.CONTACTS_LIST.indexOf(state.selectedTab))
    }

    LaunchedEffect(selectedTabIndex) {
        if(selectedTabIndex == 0 && !readContactPermissionState.status.isGranted){

        }else{
            viewModel.loadcontacts(context)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .visible(selectedTabIndex == 0 && readContactPermissionState.status.isGranted),
                onClick = {
//                    requestPermission(Manifest.permission.WRITE_CONTACTS)
                    navController.navigate("${Route.CONTACT_CREATE_SCREEN}/${Gson().toJson(Contact())}"){
                    launchSingleTop = true
                }
                          },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){

                HideableSearchTextField(
                    text = state.searchText,
                    isSearchActive = state.isSearchActive,
                    onTextChange = viewModel::onSearchTextChanges,
                    onSearchClick =  viewModel::onToggleSearch ,
                    onCloseClick =  viewModel::onToggleSearch ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .visible((readContactPermissionState.status.isGranted || selectedTabIndex == 1))
                )

                this@Column.AnimatedVisibility(
                    visible = !state.isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {

                    Text(
                        text = "Contacts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                }
            }


            RoundedTabView(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { tabIndex ->
                    selectedTabIndex = tabIndex
                    viewModel.onTabSelected(tabIndex)
                },
                tabs = listOf(Constants.PHONE_CONTACTS, Constants.RANDOM_CONTACTS),
                modifier = Modifier
//                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )


            ContactsScreen(
                state,
                onItemClick = { contact ->
                    viewModel.updateSelectedContact(contact)
                  if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact || windowInfo.screenWidthInfo is WindowInfo.WindowType.Medium){
                      navController.navigate(Route.CONTACT_DETAIL_SCREEN)
                  }
                },
                modifier = Modifier
                    .visible((readContactPermissionState.status.isGranted || selectedTabIndex == 1))
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .visible(!(readContactPermissionState.status.isGranted || selectedTabIndex == 1)),
                verticalArrangement = Arrangement.Center,
            ){
                Text(
                    text = "Kindly provide permission to read contacts",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                RoundedCornerButton(
                    buttonText = "Add",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        openAppSettings()
                    },
                    buttonColor = MaterialTheme.colorScheme.secondary,
                    buttonContentColor = MaterialTheme.colorScheme.onSecondary
                )
            }




        }
    }
}

@Composable
fun AdaptiveContactScreen(navController: NavController,viewModel: ContactListViewModel, openAppSettings: () -> Unit){
    val windowInfo = rememberWindowInfo()
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact || windowInfo.screenWidthInfo is WindowInfo.WindowType.Medium){
        ContactListScreen(modifier = Modifier.fillMaxSize(), navController = navController,viewModel ) {
            openAppSettings()
        }
    }else{
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ContactListScreen(modifier = Modifier.weight(1f), navController = navController,viewModel) {
                openAppSettings()
            }
            ContactDetailScreen(modifier  = Modifier.weight(1f),navController = navController,viewModel, onBackClicked = { }) {
                navController.navigate(Route.CALL_LOG_SCREEN)
            }
        }
    }
}

@Composable
fun RandomContactList(
    beers: LazyPagingItems<Contact>
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = beers.loadState) {
        if(beers.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (beers.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if(beers.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(beers) { beer ->
                    if(beer != null) {
                        ContactItem(
                            contact = beer
                        ){

                        }
                    }
                }
                item {
                    if(beers.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator()
                    }
                }
            }
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



