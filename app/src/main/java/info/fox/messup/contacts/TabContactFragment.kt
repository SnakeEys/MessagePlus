package info.fox.messup.contacts

import android.content.AsyncQueryHandler
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.fox.messup.R

/**
 * Created by snake
 * on 17/6/9.
 */
class TabContactFragment : Fragment(){



    companion object {
        fun newInstance(): TabContactFragment {
            val instance = TabContactFragment()
            return instance
        }
    }

    private val adapter = AdapterContact(arrayListOf<Any>())

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tab_contact, container, false) as SwipeRefreshLayout
        view.isEnabled = false
        val recycler = view.findViewById(R.id.rv_content) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()
        return view

    }


}