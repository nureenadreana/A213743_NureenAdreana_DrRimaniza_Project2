package com.example.a213743_nureenadreana_drrimaniza_lab1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel.AppViewModel
import com.example.a213743_nureenadreana_drrimaniza_lab1.R
import com.example.a213743_nureenadreana_drrimaniza_lab1.Screen


@Composable
fun BookingScreen(navController: NavController, viewModel: AppViewModel, itemName: String) {
    val allItems by viewModel.foodItems.collectAsState()
    val item = allItems.find { it.title == itemName }
    val bookings by viewModel.bookings.collectAsState()
    
    // Check if this item is already in user's bookings
    val isBooked = bookings.any { it.itemName == itemName }
    
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF))
            .verticalScroll(rememberScrollState())
    ) {
        // Image Header
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            if (item != null) {
                Image(
                    painter = if (item.imageUri != null) {
                        rememberAsyncImagePainter(item.imageUri)
                    } else {
                        painterResource(id = if (item.isFree) R.drawable.giveaway else R.drawable.food)
                    },
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Back Button
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back",
                    tint = Color(0xFF7E60AD),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            // Title and Price
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = itemName, 
                            style = MaterialTheme.typography.headlineMedium, 
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF533E7A)
                        )
                        if (isBooked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "BOOKED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color(0xFF7E60AD))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item?.distance ?: "", 
                            style = MaterialTheme.typography.bodyLarge, 
                            color = Color(0xFF7E60AD)
                        )
                    }
                }
                if (item?.isFree == false) {
                    Text(
                        text = "RM ${item.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFB37421),
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF7E60AD),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // About Item
            Text(
                text = "About this item", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF533E7A)
            )
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFF3EDFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp), tint = Color(0xFF7E60AD))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Shared by ${item?.userName ?: "N/A"}", 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item?.description ?: "No description available.", 
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notice Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isBooked) Color(0xFFE8F5E9) else Color(0xFFF3EDFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isBooked) Icons.Default.CheckCircle else Icons.Default.Info, 
                        null, 
                        tint = if (isBooked) Color(0xFF2E7D32) else Color(0xFF7E60AD)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBooked) 
                            "You have already reserved this item. Please pick it up soon!" 
                            else "Reserve this item and pick it up within 2 hours. Be quick!",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isBooked) Color(0xFF1B5E20) else Color(0xFF533E7A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Check Nutrition Info Button
            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.Nutrition.createRoute(itemName))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🔍 Check Nutrition Info")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Button
            Button(
                onClick = {
                    if (!isBooked && item != null) {
                        viewModel.confirmBooking(
                            itemName = itemName,
                            location = item.distance,
                            price = item.price,
                            isFree = item.isFree
                        )
                        navController.navigate("my_bookings")
                    } else if (isBooked) {
                        navController.navigate("my_bookings")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBooked) Color(0xFF4CAF50) else Color(0xFF7E60AD)
                )
            ) {
                Text(
                    text = if (isBooked) "View in My Bookings" else "Confirm Booking", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp, 
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
