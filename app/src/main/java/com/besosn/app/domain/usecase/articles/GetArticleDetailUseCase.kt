package com.besosn.app.domain.usecase.articles

import com.besosn.app.domain.model.Article
import com.besosn.app.domain.repository.ArticlesRepository

class GetArticleDetailUseCase(private val repository: ArticlesRepository) {
    suspend operator fun invoke(id: Int): Article? = repository.getArticle(id)
}

