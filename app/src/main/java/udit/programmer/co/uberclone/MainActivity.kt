package udit.programmer.co.uberclone

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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
                startActivity(Intent(this@MainActivity, Register_Activity::class.java))
                //showRegisterDialog()
            }
        })

        btn_signin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                startActivity(Intent(this@MainActivity, Login_Activity::class.java))
                // showLoginDialog()
            }
        })

    }

    private fun showLoginDialog() {

        var login_layout = LayoutInflater.from(this)
            .inflate(R.layout.layout_signin, null, false)

        var loginDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage("Please use your Credentials")
            .setTitle("LOGIN ")
            .setView(login_layout)
            .setPositiveButton("Login", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    dialogInterface?.dismiss()
                    if (email_et.text.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Email Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.text.toString().isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Password Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.text.toString().length < 6) {
                        Snackbar.make(root_layout, "Password is too Short", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    auth.signInWithEmailAndPassword(
                        email_et_signin.text.toString(),
                        password_et_signin.text.toString()
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
            .setCancelable(false)
            .setMessage("Please use Email to Register.")
            .setTitle("REGISTER ")
            .setView(register_layout)
            .setPositiveButton("REGISTER", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    dialogInterface?.dismiss()
                    if (name_et?.text.isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Name Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (email_et?.text.isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Email Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et?.text.isNullOrEmpty()) {
                        Snackbar.make(root_layout, "Password Field is Empty", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (password_et.text!!.length < 6) {
                        Snackbar.make(root_layout, "Password is too Short", Snackbar.LENGTH_LONG)
                            .show()
                        return
                    }
                    if (phone_et?.text.isNullOrEmpty()) {
                        Snackbar.make(
                            root_layout,
                            "Phone Number field is empty",
                            Snackbar.LENGTH_LONG
                        ).show()
                        return
                    }
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
            .setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })
            .show()


    }


}
