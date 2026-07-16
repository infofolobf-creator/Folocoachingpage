package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseData
import com.example.data.model.TrainingModule
import com.example.ui.viewmodel.FoloViewModel
import com.example.ui.theme.Terracotta
import com.example.ui.theme.IndigoSea
import com.example.ui.theme.GoldOcher
import com.example.ui.theme.DarkEarthy
import kotlinx.coroutines.delay

@Composable
fun CourseDetailScreen(
    moduleId: String,
    viewModel: FoloViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    val progressList by viewModel.progresses.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val isDataSaver by viewModel.isDataSaver.collectAsState()
    val downloadingState by viewModel.downloadingState.collectAsState()

    val hasPremiumAccess by viewModel.hasPremiumAccess.collectAsState()
    val isPremiumCourse = remember(moduleId) { moduleId.startsWith("i") || moduleId.startsWith("s") }
    val isPremiumLocked = isPremiumCourse && !hasPremiumAccess
    val subscriptionUrl by viewModel.subscriptionUrl.collectAsState()
    val isBypassMode by viewModel.isBypassMode.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val intent = remember(subscriptionUrl) {
        try {
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(subscriptionUrl))
        } catch (e: Exception) {
            null
        }
    }

    val module = remember(moduleId) {
        CourseData.modules.find { it.id == moduleId } ?: CourseData.modules.first()
    }

    val progressItem = progressList.find { it.moduleId == module.id }
    val isCompleted = progressItem?.completed == true
    val isDownloaded = progressItem?.downloaded == true
    val isDownloading = downloadingState.containsKey(module.id)
    val downloadPercent = downloadingState[module.id] ?: 0

    // Audio Player states
    var isAudioPlaying by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            while (audioProgress < 1f) {
                delay(400)
                audioProgress += 0.05f
            }
            isAudioPlaying = false
            audioProgress = 0f
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("course_detail_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Title card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp)
        ) {
            Column {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = module.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Pôle: ${module.pole} | Durée: ${module.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (isPremiumLocked) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = GoldOcher,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title with gold crown
                        Text(
                            text = "👑 FOLO Club d'Élite",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldOcher,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Débloquez l'excellence managériale",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Ce module fait partie des parcours exécutifs FOLO. Améliorez vos compétences de leader au Burkina Faso grâce à nos contenus intensifs de haut niveau.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Features list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("📚 ", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Accès Exécutif Complet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Tous les parcours de 10 jours et modules spécialisés.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("🎙️ ", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Coaching Bilingue Hors-Ligne", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Téléchargement illimité des leçons audio en Français et Dioula.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("🧠 ", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("IA Stratégique Illimitée", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Utilisation illimitée de la Boussole et de l'aide à la décision.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("🤝 ", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Communauté & Mentorat", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Accès direct aux réseaux d'entraide WhatsApp d'Afrique de l'Ouest.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Pricing Switcher
                        var selectedPlanIsYearly by remember { mutableStateOf(true) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { selectedPlanIsYearly = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!selectedPlanIsYearly) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (!selectedPlanIsYearly) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Mensuel\n5 000 F", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { selectedPlanIsYearly = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedPlanIsYearly) GoldOcher else Color.Transparent,
                                    contentColor = if (selectedPlanIsYearly) DarkEarthy else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Annuel (-25%)\n45 000 F", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Call to Action
                        Button(
                            onClick = {
                                if (intent != null) {
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Impossible d'ouvrir le lien : $subscriptionUrl", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    android.widget.Toast.makeText(context, "Lien d'abonnement invalide : $subscriptionUrl", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldOcher, contentColor = DarkEarthy),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("subscribe_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "S'abonner")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedPlanIsYearly) "S'abonner à l'année (45 000 FCFA)" else "S'abonner au mois (5 000 FCFA)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "Paiement sécurisé via Stripe ou Mobile Money. Résiliable à tout moment.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // TESTER ACCORDION / BOX
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Mode Test",
                                tint = GoldOcher,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Panel Éditeur FOLO (Mode Testeur)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "En tant que créateur de l'application, vous pouvez simuler l'expérience d'un client non-payant ou contourner le blocage pour tester toutes les fonctionnalités de l'application.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        var isEditingUrl by remember { mutableStateOf(false) }
                        var urlInput by remember { mutableStateOf(subscriptionUrl) }

                        if (isEditingUrl) {
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                label = { Text("Lien d'abonnement Stripe/Paywall") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateSubscriptionUrl(urlInput)
                                            isEditingUrl = false
                                            android.widget.Toast.makeText(context, "Lien d'abonnement mis à jour !", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Save, contentDescription = "Sauvegarder")
                                    }
                                }
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lien cible : $subscriptionUrl",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { isEditingUrl = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Modifier le lien", fontSize = 12.sp, color = GoldOcher)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Bypass de Paiement",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Active l'accès complet immédiat aux cours",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isBypassMode,
                                onCheckedChange = { viewModel.setBypassMode(it) },
                                modifier = Modifier.testTag("bypass_mode_toggle_sw")
                            )
                        }
                    }
                }
            } else {
                // Offline blocker warning
            if (isOffline && !isDownloaded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Wifi Off",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Mode Hors-Ligne Activé",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Ce module n'est pas encore téléchargé. Activez le Wi-Fi pour stocker ce cours hors connexion.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Localisation Proverbe banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sagesse locale (${module.quoteLanguage}) :",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "« ${module.proverbsLocal} »",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Equivalent Français : « ${module.proverbsFr} »",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Core objective Section
            Text(
                text = "Objectif de ce Module",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Adjust,
                            contentDescription = "Cible",
                            tint = Terracotta
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = module.objective,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Offline state and Simulated Wi-Fi memory Downloader Actions
            Text(
                text = "Téléchargement & Éco de Données",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isDownloaded) "Fichier disponible hors-ligne" else "Fichier non stocké",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDownloaded) Terracotta else Color.Gray
                            )
                            Text(
                                text = "Taille estimée : 2.4 Mo (Manuel + Audio Dioula)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        if (isDownloading) {
                            CircularProgressIndicator(
                                progress = { downloadPercent / 100f },
                                modifier = Modifier.size(36.dp),
                                color = Terracotta,
                                strokeWidth = 3.dp
                            )
                        } else {
                            if (isDownloaded) {
                                IconButton(
                                    onClick = { viewModel.deleteDownloadedModule(module.id) },
                                    modifier = Modifier.testTag("delete_download_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Supprimer le téléchargement",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (isDataSaver && isOffline) {
                                            // Wifi is restricted, can't download. Instruct
                                        } else {
                                            viewModel.simulateDownload(module.id)
                                        }
                                    },
                                    enabled = !(isOffline && !isDownloaded),
                                    modifier = Modifier.testTag("download_module_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = "Download"
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Télécharger (H-L)")
                                }
                            }
                        }
                    }

                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { downloadPercent / 100f },
                                modifier = Modifier.weight(1f),
                                color = Terracotta
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$downloadPercent%", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    if (isDataSaver) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = GoldOcher.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OfflineShare,
                                    contentDescription = "Data Saver",
                                    tint = Terracotta,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Économiseur actif : Flux direct bloqué sans Wifi.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkEarthy
                                )
                            }
                        }
                    }
                }
            }

            // Audio player block
            Text(
                text = "Pistes Audio d'Afrique de l'Est/Ouest (Bilingue Dioula)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cours Audio : ${module.audioTitleDioula.ifEmpty { "Lajɛ-baara min" }}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoSea
                    )
                    Text(
                        text = "Piste : Français + Dioula bilingue (Durée: ${module.audioDuration})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = audioProgress,
                        onValueChange = { audioProgress = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Terracotta,
                            activeTrackColor = Terracotta
                        ),
                        enabled = !(isOffline && !isDownloaded)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0:${String.format("%02d", (audioProgress * 40).toInt())}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = module.audioDuration,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { audioProgress = (audioProgress - 0.1f).coerceAtLeast(0f) },
                            enabled = !(isOffline && !isDownloaded)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Reculer 10s",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = { isAudioPlaying = !isAudioPlaying },
                            enabled = !(isOffline && !isDownloaded),
                            modifier = Modifier.testTag("audio_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
                                contentDescription = if (isAudioPlaying) "Pause" else "Lecture audio",
                                modifier = Modifier.size(56.dp),
                                tint = Terracotta
                            )
                        }

                        IconButton(
                            onClick = { audioProgress = (audioProgress + 0.1f).coerceAtMost(1f) },
                            enabled = !(isOffline && !isDownloaded)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Avancer 10s",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Glossaire Dioula - Useful terms
            Text(
                text = "Glossaire Bilingue & Guide Prononciation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CourseData.glossaryList.take(3).forEach { glossary ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = glossary.termFr,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Terracotta
                                )
                                Text(
                                    text = glossary.termDioula,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoSea
                                )
                            }
                            Text(
                                text = "Prononciation: [${glossary.pronunciation}]",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldOcher,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = glossary.definition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }
                }
            }

            // Course content manuals
            Text(
                text = "Supports de Cours Compressés (< 3Mo)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            module.contentSections.forEach { section ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = section.heading,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoSea
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = section.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (section.situationExample.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Cas d’Afrique de l'Ouest :",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = section.situationExample,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Mark completion action and Quiz trigger
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avez-vous complété cette étude ?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { viewModel.updateModuleProgress(module.id, it) },
                            modifier = Modifier.testTag("completion_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onNavigateToQuiz(module.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldOcher, contentColor = DarkEarthy)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Quiz"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lancer le Quiz du Module",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            } // End of isPremiumLocked check
        }
    }
}
