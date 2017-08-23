package info.fox.messup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}
