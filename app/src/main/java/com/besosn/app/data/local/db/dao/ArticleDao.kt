package com.besosn.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.besosn.app.data.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles")
    fun getArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticle(id: Int): ArticleEntity?
}

