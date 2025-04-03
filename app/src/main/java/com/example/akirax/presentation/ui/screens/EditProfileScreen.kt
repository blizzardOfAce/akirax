package com.example.akirax.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akirax.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun EditProfileScreen(
    onClick: () -> Unit,
    innerPadding: PaddingValues,
    profileViewModel: ProfileViewModel = getViewModel()
) {
    var name by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    val userState by profileViewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        name = userState?.name.orEmpty()
        photoUrl = userState?.photoUrl.orEmpty()
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("Photo URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    profileViewModel.updateUserProfile(name, photoUrl)
                    onClick
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
}
