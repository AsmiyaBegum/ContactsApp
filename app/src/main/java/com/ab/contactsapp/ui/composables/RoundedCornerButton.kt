package com.ab.contactsapp.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ab.contactsapp.R


@Composable
fun RoundedCornerButton(buttonText : String,
                        modifier: Modifier,
                        onClick : () -> (Unit)){
    Button(
        onClick = {
            onClick.invoke()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.teal_700),
            contentColor = colorResource(id = R.color.white)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(top = 16.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
    }
}