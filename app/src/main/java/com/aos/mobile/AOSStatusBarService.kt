package com.aos.mobile

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AOSStatusBarService : Service() {
    private lateinit var windowManager: WindowManager
    private var bar: AOSStatusBarView? = null
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            bar?.invalidate()
            handler.postDelayed(this, 1000L)
        }
    }
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            bar?.invalidate()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay()
        return START_STICKY
    }

    private fun showOverlay() {
        if (bar != null) return
        val view = AOSStatusBarView(this)
        bar = view
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dp(76),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            y = 0
        }
        try {
            windowManager.addView(view, params)
            handler.post(tick)
            registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (_: Exception) {
            stopSelf()
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        try { unregisterReceiver(batteryReceiver) } catch (_: Exception) { }
        bar?.let { try { windowManager.removeView(it) } catch (_: Exception) { } }
        bar = null
        super.onDestroy()
    }
}

class AOSStatusBarView(context: Context) : View(context) {
    private val density = resources.displayMetrics.density
    private val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        textSize = 29f
    }
    private val background = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val save = canvas.save()
        canvas.scale(density, density)
        val w = width / density
        val h = height / density

        background.shader = LinearGradient(
            0f, 0f, w, h,
            intArrayOf(Color.rgb(17, 103, 184), Color.rgb(56, 78, 126), Color.rgb(45, 70, 82), Color.rgb(13, 43, 42)),
            floatArrayOf(0f, 0.34f, 0.72f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, background)
        background.shader = RadialGradient(w * 0.48f, h * 0.15f, w * 0.55f,
            intArrayOf(0x335F9DFF, 0x00000000), null, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, background)
        background.shader = null

        val now = SimpleDateFormat("H:mm", Locale.getDefault()).format(Date())
        text.textSize = 29f
        text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        canvas.drawText(now, 58f, 49f, text)

        drawMessenger(canvas, 154f, 36f)
        drawBell(canvas, 200f, 36f)
        drawShield(canvas, 245f, 36f)

        val right = w - 56f
        drawBattery(canvas, right - 58f, 36f)
        drawWifi(canvas, right - 151f, 36f)
        drawSignal(canvas, right - 215f, 36f)
        canvas.restoreToCount(save)
    }

    private fun drawMessenger(c: Canvas, x: Float, y: Float) {
        white.style = Paint.Style.FILL
        c.drawCircle(x, y, 14.5f, white)
        path.reset()
        path.moveTo(x - 9f, y + 1f); path.lineTo(x - 2f, y - 7f)
        path.lineTo(x + 2f, y - 2f); path.lineTo(x + 8f, y - 6f)
        path.lineTo(x + 1f, y + 5f); path.lineTo(x - 3f, y + 1f)
        path.close()
        white.color = Color.rgb(48, 75, 115)
        c.drawPath(path, white)
        white.color = Color.WHITE
    }

    private fun drawBell(c: Canvas, x: Float, y: Float) {
        path.reset()
        path.moveTo(x - 10f, y + 7f)
        path.quadTo(x - 7f, y + 2f, x - 7f, y - 5f)
        path.quadTo(x - 7f, y - 14f, x, y - 15f)
        path.quadTo(x + 7f, y - 14f, x + 7f, y - 5f)
        path.quadTo(x + 7f, y + 2f, x + 10f, y + 7f)
        path.close()
        c.drawPath(path, white)
        c.drawRoundRect(x - 11f, y + 6f, x + 11f, y + 9f, 2f, 2f, white)
        c.drawCircle(x, y + 12f, 3f, white)
    }

    private fun drawShield(c: Canvas, x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y - 16f); path.lineTo(x + 12f, y - 11f)
        path.lineTo(x + 11f, y + 3f); path.quadTo(x + 8f, y + 12f, x, y + 16f)
        path.quadTo(x - 8f, y + 12f, x - 11f, y + 3f)
        path.lineTo(x - 12f, y - 11f); path.close()
        c.drawPath(path, white)
        val check = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(51, 78, 111); style = Paint.Style.STROKE; strokeWidth = 2.4f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
        path.reset(); path.moveTo(x - 5f, y); path.lineTo(x - 1f, y + 4f); path.lineTo(x + 6f, y - 4f)
        c.drawPath(path, check)
    }

    private fun drawSignal(c: Canvas, x: Float, y: Float) {
        white.style = Paint.Style.FILL
        c.drawRoundRect(x, y + 6f, x + 7f, y + 15f, 2f, 2f, white)
        c.drawRoundRect(x + 11f, y, x + 18f, y + 15f, 2f, 2f, white)
        c.drawRoundRect(x + 22f, y - 7f, x + 29f, y + 15f, 2f, 2f, white)
        c.drawRoundRect(x + 33f, y - 14f, x + 40f, y + 15f, 2f, 2f, white)
    }

    private fun drawWifi(c: Canvas, x: Float, y: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 4.2f; strokeCap = Paint.Cap.ROUND }
        c.drawArc(RectF(x - 17f, y - 14f, x + 17f, y + 16f), 220f, 100f, false, p)
        p.strokeWidth = 4f
        c.drawArc(RectF(x - 11f, y - 7f, x + 11f, y + 13f), 220f, 100f, false, p)
        c.drawCircle(x, y + 10f, 2.8f, white)
    }

    private fun drawBattery(c: Canvas, x: Float, y: Float) {
        white.style = Paint.Style.FILL
        c.drawRoundRect(x - 2f, y - 13f, x + 48f, y + 13f, 8f, 8f, white)
        c.drawRoundRect(x + 49f, y - 5f, x + 53f, y + 5f, 2f, 2f, white)
        val dark = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(35, 57, 63); textSize = 20f; typeface = Typeface.create("sans-serif", Typeface.BOLD); textAlign = Paint.Align.CENTER }
        c.drawText(batteryPercent().toString(), x + 23f, y + 7f, dark)
    }

    private fun batteryPercent(): Int {
        val intent = context.registerReceiver(null, batteryFilter)
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        return if (scale > 0) (level * 100 / scale).coerceIn(0, 100) else 100
    }
}
