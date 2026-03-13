package com.universalcalculator.ui.graphing

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.universalcalculator.core.math.ExprNode
import com.universalcalculator.core.math.ExpressionEvaluator
import com.universalcalculator.viewmodel.GraphEquation
import kotlin.math.*

enum class Relation { EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL }

class ParsedEquation(val lhs: ExprNode, val rhs: ExprNode, val relation: Relation) {
    fun diff(x: Double, y: Double): Double = lhs.eval(x, y) - rhs.eval(x, y)
    
    fun satisfiesInequality(diff: Double): Boolean {
        return when (relation) {
            Relation.LESS -> diff < 0
            Relation.LESS_EQUAL -> diff <= 0
            Relation.GREATER -> diff > 0
            Relation.GREATER_EQUAL -> diff >= 0
            Relation.EQUAL -> false
        }
    }
}

fun parseEquation(eqStr: String, evaluator: ExpressionEvaluator): ParsedEquation? {
    if (eqStr.isBlank()) return null
    var relation: Relation
    var lhsStr: String
    var rhsStr: String

    when {
        eqStr.contains("<=") -> { relation = Relation.LESS_EQUAL; val p = eqStr.split("<="); lhsStr = p[0]; rhsStr = p[1] }
        eqStr.contains(">=") -> { relation = Relation.GREATER_EQUAL; val p = eqStr.split(">="); lhsStr = p[0]; rhsStr = p[1] }
        eqStr.contains("<") -> { relation = Relation.LESS; val p = eqStr.split("<"); lhsStr = p[0]; rhsStr = p[1] }
        eqStr.contains(">") -> { relation = Relation.GREATER; val p = eqStr.split(">"); lhsStr = p[0]; rhsStr = p[1] }
        eqStr.contains("=") -> { relation = Relation.EQUAL; val p = eqStr.split("="); lhsStr = p[0]; rhsStr = p[1] }
        else -> { relation = Relation.EQUAL; lhsStr = "y"; rhsStr = eqStr }
    }

    return try {
        val lhsExpr = evaluator.compile(lhsStr)
        val rhsExpr = evaluator.compile(rhsStr)
        ParsedEquation(lhsExpr, rhsExpr, relation)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun GraphView(
    equations: List<GraphEquation>,
    modifier: Modifier = Modifier,
    evaluator: ExpressionEvaluator = remember { ExpressionEvaluator() }
) {
    var scale by remember { mutableStateOf(50f) } // Pixels per unit
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val paintBackground = Color(0xFF1E1E1E)
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(paintBackground)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(5f, 5000f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2 + offsetX
        val centerY = height / 2 + offsetY

        // Determine step size for grid based on scale
        // We want grid lines approx every 50-100 pixels
        val targetPixels = 80f
        val rawUnitSize = targetPixels / scale
        val magnitude = 10.0.pow(floor(log10(rawUnitSize.toDouble())))
        val normalized = rawUnitSize / magnitude
        val stepUnit = when {
            normalized < 1.5 -> 1.0
            normalized < 3.5 -> 2.0
            normalized < 7.5 -> 5.0
            else -> 10.0
        } * magnitude

        val stepPx = (stepUnit * scale).toFloat()

        // Draw grid & labels
        val startX = (-(centerX) / stepPx).toInt() - 1
        val endX = ((width - centerX) / stepPx).toInt() + 1
        for (i in startX..endX) {
            val x = centerX + i * stepPx
            val isAxis = i == 0
            drawLine(
                color = if (isAxis) Color.White else Color.DarkGray,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = if (isAxis) 3f else 1f
            )
            if (!isAxis) {
                val label = String.format("%.2f", i * stepUnit).trimEnd('0').trimEnd('.')
                drawContext.canvas.nativeCanvas.drawText(label, x, centerY + 35f, textPaint)
            }
        }

        val startY = (-(centerY) / stepPx).toInt() - 1
        val endY = ((height - centerY) / stepPx).toInt() + 1
        for (i in startY..endY) {
            val y = centerY + i * stepPx
            val isAxis = i == 0
            drawLine(
                color = if (isAxis) Color.White else Color.DarkGray,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = if (isAxis) 3f else 1f
            )
            if (!isAxis) {
                val label = String.format("%.2f", -i * stepUnit).trimEnd('0').trimEnd('.')
                drawContext.canvas.nativeCanvas.drawText(label, centerX + 20f, y + 10f, textPaint.apply { textAlign = Paint.Align.LEFT })
            }
        }

        textPaint.textAlign = Paint.Align.CENTER // Reset

        // Process Equations
        val activeEquations = equations.filter { it.isVisible && it.expression.text.isNotBlank() }
        
        for (eq in activeEquations) {
            val parsed = parseEquation(eq.expression.text, evaluator) ?: continue
            val isInequality = parsed.relation != Relation.EQUAL
            
            val resolution = if (isInequality) 4 else 3 // Pixels per grid cell
            val cols = (width / resolution).toInt() + 1
            val rows = (height / resolution).toInt() + 1
            
            val values = DoubleArray(cols * rows)
            
            for (r in 0 until rows) {
                val py = r * resolution
                val graphY = (centerY - py) / scale
                for (c in 0 until cols) {
                    val px = c * resolution
                    val graphX = (px - centerX) / scale
                    try {
                        values[r * cols + c] = parsed.diff(graphX.toDouble(), graphY.toDouble())
                    } catch (e: Exception) {
                        values[r * cols + c] = Double.NaN
                    }
                }
            }

            if (isInequality) {
                val points = mutableListOf<Offset>()
                for (r in 0 until rows) {
                    val py = (r * resolution).toFloat()
                    for (c in 0 until cols) {
                        val px = (c * resolution).toFloat()
                        val v = values[r * cols + c]
                        if (!v.isNaN() && parsed.satisfiesInequality(v)) {
                            points.add(Offset(px, py))
                        }
                    }
                }
                drawPoints(points, PointMode.Points, color = eq.color.copy(alpha = 0.3f), strokeWidth = resolution.toFloat())
                // Optionally draw the boundary (EQUAL) with Marching Squares for inequality boundary
            }
            
            // Marching Squares for EQUAL (boundary)
            val path = Path()
            for (r in 0 until rows - 1) {
                for (c in 0 until cols - 1) {
                    val v0 = values[r * cols + c]
                    val v1 = values[r * cols + c + 1]
                    val v2 = values[(r + 1) * cols + c + 1]
                    val v3 = values[(r + 1) * cols + c]
                    
                    if (v0.isNaN() || v1.isNaN() || v2.isNaN() || v3.isNaN()) continue
                    
                    var state = 0
                    if (v0 > 0) state = state or 1
                    if (v1 > 0) state = state or 2
                    if (v2 > 0) state = state or 4
                    if (v3 > 0) state = state or 8

                    if (state == 0 || state == 15) continue

                    val x0 = (c * resolution).toFloat()
                    val y0 = (r * resolution).toFloat()
                    val x1 = x0 + resolution
                    val y1 = y0 + resolution

                    // Interpolate
                    fun interp(vA: Double, vB: Double): Float {
                        if (vA == vB) return 0.5f // prevent div by zero
                        return (vA / (vA - vB)).toFloat().coerceIn(0f, 1f)
                    }

                    val pTop = Offset(x0 + resolution * interp(v0, v1), y0)
                    val pRight = Offset(x1, y0 + resolution * interp(v1, v2))
                    val pBottom = Offset(x0 + resolution * interp(v3, v2), y1)
                    val pLeft = Offset(x0, y0 + resolution * interp(v0, v3))

                    when (state) {
                        1, 14 -> { path.moveTo(pLeft.x, pLeft.y); path.lineTo(pTop.x, pTop.y) }
                        2, 13 -> { path.moveTo(pTop.x, pTop.y); path.lineTo(pRight.x, pRight.y) }
                        4, 11 -> { path.moveTo(pRight.x, pRight.y); path.lineTo(pBottom.x, pBottom.y) }
                        8, 7 -> { path.moveTo(pBottom.x, pBottom.y); path.lineTo(pLeft.x, pLeft.y) }
                        3, 12 -> { path.moveTo(pLeft.x, pLeft.y); path.lineTo(pRight.x, pRight.y) }
                        6, 9 -> { path.moveTo(pTop.x, pTop.y); path.lineTo(pBottom.x, pBottom.y) }
                        5 -> { path.moveTo(pLeft.x, pLeft.y); path.lineTo(pTop.x, pTop.y); path.moveTo(pRight.x, pRight.y); path.lineTo(pBottom.x, pBottom.y) }
                        10 -> { path.moveTo(pLeft.x, pLeft.y); path.lineTo(pBottom.x, pBottom.y); path.moveTo(pTop.x, pTop.y); path.lineTo(pRight.x, pRight.y) }
                    }
                }
            }
            drawPath(path, eq.color, style = Stroke(width = 4f))
        }
    }
}
