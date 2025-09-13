package com.besosn.app.di

import com.besosn.app.data.repository.ArticlesRepositoryImpl
import com.besosn.app.data.repository.InventoryRepositoryImpl
import com.besosn.app.data.repository.MatchesRepositoryImpl
import com.besosn.app.data.repository.TeamsRepositoryImpl
import com.besosn.app.domain.repository.ArticlesRepository
import com.besosn.app.domain.repository.InventoryRepository
import com.besosn.app.domain.repository.MatchesRepository
import com.besosn.app.domain.repository.TeamsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindTeamsRepository(impl: TeamsRepositoryImpl): TeamsRepository

    @Binds
    abstract fun bindMatchesRepository(impl: MatchesRepositoryImpl): MatchesRepository

    @Binds
    abstract fun bindInventoryRepository(impl: InventoryRepositoryImpl): InventoryRepository

    @Binds
    abstract fun bindArticlesRepository(impl: ArticlesRepositoryImpl): ArticlesRepository
}

