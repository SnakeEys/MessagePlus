package info.fox.messup.domain

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import java.nio.CharBuffer

/**
 * Created by
 * snake on 2017/9/25.
 */
class Contact() {

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
    private var mIsMe: Boolean = false              // true if this contact is me!
    private var mSendToVoicemail: Boolean = false   // true if this contact should not put up notification
    private var mPeopleReferenceUri: Uri? = null


    constructor(number: String): this() {
        init(number, "")
    }

    constructor(isMe: Boolean): this() {
        init(SELF_ITEM_KEY, "")
        mIsMe = isMe
    }

    private fun init(number: String, name: String) {
        mContactMethodId = CONTACT_METHOD_ID_UNKNOWN.toLong()
        mName = name
        setNumber(number)
        mNumberIsModified = false
        mLabel = ""
        mPersonId = 0
        mPresenceResId = 0
        mIsStale = true
        mSendToVoicemail = false
    }

    @Synchronized fun setRecipientId(id: Long) {
        mRecipientId = id
    }

    @Synchronized fun setNumber(number: String) {
        // If the number is not an email address, format is necessary.
        // PhoneNumberUtils.formatNumber(number)
        // mNumber = PhoneNumberUtils.formatNumber(number, mNumberE164, MmsApp.getApplication().getCurrentCountryIso())
        // else
        mNumber = number

        mNumberIsModified = true
    }

    @Synchronized fun getName() = if (TextUtils.isEmpty(mName)) mNumber else mName

    private fun notSynchronizedUpdateNameAndNumber() {
        mNameAndNumber = formatNameAndNumber(mName!!, mNumber!!, mNumberE164!!)
    }

    companion object {

        val CONTACT_METHOD_TYPE_UNKNOWN = 0
        val CONTACT_METHOD_TYPE_PHONE = 1
        val CONTACT_METHOD_TYPE_EMAIL = 2
        val CONTACT_METHOD_TYPE_SELF = 3       // the "Me" or profile contact
        val TEL_SCHEME = "tel"
        val CONTENT_SCHEME = "content"
        private val CONTACT_METHOD_ID_UNKNOWN = -1
        private val SELF_ITEM_KEY = "Self_Item_Key"


        private var sContactCache: ContactsCache? = null

        fun init(context: Context) {
            if (sContactCache != null) {

            }
            sContactCache = ContactsCache(context)
            RecipientIdCache.init(context)
        }

        fun formatNameAndNumber(name: String, number: String, format: String): String {
            // Format like this: Mike Cleron <(650) 555-1234>
            //                   Erick Tseng <(650) 555-1212>
            //                   Tutankhamun <tutank1341@gmail.com>
            //                   (408) 555-1289

            var formattedNumber = number
            /*if (!Telephony.Mms.isEmailAddress(number)) {
                formattedNumber = PhoneNumberUtils.formatNumber(number, numberE164,
                        MessApp.getApplication().getCurrentCountryIso())
            }*/

            return if (!TextUtils.isEmpty(name) && name != number) {
                "$name <$formattedNumber>"
            } else {
                formattedNumber
            }
        }

        operator fun get(number: String, canBlock: Boolean = false): Contact =
                sContactCache!!.get(number, canBlock = canBlock)
    }



    private class ContactsCache(val context: Context) {

        val mContactsHash = HashMap<String, ArrayList<Contact>>()



        operator fun get(number: String, isMe: Boolean = false, canBlock: Boolean): Contact {
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

                var candidates = mContactsHash[key]
                if (candidates != null) {
                    candidates.forEach {
                       if (isNotRegularPhoneNumber) {
                           if (numberOrEmail == it.mNumber) {
                               return it
                           }
                       } else {
                           if (PhoneNumberUtils.compare(numberOrEmail, it.mNumber)) {
                               return it
                           }
                       }
                    }
                } else {
                    candidates = ArrayList()
                    mContactsHash.put(key, candidates)
                }
                val c = if (isMe)
                    Contact(true)
                else
                    Contact(numberOrEmail)
                candidates.add(c)

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