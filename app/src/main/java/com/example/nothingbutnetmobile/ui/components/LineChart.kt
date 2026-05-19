package com.example.nothingbutnetmobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val defaultData = if (data.isEmpty()) listOf(20f, 40f, 30f, 50f, 45f) else data
    val maxVal = 100f
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (defaultData.size - 1).coerceAtLeast(1)
        
        val path = Path()
        
        defaultData.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value / maxVal) * height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = backgroundColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
        
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw grid lines
        val lines = 5
        val stepY = height / lines
        for (i in 0..lines) {
            drawLine(
                color = gridColor,
                start = Offset(0f, i * stepY),
                end = Offset(width, i * stepY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
