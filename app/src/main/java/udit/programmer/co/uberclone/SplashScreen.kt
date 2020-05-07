package udit.programmer.co.uberclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)
        val splash_screen = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(4000)
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        splash_screen.start()
    }
}
