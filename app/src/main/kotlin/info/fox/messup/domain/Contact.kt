package info.fox.messup.domain

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.TextUtils
import java.nio.CharBuffer

/**
 * Created by
 * snake on 2017/9/25.
 */
class Contact {

    private var mContactMethodId: Long = 0   // Id in phone or email Uri returned by provider of current
    // Contact, -1 is invalid. e.g. contact method id is 20 when
    // current contact has phone content://.../phones/20.
    private var mContactMethodType: Int = 0
    private var mNumber: String? = null
    private var mNumberE164: String? = null
    private var mName: String? = null
    private var mNameAndNumber: String? = null   // for display, e.g. Fred Flintstone <670-782-1123>
    private var mNumberIsModified: Boolean = false // true if the number is modified

    private var mRecipientId: Long = 0       // used to find the Recipient cache entry
    private var mLabel: String? = null
    private var mPersonId: Long = 0
    private var mPresenceResId: Int = 0      // TODO: make this a state instead of a res ID
    private var mPresenceText: String? = null
    private var mAvatar: BitmapDrawable? = null
    private var mAvatarData: ByteArray? = null
    private var mIsStale: Boolean = false
    private var mQueryPending: Boolean = false
    private val mIsMe: Boolean = false              // true if this contact is me!
    private var mSendToVoicemail: Boolean = false   // true if this contact should not put up notification
    private var mPeopleReferenceUri: Uri? = null


    @Synchronized fun setRecipientId(id: Long) {
        mRecipientId = id
    }

    companion object {

        private var sContactCache: ContactsCache? = null

        fun init(context: Context) {
            if (sContactCache != null) {

            }
            sContactCache = ContactsCache(context)
            RecipientIdCache.init(context)
        }

        operator fun get(number: String, canBlock: Boolean = false): Contact =
                sContactCache!!.get(number, canBlock = canBlock)
    }



    private class ContactsCache(val context: Context) {

        val mContactsHash = HashMap<String, ArrayList<Contact>>()



        fun get(number: String, isMe: Boolean = false, canBlock: Boolean): Contact {
            var number = number
            if (TextUtils.isEmpty(number)) {
                number = ""
            }
            val contact = internalGet(number, isMe)
            return contact
        }

        // Invert and truncate to five characters the phoneNumber so that we
        // can use it as the key in a hashtable.  We keep a mapping of this
        // key to a list of all contacts which have the same key.
        fun key(phoneNumber: String, keyBuffer: CharBuffer): String {
            keyBuffer.clear()
            keyBuffer.mark()

            var position = phoneNumber.length
            var resultCount = 0
            while (--position >= 0) {
                val c = phoneNumber[position]
                if (c.isDigit()) {
                    keyBuffer.put(c)
                    if (++resultCount == STATIC_KEY_BUFFER_MAXIMUM_LENGTH) {
                        break
                    }
                }
            }
            keyBuffer.reset()
            if (resultCount > 0) {
                return keyBuffer.toString()
            } else {
                return phoneNumber
            }

        }

        fun internalGet(numberOrEmail: String, isMe: Boolean): Contact {

            synchronized(this@ContactsCache) {

                val isNotRegularPhoneNumber = isMe // Still need to check if is email address
                val key = if (isNotRegularPhoneNumber) numberOrEmail else key(numberOrEmail, sStaticKeyBuffer)

                val candidates = mContactsHash[key]
                if (candidates != null) {
                    candidates.forEach {
                       if (isNotRegularPhoneNumber) {
                           if (numberOrEmail.equals(it.mNumber)) {
                               return it
                           }
                       } else {
                           if () {
                               return it
                           }
                       }
                    }
                } else {

                }

            }
            return Contact()
        }

        companion object {
            // Reuse this so we don't have to allocate each time we go through this
            // "get" function.
            internal val STATIC_KEY_BUFFER_MAXIMUM_LENGTH = 5
            internal var sStaticKeyBuffer = CharBuffer.allocate(STATIC_KEY_BUFFER_MAXIMUM_LENGTH)
        }
    }


}