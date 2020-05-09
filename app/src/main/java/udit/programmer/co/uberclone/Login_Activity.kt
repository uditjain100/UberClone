package udit.programmer.co.uberclone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login_.*
import kotlinx.android.synthetic.main.activity_login_.email_et_signin
import kotlinx.android.synthetic.main.activity_login_.password_et_signin
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_signin.*

class Login_Activity : AppCompatActivity() {
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val db by lazy {
        FirebaseDatabase.getInstance()
    }
    val users by lazy {
        db.getReference("Users")
    }
    lateinit var username: String
    lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)

        val sharedPreferences = getSharedPreferences("999", Context.MODE_PRIVATE)
        email_et_signin.setText(sharedPreferences.getString("username", "USERNAME"))
        password_et_signin.setText(sharedPreferences.getString("password", "PASSWORD"))

        login_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (email_et_signin.text.toString().isNotEmpty()) {
                    username = email_et_signin.text.toString()
                }
                if (password_et_signin.text.toString().isNotEmpty() || password_et_signin.text.toString().length < 6) {
                    password = password_et_signin.text.toString()
                }
                sharedPreferences.edit { putString("username", username) }
                sharedPreferences.edit { putString("password", password) }

                login_btn.isEnabled = false
                var loading_dialog =
                    SpotsDialog.Builder().setContext(this@Login_Activity).build()
                loading_dialog.show()

                auth.signInWithEmailAndPassword(
                    email_et_signin.text.toString(),
                    password_et_signin.text.toString()
                ).addOnSuccessListener {
                    loading_dialog.dismiss()
                    startActivity(Intent(this@Login_Activity, Welcome::class.java))
                    finish()
                }.addOnFailureListener {
                    loading_dialog.dismiss()
                    Snackbar.make(
                        login_activity_layout,
                        "FAILED : " + it.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                    login_btn.isEnabled = true
                }
            }

        })
    }
}
