package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vehicleman.R
import kotlinx.coroutines.delay

/**
 * Splash Screen που εμφανίζεται στην εκκίνηση της εφαρμογής.
 */
@Composable
fun Splashscreen(onTimeout: () -> Unit) {
    // 2 δευτερόλεπτα καθυστέρησης
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // Χρησιμοποιούμε το primary χρώμα για καλύτερη αντίθεση
        contentAlignment = Alignment.Center
    ) {
        // Χρήση του ic_app_logo_main.png ως splash screen image
        Image(
            painter = painterResource(id = R.mipmap.ic_app_logo_main), // Χρήση mipmap
            contentDescription = "Οθόνη Εκκίνησης",
            modifier = Modifier.size(200.dp)
        )
    }
}