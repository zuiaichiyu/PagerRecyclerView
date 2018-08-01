package com.alex.pagerRecyclerView

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list: MutableList<String> = mutableListOf(
                "11111", "222222", "3333",
                "4444", "5555", "66666",
                "77777", "88888", "9999")


        val adapter = RecyclerAdapter()
        adapter.setList(list)
        adapter.setPageWidth(rv_list.pageWidth)

        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_list.layoutManager = manager
        rv_list.adapter = adapter

    }

}
