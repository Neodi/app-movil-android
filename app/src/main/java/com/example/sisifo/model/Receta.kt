package com.example.sisifo.model

import java.io.Serializable

class Receta (
    var id: Int,
    var title: String,
    var thumbnail: String,
    var valoracion: Double,
    var dificultad: String,
    var raciones: Int,
    var calorias: Int
) : Serializable {

  override fun toString(): String {
    return "Receta(id=$id, title='$title', thumbnail='$thumbnail', valoracion=$valoracion, dificultad='$dificultad', raciones=$raciones, calorias=$calorias)"
    }
}