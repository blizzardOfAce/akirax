package com.example.akirax.presentation.ui.components.moviedetailsscreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ResellDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var price by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Set Resale Price") },
        text = {
            Column {
                Text("Enter the resale price:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = price.isEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (price.isNotEmpty()) {
                        try {
                            // Call the onConfirm action and show success toast
                            onConfirm(price)
                            Toast.makeText(context, "Price set successfully!", Toast.LENGTH_SHORT).show()
                            onDismiss() // Dismiss the dialog
                        } catch (e: Exception) {
                            // Show error toast if there's an exception
                            Toast.makeText(context, "Error setting price: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If price is empty, show an error message
                        Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    )
}


//@Composable
//fun ResellDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
//    var price by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text("Set Resale Price") },
//        text = {
//            Column {
//                Text("Enter the resale price:")
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = price,
//                    onValueChange = { price = it },
//                    label = { Text("Price") },
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
//                )
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    onConfirm(price)
//                }
//            ) {
//                Text("Confirm")
//            }
//        },
//        dismissButton = {
//            Button(onClick = { onDismiss() }) {
//                Text("Cancel")
//            }
//        },
//        containerColor = MaterialTheme.colorScheme.tertiaryContainer
//    )
//}