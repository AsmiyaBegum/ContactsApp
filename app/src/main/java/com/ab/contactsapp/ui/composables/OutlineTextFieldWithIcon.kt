package com.ab.contactsapp.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ab.contactsapp.ui.contact_list.visible


@Composable
fun OutlinedTextFieldWithIcon(
    showIcon : Boolean = true,
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector?
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for person
//        if(showIcon){
            Icon(
                imageVector = imageVector!!,
                contentDescription = text,
                tint = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
                    .visible(showIcon)
            )
//        }

        // Outlined text field for first name
        OutlinedTextField(
            value = "", // Provide initial value here
            onValueChange = {

            },
            label = {
                Text(text = text)
            },
            placeholder = {
                Text(text = text)
            },
            modifier = Modifier.fillMaxWidth() // Expand to fill available space
        )
    }
}