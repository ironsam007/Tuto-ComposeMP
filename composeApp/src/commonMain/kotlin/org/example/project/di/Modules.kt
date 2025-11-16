package org.example.project.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.example.project.book.data.database.DatabaseFactory
import org.example.project.book.data.database.FavoriteBookDatabase
import org.example.project.book.data.network.KtorRemoteBookDataSource
import org.example.project.book.data.network.RemoteBookDataSource
import org.example.project.book.data.repository.DefaultBookRepository
import org.example.project.book.domain.BookRepository
import org.example.project.book.presentation.SelectedBookViewModel
import org.example.project.book.presentation.book_detail.BookDetailViewModel
import org.example.project.book.presentation.book_list.BookListViewModel
import org.example.project.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module



//KMP provide this concept of expect & actual, for it to be instructed to expect certain Module to be platform dependant:
expect val platformModule: Module //click on add missing Actual declaration.=>Create **Module files**

val sharedModule = module {  //this Module is a container for shared dependencies

    //=> Create a singleton(for entire app) of HttpClient by calling HttpClientFactory.create() and pass to it one dependency resolved from koin using get()
    single { HttpClientFactory.create(get()) } //the engine here is platform dependent -> get() instruct koin to try to get a dependency..to be set later, cuze if not provided, koin will crash.
    //now that i have an HttpClientInstance, i can use it in KtorRemoteBookDataSource instantiation block.
    //NB: why koin is seen as a service locater compared to Dagger -> got a pool of objects, when need one it access it from there.
    //instead of single{xxx(get(),get())} for multiple fields, we use singleOf(::xxx) that instruct koin to create instance with dependencies it has already available(all constructor args if available):
    //.bind() to specify the type of the instance that will be provided by koin after instantiating this implementation???? to check
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>() //bind: provide him KtorRBDS when someone search for RemoteBookDataSource
    singleOf(::DefaultBookRepository).bind<BookRepository>()

    viewModelOf(::BookListViewModel)
    viewModelOf(::SelectedBookViewModel)
    viewModelOf(::BookDetailViewModel)

    //Provide a singleton instance of the database by calling create() on a DatabaseFactory retrieved from Koin, set a driver (here BundledSQLiteDriver), and build the database.
    single {
        get<DatabaseFactory>().create() //we dont have definition in koin for DatabaseFactory (cuz dep on pf)-> thats why create and provide this in koin need to be done on pf modules: check later
            .setDriver(BundledSQLiteDriver()) //On native/Multiplatform (SQLDelight or Room on KMP), you need to set the SQLite driver for the database.
            .build()
    }

    //=> create a singleton instance of FavoriteBookDao by getting the FavoriteBookDatabase instance registred in koin and returning its favoriteBookDao property
    //When someone ask for favoriteBookDao return the DAO from the existing db instance
    single{ get<FavoriteBookDatabase>().favoriteBookDao }

}