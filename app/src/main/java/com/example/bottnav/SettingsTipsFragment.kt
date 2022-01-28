package com.example.bottnav

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

// 설정 - 팁 모아보기 프래그먼트
class SettingsTipsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_tips, container, false)

        val dbManager = DBManager(view.context)

        val settings_tips_list = view.findViewById<ListView>(R.id.settings_tips_list)

        settings_tips_list.adapter = MyAdapter(view.context)

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsTipsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsTipsFragment().apply {
            }
    }

    // 어댑터
    private class MyAdapter(context: Context) : BaseAdapter() {

        val myContext: Context = context
        val dbManager = DBManager(context)

        val list = dbManager.getTips("tip")

        override fun getCount(): Int {
            if (list != null) {
                return list.size
            }

            return 0
        }

        override fun getItem(position: Int): String {
            val selected = list!!.get(position)
            return selected
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(myContext)
            val layout = layoutInflater.inflate(R.layout.settings_tips_list, viewGroup, false)

            val list_text = layout.findViewById<TextView>(R.id.list_textView)
            list_text.text = list!!.get(position)

            return layout
        }

    }
}