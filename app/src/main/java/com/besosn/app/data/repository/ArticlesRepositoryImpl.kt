package com.besosn.app.data.repository

import com.besosn.app.domain.model.Article
import com.besosn.app.domain.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ArticlesRepositoryImpl @Inject constructor() : ArticlesRepository {
    override fun getArticles(): Flow<List<Article>> = flow { emit(emptyList()) }
    override suspend fun getArticle(id: Int): Article? = null
}

