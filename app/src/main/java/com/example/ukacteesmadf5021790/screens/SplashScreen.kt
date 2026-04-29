package com.example.ukacteesmadf5021790.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ukacteesmadf5021790.R
import com.example.ukacteesmadf5021790.storage.AuthStorage
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit
) {
    var showAuthForm by remember { mutableStateOf(false) }
    var isSignupMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scale = remember { Animatable(0.6f) }

    // Runs splash animation and checks if the user is already logged in.
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )

        delay(1000)

        if (AuthStorage.isLoggedIn()) {
            onAuthenticated()
        } else {
            showAuthForm = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App logo.
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale.value)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Campus Companion",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D6EFD)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Plan. Track. Stay Organised.",
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!showAuthForm) {
                    CircularProgressIndicator(color = Color(0xFF0D6EFD))
                } else {
                    Text(
                        text = if (isSignupMode) "Create Account" else "Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter email and password."
                            } else if (isSignupMode && password.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                            } else {
                                if (isSignupMode) {
                                    AuthStorage.signUp(
                                        email = email,
                                        password = password,
                                        onSuccess = onAuthenticated,
                                        onFailure = { errorMessage = it }
                                    )
                                } else {
                                    AuthStorage.login(
                                        email = email,
                                        password = password,
                                        onSuccess = onAuthenticated,
                                        onFailure = { errorMessage = it }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0D6EFD)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isSignupMode) "Sign Up" else "Login",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            isSignupMode = !isSignupMode
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isSignupMode)
                                "Already have an account? Login"
                            else
                                "Don't have an account? Sign Up"
                        )
                    }
                }
            }
        }
    }
}