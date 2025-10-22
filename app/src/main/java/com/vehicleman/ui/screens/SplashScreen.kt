package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.vehicleman.R
import kotlinx.coroutines.delay

/**
 * Splash Screen που εμφανίζεται στην εκκίνηση της εφαρμογής.
 */
@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_splash_logo),
            contentDescription = "Οθόνη Εκκίνησης",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}