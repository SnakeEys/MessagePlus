package info.fox.messup.data

import android.net.Uri
import android.os.Parcelable
import android.text.TextUtils

/**
 * Created by user on 2017/7/4.
 */
class ContactList : ArrayList<Contact>() {

    fun getPresenceResId(): Int {
        return if (size != 1) 0 else get(0).getPresenceResId()
    }

    fun formatNames(separator: String): String{
        val names = arrayOfNulls<String>(size)
        var i = 0
        for (c in this) {
            names[i++] = c.getName()
        }
        return TextUtils.join(separator, names)
    }

    fun formatNamesAndNumbers(separator: String): String {
        val nans = arrayOfNulls<String>(size)
        var i = 0
        for (c in this) {
            nans[i++] = c.getNameAndNumber()
        }
        return TextUtils.join(separator, nans)
    }

    fun serialize() = TextUtils.join(";", getNumbers())

    fun getNumbers() = getNumbers(false)

    fun getNumbers(scrubForMmsAddress: Boolean): Array<String?> {
        val numbers = mutableListOf<String?>()
        var number: String?
        this.forEach {
            number = it.getNumber()
            if (!TextUtils.isEmpty(number) && !numbers.contains(number)) {
                numbers.add(number)
            }
        }
        return numbers.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        try {
            val other = other as ContactList
            // If they're different sizes, the contact
            // set is obviously different.
            if (size != other.size) {
                return false
            }

            // Make sure all the individual contacts are the same.

            return this.any { other.contains(it) }
        } catch (e: ClassCastException) {
            return false
        }

    }

    companion object {
        fun getByNumbers(numbers: Iterable<String>, canBlock: Boolean): ContactList {
            val list = ContactList()
            numbers.filter { !TextUtils.isEmpty(it) }
                    .forEach { list.add(Contact.get(it, canBlock)) }
            return list
        }

        fun getByNumbers(semiSepNumbers: String,
                         canBlock: Boolean,
                         replaceNumber: Boolean): ContactList {
            val list = ContactList()
            semiSepNumbers.split(";")
                    .filter { !TextUtils.isEmpty(it) }
                    .forEach {
                        val contact = Contact.get(it, canBlock)
                        if (replaceNumber) {
                            contact.setNumber(number = it)
                        }
                        list.add(contact)
                    }
            return list
        }

        fun blockingGetByUris(uris: Array<Parcelable>?): ContactList {
            val list = ContactList()
            if (uris != null && uris.isNotEmpty()) {
                uris.forEach {
                    if (it is Uri && TextUtils.equals("tel", it.scheme)) {
                        val contact = Contact.get(it.schemeSpecificPart, true)
                        list.add(contact)
                    }
                }

                val contacts = Contact.getPhoneByUris(uris)
                contacts?.let { list.addAll(it)  }
            }
            return list
        }

        /**
         * Returns a ContactList for the corresponding recipient ids passed in. This method will
         * create the contact if it doesn't exist, and would inject the recipient id into the contact.
         */
        fun getByIds(spaceSepIds: String, canBlock: Boolean): ContactList {
            val list = ContactList()
            for (entry in RecipientIdCache.getAddresses(spaceSepIds)) {
                if (!TextUtils.isEmpty(entry.number)) {
                    val contact = Contact.get(entry.number, canBlock)
                    contact.mRecipientId = entry.id
                    list.add(contact)
                }
            }
            return list
        }
    }
}