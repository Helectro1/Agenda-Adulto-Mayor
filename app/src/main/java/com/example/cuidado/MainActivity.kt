package com.example.cuidado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cuidado.ui.CuidaApp
import com.example.cuidado.ui.theme.CuidadoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            CuidadoTheme {
                CuidaApp()
            }
        }
    }
}