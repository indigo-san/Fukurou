package com.example.fukurou.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun SettingsSection(
    title: @Composable () -> Unit = {},
    onClick: () -> Unit,
    icon: ImageVector? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon ?: ImageVector.Builder(
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).build(),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        ) {
            title()
            content()
        }
    }
}