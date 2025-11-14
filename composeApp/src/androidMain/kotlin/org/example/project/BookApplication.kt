package org.example.project

import android.app.Application
import org.example.project.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication

//Entry point for Android to call initKoin when the app is launched
class BookApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { //KoinApplication is the receiver of this,
            androidContext(this@BookApplication)
        }
    }
}

/* NB:
from initKoin() declaration:    "fun initKoin(config: KoinAppDeclaration? =null)"
KoinAppDeclaration defined as   "typealias KoinAppDeclaration = KoinApplication.() -> Unit"
    => which means its a lambda with receiver of type KoinApplication
    => Th lambda that will be passed to initKoin has an implicit receiver of type KoinApplication
    => So, this will refer to KoinApplication instance that is created by startKoin{} in initKoin()
    => we can call androidContext() as it is an extension function on the receiver KoinApplication
    => KoinApplicatin.androidContext() will take BookApplication instance as the android context to register inside koin
        -> any future di will be delt on this instance context.


Kotlin's trailing-lambda syntax: Write lambda outside the parentheses

*/