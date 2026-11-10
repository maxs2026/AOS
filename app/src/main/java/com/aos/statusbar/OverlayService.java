package com.aos.statusbar;
import android.app.*;import android.content.*;import android.graphics.*;import android.os.*;import android.view.*;import java.text.*;import java.util.*;
public class OverlayService extends Service{
 WindowManager wm;Bar bar;Handler h=new Handler(Looper.getMainLooper());Runnable tick=new Runnable(){public void run(){if(bar!=null)bar.invalidate();h.postDelayed(this,1000);}};
 int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 public void onCreate(){super.onCreate();NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("aos","AOS overlay",NotificationManager.IMPORTANCE_LOW));startForeground(1,new Notification.Builder(this,"aos").setSmallIcon(android.R.drawable.ic_menu_info_details).setContentTitle("AOS is running").setOngoing(true).build());wm=(WindowManager)getSystemService(WINDOW_SERVICE);bar=new Bar(this);WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,dp(54),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT);p.gravity=Gravity.TOP;p.y=-dp(24);wm.addView(bar,p);h.post(tick);}
 public int onStartCommand(Intent i,int f,int id){return START_STICKY;}public void onDestroy(){h.removeCallbacks(tick);if(bar!=null&&wm!=null)wm.removeView(bar);super.onDestroy();}public IBinder onBind(Intent i){return null;}
 class Bar extends View{Paint p=new Paint(3);Bar(Context c){super(c);}void color(int x){p.setShader(null);p.setColor(x);p.setStyle(Paint.Style.FILL);}void text(Canvas c,String s,float x,float y,float z,boolean bold){color(Color.WHITE);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));p.setTextSize(z);c.drawText(s,x,y,p);}
 protected void onDraw(Canvas c){float d=getResources().getDisplayMetrics().density,w=getWidth();p.setShader(new LinearGradient(0,24*d,w,54*d,new int[]{0xf06d829e,0xf04b6381,0xf039506f},null,Shader.TileMode.CLAMP));c.drawRect(0,24*d,w,54*d,p);p.setShader(null);
 text(c,new SimpleDateFormat("HH:mm").format(new Date()),18*d,44*d,15*d,true);
 // Notification glyphs (compact, reference-like)
 color(Color.WHITE);c.drawCircle(76*d,39*d,2.1f*d,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.1f*d);Path diamond=new Path();diamond.moveTo(86*d,36*d);diamond.lineTo(89*d,39*d);diamond.lineTo(86*d,42*d);diamond.lineTo(83*d,39*d);diamond.close();c.drawPath(diamond,p);text(c,"✓",92*d,42*d,10*d,true);p.setStyle(Paint.Style.FILL);
 // Signal strength
 float sx=w-153*d;for(int i=0;i<4;i++){color(Color.WHITE);c.drawRoundRect(sx+i*5*d,45*d-(5+i*3.5f)*d,sx+(i+1)*5*d-1*d,45*d,1.4f*d,1.4f*d,p);}
 // Wi-Fi arcs and dot
 p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.8f*d);c.drawArc(w-111*d,29*d,w-91*d,49*d,225,90,false,p);c.drawArc(w-106*d,34*d,w-96*d,44*d,225,90,false,p);p.setStyle(Paint.Style.FILL);color(Color.WHITE);c.drawCircle(w-101*d,45*d,1.5f*d,p);
 // Battery outline + percentage
 Intent b=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));int lev=b==null?100:b.getIntExtra(BatteryManager.EXTRA_LEVEL,100),sc=b==null?100:b.getIntExtra(BatteryManager.EXTRA_SCALE,100),pct=sc>0?lev*100/sc:100;float bx=w-69*d,by=30*d;color(Color.WHITE);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.4f*d);c.drawRoundRect(bx,by,bx+27*d,by+16*d,3*d,3*d,p);p.setStyle(Paint.Style.FILL);c.drawRoundRect(bx+2*d,by+2*d,bx+2*d+23*d*pct/100f,by+14*d,2*d,2*d,p);text(c,pct+"%",w-34*d,44*d,10*d,true);
 }}}
