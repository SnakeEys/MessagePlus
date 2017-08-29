package info.fox.messup.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import info.fox.messup.MainActivity
import info.fox.messup.R
import info.fox.messup.activity.ArchivedActivity
import info.fox.messup.activity.ContactsActivity
import info.fox.messup.activity.UnspecifiedActivity

/**
 *<p>
 * Created by user
 * on 2017/8/21.
 *</p>
 */
abstract class DrawerActivity : AbstractViewActivity(), NavigationView.OnNavigationItemSelectedListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        val toolbar = findWidget<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val nav = findWidget<NavigationView>(R.id.nav_view)
        showActivityLog("selected $item")
        if (nav.menu.findItem(item.itemId).isChecked) {
            Log.d(javaClass.simpleName, "$item has been checked")
            return true
        }
        when (item.itemId) {
            R.id.nav_conversations -> {
                if (this is MainActivity) {

                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            R.id.nav_contacts -> {
                startActivity(Intent(this, ContactsActivity::class.java))
            }
            R.id.nav_unspecified -> {
                startActivity(Intent(this, UnspecifiedActivity::class.java))
            }
            R.id.nav_archive -> {
                startActivity(Intent(this, ArchivedActivity::class.java))
            }
            R.id.nav_setting -> {
            }
            R.id.nav_share -> {
            }
        }
        val drawer = findWidget<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        val drawer = findWidget<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val intent = NavUtils.getParentActivityIntent(this)
            intent?.let {
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (NavUtils.shouldUpRecreateTask(this, intent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(intent)
                } else {
                    NavUtils.navigateUpTo(this, intent)
                }
            }?:
            super.onBackPressed()
        }
    }
}