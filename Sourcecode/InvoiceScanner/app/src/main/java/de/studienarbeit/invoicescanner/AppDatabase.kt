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
data class Invoice (@PrimaryKey var imagePath: String,
                    @ColumnInfo(name = "iban") var iban: String,
                    @ColumnInfo(name = "bic") var bic: String,
                    @ColumnInfo(name = "amount") var amount: Double,
                    @ColumnInfo(name = "details") var details: String,
                    @ColumnInfo(name = "receiver") var receiver: String,
                    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean
                    ){
    constructor():this("","","",0.0,"","",false)
}

@Dao
interface InvoiceDao {
    @get:Query("SELECT * FROM invoice")
    val all: List<Invoice>

    @Insert
    fun insertInvoice(image : Invoice)

    @Delete
    fun deleteInvoice(image : Invoice)

    @Update
    fun updateInvoice(image : Invoice)
}

@Database(entities = arrayOf(Invoice::class) , version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun invoiceDao(): InvoiceDao
}

