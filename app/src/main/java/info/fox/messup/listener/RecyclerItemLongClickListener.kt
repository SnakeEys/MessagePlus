package info.fox.messup.listener

import android.view.View

/**
 * Created by snake
 * on 17/6/9.
 */
interface RecyclerItemLongClickListener {

    fun onRecyclerItemLongClicked(view: View, position: Int)
}