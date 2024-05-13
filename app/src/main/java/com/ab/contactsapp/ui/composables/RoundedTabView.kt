package com.ab.contactsapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun RoundedTabView(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.LightGray)
            ,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .background(color = Color.Gray),
                    color = Color.Unspecified, // Color of the indicator
                    height = 0.dp
                    // Height of the indicator
                )
            }
        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .background(
                            color = if (selectedTabIndex == index) Color.Black else Color.Transparent, // Color of selected tab
                            shape = RoundedCornerShape(16.dp) // Rounded corner shape
                        ),
                    text = {
                        Text(
                            text,
                            color = Color.LightGray
                        )
                    }
                )
            }
        }
    }
}