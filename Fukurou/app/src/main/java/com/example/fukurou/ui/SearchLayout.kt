package com.example.fukurou.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fukurou.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLayout(
    drawerState: DrawerState,
    showUserDialog: MutableState<Boolean>
) {
    val searchLayoutHeightDp = 64.dp

    Card(
        shape = RoundedCornerShape(searchLayoutHeightDp / 2),
        modifier = Modifier
            .height(searchLayoutHeightDp)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                //Todo: 検索UI
            }
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
        }
    }
}




