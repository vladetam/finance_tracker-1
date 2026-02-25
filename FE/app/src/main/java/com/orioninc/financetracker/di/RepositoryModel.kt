package com.orioninc.financetracker.di

import com.orioninc.financetracker.repository.EmployeeRepository
import com.orioninc.financetracker.repository.EmployeeRepositoryImplementation
import com.orioninc.financetracker.repository.TransactionRepository
import com.orioninc.financetracker.repository.TransactionRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        implementation: TransactionRepositoryImplementation
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(
        implementation: EmployeeRepositoryImplementation
    ) : EmployeeRepository
}