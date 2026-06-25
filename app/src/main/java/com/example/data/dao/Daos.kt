package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import com.example.data.model.Taxpayer
import com.example.data.model.Payment
import com.example.data.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}

@Dao
interface TaxpayerDao {
    @Query("SELECT * FROM taxpayers ORDER BY name ASC")
    fun getAllTaxpayers(): Flow<List<Taxpayer>>

    @Query("SELECT * FROM taxpayers WHERE id = :id")
    suspend fun getTaxpayerById(id: Int): Taxpayer?

    @Query("SELECT * FROM taxpayers WHERE tinNumber = :tin LIMIT 1")
    suspend fun getTaxpayerByTin(tin: String): Taxpayer?

    @Query("SELECT * FROM taxpayers WHERE name LIKE :query OR tinNumber LIKE :query")
    fun searchTaxpayers(query: String): Flow<List<Taxpayer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxpayer(taxpayer: Taxpayer): Long

    @Update
    suspend fun updateTaxpayer(taxpayer: Taxpayer)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC, id DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE taxpayerId = :taxpayerId")
    fun getPaymentsForTaxpayer(taxpayerId: Int): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Update
    suspend fun updatePayment(payment: Payment)

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'Completed'")
    fun getTotalRevenueCollected(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM payments WHERE status = 'Pending'")
    fun getPendingPaymentsCount(): Flow<Int>
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY generatedDate DESC")
    fun getAllReports(): Flow<List<Report>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report): Long
}
