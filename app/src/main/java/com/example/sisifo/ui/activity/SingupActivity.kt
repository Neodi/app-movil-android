package com.example.sisifo.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import com.google.firebase.auth.FirebaseAuth
import com.example.sisifo.databinding.ActivitySingupBinding
import com.example.sisifo.model.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class SingupActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var authFirebase: FirebaseAuth
    private lateinit var binding: ActivitySingupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        instancias()

        binding.buttonRegister.setOnClickListener(){

            if(
                binding.editNombre.text.toString().isNotEmpty() &&
                binding.editCorreo.text.toString().isNotEmpty() &&
                binding.editPassword.text.toString().isNotEmpty() &&
                binding.editRepetirPassword.text.toString().isNotEmpty() &&
                binding.editPassword.text.toString().equals(binding.editRepetirPassword.text.toString()) &&
                binding.radioGroupGenero.checkedRadioButtonId != -1
                //binding.editPassword.text.toString().length >= 6
            ) {

                val perfil: String = binding.spinnerPerfil.adapter.getItem(binding.spinnerPerfil.selectedItemPosition).toString()
                val radioSeleccionado: RadioButton = findViewById(binding.radioGroupGenero.checkedRadioButtonId)

                val usuario: Usuario = Usuario(
                    binding.editNombre.text.toString(),
                    binding.editCorreo.text.toString(),
                    binding.editPassword.text.toString(),
                    perfil,
                    radioSeleccionado.text.toString()
                )

                authFirebase.createUserWithEmailAndPassword(binding.editCorreo.text.toString(), binding.editPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                            intent.putExtra("usuario", usuario)

                            guardarUsuario(usuario)

                            startActivity(intent)
                            finish()
                        } else {
                            val error = it.exception
                            val mensaje = error?.message
                            Snackbar.make(
                                binding.root,
                                "Error al registrar el usuario: $mensaje",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }else {
                Snackbar.make(
                    binding.root,
                    "Error: datos introducidos incorrectos o incompletos",
                    Snackbar.LENGTH_LONG
                ).show()

            }
        }
    }

    private fun instancias(){
        authFirebase = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://dc-practica1-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    private fun guardarUsuario(usuario: Usuario){
        val referencia = firebaseDatabase.getReference("usuarios")
            .child(authFirebase.currentUser!!.uid)
            .setValue(usuario)

    }
}