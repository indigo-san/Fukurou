package com.example.fukurou.ui.theme

import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CustomTextColors(): TextFieldColors =
    TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.onBackground,
        disabledTextColor = MaterialTheme.colorScheme.onBackground,
        backgroundColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.onBackground,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
        disabledBorderColor = MaterialTheme.colorScheme.onBackground,
        errorBorderColor = MaterialTheme.colorScheme.error,
        leadingIconColor = MaterialTheme.colorScheme.onBackground,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,
        trailingIconColor = MaterialTheme.colorScheme.onBackground,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        disabledLabelColor = MaterialTheme.colorScheme.onBackground,
        errorLabelColor = MaterialTheme.colorScheme.error,
        placeholderColor = MaterialTheme.colorScheme.onBackground,
        disabledPlaceholderColor = MaterialTheme.colorScheme.primary,
    )