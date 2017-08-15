package info.fox.messup.data

import android.content.ContentValues
import android.provider.Telephony

/**
 * Created by snake
 * on 17/6/23.
 */
class Conversation {
    private constructor()

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

    private val ID = 0
    private val DATE = 1
    private val MESSAGE_COUNT = 2
    private val RECIPIENT_IDS = 3
    private val SNIPPET = 4
    private val SNIPPET_CS = 5
    private val READ = 6
    private val ERROR = 7
    private val HAS_ATTACHMENT = 8

    private var mThreadId: Long = 0             // The ID of this conversation.
    private var mRecipients: ContactList? = null    // The current set of recipients.
    private var mDate: Long = 0                 // The last update time.
    private var mMessageCount: Int = 0          // Number of messages.
    private var mSnippet: String? = null            // Text of the most recent message.
    private var mHasUnreadMessages: Boolean = false // True if there are unread messages.
    private var mHasAttachment: Boolean = false     // True if any message has an attachment.
    private var mHasError: Boolean = false          // True if any message is in an error state.
    private var mIsChecked: Boolean = false         // True if user has selected the conversation for a
    // multi-operation such as delete.

    private var sReadContentValues: ContentValues? = null
    private var sLoadingThreads: Boolean = false
    private var sDeletingThreads: Boolean = false
    private val sDeletingThreadsLock = Any()
    private var mMarkAsReadBlocked: Boolean = false
    private var mMarkAsReadWaiting: Boolean = false
}