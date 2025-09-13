package com.besosn.app.di

import com.besosn.app.domain.repository.ArticlesRepository
import com.besosn.app.domain.repository.InventoryRepository
import com.besosn.app.domain.repository.MatchesRepository
import com.besosn.app.domain.repository.TeamsRepository
import com.besosn.app.domain.usecase.articles.GetArticleDetailUseCase
import com.besosn.app.domain.usecase.articles.GetArticlesUseCase
import com.besosn.app.domain.usecase.inventory.AddInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.DeleteInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.GetInventoryItemsUseCase
import com.besosn.app.domain.usecase.matches.AddMatchUseCase
import com.besosn.app.domain.usecase.matches.DeleteMatchUseCase
import com.besosn.app.domain.usecase.matches.GetMatchesUseCase
import com.besosn.app.domain.usecase.teams.AddTeamUseCase
import com.besosn.app.domain.usecase.teams.DeleteTeamUseCase
import com.besosn.app.domain.usecase.teams.GetTeamsUseCase
import com.besosn.app.domain.usecase.teams.UpdateTeamUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideAddTeamUseCase(repository: TeamsRepository) = AddTeamUseCase(repository)

    @Provides
    fun provideGetTeamsUseCase(repository: TeamsRepository) = GetTeamsUseCase(repository)

    @Provides
    fun provideDeleteTeamUseCase(repository: TeamsRepository) = DeleteTeamUseCase(repository)

    @Provides
    fun provideUpdateTeamUseCase(repository: TeamsRepository) = UpdateTeamUseCase(repository)

    @Provides
    fun provideAddMatchUseCase(repository: MatchesRepository) = AddMatchUseCase(repository)

    @Provides
    fun provideGetMatchesUseCase(repository: MatchesRepository) = GetMatchesUseCase(repository)

    @Provides
    fun provideDeleteMatchUseCase(repository: MatchesRepository) = DeleteMatchUseCase(repository)

    @Provides
    fun provideAddInventoryItemUseCase(repository: InventoryRepository) =
        AddInventoryItemUseCase(repository)

    @Provides
    fun provideGetInventoryItemsUseCase(repository: InventoryRepository) =
        GetInventoryItemsUseCase(repository)

    @Provides
    fun provideDeleteInventoryItemUseCase(repository: InventoryRepository) =
        DeleteInventoryItemUseCase(repository)

    @Provides
    fun provideGetArticlesUseCase(repository: ArticlesRepository) =
        GetArticlesUseCase(repository)

    @Provides
    fun provideGetArticleDetailUseCase(repository: ArticlesRepository) =
        GetArticleDetailUseCase(repository)
}

