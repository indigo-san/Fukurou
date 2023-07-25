package com.example.fukurou

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.fukurou.data.AppDatabase
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.ui.FukurouApp
import com.example.fukurou.ui.theme.FukurouTheme
import com.google.accompanist.insets.ProvideWindowInsets
import java.time.DayOfWeek

class FukurouApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets(consumeWindowInsets = true) {
                FukurouTheme(true) {
                    FukurouApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FukurouTheme {
        FukurouApp()
    }
}