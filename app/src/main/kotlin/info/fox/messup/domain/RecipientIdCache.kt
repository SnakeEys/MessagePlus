package info.fox.messup.domain

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log

/**
 * Created by
 * snake on 2017/9/21.
 */
class RecipientIdCache(val context: Context) {

    private val mCache: MutableMap<Long, String>

    init {
        mCache = HashMap()
    }



    class Entry(var id: Long, var number: String)

    companion object {

        private val ID      = 0
        private val NUMBER  = 1

        private lateinit var sInstance: RecipientIdCache

        private val sAllCanonical = Uri.parse("content://mms-sms/canonical-addresses")

        private val sSingleCanonicalAddressUri = Uri.parse("content://mms-sms/canonical-address")

        fun init(context: Context) {
            sInstance = RecipientIdCache(context)
        }

        @SuppressLint("Recycle")
        fun fill() {
            val context = sInstance.context
            val c: Cursor
            try {
                c = context.contentResolver.query(sAllCanonical, null, null, null, null)
            } catch (e: SQLiteException) {
                e.printStackTrace()
                // The origin code us SqliteWrapper to query, but this class cannot be used here.
                // Source code checks memory status if it is now at a very low level when exceptions occurred.
                return
            }
            c.use {
                synchronized(sInstance) {
                    sInstance.mCache.clear()
                    while (c.moveToNext()) {
                        val id = c.getLong(ID)
                        val number = c.getString(NUMBER)
                        sInstance.mCache.put(id, number)
                    }
                }
            }
        }

        fun getAddresses(spaceSetIds: String): List<Entry> {
            val numbers = mutableListOf<Entry>()
            val ids = spaceSetIds.split(" ")
            ids.forEach {
                val longId: Long
                try {
                    longId = it.toLong()
                } catch (e: NumberFormatException) {
                    return@forEach
                }
                var number = sInstance.mCache[longId]
                if (number == null) {
                    fill()
                    number = sInstance.mCache[longId]
                }
                number?.let {
                    numbers.add(Entry(longId, it))
                }?: Log.w("RecipientIdCache", "RecipientId: $longId has empty number")
            }
            return numbers
        }

    }
}