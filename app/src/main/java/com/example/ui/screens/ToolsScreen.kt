package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BoussoleEntity
import com.example.data.local.DecideEntity
import com.example.data.local.LevelAssessmentEntity
import com.example.data.model.CourseData
import com.example.data.model.QuizQuestion
import com.example.ui.viewmodel.FoloViewModel
import com.example.ui.theme.Terracotta
import com.example.ui.theme.IndigoSea
import com.example.ui.theme.GoldOcher
import com.example.ui.theme.DarkEarthy
import kotlinx.coroutines.launch

@Composable
fun ToolsScreen(
    viewModel: FoloViewModel,
    quizModuleId: String?,
    initialTab: Int = 0,
    onQuizClosed: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabs = listOf("Boussole 🧭", "DECIDE 🛠️", "5 Niveaux 📊")

    // Retrieve Flows
    val boussoleState by viewModel.boussole.collectAsState()
    val decideEntries by viewModel.decideEntries.collectAsState()
    val assessments by viewModel.assessments.collectAsState()

    val scrollState = rememberScrollState()

    // If an active quiz was requested, render the Quiz UI overlay instead of tabs
    if (!quizModuleId.isNullOrEmpty()) {
        QuizSection(
            moduleId = quizModuleId,
            viewModel = viewModel,
            onClose = onQuizClosed
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .testTag("tools_screen_content"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upper Tools tabs selection
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        modifier = Modifier.testTag("tools_tab_$index")
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> BoussoleTabContent(
                        storedBoussole = boussoleState,
                        onSave = { n, s, e, o -> viewModel.saveBoussole(n, s, e, o) }
                    )
                    1 -> DecideTabContent(
                        entries = decideEntries,
                        onAdd = { p, a, b, c, i, d ->
                            viewModel.addDecideEntry(p, a, b, c, i, d, "")
                        },
                        onDelete = { id -> viewModel.deleteDecideEntry(id) }
                    )
                    2 -> LevelsTabContent(
                        assessments = assessments,
                        onToggle = { lvl, checked -> viewModel.updateAssessment(lvl, checked) }
                    )
                }
            }
        }
    }
}

@Composable
fun BoussoleTabContent(
    storedBoussole: BoussoleEntity?,
    onSave: (String, String, String, String) -> Unit
) {
    var nord by remember { mutableStateOf("") }
    var sud by remember { mutableStateOf("") }
    var est by remember { mutableStateOf("") }
    var ouest by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(storedBoussole) {
        if (storedBoussole != null) {
            nord = storedBoussole.nordVision
            sud = storedBoussole.sudValeurs
            est = storedBoussole.estRessources
            ouest = storedBoussole.ouestMenaces
        }
    }

    Text(
        text = "🧭 Boussole du Leader Stratégique",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = "Utilisez cet outil pour calibrer vos objectifs. La vision d'Ubuntu se fonde sur le partage collectif et les forces locales.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Form fields
    OutlinedTextField(
        value = nord,
        onValueChange = { nord = it },
        label = { Text("VISION (NORD) : Où voulons-nous être dans 1 an ?") },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boussole_nord_input"),
        leadingIcon = { Icon(Icons.Default.Explore, contentDescription = "North") },
        keyboardOptions = KeyboardOptions.Default,
        placeholder = { Text("ex: Transformer le lait de notre terroir en fierté...") }
    )

    OutlinedTextField(
        value = sud,
        onValueChange = { sud = it },
        label = { Text("VALEURS (SUD) : Principes non négociables ?") },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boussole_sud_input"),
        leadingIcon = { Icon(Icons.Default.Anchor, contentDescription = "South") },
        placeholder = { Text("ex: Intégrité, Écoute active, Ubuntu") }
    )

    OutlinedTextField(
        value = est,
        onValueChange = { est = it },
        label = { Text("RESSOURCES (EST) : Nos forces (humaines/locales) ?") },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boussole_est_input"),
        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = "East") },
        placeholder = { Text("ex: Equipe soudée, savoir-faire artisanal...") }
    )

    OutlinedTextField(
        value = ouest,
        onValueChange = { ouest = it },
        label = { Text("MENACES (OUEST) : Principal risque externe actuel ?") },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boussole_ouest_input"),
        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = "West") },
        placeholder = { Text("ex: Connectivité intermittente, fluctuation des prix...") }
    )

    Button(
        onClick = {
            focusManager.clearFocus()
            onSave(nord, sud, est, ouest)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Boussole enregistrée localement !")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("save_boussole_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
    ) {
        Icon(imageVector = Icons.Default.Save, contentDescription = "Enregistrer")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Enregistrer ma Boussole", fontWeight = FontWeight.Bold)
    }

    SnackbarHost(hostState = snackbarHostState)
}

