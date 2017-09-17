package info.fox.messup

import android.Manifest
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import info.fox.messup.activity.ConversationsFragment
import info.fox.messup.base.DrawerActivity

class MainActivity : DrawerActivity(), LoaderManager.LoaderCallbacks<Cursor> {

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
            start()
        } else {
            Snackbar.make(findWidget(R.id.fl_container), "Permission denied", Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok) { }
                    .show()
        }
    }

    private fun start() {
        // val nav = findWidget<NavigationView>(R.id.nav_view)
        // val title = nav.menu.findItem(R.id.nav_conversations).title.toString()
        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.fl_container, ConversationsFragment.newInstance(), TAG)
        t.commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val nav = findWidget<NavigationView>(R.id.nav_view)
        nav.setCheckedItem(R.id.nav_conversations)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            val contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            if (sms != PackageManager.PERMISSION_GRANTED || contacts != PackageManager.PERMISSION_GRANTED) {
            } else {
                loaderManager.initLoader(0, null, this)
            }
        } else {
            loaderManager.initLoader(0, null, this)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        data?.let {
            Log.d(TAG, "data: $data")
            val fragment = supportFragmentManager.findFragmentByTag(TAG)
            if (fragment is ConversationsFragment) {
                fragment.updateData(it)
            }
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> = when (id) {
        0 -> {
            val uri = Uri
                    .parse("content://mms-sms/conversations")
                    .buildUpon()
                    .appendQueryParameter("simple", "true").build()
            CursorLoader(this, uri, ALL_THREADS_PROJECTION, null, null, "date desc")
        }

        else -> throw UnsupportedOperationException("Unknown loader $id")
    }

    private val ALL_THREADS_PROJECTION =
            arrayOf(Telephony.Threads._ID, "date", "message_count", "recipient_ids", "snippet", "snippet_cs", "read", "error", "has_attachment")

}
