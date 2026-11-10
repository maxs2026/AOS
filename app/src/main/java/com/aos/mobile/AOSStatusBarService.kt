package com.aos.mobile
import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.os.*
import android.view.*
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class AOSStatusBarService: Service() {
 private lateinit var wm: WindowManager
 private var bar: LinearLayout? = null
 private val handler=Handler(Looper.getMainLooper())
 private val tick=object:Runnable{override fun run(){refresh();handler.postDelayed(this,1000)}}
 private val battery=object:BroadcastReceiver(){override fun onReceive(c:Context?,i:Intent?){refresh()}}
 override fun onBind(intent:Intent?)=null
 override fun onCreate(){super.onCreate();wm=getSystemService(WINDOW_SERVICE) as WindowManager}
 override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int{show();return START_STICKY}
 private fun show(){
  if(bar!=null)return
  val root=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(16),0,dp(16),0);setBackgroundColor(Color.TRANSPARENT)}
  val left=LinearLayout(this).apply{gravity=Gravity.CENTER_VERTICAL}
  val clock=TextView(this).apply{tag="clock";textColor=Color.WHITE;textSize=14f;typeface=Typeface.DEFAULT_BOLD}
  left.addView(clock);left.addView(TextView(this).apply{tag="notif";text="  ●  ▪";textColor=Color.WHITE;textSize=11f})
  root.addView(left,LinearLayout.LayoutParams(0,dp(30),1f))
  root.addView(TextView(this).apply{tag="right";textColor=Color.WHITE;textSize=12f;typeface=Typeface.DEFAULT_BOLD;gravity=Gravity.CENTER_VERTICAL})
  bar=root
  val p=WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,dp(34),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,android.graphics.PixelFormat.TRANSLUCENT)
  p.gravity=Gravity.TOP or Gravity.START;p.y=0
  try{wm.addView(root,p);refresh();handler.post(tick);registerReceiver(battery,IntentFilter(Intent.ACTION_BATTERY_CHANGED))}catch(_:Exception){stopSelf()}
 }
 private fun refresh(){
  val r=bar?:return
  r.findViewWithTag<TextView>("clock")?.text=SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date())
  val b=registerReceiver(null,IntentFilter(Intent.ACTION_BATTERY_CHANGED));val level=b?.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)?:-1;val scale=b?.getIntExtra(BatteryManager.EXTRA_SCALE,100)?:100;val pct=if(level>=0)level*100/scale else 0;val charging=b?.getIntExtra(BatteryManager.EXTRA_STATUS,-1)==BatteryManager.BATTERY_STATUS_CHARGING
  r.findViewWithTag<TextView>("right")?.text="▮▮  Wi-Fi   "+(if(charging)"⚡ " else "")+"▰ "+pct+"%"
 }
 private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
 override fun onDestroy(){handler.removeCallbacks(tick);try{unregisterReceiver(battery)}catch(_:Exception){};bar?.let{try{wm.removeView(it)}catch(_:Exception){}};bar=null;super.onDestroy()}
}