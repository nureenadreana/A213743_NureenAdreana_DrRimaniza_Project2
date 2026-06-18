package com.example.a213743_nureenadreana_drrimaniza_lab1.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel.AppViewModel
import com.example.a213743_nureenadreana_drrimaniza_lab1.location.LocationHelper

@Composable
fun AddItemForm(navController: NavHostController, viewModel: AppViewModel) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    
    val itemName by viewModel.newItemName.collectAsState()
    val itemLocation by viewModel.newItemLocation.collectAsState()
    val itemDescription by viewModel.newItemDescription.collectAsState()
    val isFree by viewModel.newItemIsFree.collectAsState()
    val itemPrice by viewModel.newItemPrice.collectAsState()
    val itemImageUri by viewModel.newItemImageUri.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationHelper.getCurrentLocation { address ->
                viewModel.updateNewItemLocation(address)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateNewItemImageUri(uri?.toString())
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF))
            .verticalScroll(rememberScrollState())
    ) {
        // Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                viewModel.clearForm()
                navController.popBackStack() 
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF7E60AD)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (!isEditing) "List New Item" else "Edit Item",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF533E7A)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Image Picker Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (itemImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(itemImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF7E60AD)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add Food Photo", color = Color.Gray)
                        }
                    }
                }
            }

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Food Details", fontWeight = FontWeight.Bold, color = Color(0xFF533E7A))
                    
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { viewModel.updateNewItemName(it) },
                        label = { Text("Food Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    OutlinedTextField(
                        value = itemDescription,
                        onValueChange = { viewModel.updateNewItemDescription(it) },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Column {
                        OutlinedTextField(
                            value = itemLocation,
                            onValueChange = { viewModel.updateNewItemLocation(it) },
                            label = { Text("Pickup Location") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3EDFF),
                                contentColor = Color(0xFF7E60AD)
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Use My Location", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sharing Type", fontWeight = FontWeight.Bold, color = Color(0xFF533E7A))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isFree,
                            onClick = { viewModel.updateNewItemIsFree(true) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF7E60AD))
                        )
                        Text("Free", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(
                            selected = !isFree,
                            onClick = { viewModel.updateNewItemIsFree(false) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF7E60AD))
                        )
                        Text("Reduced", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (!isFree) {
                        OutlinedTextField(
                            value = itemPrice,
                            onValueChange = { viewModel.updateNewItemPrice(it) },
                            label = { Text("Price (RM)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Post Button
            Button(
                onClick = {
                    viewModel.saveItem()

                    navController.popBackStack()
                },
                enabled = itemName.isNotBlank() && itemLocation.isNotBlank() && (isFree || itemPrice.isNotBlank()),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7E60AD),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (!isEditing) "Post Item" else "Save Changes",
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
