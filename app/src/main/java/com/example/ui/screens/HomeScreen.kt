package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseData
import com.example.ui.viewmodel.FoloViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Dark Luxury Theme Colors mapped strictly from HTML
private val DarkBg = Color(0xFF0A0A0A)
private val DarkSurface = Color(0xFF171717)
private val AccentGold = Color(0xFFD4AF37)
private val AccentGoldDim = Color(0xFFD4AF37).copy(alpha = 0.12f)
private val DarkBorder = Color.White.copy(alpha = 0.08f)
private val TextMain = Color(0xFFEDEDED)
private val TextMuted = Color(0xFF888888)

@Composable
fun HomeScreen(
    viewModel: FoloViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToTools: (Int) -> Unit
) {
    val context = LocalContext.current
    val progresses by viewModel.progresses.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val isDataSaver by viewModel.isDataSaver.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val downloadingState by viewModel.downloadingState.collectAsState()

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Find first incomplete module to showcase as a dynamic CTA
    val activeModule = CourseData.modules.find { module ->
        val prog = progresses.find { it.moduleId == module.id }
        prog == null || !prog.completed
    } ?: CourseData.modules.first()

    val activeProgress = progresses.find { it.moduleId == activeModule.id }
    val isDownloaded = activeProgress?.downloaded ?: false
    val isDownloading = downloadingState.containsKey(activeModule.id)
    val downloadProgress = downloadingState[activeModule.id] ?: 0
    val completedCount = progresses.count { it.completed }

    // Contact form state
    val names = userName.split(" ")
    val prefilledLastName = names.firstOrNull() ?: ""
    val prefilledFirstName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""

    var firstName by remember { mutableStateOf(prefilledFirstName) }
    var lastName by remember { mutableStateOf(prefilledLastName) }
    var email by remember { mutableStateOf("") }
    var profileType by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var bookingCode by remember { mutableStateOf("") }
    var matchedCoach by remember { mutableStateOf("") }

    // Dropdown state
    var showDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .testTag("home_screen_content")
        ) {
            // Floating-style Translucent Header / Nav
            NavigationHeader(
                onNavigateToContactForm = {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )

            // Hero Section
            HeroSection(
                onCtaClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                },
                onDownloadCtaClick = {
                    coroutineScope.launch {
                        // Scroll to highlight the download section which is near the bottom
                        scrollState.animateScrollTo((scrollState.maxValue * 0.85f).toInt())
                    }
                }
            )

            // Dynamic Progress Dashboard ("On conserve le travail de back end")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CurrentProgressWidget(
                    moduleTitle = activeModule.title,
                    completedCount = completedCount,
                    totalCount = CourseData.modules.size,
                    isDownloaded = isDownloaded,
                    isDownloading = isDownloading,
                    downloadProgress = downloadProgress,
                    onContinueClick = { onNavigateToDetail(activeModule.id) },
                    onDownloadClick = {
                        if (isDownloaded) {
                            viewModel.deleteDownloadedModule(activeModule.id)
                            Toast.makeText(context, "Module retiré du stockage hors-ligne", Toast.LENGTH_SHORT).show()
                        } else if (!isDownloading) {
                            viewModel.simulateDownload(activeModule.id)
                            Toast.makeText(context, "Téléchargement du module commencé...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Quick Tools Navigation Shortcuts
                QuickToolsSection(onNavigateToTools = onNavigateToTools)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Trust Logos Bar
            TrustLogoBar()

            Spacer(modifier = Modifier.height(48.dp))

            // Why Folo / Value Proposition
            PourquoiFoloSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Stats Section
            StatsSection()

            Spacer(modifier = Modifier.height(48.dp))

            // 4-Step Process Section
            MethodSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Program Block Showcases (Interactive details linking back to modules c1, c2, c3)
            ProgramShowcases(onNavigateToDetail = onNavigateToDetail)

            Spacer(modifier = Modifier.height(48.dp))

            // Testimonials
            TestimonialsSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Interactive Booking Form Section
            BookingFormSection(
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                email = email,
                onEmailChange = { email = it },
                profileType = profileType,
                onProfileTypeClick = { showDropdown = true },
                message = message,
                onMessageChange = { message = it },
                isSubmitted = isSubmitted,
                bookingCode = bookingCode,
                matchedCoach = matchedCoach,
                showDropdown = showDropdown,
                onDropdownDismiss = { showDropdown = false },
                onProfileTypeSelect = {
                    profileType = it
                    showDropdown = false
                },
                onSubmit = {
                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || profileType.isBlank()) {
                        Toast.makeText(context, "Veuillez remplir tous les champs requis", Toast.LENGTH_SHORT).show()
                    } else {
                        // Simulate scheduling & algorithm coach matching
                        val randCode = "FL-2026-" + (1000..9999).random() + "X"
                        val coaches = listOf("Dr. Amadou Diallo", "Mme Fatou Touré", "M. Sarah Kouamé", "Dr. Ousmane Diarra")
                        matchedCoach = coaches.random()
                        bookingCode = randCode
                        isSubmitted = true

                        // Save the form submission as a Prospect Lead in the private local database
                        viewModel.addProspect(
                            companyName = "$firstName $lastName",
                            contactEmail = email,
                            sector = "Landing Page Lead 🌐",
                            opportunityDescription = "Appel planifié. Type de profil : $profileType. Message: $message",
                            emailSubject = "FOLO Coaching : Votre appel découverte planifié - $firstName $lastName",
                            emailBody = "Bonjour $firstName $lastName,\n\n" +
                                    "Nous confirmons la planification de votre entretien d'évaluation stratégique FOLO.\n\n" +
                                    "DÉTAILS DE VOTRE RÉSERVATION :\n" +
                                    "• Code de confirmation : $randCode\n" +
                                    "• Coach FOLO désigné : $matchedCoach\n" +
                                    "• Sujet : $profileType\n" +
                                    "• Votre message : \"$message\"\n\n" +
                                    "Nous vous joignons également notre Plaquette d'excellence d'intervention.\n\n" +
                                    "À très bientôt pour votre premier pas,\n" +
                                    "L'Équipe FOLO Coaching\n" +
                                    "infofolo.bf@gmail.com | Ouagadougou",
                            testEmailSent = false,
                            realEmailSent = false,
                            isEmailValid = true,
                            notes = "Code réservation: $randCode. Coach: $matchedCoach. Demande reçue via le formulaire de la landing page."
                        )

                        Toast.makeText(context, "Appel planifié ! Lead enregistré dans votre espace de prospection.", Toast.LENGTH_LONG).show()
                    }
                },
                onReset = {
                    isSubmitted = false
                    firstName = prefilledFirstName
                    lastName = prefilledLastName
                    email = ""
                    profileType = ""
                    message = ""
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Interactive Download & Installation Section
            DownloadAppSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Accordion FAQ Section
            FaqSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Final Action CTA
            FinalCtaSection(
                onCtaClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )

            // Footer Section
            FooterSection(
                isOffline = isOffline,
                isDataSaver = isDataSaver,
                completedCount = completedCount,
                totalCount = CourseData.modules.size
            )
        }
    }
}

// ==================== COMPONENTS ====================

@Composable
fun NavigationHeader(onNavigateToContactForm: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Folo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextMain
            )
            Text(
                text = ".",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = AccentGold
            )
        }

        Button(
            onClick = onNavigateToContactForm,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGoldDim),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.testTag("nav_demarrer_btn")
        ) {
            Text(
                text = "DÉMARRER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun HeroSection(
    onCtaClick: () -> Unit,
    onDownloadCtaClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tag label
        Text(
            text = "COACHING DE PERFORMANCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        // Headline
        Text(
            text = "LIBÉREZ VOTRE\nPOTENTIEL",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 46.sp
        )

        // Subtext
        Text(
            text = "Folo accompagne les leaders et leurs équipes vers une performance durable grâce à un coaching fondé sur la science du comportement.",
            fontSize = 15.sp,
            color = TextMuted,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row of CTA Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary Reservation call-to-action
            Button(
                onClick = onCtaClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(54.dp)
                    .testTag("hero_reserver_btn")
            ) {
                Text(
                    text = "RÉSERVER UN APPEL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }

            // Secondary Mobile Download call-to-action
            OutlinedButton(
                onClick = onDownloadCtaClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("hero_download_app_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "INSTALLER L'APP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Live stats indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulseAlpha)
                    .background(Color(0xFF2E7D32), shape = CircleShape)
            )
            Text(
                text = "+2 400 leaders coachés à ce jour",
                fontSize = 13.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun CurrentProgressWidget(
    moduleTitle: String,
    completedCount: Int,
    totalCount: Int,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    downloadProgress: Int,
    onContinueClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentGold.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .testTag("progress_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VOTRE ESPACE APPRENTISSAGE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = moduleTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                }

                // Circular Progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0.65f
                    CircularProgressIndicator(
                        progress = { progress },
                        color = AccentGold,
                        trackColor = Color.White.copy(alpha = 0.05f),
                        strokeWidth = 4.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = TextMain
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onContinueClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("continue_module_btn")
                ) {
                    Text(
                        text = "Continuer ma formation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .testTag("download_sim_btn")
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            progress = { downloadProgress / 100f },
                            color = AccentGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isDownloaded) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                            contentDescription = "Télécharger",
                            tint = if (isDownloaded) AccentGold else TextMain,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickToolsSection(onNavigateToTools: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Outils de Leadership Bilingues",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Surface(
                color = AccentGold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = "FR / DY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTools(0) }
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .testTag("quick_tool_boussole")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🧭", fontSize = 20.sp)
                    Text(
                        text = "Boussole",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextMain
                    )
                    Text(
                        text = "Kèlèya-kun kounan",
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextMuted
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTools(1) }
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .testTag("quick_tool_decide")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⚡", fontSize = 20.sp)
                    Text(
                        text = "Mét. DECIDE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextMain
                    )
                    Text(
                        text = "Gnama-gnama dadi",
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun TrustLogoBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "ILS NOUS FONT CONFIANCE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Luxury grid of trust companies
        val companies = listOf("L'Oréal", "Airbus", "BNP Paribas", "Danone", "Schneider", "Accor", "TotalEnergies", "Capgemini")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            companies.forEach { name ->
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.2f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun PourquoiFoloSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "POURQUOI FOLO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Le coaching qui produit des résultats mesurables",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 32.sp
        )

        Text(
            text = "Contrairement au coaching conventionnel, Folo combine neuroscience, psychologie positive et intelligence artificielle pour créer des programmes sur mesure qui transforment durablement les comportements et la performance.",
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of 6 value cards layout
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ValueCard(
                    icon = Icons.Outlined.Psychology,
                    title = "Science",
                    desc = "Fondé sur la neuroscience & psychologie positive.",
                    modifier = Modifier.weight(1f)
                )
                ValueCard(
                    icon = Icons.Outlined.TrendingUp,
                    title = "Mesurable",
                    desc = "Tableaux de bord et KPIs de ROI clairs.",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ValueCard(
                    icon = Icons.Outlined.Groups,
                    title = "Collectif",
                    desc = "Conçu pour amplifier la cohésion d'équipe.",
                    modifier = Modifier.weight(1f)
                )
                ValueCard(
                    icon = Icons.Outlined.Shield,
                    title = "Sécurisé",
                    desc = "Chiffrement complet & discrétion absolue.",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ValueCard(
                    icon = Icons.Outlined.Public,
                    title = "Mondial",
                    desc = "Coachs certifiés dispos en 5 langues.",
                    modifier = Modifier.weight(1f)
                )
                ValueCard(
                    icon = Icons.Outlined.AutoAwesome,
                    title = "IA Prédictive",
                    desc = "Des algorithmes qui adaptent les sessions.",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ValueCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .height(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RÉSULTATS PROUVÉS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Chiffres Clés",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextMain
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(number = "87%", label = "Amélioration du bien-être", modifier = Modifier.weight(1f))
            StatCard(number = "3.5x", label = "ROI moyen constaté", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(number = "96%", label = "Rétention à 6 mois", modifier = Modifier.weight(1f))
            StatCard(number = "+2.4K", label = "Leaders accompagnés", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = number,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = AccentGold
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun MethodSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NOTRE MÉTHODE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Un processus en 4 étapes",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 32.sp
        )

        Text(
            text = "De l'évaluation initiale à la transformation durable, chaque étape est conçue pour maximiser votre progression.",
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Vertical timeline of steps
        StepCard(num = "01", title = "Évaluation & diagnostic", desc = "Un assessment complet de vos forces, axes de progression et objectifs via nos outils psychométriques certifiés.")
        StepCard(num = "02", title = "Plan sur mesure", desc = "Votre coach designe un parcours personnalisé avec des milestones clairs, des exercices pratiques et des checkpoints réguliers.")
        StepCard(num = "03", title = "Sessions de coaching", desc = "Sessions vidéo hebdomadaires de 45 à 60 minutes avec votre coach dédié, complétées par des micro-learning.")
        StepCard(num = "04", title = "Mesure & ancrage", desc = "Tableau de bord de progression, post-assessment et plan de maintien pour que les changements durent dans le temps.")
    }
}

@Composable
fun StepCard(num: String, title: String, desc: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = num,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = AccentGold.copy(alpha = 0.3f),
                lineHeight = 36.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ProgramShowcases(onNavigateToDetail: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "NOS PROGRAMMES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        // Program 1: Leadership (links to c1)
        ProgramShowcaseCard(
            category = "LEADERSHIP",
            title = "Devenez le leader que vos équipes méritent",
            desc = "Notre programme phare transforme votre style de leadership en développant votre intelligence émotionnelle, votre capacité à prendre des décisions complexes et votre impact.",
            bullets = listOf("12 sessions sur 3 mois", "360° feedback inclus", "Accès complet à la boussole Folo"),
            onActionClick = { onNavigateToDetail("c1") }
        )

        // Program 2: Équipes (links to c2)
        ProgramShowcaseCard(
            category = "ÉQUIPES",
            title = "Alignez votre équipe autour d'une vision",
            desc = "Le coaching d'équipe Folo renforce la cohésion, améliore la communication et libère l'intelligence collective. Idéal pour les phases de transformation.",
            bullets = listOf("Workshops + coaching individuel", "Diagnostic de dynamique d'équipe", "Tableau de bord collectif de progrès"),
            onActionClick = { onNavigateToDetail("c2") }
        )

        // Program 3: Résilience (links to c3)
        ProgramShowcaseCard(
            category = "RÉSILIENCE",
            title = "Développez votre résilience mentale",
            desc = "Un programme intensif pour renforcer votre capacité à gérer le stress, rebondir après l'échec et maintenir la performance dans les contextes exigeants.",
            bullets = listOf("8 sessions ciblées", "Outils de régulation émotionnelle", "Suivi de bien-être personnalisé"),
            onActionClick = { onNavigateToDetail("c3") }
        )
    }
}

@Composable
fun ProgramShowcaseCard(
    category: String,
    title: String,
    desc: String,
    bullets: List<String>,
    onActionClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = AccentGoldDim,
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = category,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextMain,
                lineHeight = 24.sp
            )

            Text(
                text = desc,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 19.sp
            )

            // Bullets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                bullets.forEach { text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            color = TextMain
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AccentGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = "EN SAVOIR PLUS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun TestimonialsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TÉMOIGNAGES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Ce que disent nos coachés",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 32.sp
        )

        // Custom horizontal carousel or stacked cards
        TestimonialCard(
            stars = 5,
            quote = "\"Folo a transformé ma façon de gérer les conflits au sein de mon équipe. En 3 mois, notre taux de rotation a baissé de 40%. Le coaching n'est pas un luxe, c'est un investissement.\"",
            name = "Sophie Martin",
            role = "VP Engineering, TechCorp"
        )

        TestimonialCard(
            stars = 5,
            quote = "\"Je doutais de l'efficacité du coaching à distance. Folo m'a prouvé le contraire. Mon coach a une capacité d'écoute extraordinaire et les outils font toute la différence.\"",
            name = "Thomas Durand",
            role = "Directeur Général, MedGroup"
        )

        TestimonialCard(
            stars = 5,
            quote = "\"Le programme de résilience m'a permis de traverser une période de merger & acquisition sans perdre mon équipe. Le ROI est évident : nous sommes sortis plus forts.\"",
            name = "Amira Benali",
            role = "CEO, FinanceHub"
        )
    }
}

@Composable
fun TestimonialCard(stars: Int, quote: String, name: String, role: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stars
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(stars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = quote,
                fontSize = 13.sp,
                color = TextMuted,
                fontStyle = FontStyle.Italic,
                lineHeight = 20.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circle initials
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AccentGoldDim, shape = CircleShape)
                        .border(1.dp, AccentGold.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(2).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }

                Column {
                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = role,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    profileType: String,
    onProfileTypeClick: () -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isSubmitted: Boolean,
    bookingCode: String,
    matchedCoach: String,
    showDropdown: Boolean,
    onDropdownDismiss: () -> Unit,
    onProfileTypeSelect: (String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(1.dp, AccentGold.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .testTag("contact_booking_section")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSubmitted) {
                Text(
                    text = "RÉSERVER UN APPEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Prêt à transformer votre performance ?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMain,
                    lineHeight = 26.sp
                )

                Text(
                    text = "Planifiez un appel découverte gratuit de 20 minutes avec un coach certifié Folo.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Input fields
                OutlinedTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = { Text("Prénom", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = Color.White.copy(alpha = 0.02f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("form_firstname")
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = { Text("Nom", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = Color.White.copy(alpha = 0.02f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("form_lastname")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email professionnel", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = Color.White.copy(alpha = 0.02f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("form_email")
                )

                // Custom Dropdown Box selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = profileType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vous êtes...", color = TextMuted) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = AccentGold
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain,
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = Color.White.copy(alpha = 0.02f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProfileTypeClick() }
                            .testTag("form_profile_dropdown")
                    )

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = onDropdownDismiss,
                        modifier = Modifier
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        val options = listOf("Un leader / dirigeant", "Un responsable RH", "Une équipe", "Autre")
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = TextMain) },
                                onClick = { onProfileTypeSelect(option) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    label = { Text("Message (optionnel)", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = Color.White.copy(alpha = 0.02f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("form_message")
                )

                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("form_submit_btn")
                ) {
                    Text(
                        text = "RÉSERVER MON APPEL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                // Success Booking Card state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(AccentGoldDim, shape = CircleShape)
                            .border(1.dp, AccentGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Succès",
                            tint = AccentGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "PLANIFICATION CONFIRMÉE !",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = "Merci $firstName ! Votre session d'alignement est bloquée. Notre algorithme prédictif vous a jumelé avec le coach optimal.",
                        fontSize = 14.sp,
                        color = TextMain,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    // Booking details box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Numéro d'appel :", fontSize = 11.sp, color = TextMuted)
                                Text(bookingCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coach assigné :", fontSize = 11.sp, color = TextMuted)
                                Text(matchedCoach, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Délai de contact :", fontSize = 11.sp, color = TextMuted)
                                Text("Moins de 24 Heures", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain)
                            }
                        }
                    }

                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text(
                            text = "NOUVEAU CRÉNEAU",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FaqSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "FAQ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Questions fréquentes",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 32.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FaqItem(
                question = "Combien coûte un programme Folo ?",
                answer = "Nos programmes démarrent à partir de 2 400€ pour un parcours individuel de 3 mois. Les programmes d'équipe sont sur devis personnalisé. Chaque investissement inclut les sessions, les outils, l'app et le suivi post-programme."
            )
            FaqItem(
                question = "Comment sont sélectionnés les coachs ?",
                answer = "Nos coachs passent un processus de sélection en 5 étapes : certification ICF minimale PCC, évaluation psychométrique, simulation de session, entretien culturel et période de probation de 6 mois. Seuls 8% des candidats sont retenus."
            )
            FaqItem(
                question = "Le coaching à distance est-il efficace ?",
                answer = "Oui. Les études montrent que le coaching par vidéo est aussi efficace que le présentiel, avec l'avantage de la flexibilité et d'un meilleur suivi entre les sessions grâce à notre plateforme digitale."
            )
            FaqItem(
                question = "En combien de temps voit-on des résultats ?",
                answer = "La majorité de nos coachés rapportent des changements perceptibles dès les 4-6 premières sessions. Les transformations durables s'ancrent typiquement entre le 2ème et le 3ème mois de programme."
            )
            FaqItem(
                question = "Proposez-vous du coaching bilingue ?",
                answer = "Absolument. Folo propose du coaching en français, anglais, allemand, espagnol, arabe, ainsi que des supports traduits en langues locales (comme le Dioula/Mooré) pour le coaching en Afrique de l'Ouest."
            )
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var isOpen by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isOpen = !isOpen }
            .border(1.dp, if (isOpen) AccentGold.copy(alpha = 0.3f) else DarkBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (isOpen) AccentGold else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = isOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = answer,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FinalCtaSection(onCtaClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F)),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(32.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "REJOIGNEZ LE MOUVEMENT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                letterSpacing = 1.5.sp
            )

            Text(
                text = "Votre potentiel\nn'a pas de limite",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextMain,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Text(
                text = "+2 400 leaders ont déjà transformé leur performance avec Folo. Le moment n'a jamais été aussi bon pour commencer.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCtaClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "DÉMARRER MAINTENANT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun FooterSection(
    isOffline: Boolean,
    isDataSaver: Boolean,
    completedCount: Int,
    totalCount: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Horizontal divider
        Divider(color = DarkBorder)

        // Title and description
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Folo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMain
                )
                Text(
                    text = ".",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )
            }
            Text(
                text = "Coaching de performance fondé sur la science du comportement. Pour les leaders et les équipes qui veulent performer durablement.",
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )
        }

        // Backend Sync Status Indicator (Pulsing glowing dots for Offline & Data saver integration)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(alphaAnim)
                        .background(if (isOffline) Color.Red else Color(0xFF2E7D32), shape = CircleShape)
                )
                Text(
                    text = if (isOffline) "HORS-LIGNE" else "SYNCHRONISÉ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    letterSpacing = 0.5.sp
                )
            }

            if (isDataSaver) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(AccentGold, shape = CircleShape)
                    )
                    Text(
                        text = "ÉCO. DE DONNÉES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Divider(color = DarkBorder)

        // Copyright info
        Text(
            text = "© 2026 Folo Coaching & Formation • Tous droits réservés.\nLe leadership par l’Ubuntu.",
            fontSize = 11.sp,
            color = TextMuted,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun DownloadAppSection() {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var isDownloadingApk by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "APPLICATION MOBILE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 1.5.sp
        )

        Text(
            text = "Installez l'Application Folo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextMain,
            lineHeight = 32.sp
        )

        Text(
            text = "Accédez instantanément à vos modules d'apprentissage hors-ligne, suivez vos sessions et gérez vos campagnes de prospection d'emailing en toute confidentialité.",
            fontSize = 13.sp,
            color = TextMuted,
            lineHeight = 18.sp
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(AccentGoldDim, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Folo Coaching S.A. (Android)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(
                            text = "Version 1.0.4 (Stable) • 12.8 Mo",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Divider(color = DarkBorder)

                // Feature bullet points
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "100% Privé & Confidentiel",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = "Toutes vos données de prospection et d'emailing sont stockées localement sur votre appareil dans une base Room (SQLite) cryptée. Aucun serveur externe n'y a accès.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Fonctionnement Hors-Ligne",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = "Idéal pour travailler dans les zones à faible connectivité du Burkina Faso. Les outils, cours et diagnostics restent entièrement opérationnels sans internet.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Intégration d'Emailing Directe",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = "Remplissez les formulaires de contact de la landing page ou importez des opportunités locales pour générer automatiquement des modèles d'email de prospection ciblés.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Divider(color = DarkBorder)

                if (isDownloadingApk) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Génération de l'archive APK sécurisée...",
                                fontSize = 12.sp,
                                color = TextMain,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(downloadProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = AccentGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AccentGold,
                            trackColor = DarkBorder
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Download APK button
                        Button(
                            onClick = {
                                isDownloadingApk = true
                                coroutineScope.launch {
                                    for (p in 1..100) {
                                        delay(15)
                                        downloadProgress = p / 100f
                                    }
                                    isDownloadingApk = false
                                    Toast.makeText(context, "APK Folo_Coaching_v1.0.apk téléchargé avec succès !", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GetApp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Télécharger l'APK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Share Webapp button
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("https://ais-pre-vghyioxign4wipe4kttmci-20434161245.europe-west1.run.app"))
                                Toast.makeText(context, "Lien d'installation copié dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Partager l'App",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Installation guide lines
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Guide rapide d'installation :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )

                    Text(
                        text = "1. Cliquez sur 'Télécharger l'APK' ou partagez le lien pour l'ouvrir sur votre mobile.\n" +
                               "2. Autorisez l'installation d'applications inconnues dans vos paramètres Android si demandé.\n" +
                               "3. Ouvrez le fichier téléchargé et lancez l'application FOLO.",
                        fontSize = 10.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
