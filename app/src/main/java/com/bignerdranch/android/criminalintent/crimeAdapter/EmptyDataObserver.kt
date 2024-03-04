package com.bignerdranch.android.criminalintent.crimeAdapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

// https://medium.com/nerd-for-tech/empty-dataset-in-recyclerview-ad86833dd5c6
// не понимаю где писать код на добавление кнопки -- во вьюмодели?

class EmptyDataObserver constructor(rv: RecyclerView?, ev: View?): RecyclerView.AdapterDataObserver() {

    private var emptyView: View? = null
    private var recyclerView: RecyclerView? = null

    init {
        recyclerView = rv
        emptyView = ev
        checkIfEmpty()
    }

    private fun checkIfEmpty() {
        if (emptyView != null && recyclerView!!.adapter != null) {
            val emptyViewVisible = recyclerView!!.adapter!!.itemCount == 0
            emptyView!!.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView!!.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onChanged() {
        super.onChanged()
        checkIfEmpty()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
    }

}