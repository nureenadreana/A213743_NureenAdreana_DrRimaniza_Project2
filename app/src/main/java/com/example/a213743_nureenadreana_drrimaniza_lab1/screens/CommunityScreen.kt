package com.example.a213743_nureenadreana_drrimaniza_lab1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommunityScreen(navController: NavController) {
    var listings by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch from Firebase when screen opens
    LaunchedEffect(Unit) {
        FirebaseRepository.getAllListings { result ->
            listings = result
            isLoading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text(
            "Community Feed",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Live food listings from the community",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading community listings...", color = Color.Gray)
                    }
                }
            }

            listings.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No listings yet. Be the first to share!", color = Color.Gray)
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listings) { listing ->
                        CommunityListingCard(listing)
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityListingCard(listing: Map<String, Any>) {
    val isFree = listing["isFree"] as? Boolean ?: true
    val imageUri = listing["imageUri"] as? String
    val timestamp = listing["timestamp"] as? Long ?: 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Section
            if (!imageUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = listing["title"] as? String ?: "Untitled",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF533E7A),
                        modifier = Modifier.weight(1f) // Ensures title doesn't squash the price
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isFree) Color(0xFFE8F5E9) else Color(0xFFF3EDFF)
                    ) {
                        Text(
                            text = if (isFree) "FREE" else "RM ${listing["price"]}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFree) Color(0xFF2E7D32) else Color(0xFF7E60AD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location Row
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = listing["location"] as? String ?: "Unknown location",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth() // Allow address to wrap normally
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // User Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = listing["userName"] as? String ?: "Anonymous",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Timestamp
                if (timestamp != 0L) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(Date(timestamp))
                    Text(
                        text = "Posted on: $date",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
