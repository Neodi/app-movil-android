package com.example.sisifo

import android.app.Application
import com.example.sisifo.model.Usuario

class Global : Application() {
    companion object {
        var uid: String? = null
    }
}