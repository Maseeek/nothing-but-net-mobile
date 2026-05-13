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
import com.example.nothingbutnetmobile.ui.theme.CardBackground
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary

@Composable
fun ArcAnalysisGraph(
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
                color = Color.White
            )
            Text(
                text = stringResource(id = R.string.real_time_trend),
                style = MaterialTheme.typography.labelSmall,
                color = OrangePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(24.dp))
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
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Graph Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        
                        // Draw horizontal lines (grid)
                        val lineCount = 3
                        for (i in 0 until lineCount) {
                            val y = height * (i + 1) / (lineCount + 1)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Draw Path (Placeholder Wave)
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
                            color = OrangePrimary,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Indicator(color = OrangePrimary, label = "CURRENT")
                        Spacer(modifier = Modifier.width(16.dp))
                        Indicator(color = Color.Gray, label = "TARGET")
                    }
                    
                    Text(
                        text = "CONSISTENCY +14%",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangePrimary,
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
                    tint = Color.Gray
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
            color = Color.White
        )
    }
}
