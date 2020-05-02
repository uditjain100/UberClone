package udit.programmer.co.uberclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.root_layout
import kotlinx.android.synthetic.main.activity_register_.*
import udit.programmer.co.uberclone.Models.User

class Register_Activity : AppCompatActivity() {

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
        setContentView(R.layout.activity_register_)

        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                auth.createUserWithEmailAndPassword(
                    email_et.text.toString(),
                    password_et.text.toString()
                )
                    .addOnSuccessListener {
                        var user = User(
                            name_et.text.toString(),
                            email_et.text.toString(),
                            password_et.text.toString(),
                            phone_et.text.toString()
                        )
                        users.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(user)
                            .addOnSuccessListener {
                                Snackbar.make(
                                    root_layout,
                                    "Registered Successfully",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(
                                    root_layout,
                                    "FAILED : " + it.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                    }
                    .addOnFailureListener {
                        Snackbar.make(
                            root_layout,
                            "FAILED : " + it.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }

        })
    }
}
