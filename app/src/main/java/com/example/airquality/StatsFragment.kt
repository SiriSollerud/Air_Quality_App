package com.example.airquality

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class StatsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var viewOfLayout: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_stats, container, false)
        lateinit var descriptionArray: Array<Statistics>
        val lookupAqis = "https://api.nilu.no/lookup/aqis?component="
        val anbefaling: TextView = viewOfLayout.findViewById(R.id.anbefaling)
        val tittel: TextView = viewOfLayout.findViewById(R.id.valgtKommuneStatistikk)
        val totalVurdering: View = viewOfLayout.findViewById(R.id.totalVurdering)

        tittel.text = valgtKommune.kommuneNavn
        totalVurdering.setBackgroundColor(Color.parseColor("#BF" + valgtKommune.fargekode.toString()))
        xValues = ArrayList()
        barEntries = ArrayList()
        resultatAqis = mutableListOf()
        stasjonsNavn = mutableListOf()
        lateinit var stasjonerValgtKommune: Array<StasjonerValgtKommune>

        //Spinner
        val lookupStations = "https://api.nilu.no/lookup/stations?area="

        runBlocking {
            try {
                //Baserer anbefaling som vi gir ved valgt kommune på pm10 verdiene som er der
                descriptionArray = Gson().fromJson(Fuel.get(lookupAqis+"pm10").awaitString(), Array<Statistics>::class.java)

                for (x in descriptionArray) {
                    for (y in x.aqis!!) {
                        if (y.color.toString() == valgtKommune.fargekode) {
                            anbefaling.text = y.description + "\n" + y.advice
                            break
                        }
                    }
                }

                stasjonerValgtKommune = Gson().fromJson(Fuel.get(lookupStations+ valgtKommune.kommuneNavn?.toLowerCase()).awaitString(), Array<StasjonerValgtKommune>::class.java)

                stasjonsNavn.add("Høyeste målinger registrert i ${valgtKommune.kommuneNavn}")
                for (element in stasjonerValgtKommune) {
                    stasjonsNavn.add(element.station.toString())
                }

                for ((counter, value) in aqisList.withIndex()) {
                    xValues.add(value)
                    barEntries.add(BarEntry(0f, counter))

                    val response =  Gson().fromJson(Fuel.get(url+"areas=${valgtKommune.kommuneNavn}"+"&components=${value}").awaitString(), Array<Stasjon>::class.java)
                    for (data in response) {
                        val temp = Luftkvalitet(data.station, value, data.value.toString())
                        resultatAqis.add(temp)
                    }
                }

            } catch (e : Exception) {
                Log.d("Error fill aqis ", e.toString())
            }
        }

        val spinner: Spinner = viewOfLayout.findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, stasjonsNavn)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        return viewOfLayout
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //TODO: onNothingSelected()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val barChart: BarChart = viewOfLayout.findViewById(R.id.barchart)

        //Dersom "Generelt" velges tas all data tilgjengelig fra alle målestasjonene og fylles opp på gitte kommune
        if (stasjonsNavn[position] == "Høyeste målinger registrert i ${valgtKommune.kommuneNavn}") {

            for (data in resultatAqis) {
                for (i in aqisList.indices) {
                    if (data.verdinavn.toString() == aqisList[i]) {
                        if (barEntries[i].`val` <= data.verdi?.toFloat() ?: 0f) {
                            barEntries[i].`val` = data.verdi?.toFloat() ?: 0f
                            break
                        }
                    }
                }
            }
        } else {

            try {
                //Resetter alle målingene til 0
                for (i in aqisList.indices) {
                    barEntries[i].`val` = 0f
                }

                //Fyller opp bar chart med målingene fra valgt stasjon fra spinner
                for (data in resultatAqis) {

                    if (data.stasjon.toString() == (stasjonsNavn[position])) {
                        for (i in aqisList.indices) {
                            if (data.verdinavn.toString() == aqisList[i]) {
                                barEntries[i].`val` = data.verdi?.toFloat() ?: 0f
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("error spinner", e.toString())
            }
        }

        //Lager bar charten her
        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 12f

        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS)

        val data = BarData(xValues, barDataSet)
        data.setValueTextColor(Color.BLACK)
        barChart.data = data
        barChart.xAxis.setLabelsToSkip(0)
        barChart.animateX(100)
    }

    companion object {
        // Method to create a new instance of this fragment
        fun newInstance(): StatsFragment{
            return StatsFragment()
        }
    }
}