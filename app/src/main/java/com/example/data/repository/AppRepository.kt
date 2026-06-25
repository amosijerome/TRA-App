package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.dao.TaxpayerDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.ReportDao
import com.example.data.model.User
import com.example.data.model.Taxpayer
import com.example.data.model.Payment
import com.example.data.model.Report
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val taxpayerDao: TaxpayerDao,
    private val paymentDao: PaymentDao,
    private val reportDao: ReportDao
) {
    // --- Users ---
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun getUserByPhone(phone: String): User? = userDao.getUserByPhone(phone)
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    // --- Taxpayers ---
    fun getAllTaxpayers(): Flow<List<Taxpayer>> = taxpayerDao.getAllTaxpayers()
    suspend fun getTaxpayerById(id: Int): Taxpayer? = taxpayerDao.getTaxpayerById(id)
    suspend fun getTaxpayerByTin(tin: String): Taxpayer? = taxpayerDao.getTaxpayerByTin(tin)
    fun searchTaxpayers(query: String): Flow<List<Taxpayer>> = taxpayerDao.searchTaxpayers("%$query%")
    suspend fun insertTaxpayer(taxpayer: Taxpayer): Long = taxpayerDao.insertTaxpayer(taxpayer)
    suspend fun updateTaxpayer(taxpayer: Taxpayer) = taxpayerDao.updateTaxpayer(taxpayer)

    // --- Payments ---
    fun getAllPayments(): Flow<List<Payment>> = paymentDao.getAllPayments()
    fun getPaymentsForTaxpayer(taxpayerId: Int): Flow<List<Payment>> = paymentDao.getPaymentsForTaxpayer(taxpayerId)
    suspend fun insertPayment(payment: Payment): Long = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = paymentDao.updatePayment(payment)
    fun getTotalRevenueCollected(): Flow<Double?> = paymentDao.getTotalRevenueCollected()
    fun getPendingPaymentsCount(): Flow<Int> = paymentDao.getPendingPaymentsCount()

    // --- Reports ---
    fun getAllReports(): Flow<List<Report>> = reportDao.getAllReports()
    suspend fun insertReport(report: Report): Long = reportDao.insertReport(report)
}
