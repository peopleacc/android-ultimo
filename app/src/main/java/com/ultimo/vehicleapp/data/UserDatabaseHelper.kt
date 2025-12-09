package com.ultimo.vehicleapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite Database Helper untuk menyimpan data user saat Remember Me diaktifkan
 */
class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ultimo_user.db"
        private const val DATABASE_VERSION = 1

        // Table name
        const val TABLE_USER = "user_session"

        // Column names
        const val COLUMN_ID = "id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_NAMA = "nama"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_TOKEN = "token"
        const val COLUMN_REMEMBER_ME = "remember_me"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_NAMA TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_TOKEN TEXT,
                $COLUMN_REMEMBER_ME INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    /**
     * Simpan user data ke SQLite
     */
    fun saveUser(
        userId: Int?,
        nama: String?,
        email: String?,
        phone: String?,
        address: String?,
        token: String?,
        rememberMe: Boolean
    ): Long {
        // Hapus data lama dulu
        clearUser()

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_NAMA, nama)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_TOKEN, token)
            put(COLUMN_REMEMBER_ME, if (rememberMe) 1 else 0)
        }

        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result
    }

    /**
     * Ambil user data dari SQLite
     * Returns null jika tidak ada data
     */
    fun getUser(): UserSession? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            null,
            null,
            null,
            null,
            null,
            null
        )

        var userSession: UserSession? = null

        if (cursor.moveToFirst()) {
            userSession = UserSession(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                token = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN)),
                rememberMe = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMEMBER_ME)) == 1
            )
        }

        cursor.close()
        db.close()
        return userSession
    }

    /**
     * Cek apakah ada user tersimpan dengan Remember Me aktif
     */
    fun hasRememberedUser(): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_REMEMBER_ME),
            "$COLUMN_REMEMBER_ME = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        val hasUser = cursor.count > 0
        cursor.close()
        db.close()
        return hasUser
    }

    /**
     * Hapus semua data user dari SQLite (saat logout)
     */
    fun clearUser() {
        val db = writableDatabase
        db.delete(TABLE_USER, null, null)
        db.close()
    }

    /**
     * Update token saja
     */
    fun updateToken(token: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOKEN, token)
        }
        db.update(TABLE_USER, values, null, null)
        db.close()
    }
}

/**
 * Data class untuk menyimpan session user
 */
data class UserSession(
    val userId: Int,
    val nama: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val token: String?,
    val rememberMe: Boolean
)
