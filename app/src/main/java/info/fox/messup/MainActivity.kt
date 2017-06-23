package info.fox.messup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    val CODE_PERMISSION_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.btn_start).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val sms = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS)
                val contacts = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_CONTACTS)
                if (sms != PackageManager.PERMISSION_GRANTED || contacts != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS), CODE_PERMISSION_READ_SMS)
                } else {
                    start()
                }
            } else {
                start()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        var flag = true
        results
                .filter { it != PackageManager.PERMISSION_GRANTED }
                .forEach { flag = false; return@forEach }
        if (flag) {
            start()
        } else {
            Snackbar.make(findViewById(R.id.btn_start), "Permission denied", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun start() {
        startActivity(Intent(this, CategoryActivity::class.java))
        // overridePendingTransition(0, 0)
        // finish()
    }

}
