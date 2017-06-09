package info.fox.messup.listener

import android.view.View

/**
 * Created by snake
 * on 17/6/9.
 */
interface RecyclerItemClickListener {

    fun onRecyclerItemClicked(view: View, position: Int)
}