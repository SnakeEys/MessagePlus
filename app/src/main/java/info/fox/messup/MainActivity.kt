package info.fox.messup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import info.fox.messup.activity.ConversationsFragment
import info.fox.messup.base.DrawerActivity

class MainActivity : DrawerActivity() {

    private val CODE_PERMISSION_READ_SMS = 1
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drawer = findWidget<DrawerLayout>(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, findWidget<Toolbar>(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle!!)

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle?.syncState()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        var flag = true
        results
                .filter { it != PackageManager.PERMISSION_GRANTED }
                .forEach { flag = false }
        if (flag) {
            onResumeFragments()
        } else {
            Snackbar.make(findWidget(R.id.fl_container), "Permission denied", Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok) { }
                    .show()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        start()
    }

    private fun start() {
        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.fl_container, ConversationsFragment.newInstance(), TAG)
        t.commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setCheckedItem(R.id.nav_conversations)
    }
}
