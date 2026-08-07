package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MetricEntity
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MetricsHistoryChart(
    historyList: List<MetricEntity>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("metrics_history_section_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(NeonCyan.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Metrics History Chart",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "HARDWARE HISTORY LOGS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${historyList.size} Snapshots Logged (Room DB)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("history_snapshot_count_text")
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NeonCyan, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Battery", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NeonAmber, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Temp", color = TextSecondary, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDarkBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Logging system snapshots to Room Database...\nUpdates every 30 seconds",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            // Draw dual line trend graph for battery % and temperature
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDarkBg)
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(106.dp)) {
                    val width = size.width
                    val height = size.height

                    val pointsCount = historyList.size
                    val reversedList = historyList.reversed() // oldest to newest

                    // Path for Battery Level
                    val batteryPath = Path()
                    val tempPath = Path()

                    reversedList.forEachIndexed { index, item ->
                        val x = (index.toFloat() / (pointsCount - 1)) * width
                        val yBattery = height - ((item.batteryLevel / 100f) * height)

                        // Normalize temperature 20°C - 60°C to graph height
                        val normTemp = ((item.tempCelsius - 20f) / 40f).coerceIn(0f, 1f)
                        val yTemp = height - (normTemp * height)

                        if (index == 0) {
                            batteryPath.moveTo(x, yBattery)
                            tempPath.moveTo(x, yTemp)
                        } else {
                            batteryPath.lineTo(x, yBattery)
                            tempPath.lineTo(x, yTemp)
                        }
                    }

                    // Draw Battery line
                    drawPath(
                        path = batteryPath,
                        color = NeonCyan,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Temperature line
                    drawPath(
                        path = tempPath,
                        color = NeonAmber,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
