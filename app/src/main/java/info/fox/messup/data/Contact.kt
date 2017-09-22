package info.fox.messup.data

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Parcelable
import android.text.TextUtils
import java.util.*

/**
 * Created by user on 2017/7/4.
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

    var mRecipientId: Long = 0       // used to find the Recipient cache entry
        @Synchronized
        set(value) {

        }
    private var mLabel: String? = null
    private var mPersonId: Long = 0
    private var mPresenceResId: Int = 0      // TODO: make this a state instead of a res ID
    private var mPresenceText: String? = null
    private var mAvatar: BitmapDrawable? = null
    private var mAvatarData: ByteArray? = null
    private var mIsStale: Boolean = false
    private var mQueryPending: Boolean = false
    private var mIsMe: Boolean = false         // true if this contact is me!
    private var mSendToVoicemail: Boolean = false   // true if this contact should not put up notification
    private var mPeopleReferenceUri: Uri? = null

    @Synchronized fun getPresenceResId() = 0

    @Synchronized fun getName() = if (TextUtils.isEmpty(mName)) mNumber else mName

    @Synchronized fun getNameAndNumber() = mNameAndNumber

    @Synchronized fun getNumber() = mNumber

    @Synchronized fun setNumber(number: String) {
//        if (!Telephony.Mms.isEmailAddress(number)) {
//            mNumber = PhoneNumberUtils.formatNumber(number, mNumberE164,
//                    MmsApp.getApplication().getCurrentCountryIso())
//        } else {
//            mNumber = number
//        }
//        notSynchronizedUpdateNameAndNumber()
//        mNumberIsModified = true
    }


    companion object {
        fun get(number: String, canBlock: Boolean): Contact? {
            return Contact()
        }

        fun getPhoneByUris(uris: Array<Parcelable>): List<Contact>? {
            return Collections.emptyList()
        }
    }
}