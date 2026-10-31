package com.chaintriangle.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaintriangle.engine.LineValidationResult
import com.chaintriangle.engine.LineValidator
import com.chaintriangle.engine.TriangleBoardState
import com.chaintriangle.model.Line4Peg
import com.chaintriangle.model.Peg
import com.chaintriangle.model.TriangleOrientation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    ChainTriangleGameApp()
                }
            }
        }
    }
}

@Composable
fun ChainTriangleGameApp() {
    var boardSize by remember { mutableIntStateOf(4) }
    var boardState by remember(boardSize) { mutableStateOf(TriangleBoardState(boardSize)) }
    var moveCount by remember(boardSize) { mutableIntStateOf(0) }

    var selectedStartPeg by remember(boardSize) { mutableStateOf<Peg?>(null) }
    var statusMessage by remember(boardSize) { mutableStateOf("Select a peg to start a 4-peg line move.") }
    var lastClosedCount by remember(boardSize) { mutableIntStateOf(0) }

    val completedTrianglesCount = boardState.completedTriangles.size
    val totalTrianglesCount = boardState.board.totalTrianglesCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Section ---
        Text(
            text = "CHAIN TRIANGLE",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Stats & Score Dashboard ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreCard(label = "Board Size", value = "N = $boardSize")
            ScoreCard(label = "Triangles", value = "$completedTrianglesCount / $totalTrianglesCount")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (lastClosedCount > 0) Color(0xFF15803D) else Color(0xFF1E293B)
            )
        ) {
            Text(
                text = statusMessage,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Interactive Board Canvas ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TriangularBoardCanvas(
                boardState = boardState,
                moveCount = moveCount,
                selectedStartPeg = selectedStartPeg,
                onPegTapped = { peg ->
                    val currentStart = selectedStartPeg
                    if (currentStart == null) {
                        selectedStartPeg = peg
                        statusMessage = "Start peg $peg selected. Tap an endpoint 3 steps away."
                    } else if (currentStart == peg) {
                        selectedStartPeg = null
                        statusMessage = "Peg deselected. Tap a peg to start."
                    } else {
                        val validation = LineValidator.validate(currentStart, peg, boardState.board)
                        if (validation is LineValidationResult.Valid) {
                            val commitResult = boardState.commitLine(validation.line)
                            val newlyClosed = commitResult.closedTrianglesCount
                            lastClosedCount = newlyClosed
                            moveCount++

                            selectedStartPeg = null
                            statusMessage = if (newlyClosed > 0) {
                                "🎉 EXCELLENT! Closed $newlyClosed triangle(s) simultaneously!"
                            } else {
                                "Move committed (${validation.line.axis.name.lowercase()}). 0 triangles closed."
                            }
                        } else {
                            val reason = (validation as LineValidationResult.Invalid).reason
                            statusMessage = "Invalid line move ($reason). Try another peg."
                            selectedStartPeg = null
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bottom Controls Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Size Selection
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(4, 5, 6).forEach { sizeOption ->
                    Button(
                        onClick = {
                            boardSize = sizeOption
                            selectedStartPeg = null
                            statusMessage = "Board reset to size N = $sizeOption."
                            lastClosedCount = 0
                            moveCount = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (boardSize == sizeOption) Color(0xFF0EA5E9) else Color(0xFF334155)
                        ),
                        modifier = Modifier.height(36.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("N=$sizeOption", fontSize = 12.sp)
                    }
                }
            }

            // Reset Button
            Button(
                onClick = {
                    boardState.reset()
                    selectedStartPeg = null
                    statusMessage = "Board state reset."
                    lastClosedCount = 0
                    moveCount++
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier.height(36.dp)
            ) {
                Text("RESET", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ScoreCard(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TriangularBoardCanvas(
    boardState: TriangleBoardState,
    moveCount: Int,
    selectedStartPeg: Peg?,
    onPegTapped: (Peg) -> Unit
) {
    val board = boardState.board
    val n = board.size

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(boardSizePointerKey(n, selectedStartPeg, moveCount)) {
                    detectTapGestures { tapOffset ->
                        val canvasWidth = size.width.toFloat()
                        val canvasHeight = size.height.toFloat()
                        val pitch = minOf(canvasWidth / (n + 0.5f), canvasHeight / (n * 0.866f + 0.5f))
                        val startX = (canvasWidth - (n - 1) * pitch) / 2f
                        val startY = (canvasHeight - (n - 1) * pitch * 0.866f) / 2f

                        var closestPeg: Peg? = null
                        var minDistanceSq = Float.MAX_VALUE
                        val maxRadiusSq = (pitch * 0.45f) * (pitch * 0.45f)

                        for (peg in board.validPegs) {
                            val px = startX + (peg.q + 0.5f * peg.r) * pitch
                            val py = startY + peg.r * pitch * 0.8660254f

                            val dx = tapOffset.x - px
                            val dy = tapOffset.y - py
                            val distSq = dx * dx + dy * dy

                            if (distSq < minDistanceSq && distSq <= maxRadiusSq) {
                                minDistanceSq = distSq
                                closestPeg = peg
                            }
                        }

                        closestPeg?.let { onPegTapped(it) }
                    }
                }
        ) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height

            val pitch = minOf(canvasWidth / (n + 0.5f), canvasHeight / (n * 0.866f + 0.5f))
            val startX = (canvasWidth - (n - 1) * pitch) / 2f
            val startY = (canvasHeight - (n - 1) * pitch * 0.866f) / 2f

            fun pegToOffset(peg: Peg): Offset {
                val x = startX + (peg.q + 0.5f * peg.r) * pitch
                val y = startY + peg.r * pitch * 0.8660254f
                return Offset(x, y)
            }

            // 1. Draw Completed Unit Triangles (Filled)
            for (triangle in boardState.completedTriangles) {
                val pegsList = triangle.pegs.toList()
                if (pegsList.size == 3) {
                    val p1 = pegToOffset(pegsList[0])
                    val p2 = pegToOffset(pegsList[1])
                    val p3 = pegToOffset(pegsList[2])

                    val path = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        close()
                    }

                    val fillColor = if (triangle.orientation == TriangleOrientation.UP) {
                        Color(0x9938BDF8) // Glowing cyan
                    } else {
                        Color(0x99F59E0B) // Glowing amber
                    }
                    drawPath(path = path, color = fillColor)
                    drawPath(path = path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                }
            }

            // 2. Draw Unclaimed Grid Lines (Faint slate)
            for (edge in board.validEdges) {
                val p1 = pegToOffset(edge.u)
                val p2 = pegToOffset(edge.v)
                drawLine(
                    color = Color(0xFF334155),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // 3. Draw Claimed Primitive Edges (Thick vibrant blue)
            for (edge in boardState.claimedEdges) {
                val p1 = pegToOffset(edge.u)
                val p2 = pegToOffset(edge.v)
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = p1,
                    end = p2,
                    strokeWidth = 4.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 4. Draw Valid Target Highlights if Start Peg is Selected
            if (selectedStartPeg != null) {
                for (peg in board.validPegs) {
                    if (peg != selectedStartPeg) {
                        val valRes = LineValidator.validate(selectedStartPeg, peg, board)
                        if (valRes is LineValidationResult.Valid) {
                            val pos = pegToOffset(peg)
                            drawCircle(
                                color = Color(0xFF22C55E), // Vibrant green target ring
                                radius = 16.dp.toPx(),
                                center = pos,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                        }
                    }
                }
            }

            // 5. Draw Peg Nodes
            for (peg in board.validPegs) {
                val pos = pegToOffset(peg)
                val isStart = peg == selectedStartPeg

                val color = when {
                    isStart -> Color(0xFFEF4444)
                    else -> Color(0xFFF8FAFC)
                }
                val radius = if (isStart) 11.dp.toPx() else 7.dp.toPx()

                drawCircle(
                    color = color,
                    radius = radius,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = radius * 0.45f,
                    center = pos
                )
            }
        }
    }
}

private fun boardSizePointerKey(size: Int, startPeg: Peg?, moveCount: Int): Any = "${size}_${startPeg}_${moveCount}"
