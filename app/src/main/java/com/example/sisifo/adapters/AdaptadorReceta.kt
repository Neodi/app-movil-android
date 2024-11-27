package com.example.sisifo.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sisifo.R
import com.example.sisifo.model.Receta
import com.example.sisifo.ui.activity.MainActivity
import com.example.sisifo.ui.activity.RecetaActivity
import com.example.sisifo.ui.activity.SingupActivity

class AdaptadorReceta (var context: Context) : RecyclerView.Adapter<AdaptadorReceta.MyHolder>() {

    val lista: ArrayList<Receta> = ArrayList()

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imagen: ImageView = view.findViewById(R.id.imagenFila)
        var titulo: TextView = view.findViewById(R.id.tituloFila)
        var valoracion: TextView = view.findViewById(R.id.valoracionCelda)
        var dificultad: TextView = view.findViewById(R.id.dificultadCelda)
        var raciones: TextView = view.findViewById(R.id.racionesCelda)
        var calorias: TextView = view.findViewById(R.id.caloriasCelda)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista: View = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return MyHolder(vista)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val receta: Receta = lista[position]
        holder.titulo.text = receta.title
        holder.valoracion.text = receta.valoracion.toString()+"⭐"
        holder.dificultad.text = "Dificultad:"+ receta.dificultad
        holder.raciones.text = "Nº Raciones "+receta.raciones.toString()
        holder.calorias.text = receta.calorias.toString()+ " Kcal"
        Glide.with(context)
            .load(receta.thumbnail)
            .placeholder(R.drawable.comida_ph)
            .into(holder.imagen)

        holder.itemView.setOnClickListener {
            Log.d("AdaptadorReceta", "Se hizo clic en el elemento $position")
            val intent = Intent(context, RecetaActivity::class.java)
            intent.putExtra("RecetaId", receta.id)
            context.startActivity(intent)
        }
    }

    fun addReceta(x: Receta) {
        lista.add(x)
        notifyItemInserted(lista.size - 1)
    }
    fun clearRecetas(){
        lista.clear()
        notifyDataSetChanged()
    }
}