package info.fox.messup.domain

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import info.fox.messup.R

/**
 * Created by
 * snake on 2017/9/23.
 */
class Conversation(val context: Context) {

    // The thread ID of this conversation.  Can be zero in the case of a
    // new conversation where the recipient set is changing as the user
    // types and we have not hit the database yet to create a thread.
    private var mThreadId: Long = 0

    private var mRecipients: ContactList? = null    // The current set of recipients.
    private var mDate: Long = 0                 // The last update time.
    private var mMessageCount: Int = 0          // Number of messages.
    private var mSnippet: String? = null            // Text of the most recent message.
    private var mHasUnreadMessages: Boolean = false // True if there are unread messages.
    private var mHasAttachment: Boolean = false     // True if any message has an attachment.
    private var mHasError: Boolean = false          // True if any message is in an error state.
    private var mIsChecked: Boolean = false         // True if user has selected the conversation for a
    // multi-operation such as delete.

    init {

    }

    private constructor(context: Context, threadId: Long = 0, allowQuery: Boolean = false): this(context) {
        if (!loadFromThreadId(threadId, allowQuery)) {
//            mRecipients = ContactList()
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
        val TAG = Conversation::class.java.simpleName

        private val ALL_THREADS_PROJECTION =
                arrayOf(Telephony.Threads._ID,
                        "date",
                        "message_count",
                        "recipient_ids",
                        "snippet",
                        "snippet_cs",
                        "read",
                        "error",
                        "has_attachment")

        private val sAllThreadsUri: Uri = Uri
                .parse("content://mms-sms/conversations")
                .buildUpon()
                .appendQueryParameter("simple", "true").build()

        private val ID              = 0
        private val DATE            = 1
        private val MESSAGE_COUNT   = 2
        private val RECIPIENT_IDS   = 3
        private val SNIPPET         = 4
        private val SNIPPET_CS      = 5
        private val READ            = 6
        private val ERROR           = 7
        private val HAS_ATTACHMENT  = 8

        private var sDeletingThreads: Boolean = false
        private val sDeletingThreadsLock = Object()


        fun createNew(context: Context): Conversation = Conversation(context)

        fun startQueryForAll(handler: AsyncQueryHandler, token: Int) {
            handler.cancelOperation(token)
            startQuery(handler, token, null)
        }

        /**
         * Start a query for in the database on the specified AsyncQueryHandler with the specified
         * "where" clause.
         *
         * @param handler An AsyncQueryHandler that will receive onQueryComplete
         *                upon completion of the query
         * @param token   The token that will be passed to onQueryComplete
         * @param selection   A where clause (can be null) to select particular conv items.
         */
        fun startQuery(handler: AsyncQueryHandler, token: Int, selection: String?) {
            handler.cancelOperation(token)
            handler.startQuery(token, null, sAllThreadsUri, ALL_THREADS_PROJECTION, selection, null, "date DESC")
        }

        fun from(context: Context, cursor: Cursor): Conversation {
            /*val threadId = cursor.getLong(ID)
            if (threadId > 0) {
                val conv = Cache[threadId]
                if (conv != null) {
                    fillFromCursor(context, conv, cursor, false)   // update the existing conv in-place
                    return conv
                }
            }*/

            val conv = Conversation(context, cursor, false)
            /*try {
                Cache.put(conv)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Tried to add duplicate Conversation to Cache (from cursor): " + conv)
                if (!Cache.replace(conv)) {
                    Log.e(TAG, "Converations.from cache.replace failed on " + conv)
                }
            }*/

            return conv
        }

        fun fillFromCursor(context: Context, conv: Conversation, cursor: Cursor, allowQuery: Boolean) {
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

    open class ConversationQueryHandler(cr: ContentResolver) : AsyncQueryHandler(cr) {
        var deleteToken: Int = 0

        override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
            if (token == deleteToken) {
                synchronized(sDeletingThreadsLock) {
                    sDeletingThreads = false
                    sDeletingThreadsLock.notifyAll()
                }
            }
        }
    }
}