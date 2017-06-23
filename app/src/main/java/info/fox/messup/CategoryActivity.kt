package info.fox.messup

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import info.fox.messup.contacts.TabContactFragment
import java.text.SimpleDateFormat
import android.provider.ContactsContract.PhoneLookup
import android.provider.ContactsContract
import android.net.Uri.withAppendedPath



class CategoryActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null


    private var mViewPager: ViewPager? = null
    private val fragments = arrayOf(TabContactFragment.newInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById(R.id.tabs) as TabLayout

        mViewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))


        val uri = Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build()
        Telephony.Threads.CONTENT_URI
        val handler = InnerHandler(contentResolver)
        handler.startQuery(1, null, uri, null, null, null, "date DESC")

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

    private inner class InnerHandler(cr: ContentResolver) : AsyncQueryHandler(cr) {
        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
            cursor?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm")
                while (cursor.moveToNext()) {
                    val _id = cursor.getLong(cursor.getColumnIndex(Telephony.Threads._ID))
                    val date = cursor.getLong(cursor.getColumnIndex(Telephony.Threads.DATE))
                    val count = cursor.getInt(cursor.getColumnIndex(Telephony.Threads.MESSAGE_COUNT))
                    val recipient = cursor.getString(cursor.getColumnIndex(Telephony.Threads.RECIPIENT_IDS))
                    val snippet = cursor.getString(cursor.getColumnIndex(Telephony.Threads.SNIPPET))
                    Log.d("conversation", "ID = $_id, DATE = ${sdf.format(date)}, COUNT = $count, RECIPIENT_IDS = $recipient, SNIPPET = $snippet")
                }
            }
        }
    }
}
