package com.ab.contactsapp.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ab.contactsapp.ui.contact_list.visible


@Composable
fun OutlinedTextFieldWithIcon(
    showIcon : Boolean = true,
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector?,
    onValueChanged : (String) -> Unit,
    fieldValue : String
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
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 14.dp)
                    .visible(showIcon)
            )
//        }

        // Outlined text field for first name
        OutlinedTextField(
            value = fieldValue, // Provide initial value here
            onValueChange = onValueChanged,
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