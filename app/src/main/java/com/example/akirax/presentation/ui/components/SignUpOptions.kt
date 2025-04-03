package com.example.akirax.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.akirax.R
import com.example.akirax.presentation.ui.theme.AkiraXTheme

@Composable
fun SignUpOptions(action: String, onClickGoogle: () -> Unit, onClickFacebook: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(width = 0.5.dp, color = Color.Companion.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onClickGoogle()
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.google_logo),
                        contentDescription = "google logo"
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "$action with Google",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(width = 0.5.dp, color = Color.Companion.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onClickFacebook()
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.facebook_logo),
                        contentDescription = "facebook logo"
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "$action with Facebook",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    AkiraXTheme {
        SignUpOptions(action = "Register", onClickGoogle = {}, onClickFacebook = {})
    }
}