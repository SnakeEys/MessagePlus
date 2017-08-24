package info.fox.messup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import info.fox.messup.base.DrawerActivity
import info.fox.messup.fragments.PlaceholderFragment

class MainActivity : DrawerActivity() {

    val CODE_PERMISSION_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDrawer()
        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setCheckedItem(R.id.nav_conversations)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            val contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            if (sms != PackageManager.PERMISSION_GRANTED || contacts != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS), CODE_PERMISSION_READ_SMS)
            } else {
                start()
            }
        } else {
            start()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        var flag = true
        results
                .filter { it != PackageManager.PERMISSION_GRANTED }
                .forEach { flag = false }
        if (flag) {
            start()
        } else {
            Snackbar.make(findWidget(R.id.fl_container), "Permission denied", Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok) { }
                    .show()
        }
    }

    fun start() {
        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.fl_container, PlaceholderFragment.newInstance("Main Page"), TAG)
        t.commit()
    }
}
