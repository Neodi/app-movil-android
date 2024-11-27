package com.example.sisifo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.sisifo.Global
import com.example.sisifo.R
import com.example.sisifo.databinding.ActivityRecetaBinding
import com.example.sisifo.model.Receta
import com.example.sisifo.model.RecetaEntera
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONArray

class RecetaActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityRecetaBinding
    private var idReceta: Int? = null
    private var recetaEntera: RecetaEntera? = null
    private lateinit var firebaseDatabase: FirebaseDatabase

    var estadoBotonFav = false
    var estadobotonCesta = false

    val uid = Global.uid
    lateinit var usuarioRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        instancias()



        supportActionBar?.hide()
        idReceta = intent.getSerializableExtra("RecetaId") as Int?
        checkReceta()

        binding.buttonFavorites.setOnClickListener(this)
        binding.buttonCesta.setOnClickListener(this)



        peticionRecetaEntera()
    }

    fun checkReceta(){
        val recetaRef = usuarioRef.child("favoritos").child(idReceta.toString())
        recetaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.buttonFavorites.setText("Favoritos⭐ ✔")
                    estadoBotonFav = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", error.message)
            }
        })

        val cestaRef = usuarioRef.child("cesta").child(idReceta.toString())
        cestaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.buttonCesta.setText("Cesta🛒 ✔")
                    estadobotonCesta = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", error.message)
            }
        })
    }

    fun instancias() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        usuarioRef = firebaseDatabase.getReference("usuarios").child(uid.toString())
    }

    private fun peticionRecetaEntera() {
        val url = "https://dummyjson.com/recipes/" + idReceta
        // Log.d("RECETA EXTENDIDA URL", url)
        val peticion = JsonObjectRequest( url, {
            val recetaEnteraPeticion: RecetaEntera = Gson().fromJson(it.toString(), RecetaEntera::class.java)
            recetaEntera = recetaEnteraPeticion
            // Log.d("RECETAENTERA EXTENDIDA", recetaEntera.toString())
            ponerDatos()
        },{
            Log.d(" ERROR RECETA EXTENDIDA", it.toString())
        })

        Volley.newRequestQueue(this).add(peticion)
    }
    private fun unirCadenas(lista: List<String>): String {
        return lista.joinToString(separator = "\n")
    }
    private fun unirCadenasSalto(lista: List<String>): String {
        return lista.joinToString(separator = "\n\n")
    }

    private fun ponerDatos() {

        //Log.d("RECETA EXTENDIDA", recetaEntera.toString())

        Glide.with(this)
            .load(recetaEntera?.image)

            .into(binding.imagenReceta)
        binding.nombreReceta.text = recetaEntera?.name

        binding.tiempoPreparacionReceta.text = "Tiempo preparación: "+recetaEntera?.prepTimeMinutes.toString() + " minutos"
        binding.tiempoCocinaReceta.text = "Tiempo cocinado: "+ recetaEntera?.cookTimeMinutes.toString() + " minutos"

        binding.ingredientesReceta.text = unirCadenas(recetaEntera?.ingredients ?: listOf())
        binding.preparacionReceta.text = unirCadenasSalto(recetaEntera?.instructions ?: listOf())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.buttonFavorites.id -> {
                if (estadoBotonFav == false) {
                    binding.buttonFavorites.setText("Favoritos⭐ ✔")
                    estadoBotonFav = true

                    val favoritosRef = usuarioRef.child("favoritos").child(idReceta.toString())
                    favoritosRef.setValue(recetaEntera)
                } else {
                    binding.buttonFavorites.setText("Favoritos⭐ ❌")
                    estadoBotonFav = false

                    usuarioRef.child("favoritos").child(idReceta.toString()).removeValue()
                }
            }

            binding.buttonCesta.id -> {
                if (estadobotonCesta == false) {
                    binding.buttonCesta.setText("Cesta🛒 ✔")
                    estadobotonCesta = true

                    val cestaRef = usuarioRef.child("cesta").child(idReceta.toString())
                    cestaRef.setValue(recetaEntera)
                } else {
                    binding.buttonCesta.setText("Cesta🛒 ❌")
                    estadobotonCesta = false

                    usuarioRef.child("cesta").child(idReceta.toString()).removeValue()
                }
            }
        }
    }
}