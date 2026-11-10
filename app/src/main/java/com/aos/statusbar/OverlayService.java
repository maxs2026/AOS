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
 // cellular bars, rising heights with rounded ends
 fill(c,Color.WHITE);float sx=w-153*d;for(int i=0;i<4;i++)c.drawRoundRect(sx+i*5*d,49*d-(5+i*3.5f)*d,sx+(i+1)*5*d-1*d,49*d,1.5f*d,1.5f*d,p);
 // wifi: three arcs and center dot
 stroke(Color.WHITE,1.7f*d);c.drawArc(w-113*d,27*d,w-89*d,49*d,225,90,false,p);c.drawArc(w-108*d,32*d,w-94*d,46*d,225,90,false,p);c.drawArc(w-103*d,37*d,w-99*d,43*d,225,90,false,p);fill(c,Color.WHITE);c.drawCircle(w-101*d,49*d,1.5f*d,p);
 // iOS-inspired battery (thin rounded shell, short terminal, level fill + centered percentage)
 Intent b=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
 int lev=b==null?100:b.getIntExtra(BatteryManager.EXTRA_LEVEL,100);
 int sc=b==null?100:b.getIntExtra(BatteryManager.EXTRA_SCALE,100);
 int pct=sc>0?Math.max(0,Math.min(100,lev*100/sc)):100;
 float bx=w-91*d,by=34*d,bw=32*d,bh=12*d;
 // outer shell: transparent glass interior and a fine white contour
 fill(c,0x22ffffff);c.drawRoundRect(bx,by,bx+bw,by+bh,3.4f*d,3.4f*d,p);
 stroke(Color.WHITE,1.05f*d);c.drawRoundRect(bx,by,bx+bw,by+bh,3.4f*d,3.4f*d,p);
 // terminal is a tiny centered rounded nub
 fill(c,0xfff5f7fb);c.drawRoundRect(bx+bw,by+3.6f*d,bx+bw+1.7f*d,by+8.4f*d,.65f*d,.65f*d,p);
 // iOS battery level fill (green when charging, otherwise white)
 boolean charging=b!=null&&(b.getIntExtra(BatteryManager.EXTRA_STATUS,-1)==BatteryManager.BATTERY_STATUS_CHARGING||b.getIntExtra(BatteryManager.EXTRA_STATUS,-1)==BatteryManager.BATTERY_STATUS_FULL);
 int levelColor=charging?0xff65d66f:Color.WHITE;
 float ix=bx+1.5f*d,iy=by+1.5f*d,iw=bw-3*d,ih=bh-3*d;
 fill(c,levelColor);if(pct>0)c.drawRoundRect(ix,iy,ix+iw*pct/100f,iy+ih,2*d,2*d,p);
 // small bold percent centered in the capsule; dark ink on filled region for legibility
 p.setShader(null);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(7.6f*d);
 String batteryLabel=pct+"%";float labelW=p.measureText(batteryLabel);
 fill(c,pct>=45?0xff26384a:Color.WHITE);
 c.drawText(batteryLabel,bx+(bw-labelW)/2,by+8.2f*d,p);
 }}
}