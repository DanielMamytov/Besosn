package com.besosn.app.data.local.db

import android.content.Context
import androidx.room.Room
import com.besosn.app.utils.Constants

/**
 * Lightweight singleton provider that exposes the Room database
 * without relying on dependency injection. This is useful in
 * fragments or helpers that need occasional access to the stored
 * data but cannot receive the database through Hilt.
 */
object DatabaseProvider {

    @Volatile
    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                Constants.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
