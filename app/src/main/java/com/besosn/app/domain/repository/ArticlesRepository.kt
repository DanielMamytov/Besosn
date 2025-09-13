package com.besosn.app.domain.repository

import com.besosn.app.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticlesRepository {
    fun getArticles(): Flow<List<Article>>
    suspend fun getArticle(id: Int): Article?
}

