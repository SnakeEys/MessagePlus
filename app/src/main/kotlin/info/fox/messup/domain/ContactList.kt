package info.fox.messup.domain

import android.os.Parcelable
import android.text.TextUtils

/**
 * Created by user on 2017/7/4.
 */
class ContactList : ArrayList<Contact>() {




    fun formatnames(separator: String): String {
        val names = arrayOfNulls<String>(size)
        var i = 0
        forEach {
            names[i++] = it.getName()
        }
        return TextUtils.join(separator, names)
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

        /**
         * Returns a ContactList for the corresponding recipient ids passed in. This method will
         * create the contact if it doesn't exist, and would inject the recipient id into the contact.
         */
        fun getByIds(spaceSepIds: String, canBlock: Boolean): ContactList {
            val list = ContactList()
            for (entry in RecipientIdCache.getAddresses(spaceSepIds)) {
                if (!TextUtils.isEmpty(entry.number)) {
                    val contact = Contact[entry.number, canBlock]
                    contact.setRecipientId(entry.id)
                    list.add(contact)
                }
            }
            return list
        }
    }
}