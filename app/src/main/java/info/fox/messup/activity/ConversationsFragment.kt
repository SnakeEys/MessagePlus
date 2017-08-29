package info.fox.messup.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.fox.messup.R
import info.fox.messup.contacts.ContactAdapter

/**
 * Created by
 * snake on 2017/8/29.
 */
class ConversationsFragment : Fragment() {


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_conversations, container, false)
        val recycler = view.findViewById(R.id.rv_content) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        val adapter = ContactAdapter(activity)
        recycler.adapter = adapter
        return view
    }




    companion object {

        fun newInstance(): ConversationsFragment {
            return ConversationsFragment()
        }
    }
}