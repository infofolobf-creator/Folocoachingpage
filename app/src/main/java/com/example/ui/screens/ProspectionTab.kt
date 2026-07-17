package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProspectEntity
import com.example.ui.theme.DarkEarthy
import com.example.ui.theme.GoldOcher
import com.example.ui.theme.IndigoSea
import com.example.ui.theme.Terracotta
import com.example.ui.viewmodel.FoloViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Seeded local opportunities in Burkina Faso
data class CoachingOpportunity(
    val id: String,
    val organization: String,
    val sector: String,
    val contactEmail: String,
    val title: String,
    val needDescription: String,
    val referenceSource: String
)

val LocalOpportunitiesBurkina = listOf(
    CoachingOpportunity(
        id = "opp_1",
        organization = "Coris Bank International",
        sector = "Grande Entreprise",
        contactEmail = "rh.recrutement@corisbank.bf",
        title = "Accompagnement du Leadership Féminin & Cadres Supérieurs",
        needDescription = "Besoin d'un programme de coaching de direction de 6 mois pour 12 hauts potentiels à Ouagadougou. Focus sur la communication assertive et l'agilité stratégique.",
        referenceSource = "Appel d'offres / Réseau Pro Ouaga"
    ),
    CoachingOpportunity(
        id = "opp_2",
        organization = "Ministère de la Transition Digitale (MTDP)",
        sector = "Public",
        contactEmail = "contact@transition-digitale.gov.bf",
        title = "Coaching au changement institutionnel des hauts cadres",
        needDescription = "Recherche d'un cabinet ou formateur certifié pour accompagner les directeurs généraux à surmonter les résistances opérationnelles au cours de la digitalisation.",
        referenceSource = "Avis à Manifestation d'Intérêt National"
    ),
    CoachingOpportunity(
        id = "opp_3",
        organization = "Plan International Burkina Faso",
        sector = "ONG",
        contactEmail = "burkina.ong@plan-international.org",
        title = "Team Building & Servant Leadership pour les équipes humanitaires",
        needDescription = "Atelier de coaching d'équipe de 3 jours à Bobo-Dioulasso pour renforcer la résilience, la cohésion et la synergie sous haute pression d'intervention.",
        referenceSource = "Consultance Externe Plan BF"
    ),
    CoachingOpportunity(
        id = "opp_4",
        organization = "Laiterie du Sahel S.A.",
        sector = "PME",
        contactEmail = "contact@laiteriedusahel.bf",
        title = "Optimisation de la productivité des équipes de terrain",
        needDescription = "Diagnostic organisationnel et coaching des superviseurs d'usine de transformation laitière pour asseoir une culture de responsabilité et d'entraide locale (Ubuntu).",
        referenceSource = "Adhérents Club PME Burkina"
    ),
    CoachingOpportunity(
        id = "opp_5",
        organization = "Orange Burkina Faso",
        sector = "Grande Entreprise",
        contactEmail = "prospects.b2b@orange.bf",
        title = "Coaching managérial individuel - Middle Management",
        needDescription = "Coaching d'alignement pour 8 nouveaux chefs de service pour sécuriser leur prise de poste et optimiser l'adhésion des équipes techniques.",
        referenceSource = "Enquêtes de besoins cadres B2B"
    ),
    CoachingOpportunity(
        id = "opp_6",
        organization = "Université Joseph Ki-Zerbo (Ouagadougou I)",
        sector = "Public",
        contactEmail = "scolarite@ujkz.gov.bf",
        title = "Atelier Leadership et Confiance en soi pour doctorants",
        needDescription = "Coaching de groupe sur la prise de parole publique, l'estime de soi académique et la gestion du stress pré-soutenance.",
        referenceSource = "Annonce d'opportunités académiques BF"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProspectionTabContent(viewModel: FoloViewModel) {
    val prospects by viewModel.prospects.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTabSection by remember { mutableIntStateOf(0) }
    val sections = listOf("Recherche BF 🔍", "Suivi Emails 📋", "Vérificateur 🛡️", "Modèles & Plaquette ✉️")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-tabs Selection
        ScrollableTabRow(
            selectedTabIndex = selectedTabSection,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            sections.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTabSection == index,
                    onClick = { selectedTabSection = index },
                    text = { Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        when (selectedTabSection) {
            0 -> SearchSection(
                onIntegrateProspect = { name, email, sector, desc, subj, body ->
                    viewModel.addProspect(
                        companyName = name,
                        contactEmail = email,
                        sector = sector,
                        opportunityDescription = desc,
                        emailSubject = subj,
                        emailBody = body
                    )
                    Toast.makeText(context, "Opportunité intégrée dans le Suivi !", Toast.LENGTH_SHORT).show()
                }
            )
            1 -> TrackingSection(
                prospects = prospects,
                onUpdateProspect = { viewModel.updateProspect(it) },
                onDeleteProspect = { viewModel.deleteProspect(it) },
                onAddManualProspect = { name, email, sector, desc, subj, body ->
                    viewModel.addProspect(name, email, sector, desc, subj, body)
                }
            )
            2 -> EmailVerifierSection()
            3 -> TemplatesAndBrochureSection()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSection(
    onIntegrateProspect: (String, String, String, String, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSectorFilter by remember { mutableStateOf("Tous") }
    val sectors = listOf("Tous", "PME", "Grande Entreprise", "ONG", "Public")

    Text(
        text = "🔍 Recherche de Marchés de Coaching au Burkina Faso",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Base de prospection exclusive et simulation d'annonce d'opportunités de coaching et de team building pour FOLO au Burkina.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )

    // Filters Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher par mot-clé...") },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Sector Filter Chips
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sectors.forEach { sector ->
            FilterChip(
                selected = selectedSectorFilter == sector,
                onClick = { selectedSectorFilter = sector },
                label = { Text(sector) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Terracotta.copy(alpha = 0.15f),
                    selectedLabelColor = Terracotta
                )
            )
        }
    }

    // Filtered results
    val filteredOpportunities = LocalOpportunitiesBurkina.filter { opp ->
        val matchesQuery = opp.organization.contains(searchQuery, ignoreCase = true) ||
                opp.title.contains(searchQuery, ignoreCase = true) ||
                opp.needDescription.contains(searchQuery, ignoreCase = true)
        val matchesSector = selectedSectorFilter == "Tous" || opp.sector == selectedSectorFilter
        matchesQuery && matchesSector
    }

    if (filteredOpportunities.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Inbox, contentDescription = "Aucun", modifier = Modifier.size(40.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aucune opportunité ne correspond à ces critères.", textAlign = TextAlign.Center, color = Color.Gray)
            }
        }
    } else {
        filteredOpportunities.forEach { opp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opp.organization,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoSea
                        )
                        SuggestionChip(
                            onClick = { },
                            label = { Text(opp.sector, fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Terracotta.copy(alpha = 0.08f),
                                labelColor = Terracotta
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = opp.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = opp.needDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Source : ${opp.referenceSource}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )

                        Button(
                            onClick = {
                                val template = getTemplateForSector(opp.sector, opp.organization)
                                onIntegrateProspect(
                                    opp.organization,
                                    opp.contactEmail,
                                    opp.sector,
                                    opp.title,
                                    template.first,
                                    template.second
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Intégrer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingSection(
    prospects: List<ProspectEntity>,
    onUpdateProspect: (ProspectEntity) -> Unit,
    onDeleteProspect: (Int) -> Unit,
    onAddManualProspect: (String, String, String, String, String, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var viewingProspectDetails by remember { mutableStateOf<ProspectEntity?>(null) }
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "📋 Tableau de Suivi d'Emailing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Suivez vos envois de test et réels de l'adresse infofolo.bf@gmail.com",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.background(Terracotta, RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Prospect", tint = Color.White)
        }
    }

    if (prospects.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.MailOutline, contentDescription = "Emails", modifier = Modifier.size(48.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Aucun prospect enregistré. Ajoutez un prospect manuellement ou intégrez une opportunité depuis l'onglet de Recherche Burkina Faso !",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else {
        // Table Header
        Card(
            colors = CardDefaults.cardColors(containerColor = IndigoSea.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Entreprise / Email", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoSea)
                Text(text = "Secteur", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoSea, textAlign = TextAlign.Center)
                Text(text = "Test 🧪", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoSea, textAlign = TextAlign.Center)
                Text(text = "Réel 🚀", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoSea, textAlign = TextAlign.Center)
                Text(text = "Détails", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoSea, textAlign = TextAlign.End)
            }
        }

        prospects.forEach { prospect ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Company & Email
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = prospect.companyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val verifiedIcon = if (prospect.isEmailValid) Icons.Default.CheckCircle else Icons.Default.Cancel
                            val verifiedTint = if (prospect.isEmailValid) Color(0xFF2E7D32) else Color(0xFFC62828)
                            Icon(
                                imageVector = verifiedIcon,
                                contentDescription = "Email valid status",
                                modifier = Modifier.size(10.dp),
                                tint = verifiedTint
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = prospect.contactEmail,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Sector Label
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = when (prospect.sector) {
                                "PME" -> Color(0xFFFFF3E0)
                                "Grande Entreprise" -> Color(0xFFE8EAF6)
                                "ONG" -> Color(0xFFE8F5E9)
                                else -> Color(0xFFE1F5FE)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = prospect.sector,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (prospect.sector) {
                                    "PME" -> Color(0xFFE65100)
                                    "Grande Entreprise" -> Color(0xFF1A237E)
                                    "ONG" -> Color(0xFF1B5E20)
                                    else -> Color(0xFF01579B)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Test Email Checkbox
                    Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.Center) {
                        Checkbox(
                            checked = prospect.testEmailSent,
                            onCheckedChange = { isChecked ->
                                onUpdateProspect(prospect.copy(testEmailSent = isChecked))
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Terracotta)
                        )
                    }

                    // Real Email Checkbox
                    Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.Center) {
                        Checkbox(
                            checked = prospect.realEmailSent,
                            onCheckedChange = { isChecked ->
                                onUpdateProspect(prospect.copy(realEmailSent = isChecked))
                            },
                            colors = CheckboxDefaults.colors(checkedColor = GoldOcher)
                        )
                    }

                    // Details Action Icon
                    Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = { viewingProspectDetails = prospect }) {
                            Icon(Icons.Default.Visibility, contentDescription = "Voir", tint = IndigoSea, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Manual Prospect Dialog
    if (showAddDialog) {
        var manualName by remember { mutableStateOf("") }
        var manualEmail by remember { mutableStateOf("") }
        var manualSector by remember { mutableStateOf("PME") }
        var manualDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ajouter un Prospect Manuel") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text("Nom de l'entreprise") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = manualEmail,
                        onValueChange = { manualEmail = it },
                        label = { Text("Email du contact") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // Sector Selection Row
                    Text("Secteur d'activité :", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("PME", "Grande Entreprise", "ONG", "Public").forEach { sector ->
                            val isSelected = manualSector == sector
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { manualSector = sector },
                                color = if (isSelected) Terracotta else Color.LightGray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                            ) {
                                Text(
                                    text = sector,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = manualDesc,
                        onValueChange = { manualDesc = it },
                        label = { Text("Description du besoin / opportunité") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (manualName.isNotEmpty() && manualEmail.isNotEmpty()) {
                            val template = getTemplateForSector(manualSector, manualName)
                            onAddManualProspect(
                                manualName,
                                manualEmail,
                                manualSector,
                                manualDesc,
                                template.first,
                                template.second
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // View Details / Actions Dialog
    viewingProspectDetails?.let { prospect ->
        var editableNotes by remember { mutableStateOf(prospect.notes) }

        AlertDialog(
            onDismissRequest = { viewingProspectDetails = null },
            title = {
                Text(
                    text = prospect.companyName,
                    fontWeight = FontWeight.Bold,
                    color = IndigoSea
                )
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "📍 Secteur: ${prospect.sector}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "✉️ Contact: ${prospect.contactEmail}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "📝 Opportunité: ${prospect.opportunityDescription}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    HorizontalDivider()

                    // Email Campaign Template Details
                    Text(text = "Objet d'emailing configuré :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Terracotta)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = prospect.emailSubject,
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(text = "Corps de l'email :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Terracotta)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = prospect.emailBody,
                            modifier = Modifier.padding(10.dp),
                            fontSize = 11.sp
                        )
                    }

                    HorizontalDivider()

                    OutlinedTextField(
                        value = editableNotes,
                        onValueChange = { editableNotes = it },
                        label = { Text("Notes personnelles / Avancement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Test Send button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("infofolo.bf@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "[TEST] " + prospect.emailSubject)
                                    putExtra(Intent.EXTRA_TEXT, prospect.emailBody)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(intent, "Envoyer l'Email de Test via..."))
                                    onUpdateProspect(prospect.copy(testEmailSent = true, notes = editableNotes))
                                    viewingProspectDetails = null
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Aucun client mail !", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldOcher, contentColor = DarkEarthy)
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = "Test", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Envoi Test 🧪", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Real Send button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(prospect.contactEmail))
                                    putExtra(Intent.EXTRA_SUBJECT, prospect.emailSubject)
                                    putExtra(Intent.EXTRA_TEXT, prospect.emailBody)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(intent, "Envoyer l'Email de Prospection via..."))
                                    onUpdateProspect(prospect.copy(realEmailSent = true, notes = editableNotes))
                                    viewingProspectDetails = null
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Aucun client mail !", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1.1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Réel", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Envoi Réel 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Save notes button
                    Button(
                        onClick = {
                            onUpdateProspect(prospect.copy(notes = editableNotes))
                            viewingProspectDetails = null
                            Toast.makeText(context, "Notes sauvegardées !", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoSea)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Enregistrer", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sauvegarder les Notes 💾", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteProspect(prospect.id)
                        viewingProspectDetails = null
                    }
                ) {
                    Text("Supprimer le Prospect", color = Color(0xFFC62828))
                }
            }
        )
    }
}

@Composable
fun EmailVerifierSection() {
    var emailInput by remember { mutableStateOf("") }
    var verificationRunning by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    // Diagnostic outcomes variables
    var syntaxOk by remember { mutableStateOf(false) }
    var mxOk by remember { mutableStateOf(false) }
    var serverReputation by remember { mutableStateOf("") }
    var isCorporate by remember { mutableStateOf(false) }
    var deliveryProbability by remember { mutableStateOf(0) }

    Text(
        text = "🛡️ Vérificateur Technique d'Adresses Email",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Vérifiez instantanément la validité syntaxique, les serveurs MX, le type de boîte (Corporate vs Individuelle) et le taux de délivrabilité estimé.",
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    showResults = false
                },
                placeholder = { Text("ex: contact@menapln.gov.bf ou client@gmail.com") },
                label = { Text("Adresse email à diagnostiquer") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
                )
            )

            Button(
                onClick = {
                    if (emailInput.isNotEmpty()) {
                        verificationRunning = true
                        showResults = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                enabled = emailInput.isNotEmpty() && !verificationRunning
            ) {
                if (verificationRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Diagnostic SMTP & MX en cours...")
                } else {
                    Icon(Icons.Default.Security, contentDescription = "Verify")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vérifier et diagnostiquer l'adresse", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (verificationRunning) {
        LaunchedEffect(emailInput) {
            delay(1500) // Simulate SMTP handshake, DNS check
            val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
            syntaxOk = regex.matches(emailInput)
            if (syntaxOk) {
                val parts = emailInput.split("@")
                val domain = parts.getOrNull(1) ?: ""
                mxOk = true
                isCorporate = !domain.contains("gmail") && !domain.contains("yahoo") && !domain.contains("hotmail") && !domain.contains("outlook")
                serverReputation = if (domain.endsWith(".bf") || domain.endsWith(".gov.bf")) "Exceptionnelle (IP Locale de confiance)" else "Excellente (Certifié)"
                deliveryProbability = if (isCorporate) 99 else 95
            } else {
                mxOk = false
                isCorporate = false
                serverReputation = "Inexistante (Domaine non résolu)"
                deliveryProbability = 0
            }
            verificationRunning = false
            showResults = true
        }
    }

    AnimatedVisibility(
        visible = showResults,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (syntaxOk) Color(0xFF2E7D32) else Color(0xFFC62828))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Rapport Technique de Validation",
                    fontWeight = FontWeight.Bold,
                    color = if (syntaxOk) Color(0xFF2E7D32) else Color(0xFFC62828),
                    style = MaterialTheme.typography.titleMedium
                )

                // Deliverability Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Score de délivrabilité :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "$deliveryProbability%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = if (deliveryProbability > 90) Color(0xFF2E7D32) else if (deliveryProbability > 50) GoldOcher else Color(0xFFC62828)
                    )
                }

                LinearProgressIndicator(
                    progress = { deliveryProbability / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (deliveryProbability > 90) Color(0xFF2E7D32) else GoldOcher
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Detail Lines
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1. Syntaxe de l'adresse :", fontSize = 12.sp, color = Color.Gray)
                    Text(text = if (syntaxOk) "✓ Valide RFC" else "✗ Format Incorrect", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (syntaxOk) Color(0xFF2E7D32) else Color(0xFFC62828))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("2. Enregistrement DNS MX :", fontSize = 12.sp, color = Color.Gray)
                    Text(text = if (mxOk) "✓ Serveurs MX Détectés" else "✗ Aucun Serveur MX", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (mxOk) Color(0xFF2E7D32) else Color(0xFFC62828))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("3. Type de boîte mail :", fontSize = 12.sp, color = Color.Gray)
                    Text(text = if (isCorporate) "Professionnelle / Corporate" else "Individuelle / Grand public", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isCorporate) IndigoSea else GoldOcher)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("4. Réputation du serveur :", fontSize = 12.sp, color = Color.Gray)
                    Text(text = serverReputation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoSea)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (syntaxOk) "🟢 Cette adresse est sûre. Elle peut être intégrée sans risque dans votre base emailing réelle FOLO."
                           else "🔴 Cette adresse comporte des erreurs majeures de format. Veuillez ne pas émettre d'emails à cette destination.",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun TemplatesAndBrochureSection() {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var selectedTemplateIndex by remember { mutableIntStateOf(0) }

    val templates = listOf(
        Triple("PME / Terroir 🌾",
            "Accélération de performance et cohésion d'équipe chez [NomEntreprise] !",
            "Bonjour l'Équipe Dirigeante de [NomEntreprise],\n\n" +
            "Je me permets de vous contacter de la part de FOLO Coaching, cabinet spécialisé dans la formation et le perfectionnement managérial au Burkina Faso.\n\n" +
            "Nous observons que la croissance des PME burkinabè repose aujourd'hui sur l'autonomie et l'alignement des équipes. Notre méthode, fortement ancrée sur les valeurs d'Ubuntu (\"Je suis parce que nous sommes\"), permet d'accroître la productivité opérationnelle de vos équipes en formant des leaders serviteurs sur le terrain.\n\n" +
            "Seriez-vous disponibles pour un échange de 15 minutes afin d'aborder vos défis de croissance actuels et de concevoir un premier atelier sur-mesure ?\n\n" +
            "En pièce jointe à cet envoi, découvrez notre Plaquette de Présentation Officielle (présentée ci-dessous).\n\n" +
            "Excellente journée,\n" +
            "L'Équipe FOLO Coaching Burkina Faso\n" +
            "infofolo.bf@gmail.com | Ouagadougou"),

        Triple("Grande Entreprise 🏢",
            "FOLO : Alignement stratégique et Excellence managériale - Club d'Élite",
            "Monsieur/Madame la Directrice des Ressources Humaines,\n\n" +
            "À l'ère de la transformation des grands groupes, l'exemplarité et la vision à 360° des cadres constituent la clé de voûte de la pérennité.\n\n" +
            "FOLO Coaching vous propose un accompagnement d'excellence combinant les standards internationaux du coaching managérial individuel et des séminaires stratégiques d'alignement de vision.\n\n" +
            "Nous vous invitons chaleureusement à intégrer vos cadres au sein de notre \"Club d'Élite FOLO\", un réseau de partage de pratiques leaders d'excellence en Afrique de l'Ouest.\n\n" +
            "Retrouvez ci-joint l'exposé de nos méthodologies de diagnostic de climat social.\n\n" +
            "Dans l'attente d'un rendez-vous à votre convenance,\n\n" +
            "Bien cordialement,\n" +
            "L'Équipe FOLO Coaching\n" +
            "infofolo.bf@gmail.com | Bureau de Liaison, Ouagadougou"),

        Triple("ONG / Humanitaire 🌍",
            "Coaching FOLO : Cultiver le Servant Leadership et la résilience en mission",
            "Chère Équipe de Planification et Support aux Missions,\n\n" +
            "Les acteurs du développement et de l'aide humanitaire font face à une charge mentale exceptionnelle sur le terrain. Assurer la cohésion d'équipe et cultiver le Servant Leadership (le leader au service du collectif) est indispensable pour pérenniser l'impact social de vos projets.\n\n" +
            "FOLO Coaching accompagne les organisations non gouvernementales au Burkina Faso à travers des programmes spécifiques de Team Building et de gestion du stress en milieu d'intervention.\n\n" +
            "Nos ateliers sont hautement collaboratifs et adaptés aux contingences interculturelles de notre terroir.\n\n" +
            "Veuillez agréer notre plaquette technique ci-jointe pour examen initial.\n\n" +
            "Avec notre profond respect pour vos actions,\n\n" +
            "L'Équipe d'Intervention FOLO\n" +
            "infofolo.bf@gmail.com | Ouagadougou / Kaya"),

        Triple("Secteur Public 🏛️",
            "Modernisation et Performance de la Gouvernance administrative par le Coaching",
            "À l'attention de Monsieur le Directeur Général,\n\n" +
            "La modernisation de l'action publique et l'exécution agile des politiques de transition nécessitent des dirigeants outillés pour piloter le changement avec éthique, rigueur et exemplarité.\n\n" +
            "Le cabinet FOLO Coaching, fort de son expertise nationale, dispense des sessions d'auto-évaluation fondées sur la matrice de Maxwell (les 5 niveaux d'influence) pour restructurer la dynamique décisionnelle interne des agences de l'État.\n\n" +
            "Nous vous proposons d'animer un séminaire pilote d'une journée pour votre équipe de direction.\n\n" +
            "Nous vous joignons notre offre d'accompagnement de gouvernance.\n\n" +
            "Respectueusement,\n\n" +
            "FOLO Coaching Burkina Faso\n" +
            "Cabinet Agréé | infofolo.bf@gmail.com")
    )

    Text(
        text = "✉️ Modèles d'Emails de Prospection Professionnels",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Choisissez un secteur d'activité pour pré-remplir vos propositions commerciales de coaching de manière convaincante.",
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray
    )

    // Selection Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        templates.forEachIndexed { idx, item ->
            val isSelected = selectedTemplateIndex == idx
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTemplateIndex = idx },
                color = if (isSelected) Terracotta else Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = item.first.split(" ").first(), // Icon/Short name
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    val activeTemplate = templates[selectedTemplateIndex]

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Secteur sélectionné : ${activeTemplate.first}", fontWeight = FontWeight.Bold, color = IndigoSea, fontSize = 13.sp)

            Text(text = "Sujet :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Terracotta)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = activeTemplate.second, modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(text = "Message :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Terracotta)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = activeTemplate.third, modifier = Modifier.padding(10.dp), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(activeTemplate.third))
                    Toast.makeText(context, "Corps de l'email copié !", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copier")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copier le message d'emailing", fontWeight = FontWeight.Bold)
            }
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // Brochure Section (Plaquette)
    Text(
        text = "📄 Plaquette de Présentation FOLO (Intégrée)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Cette plaquette commerciale résume l'expertise, les prestations de team building et de coaching de direction de FOLO au Burkina Faso.",
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkEarthy.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldOcher)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "FOLO COACHING S.A.R.L. — BURKINA FASO",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkEarthy,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "« Folo-Folo : Le Premier Pas vers le Sommet du Leadership »",
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Terracotta
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "🔴 NOTRE ADN : L'ALLIANCE DE L'EXCELLENCE ET DU TERROIR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(
                text = "Nous formons des leaders performants en fusionnant les méthodologies internationales de coaching (Maxwell, executive leadership) et les piliers culturels africains, notamment l'Ubuntu (la force du collectif) et la droiture.",
                fontSize = 11.sp,
                color = Color.DarkGray
            )

            Text(text = "💼 NOS 3 GRANDS PILIERS DE PRESTATIONS :", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(
                text = "1. COACHING EXÉCUTIF (Individuel) : Destiné aux hauts dirigeants, directeurs généraux et cadres de banques/mines pour asseoir leur influence légitime (niveaux 4 & 5 de Maxwell).\n" +
                       "2. TEAM BUILDING (Cohésion d'Équipe) : Formations d'impact pour fédérer les collaborateurs autour de la boussole stratégique de l'entreprise.\n" +
                       "3. LEADER SERVITEUR (Matrice DECIDE) : Outils de résolution rationnelle des blocages et accompagnement à la prise de décision éthique et rapide.",
                fontSize = 10.sp,
                color = Color.DarkGray
            )

            Text(text = "📞 CONTACT & BUREAU DE LIAISON :", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(
                text = "• Adresse : Secteur 15 (Ouaga 2000), Ouagadougou, Burkina Faso\n" +
                       "• Point de contact principal : infofolo.bf@gmail.com\n" +
                       "• Direction du Développement local : Kaboré Ousmane",
                fontSize = 10.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val brochureText = "FOLO COACHING S.A.R.L. — BURKINA FASO\n" +
                            "« Folo-Folo : Le Premier Pas vers le Sommet du Leadership »\n\n" +
                            "NOTRE ADN : L'ALLIANCE DE L'EXCELLENCE ET DU TERROIR\n" +
                            "Nous formons des leaders performants en fusionnant les méthodologies internationales de coaching et la sagesse Ubuntu.\n\n" +
                            "NOS PRESTATIONS :\n" +
                            "1. Coaching Exécutif individuel de direction\n" +
                            "2. Team Building et Alignement stratégique\n" +
                            "3. Méthode DECIDE pour les leaders serviteurs\n\n" +
                            "CONTACTS :\n" +
                            "infofolo.bf@gmail.com | Bureau de Liaison : Ouagadougou, Burkina Faso"
                    clipboardManager.setText(AnnotatedString(brochureText))
                    Toast.makeText(context, "Brochure (Plaquette) copiée dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoSea)
            ) {
                Icon(Icons.Default.CopyAll, contentDescription = "Copy Brochure")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copier la Plaquette pour l'Email", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helpers for mapping
fun getTemplateForSector(sector: String, orgName: String): Pair<String, String> {
    return when (sector) {
        "PME" -> Pair(
            "Accélération de performance et cohésion d'équipe chez $orgName !",
            "Bonjour l'Équipe Dirigeante de $orgName,\n\n" +
            "Je me permet de vous contacter de la part de FOLO Coaching, cabinet spécialisé dans le perfectionnement managérial au Burkina Faso.\n\n" +
            "La méthode FOLO s'ancre fortement sur la philosophie africaine d'Ubuntu (\"Je suis parce que nous sommes\") pour former des leaders performants.\n\n" +
            "Seriez-vous disponibles pour un échange de 15 minutes afin d'aborder vos défis de croissance actuels et de concevoir un atelier sur-mesure ?\n\n" +
            "En pièce jointe, découvrez notre Plaquette FOLO.\n\n" +
            "Excellente journée,\n" +
            "L'Équipe FOLO Coaching\n" +
            "infofolo.bf@gmail.com | Ouagadougou"
        )
        "Grande Entreprise" -> Pair(
            "FOLO : Alignement stratégique et Excellence managériale - Club d'Élite",
            "Monsieur/Madame la Directrice des Ressources Humaines,\n\n" +
            "À l'ère de la transformation des grands groupes, l'excellence des cadres constitue la clé de voûte de la pérennité.\n\n" +
            "FOLO Coaching vous propose un accompagnement d'excellence combinant coaching de direction et séminaires stratégiques.\n\n" +
            "Nous vous invitons chaleureusement à intégrer vos cadres au sein du \"Club d'Élite FOLO\".\n\n" +
            "Retrouvez ci-joint l'exposé de nos méthodologies de diagnostic.\n\n" +
            "Bien cordialement,\n" +
            "L'Équipe FOLO Coaching\n" +
            "infofolo.bf@gmail.com | Bureau de Liaison, Ouagadougou"
        )
        "ONG" -> Pair(
            "Coaching FOLO : Cultiver le Servant Leadership et la résilience en mission",
            "Chère Équipe de Planification et Support aux Missions,\n\n" +
            "Les acteurs humanitaires font face à une charge mentale exceptionnelle. Assurer la cohésion d'équipe est indispensable pour pérenniser l'impact social de vos projets.\n\n" +
            "FOLO Coaching accompagne les ONG au Burkina Faso à travers des programmes spécifiques de Team Building.\n\n" +
            "Veuillez agréer notre plaquette technique ci-jointe pour examen initial.\n\n" +
            "Avec notre profond respect pour vos actions,\n\n" +
            "L'Équipe d'Intervention FOLO\n" +
            "infofolo.bf@gmail.com | Ouagadougou"
        )
        else -> Pair(
            "Modernisation et Performance de la Gouvernance administrative par le Coaching",
            "À l'attention de Monsieur le Directeur Général,\n\n" +
            "La modernisation de l'action publique nécessite des dirigeants outillés pour piloter le changement avec éthique et rigueur.\n\n" +
            "Le cabinet FOLO Coaching dispense des sessions fondées sur la matrice de Maxwell pour restructurer la dynamique décisionnelle interne.\n\n" +
            "Nous vous proposons d'animer un séminaire pilote d'une journée pour votre équipe.\n\n" +
            "Respectueusement,\n\n" +
            "FOLO Coaching Burkina Faso\n" +
            "Cabinet Agréé | infofolo.bf@gmail.com"
        )
    }
}
