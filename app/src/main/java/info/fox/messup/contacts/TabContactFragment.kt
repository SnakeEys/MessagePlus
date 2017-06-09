package info.fox.messup.contacts

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.fox.messup.R
import info.fox.messup.domain.Message
import java.text.SimpleDateFormat
import java.util.*

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

    private val messages = arrayListOf<Message>()
    private val adapter = AdapterContact(messages)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val uri = Uri.parse("content://sms/")
        val column = arrayOf("_id", "thread_id", "body", "address", "person", "date")

        val cr = context?.contentResolver
        val cursor = cr?.query(uri, column, null, null, "date")
        cursor?.let {
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault())
            while (cursor.moveToNext()) {
                val _id = cursor.getInt(cursor.getColumnIndex("_id"))
                val thread_id = cursor.getInt(cursor.getColumnIndex("thread_id"))
                val address = cursor.getString(cursor.getColumnIndex("address"))//手机号
                val person = cursor.getString(cursor.getColumnIndex("person"))//联系人姓名列表
                val body = cursor.getString(cursor.getColumnIndex("body"))
                val date = cursor.getLong(cursor.getColumnIndex("date"))
                val format = sdf.format(date)
                Log.d("Message", "$_id, $thread_id, $person, $address, $body, $format")
                val msg = Message(_id, thread_id, person, address, format, body)
                messages.add(msg)
            }
        }

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