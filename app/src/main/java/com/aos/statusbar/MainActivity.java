package com.aos.statusbar;
import android.app.*;import android.os.*;import android.content.*;import android.net.Uri;import android.provider.Settings;import android.graphics.Color;import android.graphics.Typeface;import android.graphics.drawable.GradientDrawable;import android.view.*;import android.widget.*;
public class MainActivity extends Activity{
 int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 int bg=Color.rgb(12,16,23),panel=Color.rgb(24,32,43),muted=Color.rgb(145,164,190),white=Color.rgb(242,246,252),blue=Color.rgb(66,119,184);
 TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
 GradientDrawable shape(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
 LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 void gap(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 TextView heading(String s){TextView t=label(s,12,muted);t.setTypeface(null,Typeface.BOLD);t.setLetterSpacing(.12f);return t;}
 public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);getWindow().getDecorView().setSystemUiVisibility(0);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(bg);
 LinearLayout root=column();root.setPadding(dp(25),dp(28),dp(25),dp(24));scroll.addView(root);
 TextView brand=label("AOS",42,white);brand.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));root.addView(brand);
 TextView sub=label("LIQUID GLASS  /  STATUS BAR",12,muted);sub.setLetterSpacing(.13f);LinearLayout.LayoutParams sl=new LinearLayout.LayoutParams(-1,-2);sl.topMargin=dp(3);sl.bottomMargin=dp(26);root.addView(sub,sl);
 LinearLayout preview=column();preview.setPadding(dp(18),dp(17),dp(18),dp(17));preview.setBackground(shape(Color.rgb(27,39,54),18));
 TextView tag=label("LIVE DESIGN",10,Color.rgb(130,174,224));tag.setTypeface(null,Typeface.BOLD);tag.setLetterSpacing(.12f);preview.addView(tag);
 TextView ptitle=label("iOS-inspired glass",21,white);ptitle.setTypeface(null,Typeface.BOLD);LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(-1,-2);tl.topMargin=dp(7);preview.addView(ptitle,tl);
 TextView pdesc=label("A translucent overlay with custom clock, network and battery glyphs.",13,muted);pdesc.setLineSpacing(dp(3),1);LinearLayout.LayoutParams pl=new LinearLayout.LayoutParams(-1,-2);pl.topMargin=dp(5);preview.addView(pdesc,pl);root.addView(preview);
 gap(root,25);root.addView(heading("GET STARTED"));
 TextView desc=label("Grant the required access, then launch your status bar.",14,muted);LinearLayout.LayoutParams dl=new LinearLayout.LayoutParams(-1,-2);dl.topMargin=dp(8);dl.bottomMargin=dp(12);root.addView(desc,dl);
 Button a=btn("01    Display over other apps",false);a.setOnClickListener(v->{if(!Settings.canDrawOverlays(this))startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));else Toast.makeText(this,"Overlay access is enabled",Toast.LENGTH_SHORT).show();});addButton(root,a);
 Button n=btn("02    Notification access",false);n.setOnClickListener(v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));addButton(root,n);
 Button s=btn("03    Start AOS overlay",true);s.setOnClickListener(v->{if(Settings.canDrawOverlays(this)){if(Build.VERSION.SDK_INT>=26)startForegroundService(new Intent(this,OverlayService.class));else startService(new Intent(this,OverlayService.class));Toast.makeText(this,"AOS overlay started",Toast.LENGTH_SHORT).show();}else Toast.makeText(this,"Enable Display over other apps first",Toast.LENGTH_LONG).show();});addButton(root,s);
 gap(root,22);TextView foot=label("AOS   •   BUILT FOR ANDROID",10,Color.rgb(91,108,128));foot.setLetterSpacing(.08f);root.addView(foot);setContentView(scroll);}
 Button btn(String text,boolean primary){Button b=new Button(this);b.setText(text);b.setTextSize(14);b.setAllCaps(false);b.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);b.setPadding(dp(18),0,dp(12),0);b.setTextColor(primary?Color.WHITE:white);b.setBackground(shape(primary?blue:panel,13));return b;}
 void addButton(LinearLayout l,Button b){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(60));p.topMargin=dp(9);l.addView(b,p);}
}