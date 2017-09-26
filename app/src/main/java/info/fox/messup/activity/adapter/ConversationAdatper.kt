package info.fox.messup.activity.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import info.fox.messup.R
import info.fox.messup.domain.Conversation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by
 * snake on 2017/8/31.
 */
class ConversationAdatper(context: Context) : RecyclerCursorAdapter<ConversationAdatper.ConversationHolder>(context) {


    init {
        setCursorAdapter(null, 0, R.layout.item_messages)
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ConversationHolder {

        assert(mCursorAdapter != null)
        return ConversationHolder(mCursorAdapter!!.newView(context, mCursorAdapter?.cursor, parent))
    }


    class ConversationHolder(itemView: View) : CursorViewHolder(itemView) {
        private val icon = itemView.findViewById(R.id.iv_icon) as ImageView
        private val person = itemView.findViewById(R.id.tv_person) as TextView
        private val body = itemView.findViewById(R.id.tv_body) as TextView
        private val count = itemView.findViewById(R.id.tv_count) as TextView
        private val date = itemView.findViewById(R.id.tv_date) as TextView

        private val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())

        override fun bindView(context: Context, cursor: Cursor) {
            val conversation = Conversation.from(context, cursor)
            conversation.getRecipients()
            date.text = sdf.format(conversation.getDate())
            body.text = conversation.getSnippet()
            count.text = conversation.getMessageCount().toString()
            person.text = conversation.getRecipients()?.formatnames(", ")
        }

    }

}