package com.example.akirax.presentation.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akirax.R
import com.example.akirax.Screen
import com.example.akirax.presentation.auth.GoogleSignInHelper
import com.example.akirax.presentation.viewmodel.AuthState
import com.example.akirax.presentation.viewmodel.AuthViewModel
import com.example.akirax.presentation.ui.components.SignUpOptions
import com.example.akirax.presentation.viewmodel.AuthUiState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = getViewModel(),
    onClickLogin: () -> Unit

) {
    val context = LocalContext.current
    val addAccountLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Google account added successfully.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to add Google account.", Toast.LENGTH_SHORT).show()
        }
    }

    val googleSignInHelper = remember {
        GoogleSignInHelper(
            context = context,
            updateUiState = { uiState -> viewModel.updateAuthState(uiState) },
            startAddAccountIntentLauncher = addAccountLauncher
        )
    }

    SignUpContent(
        viewModel = viewModel,
        googleSignInHelper = googleSignInHelper,
        context = context,
        onClickLogin = onClickLogin
    )
}

@Composable
fun SignUpContent(
    viewModel: AuthViewModel,
    googleSignInHelper: GoogleSignInHelper,
    context: Context,
    onClickLogin: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.akira_logo),
            contentDescription = "AkiraX logo",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 36.dp, bottom = 8.dp)
        )

        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {

            OutlinedTextField(
                value = email,
                shape = RoundedCornerShape(8.dp),
               textStyle = MaterialTheme.typography.bodySmall,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = { Text("Email", style = MaterialTheme.typography.bodySmall) },
                isError = emailError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (emailError.isNotEmpty()) {
                        Text(emailError, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = password,
                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                textStyle = MaterialTheme.typography.bodySmall,
                label = { Text("Password", style = MaterialTheme.typography.bodySmall) },
                isError = passwordError.isNotEmpty(),
                supportingText = {
                    if (passwordError.isNotEmpty()) {
                        Text(
                            passwordError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showPassword) "Hide Password" else "Show Password"
                        )
                    }
                }
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = ""
                },
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                label = { Text("Confirm Password", style = MaterialTheme.typography.bodySmall) },
                isError = confirmPasswordError.isNotEmpty(),
                supportingText = {
                    if (confirmPasswordError.isNotEmpty()) {
                        Text(
                            confirmPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
            )
        }

        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = {
                emailError = ""
                passwordError = ""
                confirmPasswordError = ""

                // Validate email first
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Please enter a valid email."
                }
                // If no email error, check password
                else if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters."
                }
                // If no password error, check confirm password
                else if (password != confirmPassword) {
                    confirmPasswordError = "Passwords do not match."
                }

                // If no errors, proceed to registration
                if (emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                    viewModel.registerUser(email, password) // Call ViewModel to register user
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp)
                .size(56.dp)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Register", fontSize = 20.sp, style = MaterialTheme.typography.bodyMedium)
            }
        }


        LaunchedEffect(authState) {
            when (authState.authState) {
                is AuthState.Success -> {
                    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                   // viewModel.updateAuthState(AuthUiState(isAuthenticated = true, user = authState.user))
                    viewModel.updateAuthState(AuthUiState())
                    onClickLogin()
                }

                is AuthState.Error -> {
                    val errorMessage = (authState.authState as AuthState.Error).message
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    viewModel.updateAuthState(AuthUiState())
                }

                else -> {}
            }
        }

        SignUpOptions(
            action = "Continue",
            onClickGoogle = {
                googleSignInHelper.signInWithGoogle(isForLogin = false)
            },
            onClickFacebook = { /* Handle Facebook sign-up */ }
        )

        val annotatedText = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White)) {
            append("Already have an account? ")
        }

            pushStringAnnotation(tag = Screen.LoginScreen.route, annotation = "navigate_login")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append("Login")
            }
            pop()
        }

        var textLayoutResult: TextLayoutResult? = null

        BasicText(
            text = annotatedText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        textLayoutResult?.let { layoutResult ->
                            // Convert Offset to character position
                            val position = layoutResult.getOffsetForPosition(offset)
                            val annotations = annotatedText.getStringAnnotations(
                                tag = Screen.LoginScreen.route,
                                start = position,
                                end = position + 1
                            )
                            if (annotations.isNotEmpty()) {
                                onClickLogin()
                            }
                        }
                    }
                },
            onTextLayout = { layoutResult ->
                textLayoutResult = layoutResult // Store the layout result
            }
        )
    }
}
