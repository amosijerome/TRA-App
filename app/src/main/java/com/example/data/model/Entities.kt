package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val passwordHash: String,
    val role: String // "Revenue Officer", "Taxpayer", "Administrator"
)

@Entity(tableName = "taxpayers")
data class Taxpayer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val tinNumber: String,
    val businessType: String, // "Retail", "Wholesale", "Manufacturing", "Service", "Individual"
    val address: String
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taxpayerId: Int,
    val taxpayerName: String,
    val amount: Double,
    val taxType: String, // "Income Tax", "Value Added Tax (VAT)", "Withholding Tax", "Customs Duty"
    val paymentDate: String, // e.g., "YYYY-MM-DD"
    val status: String // "Pending", "Completed", "Rejected"
)

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reportType: String, // "Daily", "Monthly"
    val generatedDate: String,
    val generatedBy: String,
    val totalRevenue: Double,
    val totalTransactions: Int,
    val summaryText: String
)
