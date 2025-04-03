package com.example.akirax.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.akirax.R
import com.example.akirax.presentation.ui.theme.AkiraXTheme

@Composable
fun StartScreen(
    onClickLogin: () -> Unit,
    onClickSignUp: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image with 15° rotation
        Image(
            painter = painterResource(R.drawable.start_screen_image),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        // Gradient Overlay + Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0f)
                        ),
                        startY = 500f,
                        endY = 0f // Fades within this section
                    )
                )
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // App Logo
            Image(
                painter = painterResource(R.drawable.akira_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)

            )
            Text(
                text = "AkiraX",
                modifier = Modifier.padding(vertical = 4.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))
            // Login Button
            Button(
                onClick = onClickLogin,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Login",
                    style = MaterialTheme.typography.bodyMedium)
            }

            // OR Text
            Text(
                text = "- OR -",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Sign Up Button
            OutlinedButton(
                onClick = onClickSignUp,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)

            ) {
                Text(text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun StartScreenPreview(){
    AkiraXTheme {
        StartScreen(onClickLogin = {},
            onClickSignUp = {})
    }
}