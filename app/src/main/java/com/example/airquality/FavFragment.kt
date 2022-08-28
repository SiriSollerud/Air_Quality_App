package com.example.airquality

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavFragment : Fragment() {
    private lateinit var viewOfLayout: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_fav, container, false)
        val textView: TextView = viewOfLayout.findViewById(R.id.check)
        val newFavList = mutableListOf<Adapter>()
        newFavList.addAll(favorittList)
        val adapter = KommuneAdapter(newFavList, this.context!!)
        val recyclerView: RecyclerView = viewOfLayout.findViewById(R.id.recycle)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if(newFavList.size > 0) {
            textView.text = ""
        }
        return viewOfLayout
    }

    companion object {
        // Method to create a new instance of this fragment
        fun newInstance(): FavFragment{
            return FavFragment()
        }
    }
}