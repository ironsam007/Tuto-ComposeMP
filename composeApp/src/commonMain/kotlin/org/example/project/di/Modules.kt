package org.example.project.di

import org.example.project.book.data.network.KtorRemoteBookDataSource
import org.example.project.book.data.network.RemoteBookDataSource
import org.example.project.book.data.repository.DefaultBookRepository
import org.example.project.book.domain.BookRepository
import org.example.project.book.presentation.book_list.BookListViewModel
import org.example.project.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


expect val platformModule: Module

val sharedModule =module {
    single{ HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()

    viewModelOf(::BookListViewModel)

}