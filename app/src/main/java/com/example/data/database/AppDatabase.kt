package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.UserDao
import com.example.data.dao.TaxpayerDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.ReportDao
import com.example.data.model.User
import com.example.data.model.Taxpayer
import com.example.data.model.Payment
import com.example.data.model.Report
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Taxpayer::class, Payment::class, Report::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taxpayerDao(): TaxpayerDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tra_revenue_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database)
                    }
                }
            }

            private suspend fun prepopulateDatabase(db: AppDatabase) {
                val userDao = db.userDao()
                val taxpayerDao = db.taxpayerDao()
                val paymentDao = db.paymentDao()

                // 1. Prepopulate standard users (Officer, Taxpayer, Admin)
                userDao.insertUser(
                    User(
                        fullName = "Emmanuel Mollel",
                        email = "officer@tra.go.tz",
                        phoneNumber = "+255762345678",
                        passwordHash = "password123", // For this prototype, plain text or simple hash
                        role = "Revenue Officer"
                    )
                )
                userDao.insertUser(
                    User(
                        fullName = "Abasi Taxpayer",
                        email = "abasi@gmail.com",
                        phoneNumber = "+255655123456",
                        passwordHash = "password123",
                        role = "Taxpayer"
                    )
                )
                userDao.insertUser(
                    User(
                        fullName = "TRA Admin",
                        email = "admin@tra.go.tz",
                        phoneNumber = "+255711122334",
                        passwordHash = "admin123",
                        role = "Administrator"
                    )
                )

                // 2. Prepopulate taxpayers
                val tp1Id = taxpayerDao.insertTaxpayer(
                    Taxpayer(
                        name = "Mollel Tanzania Ltd",
                        tinNumber = "123-456-789",
                        businessType = "Manufacturing",
                        address = "Plot 42, Mikocheni B, Dar es Salaam"
                    )
                )
                val tp2Id = taxpayerDao.insertTaxpayer(
                    Taxpayer(
                        name = "Kilimanjaro Traders",
                        tinNumber = "234-567-890",
                        businessType = "Wholesale",
                        address = "Mbuyuni Street, Moshi, Kilimanjaro"
                    )
                )
                val tp3Id = taxpayerDao.insertTaxpayer(
                    Taxpayer(
                        name = "Serengeti Safaris",
                        tinNumber = "345-678-901",
                        businessType = "Service",
                        address = "Nyerere Road, Arusha"
                    )
                )
                val tp4Id = taxpayerDao.insertTaxpayer(
                    Taxpayer(
                        name = "Zanzibar Spice Shop",
                        tinNumber = "456-789-012",
                        businessType = "Retail",
                        address = "Stone Town, Zanzibar"
                    )
                )

                // 3. Prepopulate some payments
                paymentDao.insertPayment(
                    Payment(
                        taxpayerId = tp1Id.toInt(),
                        taxpayerName = "Mollel Tanzania Ltd",
                        amount = 12500000.0,
                        taxType = "Value Added Tax (VAT)",
                        paymentDate = "2026-06-20",
                        status = "Completed"
                    )
                )
                paymentDao.insertPayment(
                    Payment(
                        taxpayerId = tp1Id.toInt(),
                        taxpayerName = "Mollel Tanzania Ltd",
                        amount = 4500000.0,
                        taxType = "Income Tax",
                        paymentDate = "2026-06-22",
                        status = "Completed"
                    )
                )
                paymentDao.insertPayment(
                    Payment(
                        taxpayerId = tp2Id.toInt(),
                        taxpayerName = "Kilimanjaro Traders",
                        amount = 3200000.0,
                        taxType = "Withholding Tax",
                        paymentDate = "2026-06-23",
                        status = "Completed"
                    )
                )
                paymentDao.insertPayment(
                    Payment(
                        taxpayerId = tp3Id.toInt(),
                        taxpayerName = "Serengeti Safaris",
                        amount = 8500000.0,
                        taxType = "Customs Duty",
                        paymentDate = "2026-06-24",
                        status = "Pending"
                    )
                )
                paymentDao.insertPayment(
                    Payment(
                        taxpayerId = tp4Id.toInt(),
                        taxpayerName = "Zanzibar Spice Shop",
                        amount = 1200000.0,
                        taxType = "Income Tax",
                        paymentDate = "2026-06-24",
                        status = "Completed"
                    )
                )
            }
        }
    }
}
