package info.fox.messup.contacts

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
import android.widget.ImageView
import android.widget.TextView
import info.fox.messup.R
import info.fox.messup.base.RecyclerViewCursorAdapter
import info.fox.messup.listener.RecyclerItemClickListener
import info.fox.messup.listener.RecyclerItemLongClickListener

/**
 * Created by snake
 * on 17/6/28.
 */
class ContactAdapter(context: Context) : RecyclerViewCursorAdapter<ContactAdapter.Holder>(context, null, FLAG_REGISTER_CONTENT_OBSERVER) {

    private var mClickListener: RecyclerItemClickListener? = null
    private var mLongClickListener: RecyclerItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(parent?.context)?.inflate(R.layout.item_messages, parent, false)
        return Holder(view!!)
    }

    override fun onBindViewHolder(holder: Holder, cursor: Cursor?) {
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.findViewById(R.id.iv_icon) as ImageView
        val person = itemView.findViewById(R.id.tv_person) as TextView
        val body = itemView.findViewById(R.id.tv_body) as TextView
        val count = itemView.findViewById(R.id.tv_count) as TextView
        val date = itemView.findViewById(R.id.tv_date) as TextView

        init {
            itemView.setOnClickListener { mClickListener?.onRecyclerItemClicked(it, adapterPosition) }
            itemView.setOnLongClickListener {
                mLongClickListener?.onRecyclerItemLongClicked(it, adapterPosition)
                true
            }
        }

    }
}