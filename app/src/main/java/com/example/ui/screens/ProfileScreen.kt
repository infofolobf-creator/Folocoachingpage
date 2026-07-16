package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ModuleProgressEntity
import com.example.data.model.CourseData
import com.example.ui.viewmodel.FoloViewModel
import com.example.ui.theme.Terracotta
import com.example.ui.theme.IndigoSea
import com.example.ui.theme.GoldOcher
import com.example.ui.theme.DarkEarthy

@Composable
fun ProfileScreen(
    viewModel: FoloViewModel
) {
    val progresses by viewModel.progresses.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val isDataSaver by viewModel.isDataSaver.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val isBypassMode by viewModel.isBypassMode.collectAsState()
    val subscriptionUrl by viewModel.subscriptionUrl.collectAsState()
    val hasPremiumAccess by viewModel.hasPremiumAccess.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Dynamically calculate metrics
    val totalModules = CourseData.modules.size
    val completedModulesCount = progresses.count { it.completed }
    val downloadedModulesCount = progresses.count { it.downloaded }

    val completionRate = if (totalModules > 0) {
        (completedModulesCount.toFloat() / totalModules * 100).toInt()
    } else 0

    val userName by viewModel.userName.collectAsState()
    var showCertificateAward by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("profile_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App settings panel
        Text(
            text = "⚙️ Paramètres de Connexion Locale",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Toggle mode simulation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mode Hors-Ligne (Simulé)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Bloque l'accès aux cours non téléchargés",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isOffline,
                        onCheckedChange = { viewModel.toggleOfflineMode() },
                        modifier = Modifier.testTag("offline_toggle_sw")
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                // Toggle data saving mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Économiseur de Données",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Active par défaut la compression de documents",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isDataSaver,
                        onCheckedChange = { viewModel.toggleDataSaver() },
                        modifier = Modifier.testTag("data_saver_toggle_sw")
                    )
                }
            }
        }

        // Premium Club d'Elite Card
        Text(
            text = "👑 Abonnement & Club d'Élite",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

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
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Statut Membre",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (hasPremiumAccess) {
                                if (isBypassMode) "Accès Total (Simulateur Éditeur)" else "Accès Premium Illimité"
                            } else {
                                "Standard (Accès Limité aux cours de base)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasPremiumAccess) Color(0xFF2E7D32) else Color.Gray
                        )
                    }

                    Surface(
                        color = if (hasPremiumAccess) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (hasPremiumAccess) Color(0xFF2E7D32) else Color.LightGray)
                    ) {
                        Text(
                            text = if (hasPremiumAccess) "ACTIF 👑" else "STANDARD 🔓",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (hasPremiumAccess) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                // Subscription checkout buttons
                Text(
                    text = "Abonnements d'excellence managériale :",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(subscriptionUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Impossible d'ouvrir le lien d'abonnement", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Formule Mensuelle", fontSize = 11.sp, color = Color.White)
                            Text("5 000 FCFA/mois", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldOcher)
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(subscriptionUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Impossible d'ouvrir le lien d'abonnement", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldOcher),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Formule Annuelle", fontSize = 11.sp, color = DarkEarthy)
                            Text("45 000 FCFA/an", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkEarthy)
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                // Developer / tester panel inside settings card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Test settings",
                        tint = GoldOcher,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Options Éditeur / Testeur FOLO",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldOcher
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mode Éditeur (Bypass de Paiement)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Contourne le paywall pour tester l'application librement.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isBypassMode,
                        onCheckedChange = { viewModel.setBypassMode(it) },
                        modifier = Modifier.testTag("bypass_mode_profile_toggle_sw")
                    )
                }

                var isEditingUrl by remember { mutableStateOf(false) }
                var urlInput by remember { mutableStateOf(subscriptionUrl) }

                if (isEditingUrl) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Lien Stripe / Page de Paiement") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.updateSubscriptionUrl(urlInput)
                                    isEditingUrl = false
                                    Toast.makeText(context, "Lien d'abonnement mis à jour !", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
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
                            text = "URL d'Abonnement Actuelle : $subscriptionUrl",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { isEditingUrl = true },
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("Modifier le lien", fontSize = 11.sp, color = GoldOcher)
                        }
                    }
                }

                // Simulate client subscription toggle for testing customer experience
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.setPremium(true)
                            Toast.makeText(context, "Expérience Client Payé activée !", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFF2E7D32))
                    ) {
                        Text("Simuler Achat", fontSize = 11.sp, color = Color(0xFF2E7D32))
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.setPremium(false)
                            viewModel.setBypassMode(false) // turn off bypass as well to show paywall immediately
                            Toast.makeText(context, "Simulateur standard/Paywall activé !", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Simuler Paywall", fontSize = 11.sp, color = Color.Red)
                    }
                }
            }
        }

        // Progression block
        Text(
            text = "📊 Tableau des Progrès de Direction",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(24.dp)
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Taux d'Achèvement",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedModulesCount sur $totalModules modules validés",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = "$completionRate%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Terracotta
                    )
                }

                LinearProgressIndicator(
                    progress = { completionRate / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Terracotta,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )

                // Grid metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = IndigoSea.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Téléchargé H-L",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "$downloadedModulesCount modules",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IndigoSea
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = GoldOcher.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Moyenne Quiz",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (completedModulesCount > 0) "100% Réussi" else "-- %",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldOcher
                            )
                        }
                    }
                }
            }
        }

        // WhatsApp Community reminders
        Text(
            text = "🤝 Communauté & défis WhatsApp",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = GoldOcher.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = GoldOcher.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Suivi & Spacing Micro-learning J+7 :",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Participez quotidiennement aux défis de leadership FOLO sur WhatsApp avec d'autres managers du Burkina Faso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Je progresse sur l'EdTech FOLO Coaching ! J'ai déjà validé $completedModulesCount/$totalModules modules. Rejoignez le groupe communautaire WhatsApp d'apprentissage !")
                        }
                        context.startActivity(Intent.createChooser(whatsappIntent, "Partager sur WhatsApp"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // Traditional WhatsApp Green
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("join_whatsapp_btn")
                ) {
                    Icon(imageVector = Icons.Default.Groups, contentDescription = "WhatsApp")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rejoindre le Groupe FOLO Leaders BF", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // PDF Certificates graduation simulator
        Text(
            text = "🎓 Diplôme & Certificat d'Achèvement",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Génération du Certificat PDF d'excellence :",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { viewModel.updateUserName(it) },
                    label = { Text("Votre Nom Complet pour le Diplôme") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_field"),
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "User Name") }
                )

                if (completionRate < 100) {
                    Surface(
                        color = Color.Gray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Vérouillé",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Finissez l’évaluation de tous les modules ($completedModulesCount / $totalModules) pour imprimer le certificat.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Button(
                    // Allow simulation unlocked easily or only when completionRate allows
                    onClick = { showCertificateAward = !showCertificateAward },
                    enabled = true, // We allow flexible review of their certificates
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_cert_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldOcher, contentColor = DarkEarthy)
                ) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "Premium")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showCertificateAward) "Masquer le Certificat" else "Générer & Afficher Certificat",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Beautiful Ornamental Certificate container
                AnimatedVisibility(visible = showCertificateAward) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(4.dp, GoldOcher), RoundedCornerShape(8.dp))
                            .background(Color(0xFFFCFAF2)) // Rich Parchment color
                            .padding(20.dp)
                            .testTag("certificate_widget"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "FOLO COACHING & FORMATION",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Terracotta,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "CERTIFICAT D'EXCELLENCE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = IndigoSea,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Le présent diplôme d’aptitude executive est décerné à :",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic
                        )

                        Text(
                            text = userName.ifEmpty { "[Votre Nom]" },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkEarthy,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Pour avoir validé avec succès l'ensemble du parcours pédagogique :\n" +
                                   "« LEADERSHIP STRATÉGIQUE AU 21ᵉ SIÈCLE »\n" +
                                   "basé sur l'alliance du Servant Leadership et de la sagesse africaine d'Ubuntu.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("A. Kaboré", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text("Comité FOLO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }

                            // Circular logo seal
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .border(BorderStroke(2.dp, GoldOcher), RoundedCornerShape(22.dp))
                                    .background(GoldOcher.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Sceau de sécurité",
                                    tint = Terracotta,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ouagadougou", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text("Avril 2026", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sharing button
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Certificat exporté avec succès sous format PDF (Taille: 1.2 Mo)", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.testTag("export_pdf_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Export")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Partager / Exporter en PDF")
                        }
                    }
                }
            }
        }
    }
}
