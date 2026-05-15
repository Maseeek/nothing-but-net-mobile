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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll

import androidx.compose.material.icons.filled.CloudOff
import com.example.nothingbutnetmobile.ui.theme.*
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis

@Composable
fun AnalysisScreen(
    navController: NavController,
    videoUri: String? = null,
    analysisId: String? = null,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var videoFile by remember { mutableStateOf<File?>(null) }
    var videoThumbnail by remember { mutableStateOf<ImageBitmap?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
 
    LaunchedEffect(videoUri, analysisId) {
        Log.d("AnalysisScreen", "LaunchedEffect: videoUri=$videoUri, analysisId=$analysisId")
        if (videoUri != null) {
            val uri = Uri.parse(videoUri)
            viewModel.startSelection()
            
            withContext(Dispatchers.IO) {
                Log.d("AnalysisScreen", "Loading video file from URI: $uri")
                videoFile = FileUtils.getFileFromUri(context, uri)
                if (videoFile == null) {
                    Log.e("AnalysisScreen", "Failed to get file from URI")
                } else {
                    Log.d("AnalysisScreen", "File loaded successfully: ${videoFile?.absolutePath}")
                }
                
                val bitmap = VideoUtils.getVideoThumbnail(context, uri)
                if (bitmap != null) {
                    videoThumbnail = bitmap.asImageBitmap()
                    Log.d("AnalysisScreen", "Thumbnail loaded successfully")
                } else {
                    Log.e("AnalysisScreen", "Failed to load thumbnail")
                }
            }
        } else if (analysisId != null) {
            Log.d("AnalysisScreen", "Loading analysis by ID: $analysisId")
            viewModel.loadSpecificAnalysis(analysisId.toLong())
        } else {
            Log.d("AnalysisScreen", "Loading latest analysis")
            viewModel.loadLatestAnalysis()
        }
    }

    // Cleanup on dispose if the video hasn't been analyzed yet
    DisposableEffect(videoUri) {
        onDispose {
            videoFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            DashboardHeader(userName = uiState.userName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Shot Analysis",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.status) {
                    AnalysisStatus.IDLE -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (videoUri == null) "Select a video to begin analysis" else "Preparing video...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.background),
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
                                                SelectionMarker(uiState.hoopLeftNormalized, "L", scale)
                                                SelectionMarker(uiState.hoopRightNormalized, "R", scale)
                                            }
                                        }
                                    } ?: CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isLeft -> "Step 1 of 2"
                                                isRight -> "Step 2 of 2"
                                                else -> "Ready"
                                            },
                                            color = MaterialTheme.colorScheme.onPrimary,
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
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onBackground)
                                        }
                                        
                                        if (isReady) {
                                            Button(
                                                onClick = { videoFile?.let { viewModel.confirmAnalysis(it) } },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when {
                                            isLeft -> "Step 1 of 2"
                                            isRight -> "Step 2 of 2"
                                            else -> "Ready to Analyze"
                                        },
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.background),
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
                                                SelectionMarker(uiState.hoopLeftNormalized, "L", scale)
                                                SelectionMarker(uiState.hoopRightNormalized, "R", scale)
                                            }
                                        }
                                    } ?: CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                                        }
                                        
                                        if (isReady) {
                                            Button(
                                                onClick = { videoFile?.let { viewModel.confirmAnalysis(it) } },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset Selection", tint = MaterialTheme.colorScheme.onBackground)
                                    }
                                }
                            }
                        }
                    }
                    AnalysisStatus.LOADING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing your shots...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
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
                                        targetAngle = uiState.targetAngle,
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
                                        icon = Icons.Default.LocalFireDepartment,
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
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Analyze New Video", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text("No analysis data available", color = MaterialTheme.colorScheme.onBackground)
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
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Connection Issue",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "Unknown error occurred",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadLatestAnalysis() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Retry Sync")
                            }
                            TextButton(onClick = { viewModel.resetStatus() }) {
                                Text("Go Back", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
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
fun BoxScope.SelectionMarker(normalizedPos: Pair<Float, Float>?, label: String, currentScale: Float) {
    normalizedPos?.let { (nx, ny) ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val x = maxWidth * nx.coerceIn(0f, 1f)
            val y = maxHeight * ny.coerceIn(0f, 1f)
            
            Box(
                modifier = Modifier
                    .offset(x = x - 12.dp, y = y - 12.dp)
                    .graphicsLayer {
                        scaleX = 1f / currentScale
                        scaleY = 1f / currentScale
                    }
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onPrimary, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
@Composable
fun EfficiencyCard(percentage: Double, modifier: Modifier = Modifier) {
    val trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawArc(
                    color = trackColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.horizontalGradient(EmberGradient),
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
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Field Goal %",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ArcAnalysisCard(targetAngle: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Arc Analysis",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "Optimal: ${targetAngle.toInt()}°",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "STABLE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun ShotSequenceCard(results: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SHOT SEQUENCE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                results.forEachIndexed { index, result ->
                    val isMake = result == 1
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isMake) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (isMake) SuccessGreen.copy(alpha = 0.5f) else ErrorRed.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = if (isMake) SuccessGreen else ErrorRed,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LastSessionsCard(
    sessions: List<ShotAnalysis>,
    onSelect: (ShotAnalysis) -> Unit,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LAST SESSIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = "VIEW PAST ANALYSES",
                    modifier = Modifier.clickable { onViewAll() },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
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
                    Text(text = dateStr, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%.1f%% FG", session.fgPercentage),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
            }
        }
    }
}
