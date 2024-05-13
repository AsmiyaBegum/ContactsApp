package com.ab.contactsapp.ui.contact_create

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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ab.contactsapp.R
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.ui.composables.OutlinedTextFieldWithIcon
import com.ab.contactsapp.ui.composables.RoundedCornerButton
import com.ab.contactsapp.ui.contact_list.visible
import com.google.accompanist.permissions.isGranted

@Composable
fun ContactCreateScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
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
                    .size(200.dp)
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Gray, shape = CircleShape)
                    .clickable {
                        // Handle click on image box
                        // You can open a dialog or navigate to a screen for adding a photo
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add photo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            // Align the add icon's center to the center of the rounded box
                            .align(Alignment.Center)
                    )
                }
                
            }

            CreateOrEditContact(null)
            

        }


    }
}


@Composable
fun CreateOrEditContact(contact: Contact?){
  Column {
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.first_name),
          imageVector = Icons.Default.Person
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.last_name),
          showIcon = false,
          imageVector = Icons.Default.Phone
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.phone),
          imageVector = Icons.Default.Phone
      )
      OutlinedTextFieldWithIcon(
          modifier = Modifier.padding(16.dp),
          text = stringResource(id = R.string.email),
          imageVector = Icons.Default.Email
      )

      Spacer(modifier = Modifier.weight(1f))

      RoundedCornerButton(
      buttonText = stringResource(id = R.string.save_changes),
      modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .align(Alignment.CenterHorizontally),
      onClick = {

      }
      )
  }
}