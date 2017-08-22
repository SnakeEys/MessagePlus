package info.fox.messup.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import info.fox.messup.MainActivity
import info.fox.messup.R
import info.fox.messup.activity.ArchivedActivity

/**
 *<p>
 * Created by user
 * on 2017/8/21.
 *</p>
 */
abstract class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private val mWidgets = SparseArray<View>(10)
    protected var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        val toolbar = findWidget<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setNavigationItemSelectedListener(this)
        nav.setCheckedItem(R.id.nav_conversations)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle?.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                val intent = NavUtils.getParentActivityIntent(this)
                intent?.let {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    if (NavUtils.shouldUpRecreateTask(this, intent)) {
                        TaskStackBuilder.create(this).addNextIntentWithParentStack(intent)
                    } else {
                        NavUtils.navigateUpTo(this, intent)
                    }
                } ?: finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(javaClass.simpleName, "action's: $item")
        when (item.itemId) {
            R.id.nav_conversations -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.nav_contacts -> {
            }
            R.id.nav_unspecified -> {
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
        findWidget<NavigationView>(R.id.nav_view).setCheckedItem(0)
    }

    override fun onBackPressed() {
        val drawer = findWidget<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    protected fun setUpDrawer() {
        val drawer = findWidget<DrawerLayout>(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, findWidget<Toolbar>(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle!!)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T>findWidget(@IdRes id: Int): T {
        var view = mWidgets.get(id)
        if (view == null) {
            view = findViewById(id)
            mWidgets.put(id, view)
        }
        return view as T
    }
}