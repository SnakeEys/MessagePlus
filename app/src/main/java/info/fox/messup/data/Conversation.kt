package info.fox.messup.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import info.fox.messup.R
import kotlin.collections.HashSet

/**
 * Created by snake
 * on 17/6/23.
 */
class Conversation private constructor(val context: Context) {

    var mThreadId: Long = 0             // The ID of this conversation.
        @Synchronized
        get

    var mRecipients: ContactList? = null    // The current set of recipients.
        @Synchronized
        set(value) {
            field = value
            mThreadId = 0
        }
        @Synchronized
        get

    var mDate: Long = 0                 // The last update time.
        @Synchronized
        get

    var mMessageCount: Int = 0          // Number of messages.
        @Synchronized
        get

    var mSnippet: String? = null            // Text of the most recent message.
        @Synchronized
        get

    private var mHasUnreadMessages: Boolean = false // True if there are unread messages.

    private var mHasAttachment: Boolean = false     // True if any message has an attachment.
        @Synchronized
        get

    private var mHasError: Boolean = false          // True if any message is in an error state.
        @Synchronized
        get

    var mIsChecked: Boolean = false         // True if user has selected the conversation for a multi-operation such as delete.
        @Synchronized
        set
        @Synchronized
        get

    private var sReadContentValues: ContentValues? = null
    private var sLoadingThreads: Boolean = false
    private var sDeletingThreads: Boolean = false
    private val sDeletingThreadsLock = Any()
    private var mMarkAsReadBlocked: Boolean = false
    private var mMarkAsReadWaiting: Boolean = false

    init {
        mRecipients = ContactList()
    }

    private constructor(context: Context, threadId: Long = 0, allowQuery: Boolean = false): this(context) {
        if (!loadFromThreadId(threadId, allowQuery)) {
            mRecipients = ContactList()
        }
    }

    private constructor(context: Context, cursor: Cursor, allowQuery: Boolean = false): this(context) {
        fillFromCursor(context, this, cursor, allowQuery)
    }

    private fun loadFromThreadId(threadId: Long, allowQuery: Boolean): Boolean {
        val cursor = context.contentResolver.query(sAllThreadsUri, ALL_THREADS_PROJECTION, "_id=$threadId", null, null)
            try {
                if (cursor.moveToFirst()) {
                    fillFromCursor(context, this, cursor, allowQuery)
                } else {
                    return false
                }
            } catch (e: Exception) {
                Log.w(TAG, e.message)
            } finally {
                cursor?.close()
            }
        return true
    }

    /**
     * Returns true if there are any unread messages in the conversation.
     */
    fun hasUnreadMessages(): Boolean {
        synchronized(this) {
            return mHasUnreadMessages
        }
    }

    private fun setHasUnreadMessages(flag: Boolean) {
        synchronized(this) {
            mHasUnreadMessages = flag
        }
    }



