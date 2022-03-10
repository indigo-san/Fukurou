package com.example.fukurou.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun PrimaryCardScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {},
    containerColor: @Composable () -> Color = { MaterialTheme.colorScheme.primaryContainer },
    contentColor: @Composable () -> Color = { MaterialTheme.colorScheme.onPrimaryContainer },
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit){
    CardScaffold(modifier, title,actioncontainerColor,) {

    }
}


@ExperimentalMaterial3Api
@Composable
fun CardScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {},
    containerColor: @Composable () -> Color = { MaterialTheme.colorScheme.primaryContainer },
    contentColor: @Composable () -> Color = { MaterialTheme.colorScheme.onPrimaryContainer },
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
//    Surface(
//        color = MaterialTheme.colorScheme.secondaryContainer,
//        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//        shape = RoundedCornerShape(32.dp),
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(horizontal = 24.dp, vertical = 16.dp)
//                .then(modifier),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//        ) {
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Box(
//                    modifier = Modifier.weight(1f),
//                ) {
//                    ProvideTextStyle(
//                        MaterialTheme.typography.titleLarge,
//                    ) {
//                        title()
//                    }
//                }
//                action()
//            }
//            content()
//        }
//    }

    Card(
        containerColor = containerColor(),
        contentColor = contentColor(),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.clickable(enabled = isClickable, onClick = onClick)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .then(modifier),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        ProvideTextStyle(
                            MaterialTheme.typography.titleLarge,
                        ) {
                            title()
                        }
                    }
                    action()
                }
                content()
            }
        }
    }
}