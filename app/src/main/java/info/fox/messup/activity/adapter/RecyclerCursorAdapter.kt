package info.fox.messup.activity.adapter

import android.content.Context
import android.database.Cursor
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter

/**
 * Created by
 * snake on 2017/8/31.
 */
abstract class RecyclerCursorAdapter<VH : RecyclerCursorAdapter.CursorViewHolder>(val context: Context) : RecyclerView.Adapter<VH>() {

    private var holder: VH? = null

    protected var mCursorAdapter: CursorAdapter? = null

    open fun setCursorAdapter(cursor: Cursor?, flags: Int, @LayoutRes resource: Int) {
        mCursorAdapter = InnerAdapter(context, cursor, flags, resource)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        mCursorAdapter?.cursor?.moveToPosition(position)
        bindHolder(holder)
        mCursorAdapter?.bindView(null, context, mCursorAdapter?.cursor)
    }

    override fun getItemCount(): Int = mCursorAdapter?.count?: 0

    private fun bindHolder(holder: VH) {
        this.holder = holder
    }

    fun swapCursor(cursor: Cursor) {
        mCursorAdapter?.swapCursor(cursor)
        notifyDataSetChanged()
    }

    private inner class InnerAdapter(context: Context, cursor: Cursor?, flags: Int, @LayoutRes val resource: Int) : CursorAdapter(context, cursor, flags) {
        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return LayoutInflater.from(context).inflate(resource, parent, false)
        }

        override fun bindView(view: View?, context: Context, cursor: Cursor) {
            holder?.bindView(context, cursor)
        }
    }


    abstract class CursorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bindView(context: Context, cursor: Cursor)
    }
}