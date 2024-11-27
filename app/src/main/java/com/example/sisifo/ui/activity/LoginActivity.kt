package com.example.sisifo.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sisifo.databinding.ActivityLoginBinding
import com.example.sisifo.model.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authFirebase: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase


    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        instancias()

        usuario = intent.getSerializableExtra("usuario") as Usuario?



        if (usuario != null) {
            binding.editUser.setText(usuario!!.correo)
            binding.editPassword.setText(usuario!!.password)
        }

        binding.buttonSignup.setOnClickListener(this)
        binding.buttonLogin.setOnClickListener(this)

    }

    private fun instancias() {
        authFirebase = FirebaseAuth.getInstance();
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.buttonSignup.id -> {
                val intent: Intent = Intent(applicationContext, SingupActivity::class.java)
                startActivity(intent)
                finish()
            }

            binding.buttonLogin.id -> {

                if (binding.editPassword.text.toString().isNotEmpty() &&
                    binding.editUser.text.toString().isNotEmpty()
                ) {
                    authFirebase.signInWithEmailAndPassword(
                        binding.editUser.text.toString(),
                        binding.editPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent: Intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("nombre", usuario?.nombre.toString())
                            intent.putExtra("perfil", usuario?.perfil?.toString() ?: "?")
                            intent.putExtra("uid", authFirebase.currentUser!!.uid)
                            startActivity(intent)

                        } else {
                            Snackbar.make(
                                binding.root,
                                "El usuario no existe, ¿Quieres crear la cuenta?",
                                Snackbar.LENGTH_LONG
                            ).setAction("OK"){
                                val intent = Intent(applicationContext, SingupActivity::class.java)
                                startActivity(intent)
                            }.show()
                        }
                    }
                } else {
                    Snackbar.make(binding.root,
                        "Fallo de auth, revise los datos ingresados",
                        Snackbar.LENGTH_SHORT).show()
                }

            }
        }
    }
}