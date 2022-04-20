package com.example.fukurou.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fukurou.R
import com.example.fukurou.dateformatter
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    drawerState: DrawerState,
    showUserDialog: MutableState<Boolean>,
    bottomNavState: MutableState<Int>,
    weekStart: MutableState<LocalDate>
) {
    val searchLayoutHeightDp = 64.dp

    val roundSize by animateDpAsState(
        if (bottomNavState.value == 0) searchLayoutHeightDp / 2 else 0.dp,
//        animationSpec = tween()
    )

    val paddingOut by animateDpAsState(
        if (bottomNavState.value == 0) 8.dp else 0.dp,
//        animationSpec = tween()
    )

    val paddingIn by animateDpAsState(
        if (bottomNavState.value == 0) 0.dp else 4.dp,
//        animationSpec = tween()
    )

    Card(
        shape = RoundedCornerShape(roundSize),
        modifier = Modifier
            .height(searchLayoutHeightDp)
            .padding(paddingOut)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingIn)
                .let { if (bottomNavState.value == 0) it.clickable { } else it }
        ) {

            val coroutineScope = rememberCoroutineScope()
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = stringResource(id = R.string.cd_gmail_menu)
                )
            }

            if (bottomNavState.value == 0) {
                Text(
                    stringResource(id = R.string.search),
                    modifier = Modifier.weight(1f),
                    style = typography.bodySmall
                )

                // アカウントのアイコン
//        Image(
//            painter = painterResource(id = R.drawable.p3),
//            contentDescription = stringResource(id = R.string.cd_gmail_profile),
//            modifier = Modifier
//                .padding(horizontal = 8.dp)
//                .size(32.dp)
//                .clip(CircleShape)
//                .clickable(onClick = {
//                    showUserDialog.value = true
//                })
//        )
            } else if (bottomNavState.value == 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {

                }

                Row(
//                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            weekStart.value = weekStart.value.minusDays(7)
                        }
                    ) {
                        Icon(
                            Icons.Filled.ChevronLeft,
                            contentDescription = null
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = weekStart.value.format(dateformatter),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            "-",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp, 0.dp)
                        )

                        Text(
                            text = weekStart.value.plusDays(6).format(dateformatter),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    IconButton(
                        onClick = {
                            weekStart.value = weekStart.value.plusDays(7)
                        }
                    ) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}




