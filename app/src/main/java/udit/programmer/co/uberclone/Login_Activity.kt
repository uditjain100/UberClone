package udit.programmer.co.uberclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)

        login_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

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
