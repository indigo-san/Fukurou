package com.example.fukurou.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import com.example.fukurou.R
import com.example.fukurou.ui.theme.CustomTextColors

@ExperimentalMaterial3Api
@Composable
fun TextInputDialog(
    showDialog: MutableState<Boolean>,
    text: String,
    title: String,
    label: String,
    callback: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() };
    val value = remember { mutableStateOf(text) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick =
                    {
                        showDialog.value = false
                        callback(value.value)
                    }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            text = {
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = value.value,
                    onValueChange = { value.value = it },
                    colors = CustomTextColors(),
                    singleLine = true,
                    label = { Text(label) },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            showDialog.value = false
                            callback(value.value)
                        }
                    )
                )
            },
            title = {
                Text(title)
            }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}