package info.fox.messup.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import info.fox.messup.R
import info.fox.messup.base.DrawerActivity
import info.fox.messup.fragments.PlaceholderFragment

/**
 *<p>
 * Created by user
 * on 2017/8/22.
 *</p>
 */
class ArchivedActivity : DrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setCheckedItem(R.id.nav_archive)
        val title = nav.menu.findItem(R.id.nav_archive).title.toString()

        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.fl_container, PlaceholderFragment.newInstance(title), TAG)
        t.commit()
    }
}