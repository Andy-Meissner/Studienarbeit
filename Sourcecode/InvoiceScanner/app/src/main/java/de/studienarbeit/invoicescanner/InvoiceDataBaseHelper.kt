package de.studienarbeit.invoicescanner

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE IF NOT EXISTS Invoice (" +
                "Image_URI TEXT PRIMARY KEY," +
                "IBAN TEXT NOT NULL," +
                "BIC TEXT," +
                "Amount REAL NOT NULL," +
                "Details TEXT," +
                "Receiver TEXT NOT NULL," +
                "isFavorite INTEGER NOT NULL)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Invoice"

class InvoiceDataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }
}

