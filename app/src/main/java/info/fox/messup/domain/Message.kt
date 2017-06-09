package info.fox.messup.domain

/**
 * Created by snake
 * on 17/6/9.
 */
data class Message(var _id: Int = 0, var thread_id: Int = 0, var person: String?, var address: String?, var date: String, var body: String? = "")