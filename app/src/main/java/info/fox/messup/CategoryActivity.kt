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
import java.util.*


class CategoryActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null


    private var mViewPager: ViewPager? = null
    private var handler: InnerHandler? = null
    private val fragments = arrayOf(TabContactFragment.newInstance())

    val ALL_THREADS_PROJECTION =
            arrayOf(Telephony.Threads._ID, "date", "message_count", "recipient_ids", "snippet", "snippet_cs", "read", "error", "has_attachment")

    private val ID = 0
    private val DATE = 1
    private val MESSAGE_COUNT = 2
    private val RECIPIENT_IDS = 3
    private val SNIPPET = 4
    private val SNIPPET_CS = 5
    private val READ = 6
    private val ERROR = 7
    private val HAS_ATTACHMENT = 8

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

        val uri = Uri
                .parse("content://mms-sms/conversations")
                .buildUpon()
                .appendQueryParameter("simple", "true").build()
        handler = InnerHandler(contentResolver)
        handler?.startQuery(1, null, uri, ALL_THREADS_PROJECTION, null, null, "date DESC")

    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks(null)
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


    open class ConversationQueryHandler(cr: ContentResolver) : AsyncQueryHandler(cr) {
        private var mDeleteToken: Int = 0

        fun setDeleteToken(token: Int) {
            mDeleteToken = token
        }

        /**
         * Always call this super method from your overridden onDeleteComplete function.
         */
        override fun onDeleteComplete(token: Int, cookie: Any, result: Int) {

        }
    }

    private class InnerHandler(cr: ContentResolver) : ConversationQueryHandler(cr) {
        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
            cursor?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
                while (cursor.moveToNext()) {
                    val _id = cursor.getLong(cursor.getColumnIndex(Telephony.Threads._ID))
                    val date = cursor.getLong(cursor.getColumnIndex(Telephony.Threads.DATE))
                    val count = cursor.getInt(cursor.getColumnIndex(Telephony.Threads.MESSAGE_COUNT))
                    val recipient = cursor.getString(cursor.getColumnIndex(Telephony.Threads.RECIPIENT_IDS))
                    val snippet = cursor.getString(cursor.getColumnIndex(Telephony.Threads.SNIPPET))
                    Log.d("conversation", "ID = $_id, DATE = ${sdf.format(date)}, COUNT = $count, RECIPIENT_IDS = $recipient, SNIPPET = $snippet")
                }
                cursor.close()
            }
        }
    }
}
