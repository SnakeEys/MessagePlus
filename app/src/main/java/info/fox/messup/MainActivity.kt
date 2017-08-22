package info.fox.messup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import info.fox.messup.activity.ArchivedActivity
import info.fox.messup.base.DrawerActivity

class MainActivity : DrawerActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDrawer()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(javaClass.simpleName, "action's: $item")
        when (item.itemId) {
            R.id.nav_conversations -> {
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
        closeDrawer()
        return true
    }

}
