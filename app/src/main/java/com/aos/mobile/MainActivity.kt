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
import androidx.compose.ui.viewinterop.AndroidView

private val appBg = Color(0xFF080D17)
private val cardBg = Color(0xFF141D2B)
private val accentBlue = Color(0xFF8BB9FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AOSApp(this) }
    }
}

@Composable
fun AOSApp(activity: MainActivity) {
    var page by remember { mutableIntStateOf(0) }
    var enabled by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var iconSize by remember { mutableFloatStateOf(14f) }
    var transparency by remember { mutableFloatStateOf(0.35f) }

    MaterialTheme(colorScheme = darkColorScheme(primary = accentBlue, background = appBg, surface = cardBg)) {
        Column(
            Modifier.fillMaxSize().background(appBg).statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("AOS", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("LIQUID GLASS EXPERIENCE", color = Color(0xFF8795A9), fontSize = 9.sp, letterSpacing = 2.sp)
                }
                Spacer(Modifier.weight(1f))
                Surface(color = cardBg, shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.padding(12.dp))
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 20.dp)
                    .background(cardBg, RoundedCornerShape(18.dp)).padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                TabChip("Status Bar", page == 0, Modifier.weight(1f)) { page = 0 }
                TabChip("Control Center", page == 1, Modifier.weight(1f)) { page = 1 }
            }

            if (page == 0) {
                Text("Status Bar", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                Text("A clean iOS-inspired look for your screen", color = Color(0xFF8B98AA), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

                Surface(color = Color(0xFF1B2635), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("LIVE PREVIEW", color = Color(0xFF9BAAC0), fontSize = 10.sp, letterSpacing = 1.7.sp, modifier = Modifier.padding(start = 4.dp, bottom = 10.dp))
                        AndroidView(
                            factory = { context -> AOSStatusBarView(context) },
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        )
                        Text("Reference-matched glass status bar", color = Color(0xFFB8C4D5), fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 9.dp, bottom = 3.dp))
                    }
                }

                Surface(color = cardBg, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Enable overlay", color = Color.White, fontWeight = FontWeight.Medium)
                                Text("Display AOS over your current screen", color = Color(0xFF8997AA), fontSize = 11.sp)
                            }
                            Switch(checked = enabled, onCheckedChange = {
                                enabled = it
                                if (it) {
                                    if (!Settings.canDrawOverlays(activity)) {
                                        activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.packageName)))
                                    } else activity.startService(Intent(activity, AOSStatusBarService::class.java))
                                } else activity.stopService(Intent(activity, AOSStatusBarService::class.java))
                            })
                        }
                        HorizontalDivider(color = Color(0xFF263244))
                        SettingRow("Notification icons", "Show notification indicators", notifications) { notifications = it }
                    }
                }

                Text("Appearance", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 19.dp, bottom = 4.dp))
                Surface(color = cardBg, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Icon size", color = Color.White, fontSize = 13.sp)
                        Slider(iconSize, { iconSize = it }, valueRange = 10f..20f)
                        Text("Glass transparency", color = Color.White, fontSize = 13.sp)
                        Slider(transparency, { transparency = it }, valueRange = 0f..1f)
                    }
                }

                Button(
                    onClick = { activity.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) },
                    modifier = Modifier.fillMaxWidth().padding(top = 13.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Notification access")
                }
                OutlinedButton(
                    onClick = {
                        if (!Settings.canDrawOverlays(activity)) activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.packageName)))
                        else activity.startService(Intent(activity, AOSStatusBarService::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Start AOS status bar") }
            } else {
                Text("Control Center", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                Text("Quick controls, styled to match AOS", color = Color(0xFF8B98AA), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))
                listOf("Wi-Fi", "Bluetooth", "Airplane Mode", "Focus", "Brightness", "Volume").forEach { label ->
                    Surface(color = cardBg, shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)) {
                        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(label, color = Color.White, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF8B98AA))
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text("AOS  •  Made for your screen", color = Color(0xFF68778D), fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp))
        }
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (selected) accentBlue else Color.Transparent,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(label, color = if (selected) Color(0xFF101827) else Color(0xFFB5C0D0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 12.dp).wrapContentWidth(Alignment.CenterHorizontally))
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color(0xFF8997AA), fontSize = 11.sp)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
