package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BoussoleEntity
import com.example.data.local.DecideEntity
import com.example.data.local.LevelAssessmentEntity
import com.example.data.local.ModuleProgressEntity
import com.example.data.model.CourseData
import com.example.data.repository.FoloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoloViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FoloRepository(
        database.boussoleDao(),
        database.decideDao(),
        database.levelAssessmentDao(),
        database.moduleProgressDao(),
        database.prospectDao()
    )

    private val prefs = application.getSharedPreferences("folo_prefs", android.content.Context.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            repository.ensurePreseededData(CourseData.modules.map { it.id })
        }
    }

    val prospects: StateFlow<List<com.example.data.local.ProspectEntity>> = repository.prospectsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val boussole: StateFlow<BoussoleEntity?> = repository.boussoleFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val decideEntries: StateFlow<List<DecideEntity>> = repository.decideEntriesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val assessments: StateFlow<List<LevelAssessmentEntity>> = repository.assessmentsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val progresses: StateFlow<List<ModuleProgressEntity>> = repository.progressesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isOfflineMode = MutableStateFlow(prefs.getBoolean("offline_mode", false))
    val isOfflineMode = _isOfflineMode.asStateFlow()

    private val _isDataSaver = MutableStateFlow(prefs.getBoolean("data_saver", true)) // Enabled by default for emerging markets
    val isDataSaver = _isDataSaver.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Kaboré Ousmane") ?: "Kaboré Ousmane")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("user_email", "infofolo.bf@gmail.com") ?: "infofolo.bf@gmail.com")
    val userEmail = _userEmail.asStateFlow()

    // Subscription variables
    private val _isPremium = MutableStateFlow(prefs.getBoolean("is_premium", false))
    val isPremium = _isPremium.asStateFlow()

    // Editor / tester bypass mode - enabled by default so the creator can test without friction initially
    private val _isBypassMode = MutableStateFlow(prefs.getBoolean("is_bypass_mode", true))
    val isBypassMode = _isBypassMode.asStateFlow()

    // Customizable landing page checkout url
    private val _subscriptionUrl = MutableStateFlow(prefs.getString("subscription_url", "https://folocoaching.com/abonnement") ?: "https://folocoaching.com/abonnement")
    val subscriptionUrl = _subscriptionUrl.asStateFlow()

    // Combined state representing whether the user has premium level features unlocked (either by purchase or bypass/test mode)
    val hasPremiumAccess: StateFlow<Boolean> = combine(isPremium, isBypassMode) { premium, bypass ->
        premium || bypass
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = prefs.getBoolean("is_premium", false) || prefs.getBoolean("is_bypass_mode", true)
    )

    fun updateUserName(name: String) {
        _userName.value = name
        prefs.edit().putString("user_name", name).apply()
    }

    fun updateUserEmail(email: String) {
        _userEmail.value = email
        prefs.edit().putString("user_email", email).apply()
    }

    private val _downloadingState = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadingState = _downloadingState.asStateFlow()

    fun toggleOfflineMode() {
        val newValue = !_isOfflineMode.value
        _isOfflineMode.value = newValue
        prefs.edit().putBoolean("offline_mode", newValue).apply()
    }

    fun toggleDataSaver() {
        val newValue = !_isDataSaver.value
        _isDataSaver.value = newValue
        prefs.edit().putBoolean("data_saver", newValue).apply()
    }

    fun setPremium(value: Boolean) {
        _isPremium.value = value
        prefs.edit().putBoolean("is_premium", value).apply()
    }

    fun setBypassMode(value: Boolean) {
        _isBypassMode.value = value
        prefs.edit().putBoolean("is_bypass_mode", value).apply()
    }

    fun updateSubscriptionUrl(url: String) {
        _subscriptionUrl.value = url
        prefs.edit().putString("subscription_url", url).apply()
    }

    fun saveBoussole(nord: String, sud: String, est: String, ouest: String) {
        viewModelScope.launch {
            repository.saveBoussole(nord, sud, est, ouest)
        }
    }

    fun addDecideEntry(
        probleme: String,
        optA: String,
        optB: String,
        consultant: String,
        intIa: String,
        decision: String,
        evaluation: String
    ) {
        viewModelScope.launch {
            repository.addDecideEntry(
                DecideEntity(
                    probleme = probleme,
                    optionA = optA,
                    optionB = optB,
                    consultant = consultant,
                    intuitionIa = intIa,
                    decision = decision,
                    evaluation = evaluation
                )
            )
        }
    }

    fun deleteDecideEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteDecideEntry(id)
        }
    }

    fun updateAssessment(level: Int, checked: Boolean) {
        viewModelScope.launch {
            repository.updateAssessment(level, checked)
        }
    }

    fun updateModuleProgress(moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.updateModuleProgress(moduleId, completed)
        }
    }

    fun simulateDownload(moduleId: String) {
        viewModelScope.launch {
            _downloadingState.value = _downloadingState.value + (moduleId to 0)
            for (prg in 10..100 step 15) {
                kotlinx.coroutines.delay(100)
                _downloadingState.value = _downloadingState.value + (moduleId to prg)
            }
            _downloadingState.value = _downloadingState.value - moduleId
            repository.updateModuleDownload(moduleId, true)
        }
    }

    fun deleteDownloadedModule(moduleId: String) {
        viewModelScope.launch {
            repository.updateModuleDownload(moduleId, false)
        }
    }

    fun addProspect(
        companyName: String,
        contactEmail: String,
        sector: String,
        opportunityDescription: String,
        emailSubject: String,
        emailBody: String,
        testEmailSent: Boolean = false,
        realEmailSent: Boolean = false,
        isEmailValid: Boolean = true,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addProspect(
                com.example.data.local.ProspectEntity(
                    companyName = companyName,
                    contactEmail = contactEmail,
                    sector = sector,
                    opportunityDescription = opportunityDescription,
                    emailSubject = emailSubject,
                    emailBody = emailBody,
                    testEmailSent = testEmailSent,
                    realEmailSent = realEmailSent,
                    isEmailValid = isEmailValid,
                    notes = notes
                )
            )
        }
    }

    fun updateProspect(prospect: com.example.data.local.ProspectEntity) {
        viewModelScope.launch {
            repository.updateProspect(prospect)
        }
    }

    fun deleteProspect(id: Int) {
        viewModelScope.launch {
            repository.deleteProspectById(id)
        }
    }
}
