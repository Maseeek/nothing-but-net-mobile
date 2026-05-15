package com.example.nothingbutnetmobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nothingbutnetmobile.R
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nothingbutnetmobile.ui.theme.*

@Composable
fun ArcAnalysisGraph(
    shotAngles: List<Double>,
    shotsResults: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(id = R.string.arc_analysis),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.real_time_trend),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.optimal_angle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Graph Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    val pathColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        
                        // Draw horizontal lines (grid)
                        val lineCount = 3
                        for (i in 0 until lineCount) {
                            val y = height * (i + 1) / (lineCount + 1)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Plot actual points
                        if (shotAngles.isNotEmpty()) {
                            val maxAngle = (shotAngles.maxOrNull() ?: 60.0).coerceAtLeast(60.0) + 10
                            val minAngle = (shotAngles.minOrNull() ?: 30.0).coerceAtMost(30.0) - 10
                            val angleRange = maxAngle - minAngle
                            
                            val pointSpacing = width / (shotAngles.size.coerceAtLeast(2) - 1)
                            
                            val path = Path()
                            val points = mutableListOf<Offset>()
                            
                            shotAngles.forEachIndexed { index, angle ->
                                val x = index * pointSpacing
                                // Invert Y (higher angle = higher up visually)
                                val normalizedY = 1f - ((angle - minAngle) / angleRange).toFloat()
                                val y = height * normalizedY
                                
                                val point = Offset(x, y)
                                points.add(point)
                                
                                if (index == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            
                            drawPath(
                                path = path,
                                color = pathColor,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            
                            // Draw nodes colored by make/miss
                            points.forEachIndexed { index, point ->
                                val isMake = shotsResults.getOrNull(index) == 1
                                val nodeColor = if (isMake) SuccessGreen else ErrorRed
                                drawCircle(
                                    color = nodeColor,
                                    radius = 4.dp.toPx(),
                                    center = point
                                )
                            }
                        } else {
                            // Placeholder if no data
                            val path = Path().apply {
                                moveTo(0f, height * 0.8f)
                                cubicTo(
                                    width * 0.2f, height * 0.1f,
                                    width * 0.4f, height * 1.2f,
                                    width * 0.6f, height * 0.2f
                                )
                                cubicTo(
                                    width * 0.8f, height * 0.3f,
                                    width * 0.9f, height * 0.7f,
                                    width, height * 0.8f
                                )
                            }
                            drawPath(
                                path = path,
                                color = primaryColor,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Indicator(color = SuccessGreen, label = "MAKE")
                        Spacer(modifier = Modifier.width(16.dp))
                        Indicator(color = ErrorRed, label = "MISS")
                    }
                    
                    Text(
                        text = if (shotAngles.isNotEmpty()) "LATEST SESSION" else "NO DATA YET",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun Indicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
