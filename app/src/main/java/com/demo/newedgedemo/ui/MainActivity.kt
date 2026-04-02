package com.demo.newedgedemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.demo.newedgedemo.ui.grammar.GrammarCheckScreen
import com.demo.newedgedemo.ui.theme.NewEdgeDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewEdgeDemoTheme {
                GrammarCheckScreen(onBackClick = { finish() })
            }
        }
    }
}