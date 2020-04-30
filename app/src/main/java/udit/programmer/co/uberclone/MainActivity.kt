package udit.programmer.co.uberclone

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_register.*
import kotlinx.android.synthetic.main.layout_register.email_et
import kotlinx.android.synthetic.main.layout_register.password_et
import kotlinx.android.synthetic.main.layout_signin.*
import udit.programmer.co.uberclone.Models.User
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyConfig.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : AppCompatActivity() {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val db by lazy {
        FirebaseDatabase.getInstance()
    }
    val users by lazy {
        db.getReference("Users")
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("Fonts/fonts_file.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        setContentView(R.layout.activity_main)

        btn_register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                showRegisterDialog()
            }
        })

        btn_signin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                showLoginDialog()
            }
        })

    }

    private fun showLoginDialog() {

        var login_layout = LayoutInflater.from(this)
            .inflate(R.layout.layout_signin, null, false)

        var loginDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage("Please use Email to Register.")
            .setTitle("LOGIN ")
            .setView(register_layout)
            .setPositiveButton("Login", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    dialogInterface?.dismiss()
                    if (email_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Email Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Password Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.toString().length < 6) {
                        Snackbar.make(root_layout, "Password is too Short", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (phone_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Phone Number", Snackbar.LENGTH_LONG).show()
                        return
                    }

                    auth.signInWithEmailAndPassword(
                        email_et_signin.toString(),
                        password_et_signin.toString()
                    ).addOnSuccessListener {
                        startActivity(Intent(this@MainActivity, Welcome::class.java))
                        finish()
                    }.addOnFailureListener {
                        Snackbar.make(
                            root_layout,
                            "FAILED : " + it.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            })
            .setNegativeButton("Cancel ", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, which: Int) {
                    dialogInterface?.dismiss()
                }

            })
            .show()
    }

    private fun showRegisterDialog() {

        var register_layout = LayoutInflater.from(this)
            .inflate(R.layout.layout_register, null, false)

        var registerDialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage("Please use Email to Register.")
            .setTitle("REGISTER ")
            .setView(register_layout)
            .setPositiveButton("REGISTER", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    dialogInterface?.dismiss()
                    if (name_et.toString().isEmpty()) {
                        Snackbar.make(root_layout, "Name Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (email_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Email Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Password Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.toString().length < 6) {
                        Snackbar.make(root_layout, "Password is too Short", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (phone_et.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Phone Number", Snackbar.LENGTH_LONG).show()
                        return
                    }
                    auth.createUserWithEmailAndPassword(email_et.toString(), password_et.toString())
                        .addOnSuccessListener {
                            lateinit var user: User
                            user.apply {
                                name = name_et.toString()
                                email = email_et.toString()
                                password = password_et.toString()
                                phone = phone_et.toString()
                            }
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
            .setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })
            .show()


    }


}