@Composable
fun DecideTabContent(
    entries: List<DecideEntity>,
    onAdd: (String, String, String, String, String, String) -> Unit,
    onDelete: (Int) -> Unit
) {
    var problem by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var consultant by remember { mutableStateOf("") }
    var intuitionIa by remember { mutableStateOf("") }
    var decision by remember { mutableStateOf("") }

    var isAddingNew by remember { mutableStateOf(false) }

    Text(
        text = "🛠️ Méthode DECIDE : Matrice Décisionnelle",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = "Le leader serviteur décide sans attendre 100% de certitudes. Évaluez méthodiquement vos cas.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
    )

    if (isAddingNew) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Saisir une nouvelle décision",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = problem,
                    onValueChange = { problem = it },
                    label = { Text("D - Définir le problème réel") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_problem_input")
                )

                OutlinedTextField(
                    value = optionA,
                    onValueChange = { optionA = it },
                    label = { Text("E - Option A") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_opt_a")
                )

                OutlinedTextField(
                    value = optionB,
                    onValueChange = { optionB = it },
                    label = { Text("E - Option B") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_opt_b")
                )

                OutlinedTextField(
                    value = consultant,
                    onValueChange = { consultant = it },
                    label = { Text("C - Personne de confiance consultée") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_consultant")
                )

                OutlinedTextField(
                    value = intuitionIa,
                    onValueChange = { intuitionIa = it },
                    label = { Text("I - Intuition / Suggestions IA") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_ia")
                )

                OutlinedTextField(
                    value = decision,
                    onValueChange = { decision = it },
                    label = { Text("D - Décision finale retenue") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_decision")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isAddingNew = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }

                    Button(
                        onClick = {
                            if (problem.isNotEmpty() && decision.isNotEmpty()) {
                                onAdd(problem, optionA, optionB, consultant, intuitionIa, decision)
                                // Reset fields
                                problem = ""
                                optionA = ""
                                optionB = ""
                                consultant = ""
                                intuitionIa = ""
                                decision = ""
                                isAddingNew = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_decide_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) {
                        Text("Trancher (D)")
                    }
                }
            }
        }
    } else {
        Button(
            onClick = { isAddingNew = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_decide_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyser un Problème (DECIDE)", fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Historique décisionnel (${entries.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    if (entries.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
        ) {
            Text(
                text = "Aucune analyse sauvegardée. Lancez votre première fiche DECIDE en appuyant sur le bouton ci-dessus.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        entries.forEach { entry ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("decide_card_${entry.id}"),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matrice #${entry.id}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Terracotta,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { onDelete(entry.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Problème : ${entry.probleme}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Option A : ${entry.optionA}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• Option B : ${entry.optionB}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• Conseil de : ${entry.consultant}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "• Intuition / IA : ${entry.intuitionIa}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = GoldOcher.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "DÉCISION : ${entry.decision}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkEarthy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelsTabContent(
    assessments: List<LevelAssessmentEntity>,
    onToggle: (Int, Boolean) -> Unit
) {
    val levelNames = listOf(
        "Niveau 1 – La Position/Titre",
        "Niveau 2 – La Permission/Relation",
        "Niveau 3 – La Production/Résultat",
        "Niveau 4 – Le Développement humain",
        "Niveau 5 – Le Sommet/Valeurs"
    )

    val explanations = listOf(
        "On vous suit uniquement parce qu'on le doit (votre titre formel). L'engagement de l'équipe est minimal.",
        "On vous suit parce qu'on le veut bien (confiance partagée, respect et écoute ouverte).",
        "On vous suit à cause de ce que vous faites pour l'organisation (exemplarité des résultats).",
        "On vous suit à cause de ce que vous faites pour eux (coaching, mentorat, montée en compétences et autonomie).",
        "On vous suit à cause de ce que vous incarnez (notoriété, intégrité, valeurs partagées, héritage)."
    )

    Text(
        text = "📊 Auto-évaluation : Les 5 Niveaux de Leadership",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = "Selon John Maxwell, l'influence s'acquiert par paliers. Cochez les niveaux que vous incarnez solidement au quotidien.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
    )

    Spacer(modifier = Modifier.height(8.dp))

    (1..5).forEach { level ->
        val entity = assessments.find { it.level == level }
        val isChecked = entity?.checked == true

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isChecked) GoldOcher.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isChecked) GoldOcher else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(20.dp)
                )
                .testTag("level_card_$level"),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { onToggle(level, !isChecked) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onToggle(level, it) },
                    modifier = Modifier.testTag("level_checkbox_$level")
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = levelNames[level - 1],
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isChecked) Terracotta else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = explanations[level - 1],
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Interactive Quiz View Controller
@Composable
fun QuizSection(
    moduleId: String,
    viewModel: FoloViewModel,
    onClose: () -> Unit
) {
    val questions = remember(moduleId) { CourseData.quizList[moduleId] ?: emptyList() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showExplanation by remember { mutableStateOf(false) }
    var quizFinished by remember { mutableStateOf(false) }

    val currentQuestion = if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
        questions[currentQuestionIndex]
    } else null

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .testTag("quiz_outer_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quiz d'Évaluation Rapide",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Terracotta
                )
                IconButton(onClick = onClose, modifier = Modifier.testTag("quiz_close_btn")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer Quiz")
                }
            }

            if (questions.isEmpty() || currentQuestion == null) {
                // No questions yet, just general overview
                Text(
                    text = "Aucun quiz disponible pour ce module pour l'instant.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onClose) {
                    Text("Fermer")
                }
            } else if (quizFinished) {
                // Quiz concluded states
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Success",
                    tint = GoldOcher,
                    modifier = Modifier.size(72.dp)
                )

                Text(
                    text = "Quiz Terminé !",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                val percentage = (score.toFloat() / questions.size * 100).toInt()
                Text(
                    text = "Votre score : $score / ${questions.size} ($percentage%)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = IndigoSea
                )

                Text(
                    text = if (percentage >= 100) "Excellent ! Vous maîtrisez les valeurs de leadership serviteur d'Ubuntu !"
                           else "Bien joué ! Relisez le glossaire Dioula et retentez pour atteindre 100%.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Button(
                    onClick = {
                        // Mark progress as completed in SQLite
                        viewModel.updateModuleProgress(moduleId, true)
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("finish_quiz_done_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("Valider et finaliser le module", fontWeight = FontWeight.Bold)
                }

            } else {
                // Interactive quiz questions running
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} de ${questions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )

                    Text(
                        text = "Score : $score",
                        style = MaterialTheme.typography.labelMedium,
                        color = Terracotta,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { (currentQuestionIndex.toFloat() / questions.size) },
                    modifier = Modifier.fillMaxWidth(),
                    color = Terracotta
                )

                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Render options
                currentQuestion.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = selectedOptionIndex == optIndex
                    val isCorrect = optIndex == currentQuestion.correctIndex

                    val cardColor = when {
                        showExplanation && isCorrect -> Color.Green.copy(alpha = 0.15f)
                        showExplanation && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.15f)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderCol = when {
                        showExplanation && isCorrect -> Color.Green
                        showExplanation && isSelected && !isCorrect -> Color.Red
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color.Gray.copy(alpha = 0.3f)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !showExplanation) {
                                selectedOptionIndex = optIndex
                            }
                            .testTag("quiz_option_$optIndex"),
                        border = Row {}.let {
                            androidx.compose.foundation.BorderStroke(1.2.dp, borderCol)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (!showExplanation) selectedOptionIndex = optIndex },
                                enabled = !showExplanation
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Sub-explanation
                if (showExplanation) {
                    Surface(
                        color = IndigoSea.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (selectedOptionIndex == currentQuestion.correctIndex) "✓ Bonne Réponse !" else "✗ Réponse Incorrecte",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOptionIndex == currentQuestion.correctIndex) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Command buttons
                Button(
                    onClick = {
                        if (!showExplanation) {
                            if (selectedOptionIndex == currentQuestion.correctIndex) {
                                score++
                            }
                            showExplanation = true
                        } else {
                            if (currentQuestionIndex + 1 < questions.size) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                                showExplanation = false
                            } else {
                                quizFinished = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_action_btn"),
                    enabled = selectedOptionIndex != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text(
                        text = if (!showExplanation) "Valider la Réponse" else "Question Suivante",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
