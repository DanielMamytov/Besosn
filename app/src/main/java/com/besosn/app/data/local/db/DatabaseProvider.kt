package com.besosn.app.data.local.db

import android.content.Context
import androidx.room.Room
import com.besosn.app.utils.Constants


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
