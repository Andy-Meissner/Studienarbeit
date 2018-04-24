package de.studienarbeit.invoicescanner

import android.arch.persistence.room.*
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Dao

/**
 * Created by andym on 24.04.2018.
 */
@Entity
data class Invoice (@PrimaryKey(autoGenerate = true) var id: Long?,
                    @ColumnInfo(name = "imagePath") var imagePath: String,
                    @ColumnInfo(name = "recognizedText") var recognizedText: String){
    constructor():this(null,"","")
}

@Dao
interface InvoiceDao {
    @get:Query("SELECT * FROM invoice")
    val all: List<Invoice>

    @Query("SELECT * FROM invoice WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Invoice>

    @Query("SELECT * FROM invoice WHERE imagePath LIKE :first AND " + "recognizedText LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Invoice

    @Insert
    fun insertAll(vararg users: Invoice)

    @Delete
    fun delete(user: Invoice)
}

@Database(entities = arrayOf(Invoice::class) , version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun invoiceDao(): InvoiceDao
}

