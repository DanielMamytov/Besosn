package com.besosn.app.presentation.ui.articles

import androidx.annotation.DrawableRes

data class ArticleUiModel(
    val id: Int,
    val title: String,
    val content: String,
    @DrawableRes val imageRes: Int
)
