package com.besosn.app.di

import com.besosn.app.domain.repository.InventoryRepository
import com.besosn.app.domain.usecase.inventory.AddInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.DeleteInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.GetInventoryItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideAddInventoryItemUseCase(repository: InventoryRepository) =
        AddInventoryItemUseCase(repository)

    @Provides
    fun provideGetInventoryItemsUseCase(repository: InventoryRepository) =
        GetInventoryItemsUseCase(repository)

    @Provides
    fun provideDeleteInventoryItemUseCase(repository: InventoryRepository) =
        DeleteInventoryItemUseCase(repository)

}

