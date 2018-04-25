package de.studienarbeit.invoicescanner

import android.arch.persistence.room.*
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Dao
import android.support.annotation.NonNull

/**
 * Created by andym on 24.04.2018.
 */
@Entity
data class Invoice (@PrimaryKey(autoGenerate = true) var id: Int? = null,
                    @ColumnInfo(name = "imagePath") var imagePath: String,
                    @ColumnInfo(name = "iban")@NonNull var iban: String,
                    @ColumnInfo(name = "bic") var bic: String,
                    @ColumnInfo(name = "amount") @NonNull var amount: Double,
                    @ColumnInfo(name = "details") var details: String,
                    @ColumnInfo(name = "receiver") @NonNull var receiver: String,
                    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean
                    ){
    constructor():this(null,"","","",0.0,"","",false)
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

