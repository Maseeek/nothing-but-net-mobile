package com.example.nothingbutnetmobile.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.nothingbutnetmobile.ui.components.BottomNavigationBar
import com.example.nothingbutnetmobile.ui.components.DashboardHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import com.example.nothingbutnetmobile.ui.utils.FileUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.nothingbutnetmobile.ui.utils.VideoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AnalysisScreen(
    navController: NavController,
    videoUri: String? = null,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var videoFile by remember { mutableStateOf<File?>(null) }
    var videoThumbnail by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(videoUri) {
        videoUri?.let { uriString ->
            val uri = Uri.parse(uriString)
            videoFile = FileUtils.getFileFromUri(context, uri)
            viewModel.startSelection()
            
            withContext(Dispatchers.IO) {
                val bitmap = VideoUtils.getVideoThumbnail(context, uri)
                if (bitmap != null) {
                    videoThumbnail = bitmap.asImageBitmap()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .padding(horizontal = 20.dp)
        ) {
            DashboardHeader(userName = uiState.userName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Shot Analysis",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray.copy(alpha = 0.1f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.status) {
                    AnalysisStatus.IDLE -> {
                        Text(
                            text = if (videoUri == null) "Select a video to begin analysis" else "Preparing video...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    AnalysisStatus.SELECTING_LEFT, AnalysisStatus.SELECTING_RIGHT -> {
                        val isLeft = uiState.status == AnalysisStatus.SELECTING_LEFT
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Step indicator
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFD84A1A), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isLeft) "Step 1 of 2" else "Step 2 of 2",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = if (isLeft) "Click the left edge of the basketball hoop" else "Click the right edge of the basketball hoop",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .border(2.dp, Color(0xFFFB5607), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                videoThumbnail?.let { bitmap ->
                                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .aspectRatio(aspectRatio, matchHeightConstraintsFirst = false)
                                    ) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = "Video Frame",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .pointerInput(uiState.status) {
                                                    detectTapGestures { offset ->
                                                        val normX = offset.x / size.width
                                                        val normY = offset.y / size.height
                                                        val x = (normX * bitmap.width).toInt()
                                                        val y = (normY * bitmap.height).toInt()
                                                        
                                                        if (isLeft) {
                                                            viewModel.setHoopLeft(x, y, normX, normY)
                                                        } else {
                                                            videoFile?.let { file ->
                                                                viewModel.setHoopRight(x, y, normX, normY, file)
                                                            }
                                                        }
                                                    }
                                                },
                                            contentScale = ContentScale.Fit
                                        )

                                        // Selection markers overlay
                                        SelectionMarker(uiState.hoopLeftNormalized, "L")
                                        SelectionMarker(uiState.hoopRightNormalized, "R")
                                    }
                                } ?: run {
                                    CircularProgressIndicator(color = Color(0xFFFB5607))
                                }
                            }
                            
                            // Bottom Actions
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                                }

                                
                                IconButton(onClick = { viewModel.startSelection() }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset Selection", tint = Color.White)
                                }
                            }
                        }
                    }
                    AnalysisStatus.LOADING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFFFB5607))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing your shots...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                    AnalysisStatus.SUCCESS -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.Green,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.analysisResult ?: "Analysis Complete",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { navController.navigate("home") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB5607))
                            ) {
                                Text("Back to Dashboard")
                            }
                        }
                    }
                    AnalysisStatus.ERROR -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.errorMessage ?: "Analysis Failed",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Red,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { navController.navigate("record") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Composable
fun BoxScope.SelectionMarker(normalizedPos: Pair<Float, Float>?, label: String) {
    normalizedPos?.let { (nx, ny) ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val x = maxWidth * nx.coerceIn(0f, 1f)
            val y = maxHeight * ny.coerceIn(0f, 1f)
            
            Box(
                modifier = Modifier
                    .offset(x = x - 12.dp, y = y - 12.dp)
                    .size(24.dp)
                    .background(Color(0xFFFB5607), androidx.compose.foundation.shape.CircleShape)
                    .border(2.dp, Color.White, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
