package com.example.fukurou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.fukurou.ui.FukurouApp
import com.example.fukurou.ui.theme.FukurouTheme
import com.google.accompanist.insets.ProvideWindowInsets

@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            ProvideWindowInsets(consumeWindowInsets = false){
                FukurouTheme() {
                    FukurouApp()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FukurouTheme {
        FukurouApp()
    }
}