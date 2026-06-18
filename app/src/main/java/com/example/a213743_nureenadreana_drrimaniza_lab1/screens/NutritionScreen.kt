package com.example.a213743_nureenadreana_drrimaniza_lab1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a213743_nureenadreana_drrimaniza_lab1.api.WgerInstance
import com.example.a213743_nureenadreana_drrimaniza_lab1.api.WgerItem
import kotlinx.coroutines.launch

@Composable
fun NutritionScreen(navController: NavController, itemName: String = "") {
    var query by remember { mutableStateOf(itemName) } // pre-filled!
    var results by remember { mutableStateOf<List<WgerItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Auto-search when screen opens if itemName is provided
    LaunchedEffect(itemName) {
        if (itemName.isNotBlank()) {
            isLoading = true
            errorMessage = ""
            try {
                val response = WgerInstance.api.searchFood(name = itemName)
                results = response.results
                if (results.isEmpty()) {
                    errorMessage = "No nutrition data found for \"$itemName\"."
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load. Check internet connection."
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Column {
                Text(
                    "Nutrition Info",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Powered by Wger API",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search bar (still there so user can manually search too)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Food name") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (query.isNotBlank()) {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        results = emptyList()
                        try {
                            val response = WgerInstance.api.searchFood(name = query)
                            results = response.results
                            if (results.isEmpty()) {
                                errorMessage = "No results found for \"$query\"."
                            }
                        } catch (e: Exception) {
                            errorMessage = "Failed to load. Check internet connection."
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Search", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // States
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Looking up \"$query\"...",
                            color = Color.Gray
                        )
                    }
                }
            }

            errorMessage.isNotBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            results.isNotEmpty() -> {
                Text(
                    "${results.size} result(s) for \"$query\"",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(results) { item ->
                        NutritionCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionCard(item: WgerItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Food name
            Text(
                text = item.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Per 100g",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionStat(
                    label = "Energy",
                    value = item.energy?.let { "%.0f".format(it) },
                    unit = "kcal"
                )
                NutritionStat(
                    label = "Protein",
                    value = item.protein?.let { "%.1f".format(it.toDoubleOrNull() ?: 0.0) },
                    unit = "g"
                )
                NutritionStat(
                    label = "Carbs",
                    value = item.carbohydrates?.let { "%.1f".format(it.toDoubleOrNull() ?: 0.0) },
                    unit = "g"
                )
                NutritionStat(
                    label = "Fat",
                    value = item.fat?.let { "%.1f".format(it.toDoubleOrNull() ?: 0.0) },
                    unit = "g"
                )
            }

            // Fiber & sugar (if available)
            if (!item.fiber.isNullOrBlank() || !item.sugar.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item.fiber?.let {
                        Text(
                            "Fiber: ${it}g",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    item.sugar?.let {
                        Text(
                            "Sugar: ${it}g",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionStat(label: String, value: String?, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            unit,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
