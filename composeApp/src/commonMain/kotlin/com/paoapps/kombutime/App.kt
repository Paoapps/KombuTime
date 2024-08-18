package com.paoapps.kombutime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.ui.view.BatchesView
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.module

val module = module {
    single { Model() }
}

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(module)
    }) {
        AppTheme {
            var showContent by remember { mutableStateOf(false) }

            BatchesView()
        }

    }
}
