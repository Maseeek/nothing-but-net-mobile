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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import com.example.nothingbutnetmobile.ui.components.BottomNavigationBar
import com.example.nothingbutnetmobile.ui.components.DashboardHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import com.example.nothingbutnetmobile.ui.utils.FileUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.nothingbutnetmobile.ui.utils.VideoUtils
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.clickable

import androidx.compose.material.icons.filled.CloudOff

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
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            val uri = Uri.parse(videoUri)
            viewModel.startSelection()
            
            withContext(Dispatchers.IO) {
                videoFile = FileUtils.getFileFromUri(context, uri)
                val bitmap = VideoUtils.getVideoThumbnail(context, uri)
                if (bitmap != null) {
                    videoThumbnail = bitmap.asImageBitmap()
                }
            }
        } else {
            viewModel.loadLatestAnalysis()
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
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (videoUri == null) "Select a video to begin analysis" else "Preparing video...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                    AnalysisStatus.SELECTING_LEFT, AnalysisStatus.SELECTING_RIGHT, AnalysisStatus.READY -> {
                        val configuration = LocalConfiguration.current
                        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                        val isLeft = uiState.status == AnalysisStatus.SELECTING_LEFT
                        val isRight = uiState.status == AnalysisStatus.SELECTING_RIGHT
                        val isReady = uiState.status == AnalysisStatus.READY

                        if (isLandscape) {
                            // Landscape layout: Side-by-side
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Left side: Image selection area
                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .fillMaxHeight()
                                        .border(2.dp, Color(0xFFFB5607), RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    videoThumbnail?.let { bitmap ->
                                        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                        BoxWithConstraints(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val containerWidth = maxWidth
                                            val containerHeight = maxHeight
                                            val containerRatio = containerWidth / containerHeight
                                            
                                            val finalModifier = if (containerRatio > aspectRatio) {
                                                Modifier.fillMaxHeight().aspectRatio(aspectRatio)
                                            } else {
                                                Modifier.fillMaxWidth().aspectRatio(aspectRatio)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .then(finalModifier)
                                                    .clipToBounds()
                                                    .pointerInput(Unit) {
                                                        detectTransformGestures { _, pan, zoom, _ ->
                                                            scale = (scale * zoom).coerceIn(1f, 5f)
                                                            val maxOffsetX = (size.width * (scale - 1f)) / 2f
                                                            val maxOffsetY = (size.height * (scale - 1f)) / 2f
                                                            offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                                            offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                                        }
                                                    }
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                        translationX = offsetX
                                                        translationY = offsetY
                                                    }
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
                                                                
                                                                if (normX.isNaN() || normY.isNaN()) return@detectTapGestures

                                                                val x = (normX * bitmap.width).toInt()
                                                                val y = (normY * bitmap.height).toInt()
                                                                
                                                                if (isLeft) {
                                                                    viewModel.setHoopLeft(x, y, normX, normY)
                                                                } else if (isRight) {
                                                                    viewModel.setHoopRight(x, y, normX, normY)
                                                                }
                                                            }
                                                        },
                                                    contentScale = ContentScale.Fit
                                                )
                                                SelectionMarker(uiState.hoopLeftNormalized, "L")
                                                SelectionMarker(uiState.hoopRightNormalized, "R")
                                            }
                                        }
                                    } ?: CircularProgressIndicator(color = Color(0xFFFB5607))
                                }

                                // Right side: Controls and instructions
                                Column(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFD84A1A), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isLeft -> "Step 1 of 2"
                                                isRight -> "Step 2 of 2"
                                                else -> "Ready"
                                            },
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = when {
                                            isLeft -> "Select left edge of hoop"
                                            isRight -> "Select right edge of hoop"
                                            else -> "Coordinates confirmed"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { 
                                            scale = 1f
                                            offsetX = 0f
                                            offsetY = 0f
                                            viewModel.startSelection() 
                                        }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                                        }
                                        
                                        if (isReady) {
                                            Button(
                                                onClick = { videoFile?.let { viewModel.confirmAnalysis(it) } },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB5607))
                                            ) {
                                                Text("Analyze")
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Portrait layout
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
                                        text = when {
                                            isLeft -> "Step 1 of 2"
                                            isRight -> "Step 2 of 2"
                                            else -> "Ready to Analyze"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = when {
                                        isLeft -> "Click the left edge of the basketball hoop"
                                        isRight -> "Click the right edge of the basketball hoop"
                                        else -> "Confirm hoop coordinates"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                
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
                                        BoxWithConstraints(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val containerWidth = maxWidth
                                            val containerHeight = maxHeight
                                            val containerRatio = containerWidth / containerHeight
                                            
                                            val finalModifier = if (containerRatio > aspectRatio) {
                                                Modifier.fillMaxHeight().aspectRatio(aspectRatio)
                                            } else {
                                                Modifier.fillMaxWidth().aspectRatio(aspectRatio)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .then(finalModifier)
                                                    .clipToBounds()
                                                    .pointerInput(Unit) {
                                                        detectTransformGestures { _, pan, zoom, _ ->
                                                            scale = (scale * zoom).coerceIn(1f, 5f)
                                                            val maxOffsetX = (size.width * (scale - 1f)) / 2f
                                                            val maxOffsetY = (size.height * (scale - 1f)) / 2f
                                                            offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                                            offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                                        }
                                                    }
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                        translationX = offsetX
                                                        translationY = offsetY
                                                    }
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
                                                                
                                                                if (normX.isNaN() || normY.isNaN()) return@detectTapGestures

                                                                val x = (normX * bitmap.width).toInt()
                                                                val y = (normY * bitmap.height).toInt()
                                                                
                                                                if (isLeft) {
                                                                    viewModel.setHoopLeft(x, y, normX, normY)
                                                                } else if (isRight) {
                                                                    viewModel.setHoopRight(x, y, normX, normY)
                                                                }
                                                            }
                                                        },
                                                    contentScale = ContentScale.Fit
                                                )
                                                SelectionMarker(uiState.hoopLeftNormalized, "L")
                                                SelectionMarker(uiState.hoopRightNormalized, "R")
                                            }
                                        }
                                    } ?: CircularProgressIndicator(color = Color(0xFFFB5607))
                                }
                                
                                // Bottom Actions
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                                        }
                                        
                                        if (isReady) {
                                            Button(
                                                onClick = { videoFile?.let { viewModel.confirmAnalysis(it) } },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB5607)),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text("Analyze Now")
                                            }
                                        }
                                    }
                                    
                                    IconButton(onClick = { 
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                        viewModel.startSelection() 
                                    }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset Selection", tint = Color.White)
                                    }
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
                        val analysis = uiState.selectedAnalysis
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 24.dp)
                        ) {
                            if (analysis != null) {
                                // Main Analytics Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    EfficiencyCard(
                                        percentage = analysis.fgPercentage,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    ArcAnalysisCard(
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    StatCard(
                                        label = "TOTAL MAKES",
                                        value = analysis.makes.toString(),
                                        icon = Icons.Default.CheckCircle,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = "MAX STREAK",
                                        value = analysis.longestStreak.toString(),
                                        icon = Icons.Default.Refresh, // Changed to dynamic below
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                ShotSequenceCard(results = analysis.shotsResults)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                LastSessionsCard(
                                    sessions = uiState.recentAnalyses,
                                    onSelect = { viewModel.selectAnalysis(it) },
                                    onViewAll = { navController.navigate("history") }
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = { viewModel.resetStatus() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB5607)),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Analyze New Video", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text("No analysis data available", color = Color.White)
                            }
                        }
                    }
                    AnalysisStatus.ERROR -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Connection Issue",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "Unknown error occurred",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadLatestAnalysis() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB5607))
                            ) {
                                Text("Retry Sync")
                            }
                            TextButton(onClick = { viewModel.resetStatus() }) {
                                Text("Go Back", color = Color.Gray)
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
@Composable
fun EfficiencyCard(percentage: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawArc(
                    color = Color.White.copy(alpha = 0.1f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFB5607), Color(0xFFFFBE0B))),
                    startAngle = 135f,
                    sweepAngle = (270f * (percentage / 100f)).toFloat(),
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f%%", percentage),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Field Goal %",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ArcAnalysisCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFFFB5607),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Arc Analysis",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "Optimal: 55°",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFB5607).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "STABLE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFFFB5607)
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = Color.White)
        }
    }
}

@Composable
fun ShotSequenceCard(results: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SHOT SEQUENCE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                results.forEachIndexed { index, result ->
                    val isMake = result == 1
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isMake) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .border(
                                1.dp,
                                if (isMake) Color(0xFF4CAF50) else Color(0xFFF44336),
                                androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = if (isMake) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LastSessionsCard(
    sessions: List<com.example.nothingbutnetmobile.domain.model.ShotAnalysis>,
    onSelect: (com.example.nothingbutnetmobile.domain.model.ShotAnalysis) -> Unit,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LAST SESSIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Gray
                    )
                }
                Text(
                    text = "VIEW PAST ANALYSES",
                    modifier = Modifier.clickable { onViewAll() },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFFFB5607)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            sessions.forEach { session ->
                val sdf = SimpleDateFormat("d MMM", Locale.US)
                val dateStr = sdf.format(Date(session.timestamp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(session) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = dateStr, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%.1f%% FG", session.fgPercentage),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    }
}
