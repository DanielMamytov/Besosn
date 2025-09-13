package com.besosn.app.domain.usecase.articles

import com.besosn.app.domain.model.Article
import com.besosn.app.domain.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesUseCase(private val repository: ArticlesRepository) {
    operator fun invoke(): Flow<List<Article>> = repository.getArticles()
}

