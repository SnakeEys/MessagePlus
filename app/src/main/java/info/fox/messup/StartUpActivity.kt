package info.fox.messup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
