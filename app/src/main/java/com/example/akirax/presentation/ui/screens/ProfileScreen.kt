package com.example.akirax.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.akirax.domain.model.Resource
import com.example.akirax.presentation.viewmodel.ProfileViewModel


@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    innerPadding: PaddingValues,
    onClickEditProfile: () -> Unit,
    onClickTicket: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val userState by profileViewModel.userState.collectAsState()
    val updateState by profileViewModel.updateState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        // User Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberAsyncImagePainter(userState?.photoUrl ?: ""),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = userState?.name ?: "Name unavailable",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        val options = listOf(
            "Edit Profile" to Icons.Default.Edit to onClickEditProfile,
            "My Tickets" to Icons.Default.Receipt to onClickTicket,
            "Change Password" to Icons.Default.Lock to {},
            "Privacy Policy" to Icons.Default.PrivacyTip to {}
        )


        LazyColumn {
            items(options) { option ->
                val (titleAndIcon, action) = option
                val (title, icon) = titleAndIcon
                ProfileOption(title = title, icon = icon, onClick = action)
            }
        }

        // Edit Profile Status
        when (updateState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> Text(
                text = (updateState as Resource.Success).data,
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )

            is Resource.Error -> Text(
                text = (updateState as Resource.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )

            else -> {}
        }

        Spacer(modifier = Modifier.height(180.dp))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
        ) {
            Text(text = "Logout", color = Color.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun ProfileOption(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null
        )
    }
}


