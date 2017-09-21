package info.fox.messup.data

/**
 * Created by
 * snake on 2017/9/21.
 */
class RecipientIdCache {


    class Entry(var id: Long, var number: String)

    companion object {

        fun getAddresses(spaceSetIds: String): List<Entry> {
            val numbers = listOf<Entry>()
            return numbers
        }

    }
}