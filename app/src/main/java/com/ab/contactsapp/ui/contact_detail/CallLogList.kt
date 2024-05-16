package com.ab.contactsapp.ui.contact_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ab.contactsapp.R
import com.ab.contactsapp.domain.model.CallLogGroup
import com.ab.contactsapp.ui.composables.RoundedBorderIcon
import com.ab.contactsapp.ui.contact_list.ContactListViewModel
import com.ab.contactsapp.utils.Utils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun CallLogList(calls : List<CallLogGroup>){
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {

        LazyColumn {
            items(calls) { group ->
                Text(
                    text = DateFormat.getDateInstance().format(Date(group.date)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(vertical = 8.dp)
                        .padding(start = 10.dp)

                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp,top = 8.dp)
                ) {
                    group.callLogs.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            // Display call icon based on call type
                            val icon = when (entry.type) {
                                "Incoming" -> R.drawable.ic_incoming_call
                                "Outgoing" -> R.drawable.ic_outgoing_call
                                "Missed" -> R.drawable.ic_missed_call
                                else -> R.drawable.ic_incoming_call
                            }
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(10.dp))
                            // Display call time
                            Text(
                                text = SimpleDateFormat.getTimeInstance().format(Date(entry.date)),
                                modifier = Modifier.width(100.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            // Display call type and duration
                            Text(
                                fontSize = 12.sp,
                                text = Utils.getCallTypeAndDuration(entry),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun CallLogScreen(navController: NavController,viewModel: ContactListViewModel){
    val calls by viewModel.callLogEntries.collectAsState(initial = listOf())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {

                RoundedBorderIcon(
                    R.drawable.ic_arrow_back,
                ){
                    navController.popBackStack()
                }

                Text(
                    text = "Call History",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                CallLogList(calls = calls)
            }

        }
    }
}