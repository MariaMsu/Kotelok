package com.designdrivendevelopment.kotelok.application

import android.app.Application

class KotelokApplication : Application() {
    val appComponent: AppComponent by lazy { AppComponent(applicationContext) }
}
