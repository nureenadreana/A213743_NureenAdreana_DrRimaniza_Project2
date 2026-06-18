package com.example.a213743_nureenadreana_drrimaniza_lab1.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel.AppViewModel
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodItemData
import com.example.a213743_nureenadreana_drrimaniza_lab1.R
import com.example.a213743_nureenadreana_drrimaniza_lab1.Screen

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    shouldFocusSearch: Boolean = false
) {
    val user by viewModel.userProfile.collectAsState()
    val allItems by viewModel.foodItems.collectAsState()
    val bookmarkedItemIds by viewModel.bookmarkedItemIds.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var expandedLocation by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("Bangi") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(shouldFocusSearch) {
        if (shouldFocusSearch) focusRequester.requestFocus()
    }

    val locations = listOf("Bangi", "Kajang", "Semenyih", "Putrajaya", "Cyberjaya")
    val filteredItems = allItems.filter { it.title.contains(searchText, ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF)) // Light lavender background
    ) {
        // 1. HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Location",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF7E60AD)
                    )
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { expandedLocation = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF7E60AD)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedLocation,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF533E7A)
                            )
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF533E7A))
                        }
                        DropdownMenu(
                            expanded = expandedLocation,
                            onDismissRequest = { expandedLocation = false }
                        ) {
                            locations.forEach { location ->
                                DropdownMenuItem(
                                    text = { Text(text = location) },
                                    onClick = {
                                        selectedLocation = location
                                        expandedLocation = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Notification Icon matching Profile style
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { navController.navigate("notifications") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        null,
                        tint = Color(0xFF7E60AD),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Welcome Text
        item {
            Text(
                text = "Hello, ${user.name}! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF533E7A),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. SEARCH BAR
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search delicious food...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF7E60AD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF7E60AD),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. FREE FOOD SECTION
        item {
            SectionHeader("Free for You")
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val freeItems = filteredItems.filter { it.isFree }
                if (freeItems.isEmpty()) {
                   Text("No free items found", color = Color.Gray, fontSize = 14.sp)
                } else {
                    freeItems.forEach { item ->
                        FoodItemCard(
                            item = item,
                            navController = navController,
                            isBookmarked = bookmarkedItemIds.contains(item.id),
                            onBookmarkClick = { viewModel.toggleBookmark(item.id) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. REDUCED PRICE SECTION
        item {
            SectionHeader("Reduced Price")
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val reducedItems = filteredItems.filter { !it.isFree }
                if (reducedItems.isEmpty()) {
                    Text("No reduced items found", color = Color.Gray, fontSize = 14.sp)
                } else {
                    reducedItems.forEach { item ->
                        FoodItemCard(
                            item = item,
                            navController = navController,
                            isBookmarked = bookmarkedItemIds.contains(item.id),
                            onBookmarkClick = { viewModel.toggleBookmark(item.id) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF533E7A)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            null,
            tint = Color(0xFF7E60AD)
        )
    }
}

@Composable
fun FoodItemCard(
    item: FoodItemData,
    navController: NavController,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(130.dp).fillMaxWidth()) {
                Image(
                    painter = if (!item.imageUri.isNullOrEmpty()) {
                        // Coil boleh membaca String path secara terus
                        rememberAsyncImagePainter(item.imageUri)
                    } else {
                        painterResource(id = if (item.isFree) R.drawable.giveaway else R.drawable.food)
                    },
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // STATUS BADGE
                Surface(
                    modifier = Modifier.padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.isFree) Color(0xFFE8DEF8) else Color(0xFFFFDBC1)
                ) {
                    Text(
                        text = if (item.isFree) "FREE" else "RM ${item.price}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isFree) Color(0xFF7E60AD) else Color(0xFFB37421)
                    )
                }

                // BOOKMARK ICON
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFF7E60AD) else Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF533E7A),
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF7E60AD)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Listed by ${item.userName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7E60AD)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Booking.createRoute(item.title)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E60AD))
                    ) {
                        Text("Book Now", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
