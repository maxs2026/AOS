package com.aos.mobile
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
private val bg=Color(0xFF080B12); private val panel=Color(0xFF171D2A); private val accent=Color(0xFF8AB4FF)
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{AOSApp()}}}
@Composable fun AOSApp(){
 var wifi by remember{mutableStateOf(true)};var bt by remember{mutableStateOf(false)};var airplane by remember{mutableStateOf(false)};var focus by remember{mutableStateOf(false)}
 var brightness by remember{mutableFloatStateOf(.7f)};var volume by remember{mutableFloatStateOf(.55f)}
 MaterialTheme(colorScheme=darkColorScheme(primary=accent,background=bg,surface=panel)){
 Column(Modifier.fillMaxSize().background(bg).padding(20.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Text("AOS",fontSize=34.sp,fontWeight=FontWeight.Bold,color=Color.White);Spacer(Modifier.width(10.dp));Text("Liquid Glass",color=Color.LightGray);Spacer(Modifier.weight(1f));Icon(Icons.Default.Settings,null,tint=Color.White)}
 Text("Control Center",fontSize=24.sp,fontWeight=FontWeight.SemiBold,color=Color.White,modifier=Modifier.padding(top=22.dp,bottom=14.dp))
 LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){
 items(listOf("Wi-Fi","Bluetooth","Airplane","Focus")){name->val on=when(name){"Wi-Fi"->wifi;"Bluetooth"->bt;"Airplane"->airplane;else->focus};val icon=when(name){"Wi-Fi"->Icons.Default.Wifi;"Bluetooth"->Icons.Default.Bluetooth;"Airplane"->Icons.Default.Flight;else->Icons.Default.DoNotDisturbOn}
 Card(colors=CardDefaults.cardColors(containerColor=if(on)Color(0xFF244D91)else panel),shape=RoundedCornerShape(24.dp),modifier=Modifier.height(118.dp).clickable{when(name){"Wi-Fi"->wifi=!wifi;"Bluetooth"->bt=!bt;"Airplane"->airplane=!airplane;else->focus=!focus}}){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.SpaceBetween){Icon(icon,null,tint=Color.White);Text(name,color=Color.White,fontWeight=FontWeight.Medium);Text(if(on)"On" else "Off",color=Color.LightGray,fontSize=12.sp)}}
 }}
 GlassSlider("Brightness",Icons.Default.Brightness6,brightness){brightness=it};GlassSlider("Volume",Icons.Default.VolumeUp,volume){volume=it}
 Text("AOS • iOS-inspired interface preview",color=Color.Gray,fontSize=12.sp,modifier=Modifier.align(Alignment.CenterHorizontally).padding(top=12.dp))
 }}
}
@Composable fun GlassSlider(label:String,icon:androidx.compose.ui.graphics.vector.ImageVector,value:Float,onChange:(Float)->Unit){
 Card(colors=CardDefaults.cardColors(containerColor=panel),shape=RoundedCornerShape(22.dp),modifier=Modifier.fillMaxWidth().padding(top=12.dp)){Row(Modifier.padding(horizontal=16.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=Color.White);Spacer(Modifier.width(12.dp));Text(label,color=Color.White);Spacer(Modifier.weight(1f));Text("${(value*100).toInt()}%",color=Color.LightGray)};Slider(value=value,onValueChange=onChange,modifier=Modifier.padding(horizontal=12.dp))}
}