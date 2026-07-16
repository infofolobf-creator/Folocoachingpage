package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseData
import com.example.data.model.TrainingModule
import com.example.ui.theme.Terracotta
import com.example.ui.theme.IndigoSea
import com.example.ui.theme.GoldOcher
import com.example.data.local.ModuleProgressEntity
import com.example.ui.viewmodel.FoloViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(
    viewModel: FoloViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val progresses by viewModel.progresses.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val hasPremiumAccess by viewModel.hasPremiumAccess.collectAsState()

    var selectedPole by remember { mutableStateOf("Tous") }
    val poles = listOf("Tous", "Courtes (1j)", "Intensives (10j)", "Spécialisée (2j)")

    val filteredModules = if (selectedPole == "Tous") {
        CourseData.modules
    } else {
        CourseData.modules.filter { it.pole == selectedPole }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("catalog_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header and description banner with Warm Organic styling
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Styled cultural brand mark "F"
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "F",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldOcher
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "FOLO Coaching & Formation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Le leadership par l’Ubuntu • Avril 2026",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = IndigoSea,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text(
                        text = "« Un chef ne marche pas devant… il marche avec. »",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "(Proverbe Mooré de sagesse de leadership)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Search/Filter tab row
        item {
            Column {
                Text(
                    text = "Pôles de Formations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Responsive horizontal flow of chips instead of narrow tabs
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    poles.forEach { pole ->
                        FilterChip(
                            selected = selectedPole == pole,
                            onClick = { selectedPole = pole },
                            label = { Text(pole, fontSize = 12.sp) },
                            modifier = Modifier.testTag("chip_$pole"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // List of courses
        items(filteredModules, key = { it.id }) { module ->
            val progressItem = progresses.find { it.moduleId == module.id }
            val isCompleted = progressItem?.completed == true
            val isDownloaded = progressItem?.downloaded == true

            // When in hard offline mode, show alert indicator if not downloaded
            val isLockedByOffline = isOffline && !isDownloaded

            val isPremiumCourse = module.id.startsWith("i") || module.id.startsWith("s")
            val isPremiumLocked = isPremiumCourse && !hasPremiumAccess

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLockedByOffline) Color.DarkGray.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDetail(module.id) }
                    .border(
                        width = 1.5.dp,
                        color = if (isCompleted) GoldOcher else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("course_card_${module.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = when (module.pole) {
                                    "Courtes (1j)" -> Terracotta.copy(alpha = 0.15f)
                                    "Intensives (10j)" -> IndigoSea.copy(alpha = 0.15f)
                                    else -> GoldOcher.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = module.pole,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when (module.pole) {
                                        "Courtes (1j)" -> Terracotta
                                        "Intensives (10j)" -> IndigoSea
                                        else -> GoldOcher
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isPremiumCourse) {
                                Surface(
                                    color = if (isPremiumLocked) GoldOcher.copy(alpha = 0.15f) else Color(0xFF2E7D32).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPremiumLocked) Icons.Default.Lock else Icons.Default.WorkspacePremium,
                                            contentDescription = "Premium Status",
                                            tint = if (isPremiumLocked) GoldOcher else Color(0xFF2E7D32),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (isPremiumLocked) "Premium 👑" else "Débloqué 🔓",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isPremiumLocked) GoldOcher else Color(0xFF2E7D32),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (isDownloaded) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = "Disponible hors-ligne",
                                    tint = IndigoSea,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Niveau complété",
                                    tint = GoldOcher,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Durée",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = module.duration,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (isLockedByOffline) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.OfflineBolt,
                                    contentDescription = "H-L",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Requis Téléchargement",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Étudier",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Aller au cours",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
