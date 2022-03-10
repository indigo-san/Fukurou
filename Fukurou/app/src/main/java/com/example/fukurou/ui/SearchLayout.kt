package com.example.fukurou.ui

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fukurou.R
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun SearchLayout(
    offset: Int,
    drawerState: DrawerState,
    showUserDialog: MutableState<Boolean>,
    onCreateNewEmailClickListener: () -> Unit
) {

    val searchLayoutHeightDp = 64.dp

    Card(
        shape = RoundedCornerShape(searchLayoutHeightDp / 2),
        modifier = Modifier
            .graphicsLayer(translationY = offset.toFloat())
            .height(searchLayoutHeightDp)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {

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
                "Search in emails",
                modifier = Modifier.weight(1f),
                //Todo
                style = typography.bodySmall
            )
//        TextField(
//            value = TextFieldValue(""),
//            placeholder = { Text("Search in emails") },
//            onValueChange = {},
//            modifier = Modifier.weight(1f),
//            colors = TextFieldDefaults.textFieldColors(
//                backgroundColor = background,
//                cursorColor = MaterialTheme.colors.onSurface,
//                focusedIndicatorColor = background,
//                disabledIndicatorColor = background
//            ),
//            textStyle = typography.body2
//        )

            val accessibilityManager =
                LocalContext.current.getSystemService(Context.ACCESSIBILITY_SERVICE)
                        as AccessibilityManager
            if (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled) {
                IconButton(
                    onClick = {
                        onCreateNewEmailClickListener.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(id = R.string.cd_create_new_email)
                    )
                }
            }

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




