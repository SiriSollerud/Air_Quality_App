package com.example.airquality

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private lateinit var viewOfLayout: View
    lateinit var stasjonArray: Array<Stasjon>
    lateinit var areasArray: Array<Areas>
    //NILU:
    private val niluStasjonsdataPM10 = "https://api.nilu.no/aq/utd?&components=pm10"
    private val niluLookupAreas = "https://api.nilu.no/lookup/areas"
    private val apiProxyIN2000 = "https://in2000-apiproxy.ifi.uio.no/weatherapi/nowcast/2.0/complete?"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_search, container, false)
        // Create air quality data
        val gson = Gson()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create recyclerview and adapter
                stasjonArray = gson.fromJson(Fuel.get(niluStasjonsdataPM10).awaitString(), Array<Stasjon>::class.java)
                areasArray = gson.fromJson(Fuel.get(niluLookupAreas).awaitString(), Array<Areas>::class.java)
                var vaer = ""
                var vaerBeskrivelse = ""
                var pm10Max = 0f
                var stasjonMedHoyestePM10Maaling: Stasjon
                var c = 0
                val adapterArray: Array<Adapter?> = arrayOfNulls(areasArray.size)
                for (i in areasArray.indices) {
                    for (dataStasjon in stasjonArray) {
                        if ((areasArray[i].area == dataStasjon.area)) {
                            try {
                                if (c == 0) {
                                    val tempRespone = gson.fromJson(Fuel.get(apiProxyIN2000 + "lat=${dataStasjon.latitude.toString()}" + "&lon=${dataStasjon.longitude.toString()}").awaitString(), Base::class.java)
                                    val list = tempRespone.properties?.timeseries
                                    vaer = list?.get(0)?.data?.instant?.details?.air_temperature.toString() + "°C"
                                    vaerBeskrivelse = list?.get(0)?.data?.next_1_hours?.summary?.symbol_code.toString()
                                    c++
                                }
                            } catch (e: Exception) {
                                vaer = "Ingen data"
                                vaerBeskrivelse = "Ingen data"
                            }
                            stasjonMedHoyestePM10Maaling = dataStasjon
                            if (dataStasjon.value?.toFloat() ?: 0f > pm10Max) {
                                pm10Max = dataStasjon.value?.toFloat() ?: 0f
                                stasjonMedHoyestePM10Maaling = dataStasjon
                            }
                            val res: Resources = resources
                            val mDrawableName = vaerBeskrivelse
                            val resID: Int = res.getIdentifier(mDrawableName, "raw", activity?.packageName)
                            val temp = Adapter(areasArray[i].area, stasjonMedHoyestePM10Maaling.color, vaer, vaerBeskrivelse, resID, stasjonMedHoyestePM10Maaling.value, false)
                            adapterArray[i] = temp
                        }
                    }
                    c=0
                }
                adapterList = mutableSetOf()
                for (x in adapterArray) {
                    if (x != null) {
                        adapterList.add(x)
                    }
                }
                updateRecycler(adapterList)
            } catch (e: Exception) {
                val toast: Toast = Toast.makeText(context, "Error: $e", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
        // Create searchview and listener
        val searchView: SearchView = viewOfLayout.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // When query is submitted
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchCounty(query!!, adapterList)
                val keyboard = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
                return true
            }
            // When query is changed
            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrEmpty()){
                    updateRecycler(adapterList)
                }
                return true
            }
        })
        return viewOfLayout
    }
    // Search function
    private fun searchCounty(query: String, set: MutableSet<Adapter>) {
        val searchList = mutableSetOf<Adapter>()
        // Adds counties that start with the query to the searchList
        for (i in set) {
            if (i.kommuneNavn!!.startsWith(query, ignoreCase = true)) {
                searchList.add(i)
            }
        }
        // Adds counties that contains the query and is not already in the list to the searchList
        for (i in set) {
            if (i.kommuneNavn!!.contains(query, ignoreCase = true) && !searchList.contains(i)) {
                searchList.add(i)
            }
        }

        updateRecycler(searchList)
        // If query is not in list
        if(searchList.size <= 0){
            val toast: Toast = Toast.makeText(this.context, "Kommune med navn eller som inneholder '$query' finnes ikke", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }
    // Update recyclerview
    private fun updateRecycler(set: MutableSet<Adapter>){
        activity!!.runOnUiThread {
            val tmp = mutableListOf<Adapter>()
            tmp.addAll(set)
            val adapter = KommuneAdapter(tmp, this.context!!)
            val recyclerView: RecyclerView = activity!!.findViewById(R.id.recycle)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
    }

    companion object {
        // Method to create a new instance of this fragment
        fun newInstance(): SearchFragment{
            return SearchFragment()
        }
    }
}