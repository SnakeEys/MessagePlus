package info.fox.messup.contacts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import info.fox.messup.R
import info.fox.messup.listener.RecyclerItemClickListener
import info.fox.messup.listener.RecyclerItemLongClickListener

/**
 * Created by snake
 * on 17/6/9.
 */
internal class AdapterContact(private var data: List<*>) : RecyclerView.Adapter<AdapterContact.ViewHolder>() {

    private var mClickListener: RecyclerItemClickListener? = null

    private var mLongClickListener: RecyclerItemLongClickListener? = null

    fun getData(): List<*> {
        return data
    }

    fun setClickListener(listener: RecyclerItemClickListener) {
        mClickListener = listener
    }

    fun setLongClickListener(listener: RecyclerItemLongClickListener) {
        mLongClickListener = listener
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_messages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = data[position]

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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