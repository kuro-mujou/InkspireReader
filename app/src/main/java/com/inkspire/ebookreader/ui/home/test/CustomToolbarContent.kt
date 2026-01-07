package com.inkspire.ebookreader.ui.home.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomToolbarContent() {
    Column (
        modifier = Modifier
            .background(Color.Black, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomPopupNoDefaultMenu()
        VerticalDivider(modifier = Modifier.height(16.dp), color = Color.Gray)
        CustomPopupNoDefaultMenu()
        VerticalDivider(modifier = Modifier.height(16.dp), color = Color.Gray)
        CustomPopupNoDefaultMenu()
    }
}