package com.aos.mobile
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val bg=Color(0xFF080B12);private val panel=Color(0xFF171D2A);private val accent=Color(0xFF8AB4FF)
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{AOSApp(this)}}}
@Composable fun AOSApp(activity:MainActivity){var page by remember{mutableStateOf(0)};var enabled by remember{mutableStateOf(false)};var notif by remember{mutableStateOf(true)};var size by remember{mutableFloatStateOf(14f)};var transparency by remember{mutableFloatStateOf(0f)}
 MaterialTheme(colorScheme=darkColorScheme(primary=accent,background=bg,surface=panel)){
 Column(Modifier.fillMaxSize().background(bg).padding(20.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Text("AOS",fontSize=32.sp,fontWeight=FontWeight.Bold,color=Color.White);Spacer(Modifier.width(10.dp));Text("Liquid Glass",color=Color.LightGray);Spacer(Modifier.weight(1f));Icon(Icons.Default.Settings,null,tint=Color.White)}
 Row(Modifier.fillMaxWidth().padding(top=20.dp,bottom=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){TabChip("Status Bar",page==0){page=0};TabChip("Control Center",page==1){page=1}}
 if(page==0){Text("Status Bar",fontSize=25.sp,fontWeight=FontWeight.SemiBold,color=Color.White);Text("iOS-inspired • notch-free",color=Color.Gray,modifier=Modifier.padding(top=4.dp,bottom=16.dp))
 Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF242B38)),shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth().height(76.dp)){Row(Modifier.fillMaxSize().padding(horizontal=18.dp),verticalAlignment=Alignment.CenterVertically){Text("9:41",color=Color.White,fontWeight=FontWeight.Bold);Text(if(notif)"  ●  ▪  ✉" else "",color=Color.White,fontSize=12.sp);Spacer(Modifier.weight(1f));Text("▮▮  Wi-Fi  ▰  87%",color=Color.White,fontWeight=FontWeight.SemiBold,fontSize=size.sp)}}
 Row(Modifier.fillMaxWidth().padding(top=18.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Enable overlay",color=Color.White,fontWeight=FontWeight.Medium);Text("Show AOS over your screen",color=Color.Gray,fontSize=12.sp)};Switch(enabled,onCheckedChange={enabled=it;if(it){if(!Settings.canDrawOverlays(activity))activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+activity.packageName))) else activity.startService(Intent(activity,AOSStatusBarService::class.java))}else activity.stopService(Intent(activity,AOSStatusBarService::class.java))})}
 SettingRow("Notification icons","Allow AOS to show app notifications",notif){notif=it}
 Text("Clock & icon size",color=Color.White,modifier=Modifier.padding(top=16.dp));Slider(size,{size=it},valueRange=10f..20f)
 Text("Transparency",color=Color.White);Slider(transparency,{transparency=it},valueRange=0f..1f)
 Button(onClick={activity.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))},modifier=Modifier.fillMaxWidth().padding(top=10.dp)){Icon(Icons.Default.Notifications,null);Spacer(Modifier.width(8.dp));Text("Grant Notification Access")}
 Button(onClick={if(!Settings.canDrawOverlays(activity))activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+activity.packageName)))else activity.startService(Intent(activity,AOSStatusBarService::class.java))},modifier=Modifier.fillMaxWidth()){Text("Start AOS Status Bar")}
 }else{Text("Control Center",fontSize=25.sp,fontWeight=FontWeight.SemiBold,color=Color.White);Text("Your existing AOS panel",color=Color.Gray,modifier=Modifier.padding(top=4.dp,bottom=18.dp));listOf("Wi-Fi","Bluetooth","Airplane Mode","Focus","Brightness","Volume").forEach{Card(colors=CardDefaults.cardColors(containerColor=panel),modifier=Modifier.fillMaxWidth().padding(bottom=10.dp)){Text(it,color=Color.White,modifier=Modifier.padding(18.dp))}}}
 Spacer(Modifier.weight(1f));Text("AOS • Customize your experience",color=Color.Gray,fontSize=12.sp,modifier=Modifier.align(Alignment.CenterHorizontally))
 }}
}
@Composable fun TabChip(text:String,selected:Boolean,onClick:()->Unit){Surface(color=if(selected)accent else panel,shape=RoundedCornerShape(50),modifier=Modifier.clickable(onClick=onClick)){Text(text,color=if(selected)Color.Black else Color.White,modifier=Modifier.padding(horizontal=16.dp,vertical=10.dp),fontWeight=FontWeight.Medium)}}
@Composable fun SettingRow(title:String,sub:String,value:Boolean,onChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth().padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(title,color=Color.White);Text(sub,color=Color.Gray,fontSize=12.sp)};Switch(value,onChange)}}
