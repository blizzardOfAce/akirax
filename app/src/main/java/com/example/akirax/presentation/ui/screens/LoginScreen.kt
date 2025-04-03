package com.example.akirax.presentation.ui.screens


import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akirax.R
import com.example.akirax.Screen
import com.example.akirax.presentation.auth.GoogleSignInHelper
import com.example.akirax.presentation.ui.components.SignUpOptions
import com.example.akirax.presentation.viewmodel.AuthState
import com.example.akirax.presentation.viewmodel.AuthUiState
import com.example.akirax.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = getViewModel(),
    onClickRegister: () -> Unit,
    onClickLogin: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
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

    LaunchedEffect(authState) {
        when (val state = authState.authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                onClickLogin()
                viewModel.updateAuthState(AuthUiState(isAuthenticated = true, user = state.user))
            }

            is AuthState.Error -> {
                val errorMessage = state.message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.updateAuthState(AuthUiState())
            }

            else -> {
            }
        }
    }

    LoginContent(
        authViewModel = viewModel,
        googleSignInHelper = googleSignInHelper,
        onClickRegister = onClickRegister
    )
}

@Composable
fun LoginContent(
    authViewModel: AuthViewModel = getViewModel(),
    onClickRegister: () -> Unit,
    googleSignInHelper: GoogleSignInHelper
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Akira Logo
        Image(
            painter = painterResource(R.drawable.akira_logo),
            contentDescription = "AkiraX logo",
            modifier = Modifier
                .fillMaxWidth()
                .size(160.dp)
                .padding(top = 56.dp, bottom = 8.dp)
        )

        // Welcome Text
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
        )

        // Sign Up Options
        SignUpOptions(
            action = "Login",
            onClickGoogle = {
                googleSignInHelper.signInWithGoogle(isForLogin = true)
            },
            onClickFacebook = {}
        )

        Text(
            text = "- or continue with -",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            textStyle = MaterialTheme.typography.bodySmall,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { email = it },
            label = { Text("Enter your email", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(Icons.Default.AccountBox, contentDescription = "Email Icon")
            },
            supportingText = {
                if (emailError.isNotEmpty()) {
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 4.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = password,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            onValueChange = { password = it },
            label = { Text("Password", style = MaterialTheme.typography.bodySmall) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Password, contentDescription = "Password Icon")
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            supportingText = {
                if (passwordError.isNotEmpty()) {
                    Text(
                        passwordError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 8.dp)
        )

        // Forgot Password Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ClickableText(
                text = buildAnnotatedString { append("Forgot Password?") },
                onClick = { showSheet = true },
                style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (showSheet) {
            // TODO: Add ResetPasswordBottomSheet implementation
        }

        // Login Button
        Button(
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp),
            onClick = {
                // Reset error messages
                emailError = ""
                passwordError = ""

                // Validate email first
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Please enter a valid email."
                } else if (password.isEmpty() || password.length < 6) {
                    passwordError = "Password must be at least 6 characters."
                }

                // If no errors, proceed to login
                if (emailError.isEmpty() && passwordError.isEmpty()) {
                    authViewModel.loginUser(email, password)
                }
            }
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 20.sp, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Annotated String for "Register" Text
        val annotatedText = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White)) {
                append("Don't have an account yet? ")
            }
            pushStringAnnotation(tag = Screen.SignUpScreen.route, annotation = "navigate_register")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("Register")
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
                                tag = Screen.SignUpScreen.route,
                                start = position,
                                end = position + 1
                            )
                            if (annotations.isNotEmpty()) {
                                onClickRegister()
                            }
                        }
                    }
                },
            onTextLayout = { layoutResult ->
                textLayoutResult = layoutResult
            }
        )

    }
}
