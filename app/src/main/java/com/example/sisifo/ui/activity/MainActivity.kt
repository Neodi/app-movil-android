package com.example.sisifo.ui.activity

import android.content.res.Configuration
import android.health.connect.datatypes.MealType
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sisifo.Global
import com.example.sisifo.R
import com.example.sisifo.adapters.AdaptadorReceta
import com.example.sisifo.databinding.ActivityMainBinding
import com.example.sisifo.model.Receta
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adaptadorMealTypes: ArrayAdapter<String>
    private lateinit var adaptadorReceta: AdaptadorReceta
    private lateinit var listaMealTypes: ArrayList<String>
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var opcionMenuSeleccionada: Int = R.id.menu_ver_todas

    // private lateinit var spinnerMealTypes: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // spinnerMealTypes = binding.spinnerFiltro

        instancias()
        peticionMealTypes()
        persoSpinner()
        persoRecycler()
        peticionEspecifica()
        setTitle("Recetas Online")


        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                adaptadorReceta.clearRecetas()
                if (binding.spinnerFiltro.selectedItem.toString().equals("Todas las recetas")) {
                    peticionGeneral()
                } else {
                    peticionEspecifica()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                adaptadorReceta.clearRecetas()
                peticionGeneral()
            }
        }

        val uIdUsuario = intent.getStringExtra("uid")
        setDatosUser(uIdUsuario.toString(), binding)


    }
    override fun onResume() {
        Log.d("RESUME", "RESUME")
        super.onResume()
        when (opcionMenuSeleccionada) {
            R.id.menu_ver_carrito -> {
                setTitle("Recetas Carrito")
                adaptadorReceta.clearRecetas()
                peticionMenu("cesta")
            }
            R.id.menu_ver_favoritos -> {
                setTitle("Recetas Favoritas")
                adaptadorReceta.clearRecetas()
                peticionMenu("favoritos")
            }
            R.id.menu_ver_todas -> {
                binding.spinnerFiltro.visibility = View.VISIBLE
                setTitle("Recetas Online")
                adaptadorReceta.clearRecetas()
                peticionGeneral()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        opcionMenuSeleccionada = item.itemId
        when (item.itemId) {
            R.id.menu_ver_carrito -> {
                Log.d("CARRO", "VER CARRITO")
                setTitle("Recetas Carrito")

                adaptadorReceta.clearRecetas()
                peticionMenu("cesta")
            }
            R.id.menu_ver_favoritos -> {
                Log.d("FAVORITOS", "VER FAVORITOS")
                setTitle("Recetas Favoritas")

                adaptadorReceta.clearRecetas()
                peticionMenu("favoritos")
            }
            R.id.menu_ver_todas -> {
                binding.spinnerFiltro.visibility = View.VISIBLE
                Log.d("TODAS", "VER TODAS")
                setTitle("Recetas Online")

                adaptadorReceta.clearRecetas()
                peticionGeneral()
            }

        }
        return true
    }


    private fun peticionMenu(seleccion: String) {
        Log.d("SELECCION", seleccion)

        binding.spinnerFiltro.visibility = View.GONE

        adaptadorReceta.clearRecetas()
        firebaseDatabase.reference.child("usuarios").child(Global.uid.toString()).child(seleccion)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (child in snapshot.children){
                            val idReceta = child.key.toString()
                            Log.d("$seleccion---", idReceta)

                            val id = child.key.toString().toInt()
                            val title = child.child("name").value.toString()
                            val thumbnail = child.child("image").value.toString()
                            val valoracion = child.child("rating").value.toString().toDouble()
                            val dificultad = child.child("difficulty").value.toString()
                            val raciones = child.child("servings").value.toString().toInt()
                            val calorias = child.child("caloriesPerServing").value.toString().toInt()

                            val receta =
                                Receta(id, title, thumbnail, valoracion, dificultad, raciones, calorias)

                            adaptadorReceta.addReceta(receta)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", error.toString())
                }
            })
    }

    private fun setDatosUser(uid: String, binding: ActivityMainBinding) {
        firebaseDatabase.reference.child("usuarios").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){
                        Global.uid = uid
                        val nombre = snapshot.child("nombre").value.toString()
                        val perfil = snapshot.child("perfil").value.toString()
                        val genero = snapshot.child("genero").value.toString()

                        if (genero == "Hombre"){
                            binding.textoBienvenida.setText("Bienvenido $nombre")
                        }else if(genero == "Mujer"){
                            binding.textoBienvenida.setText("Bienvenida $nombre")
                        }else{
                            binding.textoBienvenida.setText("Bienvenid@ $nombre")
                        }
                        binding.textPerfil.setText("$perfil")


                    }else{
                        binding.textoBienvenida.setText("Bienvenid@")
                        binding.textPerfil.setText("Invitado")
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", error.toString())
                }
            })
    }

    private fun peticionGeneral() {
        val url = "https://dummyjson.com/recipes/"
        val peticion: JsonObjectRequest = JsonObjectRequest(url, { response ->
            val recetas = response.getJSONArray("recipes")

            for (i in 0 until recetas.length()) {
                val recetaJSON = recetas.getJSONObject(i)

                val id = recetaJSON.getInt("id")
                val title = recetaJSON.getString("name")
                val thumbnail = recetaJSON.getString("image")
                val valoracion = recetaJSON.getDouble("rating")
                val dificultad = recetaJSON.getString("difficulty")
                val raciones = recetaJSON.getInt("servings")
                val calorias = recetaJSON.getInt("caloriesPerServing")//a@a.com

                val receta =
                    Receta(id, title, thumbnail, valoracion, dificultad, raciones, calorias)

                adaptadorReceta.addReceta(receta)
            }

        }, {
            Log.d("ERROR", it.toString())
        })

        Volley.newRequestQueue(applicationContext).add(peticion)
    }

    private fun peticionEspecifica() {
        val url = "https://dummyjson.com/recipes/"
        val mealTypeEspecifico = "meal-type/" + binding.spinnerFiltro.selectedItem.toString()
        val peticion: JsonObjectRequest = JsonObjectRequest(url + mealTypeEspecifico, { response ->
            val recetas = response.getJSONArray("recipes")
            for (i in 0 until recetas.length()) {
                val recetaJSON = recetas.getJSONObject(i)

                val id = recetaJSON.getInt("id")
                val title = recetaJSON.getString("name")
                val thumbnail = recetaJSON.getString("image")
                val valoracion = recetaJSON.getDouble("rating")
                val dificultad = recetaJSON.getString("difficulty")
                val raciones = recetaJSON.getInt("servings")
                val calorias = recetaJSON.getInt("caloriesPerServing")

                val receta =
                    Receta(id, title, thumbnail, valoracion, dificultad, raciones, calorias)
                // Log.d("RECETA", receta.toString())
                adaptadorReceta.addReceta(receta)
            }
        }, {
            Log.d("ERROR", it.toString())
        })


        Volley.newRequestQueue(applicationContext).add(peticion)
    }

    fun persoRecycler() {
        binding.recyclerItems.adapter = adaptadorReceta

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recyclerItems.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        } else {
            binding.recyclerItems.layoutManager = GridLayoutManager(this, 2)
        }
    }

    fun persoSpinner() {
        adaptadorMealTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = adaptadorMealTypes
    }

    private fun peticionMealTypes() {
        val url = "https://dummyjson.com/recipes"

        val peticion: JsonObjectRequest = JsonObjectRequest(url, { response ->
            val todasLasRecetas = response.getJSONArray("recipes")
            for (i in 0 until todasLasRecetas.length()) {
                val receta = todasLasRecetas.getJSONObject(i)
                val mealTypes = receta.getJSONArray("mealType")
                for (j in 0 until mealTypes.length()) {
                    val mealType = mealTypes.getString(j)
                    if (!listaMealTypes.contains(mealType)) {
                        listaMealTypes.add(mealType)
                    }
                }
            }
            listaMealTypes.sort()
            listaMealTypes.add(0, "Todas las recetas")
            adaptadorMealTypes.notifyDataSetChanged()
            // Log.d(" TODOS MEALTYPES", listaMealTypes.toString())
        }, {
            Log.d("ERROR", it.toString())
        })
        // spinnerMealTypes.adapter = adaptadorMealTypes

        Volley.newRequestQueue(applicationContext).add(peticion)
    }

    fun instancias() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        listaMealTypes = ArrayList()
        adaptadorReceta = AdaptadorReceta(this)
        adaptadorMealTypes = ArrayAdapter(
            applicationContext, android.R.layout.simple_spinner_item, listaMealTypes
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }






}