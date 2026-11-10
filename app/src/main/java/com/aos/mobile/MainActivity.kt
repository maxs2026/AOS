package com.aos.mobile

import android.graphics.*
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AOSBackground = Color(0xFF080D17)
private val AOSCard = Color(0xFF151F2E)
private val AOSBlue = Color(0xFF8CB9FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide Android's native status bar while AOS is open, then draw our own
        // reference-style bar inside this app. Android does not allow ordinary
        // apps to replace SystemUI globally over other apps.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContent { AOSHome() }
    }
}

@Composable
private fun AOSHome() {
    var notifications by remember { mutableStateOf(true) }
    var clockFormat by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(primary = AOSBlue, background = AOSBackground, surface = AOSCard)) {
        Column(
            Modifier.fillMaxSize().background(AOSBackground)
        ) {
            AndroidView(
                factory = { context -> ReferenceStatusBarView(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Column(
                Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("AOS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("LIQUID GLASS EXPERIENCE", color = Color(0xFF8997AA), fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(color = AOSCard, shape = RoundedCornerShape(17.dp)) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.padding(13.dp))
                    }
                }

                Surface(
                    color = AOSCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 25.dp)
                ) {
                    Column(Modifier.padding(17.dp)) {
                        Text("STATUS BAR", color = Color(0xFF9EACC0), fontSize = 11.sp, letterSpacing = 1.7.sp)
                        Text("Reference design", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        Text(
                            "The custom bar is shown at the top while AOS is open. It follows the layout of your reference.",
                            color = Color(0xFF9AA8BA), fontSize = 13.sp, lineHeight = 19.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }

                Surface(
                    color = AOSCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                ) {
                    Column(Modifier.padding(17.dp)) {
                        Text("OPTIONS", color = Color(0xFF9EACC0), fontSize = 11.sp, letterSpacing = 1.7.sp)
                        Row(Modifier.fillMaxWidth().padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Notification symbols", color = Color.White, fontWeight = FontWeight.Medium)
                                Text("Show the three reference symbols", color = Color(0xFF8997AA), fontSize = 12.sp)
                            }
                            Switch(checked = notifications, onCheckedChange = { notifications = it })
                        }
                        HorizontalDivider(color = Color(0xFF293547))
                        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("24-hour clock", color = Color.White, fontWeight = FontWeight.Medium)
                                Text("Use 17:08 instead of 5:08 PM", color = Color(0xFF8997AA), fontSize = 12.sp)
                            }
                            Switch(checked = clockFormat, onCheckedChange = { clockFormat = it })
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "AOS • Custom status bar",
                    color = Color(0xFF68778D), fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/** Draws the user's reference composition proportionally at any screen width. */
private class ReferenceStatusBarView(context: android.content.Context) : View(context) {
    private val density = resources.displayMetrics.density
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            invalidate()
            handler.postDelayed(this, 1000L)
        }
    }
    private val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; style = Paint.Style.FILL }
    private val dark = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(45, 68, 103); style = Paint.Style.FILL }
    private val clockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
    private val batteryText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(42, 62, 75)
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val path = Path()

    init { handler.post(tick) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val save = canvas.save()
        canvas.scale(density, density)
        val w = width / density
        val h = height / density
        val u = h / 56f

        val gradient = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, w, h,
                intArrayOf(android.graphics.Color.rgb(0, 100, 190), android.graphics.Color.rgb(55, 82, 132), android.graphics.Color.rgb(34, 68, 77), android.graphics.Color.rgb(9, 43, 39)),
                floatArrayOf(0f, .36f, .73f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, gradient)
        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(w * .47f, h * .2f, w * .52f, intArrayOf(0x335A8FFF, 0x00000000), null, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h, glow)

        val cy = h * .51f
        val clock = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
        clockPaint.textSize = 22f * u
        canvas.drawText(clock, w * .075f, cy + 8f * u, clockPaint)

        drawMessenger(canvas, w * .201f, cy, u)
        drawBell(canvas, w * .26f, cy, u)
        drawShield(canvas, w * .319f, cy, u)

        drawSignal(canvas, w * .714f, cy, u)
        drawWifi(canvas, w * .812f, cy, u)
        drawBattery(canvas, w * .864f, cy, u)
        canvas.restoreToCount(save)
    }

    private fun drawMessenger(c: Canvas, x: Float, y: Float, u: Float) {
        c.drawCircle(x, y, 14f * u, white)
        path.reset()
        path.moveTo(x - 10f*u, y + 1f*u); path.lineTo(x - 2f*u, y - 7f*u)
        path.lineTo(x + 2f*u, y - 2f*u); path.lineTo(x + 9f*u, y - 7f*u)
        path.lineTo(x + 2f*u, y + 6f*u); path.lineTo(x - 3f*u, y + 2f*u)
        path.close()
        c.drawPath(path, dark)
        path.reset()
        path.moveTo(x - 9f*u, y + 10f*u); path.lineTo(x - 10f*u, y + 17f*u); path.lineTo(x - 2f*u, y + 11f*u); path.close()
        c.drawPath(path, white)
    }

    private fun drawBell(c: Canvas, x: Float, y: Float, u: Float) {
        path.reset()
        path.moveTo(x - 10f*u, y + 7f*u)
        path.quadTo(x - 7f*u, y + 2f*u, x - 7f*u, y - 5f*u)
        path.quadTo(x - 7f*u, y - 15f*u, x, y - 15f*u)
        path.quadTo(x + 7f*u, y - 15f*u, x + 7f*u, y - 5f*u)
        path.quadTo(x + 7f*u, y + 2f*u, x + 10f*u, y + 7f*u)
        path.close()
        c.drawPath(path, white)
        c.drawRoundRect(x - 11f*u, y + 6f*u, x + 11f*u, y + 9f*u, 2f*u, 2f*u, white)
        c.drawCircle(x, y + 12f*u, 3f*u, white)
    }

    private fun drawShield(c: Canvas, x: Float, y: Float, u: Float) {
        path.reset()
        path.moveTo(x, y - 16f*u); path.lineTo(x + 12f*u, y - 11f*u)
        path.lineTo(x + 11f*u, y + 3f*u); path.quadTo(x + 8f*u, y + 12f*u, x, y + 16f*u)
        path.quadTo(x - 8f*u, y + 12f*u, x - 11f*u, y + 3f*u)
        path.lineTo(x - 12f*u, y - 11f*u); path.close()
        c.drawPath(path, white)
        val check = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(48, 72, 103); style = Paint.Style.STROKE; strokeWidth = 2.4f*u; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
        path.reset(); path.moveTo(x - 5f*u, y); path.lineTo(x - 1f*u, y + 4f*u); path.lineTo(x + 6f*u, y - 4f*u)
        c.drawPath(path, check)
    }

    private fun drawSignal(c: Canvas, x: Float, y: Float, u: Float) {
        val widths = floatArrayOf(6f, 7f, 7f, 7f)
        val heights = floatArrayOf(8f, 14f, 21f, 28f)
        var offset = 0f
        for (i in heights.indices) {
            c.drawRoundRect(x + offset*u, y + (14f-heights[i])*u/2f, x + (offset+widths[i])*u, y + (14f+heights[i])*u/2f, 2f*u, 2f*u, white)
            offset += widths[i] + 4f
        }
    }

    private fun drawWifi(c: Canvas, x: Float, y: Float, u: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 3.7f*u; strokeCap = Paint.Cap.ROUND }
        c.drawArc(RectF(x - 18f*u, y - 14f*u, x + 18f*u, y + 17f*u), 220f, 100f, false, p)
        c.drawArc(RectF(x - 11f*u, y - 7f*u, x + 11f*u, y + 14f*u), 220f, 100f, false, p)
        c.drawCircle(x, y + 10f*u, 2.7f*u, white)
    }

    private fun drawBattery(c: Canvas, x: Float, y: Float, u: Float) {
        val levelIntent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = levelIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100
        val scale = levelIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val percent = if (scale > 0) (level * 100 / scale).coerceIn(0, 100) else 100
        c.drawRoundRect(x, y - 12f*u, x + 43f*u, y + 12f*u, 7f*u, 7f*u, white)
        c.drawRoundRect(x + 44f*u, y - 5f*u, x + 47f*u, y + 5f*u, 2f*u, 2f*u, white)
        batteryText.textSize = 17f*u
        c.drawText(percent.toString(), x + 21.5f*u, y + 6f*u, batteryText)
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(tick)
        super.onDetachedFromWindow()
    }
}
