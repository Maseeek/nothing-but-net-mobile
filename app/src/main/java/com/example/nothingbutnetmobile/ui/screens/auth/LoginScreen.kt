package com.example.nothingbutnetmobile.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nothingbutnetmobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authState: AuthState,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo (Visual Only)
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.nothingbutnetmobile.R.drawable.nbn_light),
                contentDescription = "NothingButNet Logo",
                modifier = Modifier
                    .size(240.dp)
                    .padding(bottom = 0.dp)
            )

            // Login Card with Gradient Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground, RoundedCornerShape(24.dp))
                    .padding(2.dp) // Border thickness
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(OrangePrimary.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(22.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = com.example.nothingbutnetmobile.R.string.login),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Error Message
                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.message,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(id = com.example.nothingbutnetmobile.R.string.username), color = TextGray) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
                        },
                        colors = OutlinedTextFieldDefaults.run {
                            colors(
                                                focusedIndicatorColor = OrangePrimary,
                                                unfocusedIndicatorColor = DarkSurface,
                                                focusedTextColor = TextWhite,
                                                unfocusedTextColor = TextWhite,
                                                cursorColor = OrangePrimary,
                                                focusedContainerColor = DarkSurface,
                                                unfocusedContainerColor = DarkSurface
                                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        enabled = authState !is AuthState.Loading
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(id = com.example.nothingbutnetmobile.R.string.password), color = TextGray) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextGray)
                        },
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = TextGray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            autoCorrect = false
                        ),
                        colors = OutlinedTextFieldDefaults.run {
                            colors(
                                                focusedIndicatorColor = OrangePrimary,
                                                unfocusedIndicatorColor = DarkSurface,
                                                focusedTextColor = TextWhite,
                                                unfocusedTextColor = TextWhite,
                                                cursorColor = OrangePrimary,
                                                focusedContainerColor = DarkSurface,
                                                unfocusedContainerColor = DarkSurface
                                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        singleLine = true,
                        enabled = authState !is AuthState.Loading
                    )

                    // Login Button
                    Button(
                        onClick = { onLoginClick(username, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(id = com.example.nothingbutnetmobile.R.string.login).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = com.example.nothingbutnetmobile.R.string.no_account).split("?")[0] + "?",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onRegisterClick,
                    enabled = authState !is AuthState.Loading
                ) {
                    Text(
                        text = stringResource(id = com.example.nothingbutnetmobile.R.string.register),
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
