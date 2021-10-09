package com.designdrivendevelopment.kotelok

import android.app.Application

class KotelokApplication : Application() {
    val appComponent: AppComponent by lazy { AppComponent(applicationContext) }
}
