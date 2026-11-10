package com.aos.statusbar;
import android.app.*;import android.content.*;import android.graphics.*;import android.os.*;import android.view.*;import java.text.*;import java.util.*;
public class OverlayService extends Service{
 WindowManager wm;Bar bar;Handler h=new Handler(Looper.getMainLooper());Runnable tick=new Runnable(){public void run(){if(bar!=null)bar.invalidate();h.postDelayed(this,1000);}};
 int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 public void onCreate(){super.onCreate();NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("aos","AOS overlay",NotificationManager.IMPORTANCE_LOW));startForeground(1,new Notification.Builder(this,"aos").setSmallIcon(android.R.drawable.ic_menu_info_details).setContentTitle("AOS is running").setOngoing(true).build());wm=(WindowManager)getSystemService(WINDOW_SERVICE);bar=new Bar(this);WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,dp(54),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT);p.gravity=Gravity.TOP;p.y=-dp(24);wm.addView(bar,p);h.post(tick);}
 public int onStartCommand(Intent i,int f,int id){return START_STICKY;}public void onDestroy(){h.removeCallbacks(tick);if(bar!=null&&wm!=null)wm.removeView(bar);super.onDestroy();}public IBinder onBind(Intent i){return null;}
 class Bar extends View{
 Paint p=new Paint(3);Bar(Context c){super(c);}
 void fill(Canvas c,int color){p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.FILL);}
 void stroke(int color,float sw){p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sw);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);}
 void path(Canvas c,float d,float...xy){Path a=new Path();a.moveTo(xy[0]*d,xy[1]*d);for(int i=2;i<xy.length;i+=2)a.lineTo(xy[i]*d,xy[i+1]*d);c.drawPath(a,p);}
 void text(Canvas c,String s,float x,float y,float z,boolean bold){fill(c,Color.WHITE);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));p.setTextSize(z);c.drawText(s,x,y,p);}
 void notif(Canvas c,float x,float y,float d,int type){
 fill(c,Color.WHITE);
 if(type==0){ // message bubble
 c.drawRoundRect(x-5*d,y-5*d,x+5*d,y+4*d,4*d,4*d,p);Path tail=new Path();tail.moveTo(x-2*d,y+2*d);tail.lineTo(x-4*d,y+6*d);tail.lineTo(x+1*d,y+3*d);tail.close();c.drawPath(tail,p);fill(c,0xff536b88);c.drawCircle(x-2*d,y-1*d,.7f*d,p);c.drawCircle(x,y-1*d,.7f*d,p);c.drawCircle(x+2*d,y-1*d,.7f*d,p);
 }else if(type==1){ // small app glyph (rounded square + star)
 c.drawRoundRect(x-4.5f*d,y-4.5f*d,x+4.5f*d,y+4.5f*d,2*d,2*d,p);fill(c,0xff536b88);Path star=new Path();star.moveTo(x,y-3*d);star.lineTo(x+1*d,y-1*d);star.lineTo(x+3*d,y);star.lineTo(x+1*d,y+1*d);star.lineTo(x,y+3*d);star.lineTo(x-1*d,y+1*d);star.lineTo(x-3*d,y);star.lineTo(x-1*d,y-1*d);star.close();c.drawPath(star,p);
 }else{ // check mark
 stroke(Color.WHITE,1.8f*d);path(c,d,x-4,y,x-1,y+3,x+4,y-4);
 }
 }
 protected void onDraw(Canvas c){float d=getResources().getDisplayMetrics().density,w=getWidth();
 p.setShader(new LinearGradient(0,24*d,w,54*d,new int[]{0xf06d829e,0xf04b6381,0xf039506f},null,Shader.TileMode.CLAMP));c.drawRect(0,24*d,w,54*d,p);p.setShader(null);
 text(c,new SimpleDateFormat("h:mm a",java.util.Locale.US).format(new Date()),18*d,44*d,15*d,true);
 notif(c,76,39,d,0);notif(c,88,39,d,1);notif(c,100,39,d,2);
 // compact cellular bars, closely grouped with Wi-Fi and battery
 fill(c,Color.WHITE);float sx=w-105*d;for(int i=0;i<4;i++)c.drawRoundRect(sx+i*4.4f*d,49*d-(5+i*3.5f)*d,sx+(i+1)*4.4f*d-0.8f*d,49*d,1.3f*d,1.3f*d,p);
 // iOS Wi-Fi: filled, tapered curved bands (not Android-style stroked arcs)
 float wx=w-72*d,wy=48.5f*d;
 fill(c,Color.WHITE);
 // Smooth iOS Wi-Fi glyph: broad tapered arches and a round signal dot
 Path wifi=new Path();
 wifi.moveTo(wx-10.2f*d,wy-1.2f*d);wifi.cubicTo(wx-5.8f*d,wy-6.1f*d,wx-2.5f*d,wy-7.7f*d,wx,wy-7.7f*d);wifi.cubicTo(wx+2.5f*d,wy-7.7f*d,wx+5.8f*d,wy-6.1f*d,wx+10.2f*d,wy-1.2f*d);
 wifi.cubicTo(wx+8.8f*d,wy-0.4f*d,wx+8.0f*d,wy-0.2f*d,wx+7.2f*d,wy-0.9f*d);wifi.cubicTo(wx+3.9f*d,wy-4.5f*d,wx+1.7f*d,wy-5.2f*d,wx,wy-5.2f*d);wifi.cubicTo(wx-1.7f*d,wy-5.2f*d,wx-3.9f*d,wy-4.5f*d,wx-7.2f*d,wy-0.9f*d);wifi.cubicTo(wx-8.0f*d,wy-0.2f*d,wx-8.8f*d,wy-0.4f*d,wx-10.2f*d,wy-1.2f*d);wifi.close();c.drawPath(wifi,p);
 Path mid=new Path();
 mid.moveTo(wx-6.4f*d,wy-1.2f*d);mid.cubicTo(wx-3.8f*d,wy-3.7f*d,wx-1.5f*d,wy-4.5f*d,wx,wy-4.5f*d);mid.cubicTo(wx+1.5f*d,wy-4.5f*d,wx+3.8f*d,wy-3.7f*d,wx+6.4f*d,wy-1.2f*d);
 mid.cubicTo(wx+5.2f*d,wy-0.2f*d,wx+4.4f*d,wy-0.2f*d,wx+3.5f*d,wy-1.0f*d);mid.cubicTo(wx+1.8f*d,wy-2.5f*d,wx+0.8f*d,wy-2.9f*d,wx,wy-2.9f*d);mid.cubicTo(wx-0.8f*d,wy-2.9f*d,wx-1.8f*d,wy-2.5f*d,wx-3.5f*d,wy-1.0f*d);mid.cubicTo(wx-4.4f*d,wy-0.2f*d,wx-5.2f*d,wy-0.2f*d,wx-6.4f*d,wy-1.2f*d);mid.close();c.drawPath(mid,p);
 c.drawCircle(wx,wy,1.45f*d,p);
 // Rebuilt iOS-style battery: capsule, level fill and centered percentage
 Intent b=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
 int lev=b==null?100:b.getIntExtra(BatteryManager.EXTRA_LEVEL,100),sc=b==null?100:b.getIntExtra(BatteryManager.EXTRA_SCALE,100);
 int pct=sc>0?Math.max(0,Math.min(100,lev*100/sc)):100;
 float bx=w-39*d,by=33.5f*d,bw=29*d,bh=14*d;
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