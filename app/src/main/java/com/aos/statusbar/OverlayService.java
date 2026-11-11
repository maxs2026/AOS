package com.aos.statusbar;
import android.app.*;import android.content.*;import android.graphics.*;import android.os.*;import android.view.*;import java.text.*;import java.util.*;
public class OverlayService extends Service{
 WindowManager wm;Bar bar;Handler h=new Handler(Looper.getMainLooper());Runnable tick=new Runnable(){public void run(){if(bar!=null)bar.invalidate();h.postDelayed(this,1000);}};
 int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 public void onCreate(){super.onCreate();NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("aos","AOS overlay",NotificationManager.IMPORTANCE_LOW));startForeground(1,new Notification.Builder(this,"aos").setSmallIcon(android.R.drawable.ic_menu_info_details).setContentTitle("AOS is running").setOngoing(true).build());wm=(WindowManager)getSystemService(WINDOW_SERVICE);bar=new Bar(this);WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,dp(44),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT);p.gravity=Gravity.TOP;p.y=0;wm.addView(bar,p);h.post(tick);}
 public int onStartCommand(Intent i,int f,int id){return START_STICKY;}public void onDestroy(){h.removeCallbacks(tick);if(bar!=null&&wm!=null)wm.removeView(bar);super.onDestroy();}public IBinder onBind(Intent i){return null;}
 class Bar extends View{
 Paint p=new Paint(3);Bar(Context c){super(c);}
 void fill(Canvas c,int color){p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.FILL);}
 void stroke(int color,float sw){p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sw);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);}
 void path(Canvas c,float d,float...xy){Path a=new Path();a.moveTo(xy[0]*d,xy[1]*d);for(int i=2;i<xy.length;i+=2)a.lineTo(xy[i]*d,xy[i+1]*d);c.drawPath(a,p);}
 void text(Canvas c,String s,float x,float y,float z,boolean bold){fill(c,Color.WHITE);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));p.setTextSize(z);c.drawText(s,x,y,p);}
 void notif(Canvas c,float x,float y,float d,int type){
 fill(c,Color.WHITE);
 if(type==0){ // Messenger-style circular chat badge with lightning
 Path bubble=new Path();bubble.moveTo(x-5.2f*d,y-4.5f*d);bubble.cubicTo(x-1*d,y-6*d,x+5*d,y-3.5f*d,x+5*d,y+1*d);bubble.cubicTo(x+5*d,y+4*d,x+2*d,y+5*d,x-1*d,y+4.5f*d);bubble.lineTo(x-4*d,y+7*d);bubble.lineTo(x-3.5f*d,y+3.2f*d);bubble.cubicTo(x-6*d,y+1*d,x-6*d,y-2*d,x-5.2f*d,y-4.5f*d);bubble.close();c.drawPath(bubble,p);
 fill(c,0xff536b88);Path bolt=new Path();bolt.moveTo(x-1.5f*d,y-2.8f*d);bolt.lineTo(x+1.4f*d,y-1*d);bolt.lineTo(x-.2f*d,y+.1f*d);bolt.lineTo(x+1.2f*d,y+2.4f*d);bolt.lineTo(x-2*d,y+.3f*d);bolt.lineTo(x-.5f*d,y-.8f*d);bolt.close();c.drawPath(bolt,p);
 }else if(type==1){ // classic notification bell
 fill(c,Color.WHITE);Path bell=new Path();bell.moveTo(x-4.8f*d,y+2.8f*d);bell.cubicTo(x-3.2f*d,y+1*d,x-3.8f*d,y-4.5f*d,x,y-5.2f*d);bell.cubicTo(x+3.8f*d,y-4.5f*d,x+3.2f*d,y+1*d,x+4.8f*d,y+2.8f*d);bell.close();c.drawPath(bell,p);c.drawCircle(x,y-5.4f*d,1.2f*d,p);c.drawRoundRect(x-5.2f*d,y+2.2f*d,x+5.2f*d,y+4*d,1*d,1*d,p);c.drawCircle(x,y+5*d,1.5f*d,p);
 }else{ // shield with check
 Path shield=new Path();shield.moveTo(x,y-6*d);shield.cubicTo(x+2.2f*d,y-4.5f*d,x+4.5f*d,y-4.2f*d,x+5*d,y-3.8f*d);shield.lineTo(x+4.5f*d,y+1.5f*d);shield.cubicTo(x+3.5f*d,y+4*d,x+1.5f*d,y+5.5f*d,x,y+6*d);shield.cubicTo(x-1.5f*d,y+5.5f*d,x-3.5f*d,y+4*d,x-4.5f*d,y+1.5f*d);shield.lineTo(x-5*d,y-3.8f*d);shield.cubicTo(x-4.5f*d,y-4.2f*d,x-2.2f*d,y-4.5f*d,x,y-6*d);shield.close();c.drawPath(shield,p);stroke(0xff536b88,1.7f*d);path(c,d,x-2.5f,y,x-.5f,y+2,x+3,y-2.5f);
 }}
 protected void onDraw(Canvas c){float d=getResources().getDisplayMetrics().density,w=getWidth();
 p.setShader(new LinearGradient(0,0,w,44*d,new int[]{0xf06d829e,0xf04b6381,0xf039506f},null,Shader.TileMode.CLAMP));c.drawRect(0,0,w,44*d,p);p.setShader(null);
 text(c,new SimpleDateFormat("h:mm",java.util.Locale.US).format(new Date()),18*d,33*d,18*d,true);
 notif(c,76,22,d,0);notif(c,106,22,d,1);notif(c,136,22,d,2);
 // compact cellular bars, closely grouped with Wi-Fi and battery
 fill(c,Color.WHITE);float sx=w-105*d;for(int i=0;i<4;i++)c.drawRoundRect(sx+i*4.4f*d,34*d-(5+i*3.5f)*d,sx+(i+1)*4.4f*d-0.8f*d,34*d,1.3f*d,1.3f*d,p);
 // Apple-like Wi-Fi glyph: three separate solid curved signal bands + dot
 float wx=w-72*d,wy=33.5f*d;
 fill(c,Color.WHITE);
 // Outer band (wide, gently tapered)
 Path wifiOuter=new Path();
 wifiOuter.moveTo(wx-11*d,wy-1*d);
 wifiOuter.cubicTo(wx-5.8f*d,wy-7.5f*d,wx+5.8f*d,wy-7.5f*d,wx+11*d,wy-1*d);
 wifiOuter.cubicTo(wx+9.8f*d,wy+0.1f*d,wx+8.9f*d,wy+0.1f*d,wx+7.8f*d,wy-1*d);
 wifiOuter.cubicTo(wx+3.8f*d,wy-5.2f*d,wx-3.8f*d,wy-5.2f*d,wx-7.8f*d,wy-1*d);
 wifiOuter.cubicTo(wx-8.9f*d,wy+0.1f*d,wx-9.8f*d,wy+0.1f*d,wx-11*d,wy-1*d);wifiOuter.close();c.drawPath(wifiOuter,p);
 // Middle band
 Path wifiMid=new Path();
 wifiMid.moveTo(wx-7.1f*d,wy-1*d);
 wifiMid.cubicTo(wx-3.5f*d,wy-4.8f*d,wx+3.5f*d,wy-4.8f*d,wx+7.1f*d,wy-1*d);
 wifiMid.cubicTo(wx+5.9f*d,wy+0.1f*d,wx+5*d,wy+0.1f*d,wx+4*d,wy-1*d);
 wifiMid.cubicTo(wx+1.8f*d,wy-3*d,wx-1.8f*d,wy-3*d,wx-4*d,wy-1*d);
 wifiMid.cubicTo(wx-5*d,wy+0.1f*d,wx-5.9f*d,wy+0.1f*d,wx-7.1f*d,wy-1*d);wifiMid.close();c.drawPath(wifiMid,p);
 // Inner curved band
 Path wifiInner=new Path();
 wifiInner.moveTo(wx-3.4f*d,wy-1*d);
 wifiInner.cubicTo(wx-1.7f*d,wy-2.8f*d,wx+1.7f*d,wy-2.8f*d,wx+3.4f*d,wy-1*d);
 wifiInner.cubicTo(wx+2.4f*d,wy+0.2f*d,wx+1.5f*d,wy+0.2f*d,wx+0.8f*d,wy-0.6f*d);
 wifiInner.cubicTo(wx+0.3f*d,wy-1.1f*d,wx-0.3f*d,wy-1.1f*d,wx-0.8f*d,wy-0.6f*d);
 wifiInner.cubicTo(wx-1.5f*d,wy+0.2f*d,wx-2.4f*d,wy+0.2f*d,wx-3.4f*d,wy-1*d);wifiInner.close();c.drawPath(wifiInner,p);
 c.drawCircle(wx,wy,1.45f*d,p);
 // Rebuilt iOS-style battery: capsule, level fill and centered percentage
 Intent b=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
 int lev=b==null?100:b.getIntExtra(BatteryManager.EXTRA_LEVEL,100),sc=b==null?100:b.getIntExtra(BatteryManager.EXTRA_SCALE,100);
 int pct=sc>0?Math.max(0,Math.min(100,lev*100/sc)):100;
 float bx=w-39*d,by=10.5f*d,bw=29*d,bh=14*d;
 fill(c,0x18ffffff);c.drawRoundRect(bx,by,bx+bw,by+bh,4.2f*d,4.2f*d,p);
 float ix=bx+1.25f*d,iy=by+1.25f*d,iw=bw-2.5f*d,ih=bh-2.5f*d;
 boolean charging=b!=null&&(b.getIntExtra(BatteryManager.EXTRA_STATUS,-1)==BatteryManager.BATTERY_STATUS_CHARGING||b.getIntExtra(BatteryManager.EXTRA_STATUS,-1)==BatteryManager.BATTERY_STATUS_FULL);
 int levelColor=charging?0xff65d66f:Color.WHITE;
 if(pct>0){fill(c,levelColor);c.drawRoundRect(ix,iy,ix+iw*pct/100f,iy+ih,3.1f*d,3.1f*d,p);}
 stroke(0xfff7f9fc,1.15f*d);c.drawRoundRect(bx,by,bx+bw,by+bh,4.2f*d,4.2f*d,p);
 fill(c,0xfff7f9fc);c.drawRoundRect(bx+bw+1*d,by+4.4f*d,bx+bw+2.8f*d,by+9.6f*d,.8f*d,.8f*d,p);
 p.setShader(null);p.setStyle(Paint.Style.FILL);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(8.1f*d);
 String batteryLabel=pct+"%";float labelW=p.measureText(batteryLabel);
 fill(c,pct>=55?0xff26384a:Color.WHITE);c.drawText(batteryLabel,bx+(bw-labelW)/2,by+9.7f*d,p);
 }}
}