package com.example.fukurou.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.ReportState
import com.example.fukurou.ui.details.ReportDetailBody
import com.example.fukurou.ui.details.SchooldayDetailBody
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(navController: NavHostController) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("教科を追加") },
                onClick = { navController.navigate("create-subject") }
            )
        },
        topBar = {
            MediumTopAppBar(
                title = { Text("教科を管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }

                },
                actions = { }
            )
        }
    ) { padding->

    }
}