    companion object {

        val TAG: String = Conversation::class.java.simpleName

        val ALL_THREADS_PROJECTION =
                arrayOf(Telephony.Threads._ID,
                        "date",
                        "message_count",
                        "recipient_ids",
                        "snippet",
                        "snippet_cs",
                        "read",
                        "error",
                        "has_attachment")

        val sAllThreadsUri: Uri = Uri
                .parse("content://mms-sms/conversations")
                .buildUpon()
                .appendQueryParameter("simple", "true").build()

        val UNREAD_PROJECTION = arrayOf(Telephony.Threads._ID, Telephony.Threads.READ)

        private val UNREAD_SELECTION = "(read=0 OR seen=0)"

        private val SEEN_PROJECTION = arrayOf("seen")

        private val ID = 0
        private val DATE = 1
        private val MESSAGE_COUNT = 2
        private val RECIPIENT_IDS = 3
        private val SNIPPET = 4
        private val SNIPPET_CS = 5
        private val READ = 6
        private val ERROR = 7
        private val HAS_ATTACHMENT = 8

        private var sReadContentValues: ContentValues? = null
        private var sLoadingThreads: Boolean = false
        private var sDeletingThreads: Boolean = false
        private val sDeletingThreadsLock = Object()

        fun createNew(context: Context) = Conversation(context)

        /**
         * Find the conversation matching the provided thread ID.
         */
        operator fun get(context: Context, threadId: Long, allowQuery: Boolean): Conversation {
            var conv = Cache.get(threadId)
            if (conv != null)
                return conv

            conv = Conversation(context, threadId, allowQuery)
            try {
                Cache.put(conv)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Tried to add duplicate Conversation to Cache (from threadId): " + conv)
                if (!Cache.replace(conv)) {
                    Log.e(TAG, "get by threadId cache.replace failed on " + conv)
                }
            }
            return conv
        }

        operator fun get(context: Context, contactList: ContactList, allowQuery: Boolean): Conversation {
            if (contactList.size < 1) {
                return createNew(context)
            }

            val conv = Cache[contactList]
            if (conv != null) {
                return conv
            }

            return createNew(context)
        }

        fun getOrCreateThreadId(context: Context, list: ContactList): Long {
            val recipients = HashSet<String>(10)
            var cacheContact: Contact?
            for (c in list) {
                cacheContact = Contact.get(c.getNumber()?: "", false)
                if (cacheContact != null) {
                    recipients.add(cacheContact.getNumber()?: "")
                } else {
                    recipients.add(c.getNumber()?: "")
                }
            }
            synchronized(sDeletingThreadsLock){
                val now = System.currentTimeMillis()
                while (sDeletingThreads) {
                    try {
                        sDeletingThreadsLock.wait(30000)
                    } catch (e: Exception) {}
                    if (System.currentTimeMillis() - now > 29000) {
                        // Time out
                        sDeletingThreads = false
                        break
                    }
                }
                return Telephony.Threads.getOrCreateThreadId(context, recipients)
            }
        }

        fun from(context: Context, cursor: Cursor): Conversation {
            // First look in the cache for the Conversation and return that one. That way, all the
            // people that are looking at the cached copy will get updated when fillFromCursor() is
            // called with this cursor.
            val threadId = cursor.getLong(ID)
            if (threadId > 0) {
                val conv = Cache[threadId]
                if (conv != null) {
                    fillFromCursor(context, conv, cursor, false)   // update the existing conv in-place
                    return conv
                }
            }
            val conv = Conversation(context, cursor, false)
            try {
                Cache.put(conv)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Tried to add duplicate Conversation to Cache (from cursor): " + conv)
                if (!Cache.replace(conv)) {
                    Log.e(TAG, "Converations.from cache.replace failed on " + conv)
                }
            }

            return conv
        }

        private fun fillFromCursor(context: Context, conv: Conversation, cursor: Cursor, allowQuery: Boolean) {
            synchronized(conv) {
                conv.mThreadId = cursor.getLong(ID)

                conv.mDate = cursor.getLong(DATE)
                conv.mMessageCount = cursor.getInt(MESSAGE_COUNT)

                // Replace the snippet with a default value if i`t's empty.
                var snippet = cursor.getString(SNIPPET)
                /*
                    Some method inside MessageUtils is invisible
                    MessageUtils.cleanseMmsSubject(context, MessageUtils.extractEncStrFromCursor(cursor, SNIPPET, SNIPPET_CS))
                */
                if (TextUtils.isEmpty(snippet)) {
                    snippet = context.getString(R.string.no_subject_view)
                }
                conv.mSnippet = snippet

                conv.setHasUnreadMessages(cursor.getInt(READ) == 0)
                conv.mHasError = cursor.getInt(ERROR) != 0
                conv.mHasAttachment = cursor.getInt(HAS_ATTACHMENT) != 0
            }

            // Fill in as much of the conversation as we can before doing the slow stuff of looking
            // up the contacts associated with this conversation.
            val recipientIds = cursor.getString(RECIPIENT_IDS)
            val recipients = ContactList.getByIds(recipientIds, allowQuery)
            synchronized(conv) {
                conv.mRecipients = recipients
            }
        }
    }

    private class Cache private constructor(){

        private var mCache: HashSet<Conversation> = HashSet(10)

        companion object {
            internal val instance = Cache()

            internal operator fun get(threadId: Long): Conversation? {
                synchronized(instance) {
                    instance.mCache
                            .filter { it.mThreadId == threadId }
                            .forEach { return it }
                }
                return null
            }

            internal operator fun get(list: ContactList): Conversation? {
                synchronized(instance) {
                    instance.mCache
                            .filter { it.mRecipients == list }
                            .forEach { return it }
                }
                return null
            }

            internal fun put(conv: Conversation) {
                synchronized(instance) {
                    instance.mCache.add(conv)
                }
            }

            internal fun replace(conv: Conversation): Boolean {
                synchronized(instance) {
                    if (instance.mCache.contains(conv)) {
                        return false
                    }
                    instance.mCache.remove(conv)
                    instance.mCache.add(conv)
                    return true
                }
            }

            internal fun remove(threadId: Long) {
                synchronized(instance) {
                    instance.mCache
                            .filter { it.mThreadId == threadId }
                            .forEach {
                                instance.mCache.remove(it)
                                return
                            }
                }
            }

            internal fun keepOnly(threads: Set<Long>) {
                synchronized(instance) {
                    val iterator = instance.mCache.iterator()
                    while (iterator.hasNext()) {
                        val c = iterator.next()
                        if (!threads.contains(c.mThreadId)) {
                            iterator.remove()
                        }
                    }
                }
            }
        }
    }
}