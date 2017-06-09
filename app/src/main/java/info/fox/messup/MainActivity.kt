package info.fox.messup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val CODE_PERMISSION_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.btn_start).setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS), CODE_PERMISSION_READ_SMS)
            } else {
                start()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        start()
    }

    private fun start() {
        startActivity(Intent(this, CategoryActivity::class.java))
        // overridePendingTransition(0, 0)
        // finish()
    }

}
