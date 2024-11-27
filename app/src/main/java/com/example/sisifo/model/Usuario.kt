package com.example.sisifo.model

import java.io.Serializable

class Usuario(
    var nombre: String?=null,
    var correo: String?=null,
    var password: String?=null,
    var perfil: String?=null,
    var genero: String?=null
) : Serializable {
}