package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.database.AppDatabase
import com.example.data.model.User
import com.example.data.model.Taxpayer
import com.example.data.model.Payment
import com.example.data.model.Report
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Search Query State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Reactive Data Flows ---
    val taxpayers: StateFlow<List<Taxpayer>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllTaxpayers()
            } else {
                repository.searchTaxpayers(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<Payment>> = repository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<Report>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double> = repository.getTotalRevenueCollected()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingPaymentsCount: StateFlow<Int> = repository.getPendingPaymentsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- AI Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("ai", "Habari! I am your TRA AI Tax Assistant. How can I help you today with tax guidelines, filing requirements, or payment verification?")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading.asStateFlow()

    // --- Actions ---

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun login(emailOrPhone: String, passwordHash: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val user = if (emailOrPhone.contains("@")) {
                repository.getUserByEmail(emailOrPhone)
            } else {
                repository.getUserByPhone(emailOrPhone)
            }

            if (user != null && user.passwordHash == passwordHash) {
                _currentUser.value = user
                onSuccess()
            } else {
                _authError.value = "Invalid email/phone or password."
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, passwordHash: String, role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            // Check if user already exists
            val existingEmail = repository.getUserByEmail(email)
            val existingPhone = repository.getUserByPhone(phone)

            if (existingEmail != null || existingPhone != null) {
                _authError.value = "User with this email or phone number already exists."
                return@launch
            }

            val newUser = User(
                fullName = fullName,
                email = email,
                phoneNumber = phone,
                passwordHash = passwordHash,
                role = role
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            onSuccess()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        _currentUser.value = null
        onSuccess()
    }

    fun registerTaxpayer(name: String, tinNumber: String, businessType: String, address: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existingTin = repository.getTaxpayerByTin(tinNumber)
            if (existingTin != null) {
                // Already registered
                onSuccess()
                return@launch
            }
            val taxpayer = Taxpayer(
                name = name,
                tinNumber = tinNumber,
                businessType = businessType,
                address = address
            )
            repository.insertTaxpayer(taxpayer)
            onSuccess()
        }
    }

    fun recordPayment(taxpayerId: Int, taxpayerName: String, amount: Double, taxType: String, paymentDate: String, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val payment = Payment(
                taxpayerId = taxpayerId,
                taxpayerName = taxpayerName,
                amount = amount,
                taxType = taxType,
                paymentDate = paymentDate,
                status = status
            )
            repository.insertPayment(payment)
            onSuccess()
        }
    }

    fun updatePaymentStatus(payment: Payment, newStatus: String) {
        viewModelScope.launch {
            repository.updatePayment(payment.copy(status = newStatus))
        }
    }

    fun generateReport(reportType: String, generatedBy: String) {
        viewModelScope.launch {
            // Calculate totals dynamically from current payments
            val currentPayments = payments.value
            val completedPayments = currentPayments.filter { it.status == "Completed" }
            val totalRev = completedPayments.sumOf { it.amount }
            val count = completedPayments.size

            val summary = "TRA Collection Report ($reportType)\n" +
                    "Generated on: ${System.currentTimeMillis()}\n" +
                    "Total Revenue Collected: ${String.format("%,.2f", totalRev)} TZS\n" +
                    "Successful Transactions: $count\n" +
                    "Officer In-Charge: $generatedBy\n\n" +
                    "Summary Breakdown:\n" +
                    completedPayments.groupBy { it.taxType }
                        .map { (type, pmts) -> "- $type: ${String.format("%,.2f", pmts.sumOf { it.amount })} TZS (${pmts.size} txn)" }
                        .joinToString("\n")

            val report = Report(
                reportType = reportType,
                generatedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                generatedBy = generatedBy,
                totalRevenue = totalRev,
                totalTransactions = count,
                summaryText = summary
            )
            repository.insertReport(report)
        }
    }

    // --- AI Chat Assistance ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMessage = ChatMessage("user", text)
        _chatMessages.value = _chatMessages.value + userMessage
        _chatLoading.value = true

        viewModelScope.launch {
            val systemInstruction = "You are a professional Tanzania Revenue Authority (TRA) digital assistant. " +
                    "Your role is to help taxpayers and revenue officers understand Tanzanian tax codes, guidelines, " +
                    "and standard procedures. Always provide accurate, polite, and helpful information. " +
                    "Mention terms in Tanzanian Shillings (TZS), TIN (Taxpayer Identification Number), " +
                    "VAT (Value Added Tax, standard 18%), Income Tax, and standard filing dates. Keep responses clear and structured."

            val aiResponse = GeminiClient.generateTaxAdvice(text, systemInstruction)
            
            _chatMessages.value = _chatMessages.value + ChatMessage("ai", aiResponse)
            _chatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("ai", "Hello! I am your TRA AI Tax Assistant. How can I assist you with tax compliance, calculations, or guidelines today?")
        )
    }
}

// --- Factory ---
class MainViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
