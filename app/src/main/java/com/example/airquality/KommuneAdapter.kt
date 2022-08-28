package com.example.airquality

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class KommuneAdapter(private val kommuneListe: MutableList<Adapter>, context: Context): RecyclerView.Adapter<KommuneAdapter.ViewHolder>(){
    private val context: Context? = context
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.kommune_name)
        var color: TextView = view.findViewById(R.id.kommune_name)
        var cardView: CardView = view.findViewById(R.id.cardview)
        var weather: TextView = view.findViewById(R.id.weather)
        var aqi: TextView = view.findViewById(R.id.AQIValue)
        var weatherValue: TextView = view.findViewById(R.id.weatherValue)
        var favorittB: CheckBox = view.findViewById(R.id.favorittBoks)
        var status: TextView = view.findViewById(R.id.status)
        var statusValue: TextView = view.findViewById(R.id.statusValue)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card, viewGroup, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.name.text = kommuneListe[pos].kommuneNavn
        viewHolder.color.setBackgroundColor(Color.parseColor("#BF${kommuneListe[pos].fargekode}"))
        kommuneListe[pos].ikonID?.let { viewHolder.weather.setBackgroundResource(it) }
        viewHolder.weatherValue.text = kommuneListe[pos].vaer.toString()
        viewHolder.favorittB.isChecked = false
        viewHolder.aqi.text = kommuneListe[pos].aqiVal?.toInt().toString()

        if (kommuneListe[pos].aqiVal?.toInt() ?: 0  < 60) {
            viewHolder.status.setBackgroundResource(R.drawable.ic_outline_sentiment_satisfied_24)
            viewHolder.statusValue.text = "Good"
        } else if ((kommuneListe[pos].aqiVal?.toInt() ?: 0  > 60) && (kommuneListe[pos].aqiVal?.toInt() ?: 0  < 120)) {
            viewHolder.status.setBackgroundResource(R.drawable.ic_outline_sentiment_neutral_24)
            viewHolder.statusValue.text = "Moderate"
        } else if ((kommuneListe[pos].aqiVal?.toInt() ?: 0  > 120) && (kommuneListe[pos].aqiVal?.toInt() ?: 0  < 400)) {
            viewHolder.status.setBackgroundResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24_bad)
            viewHolder.statusValue.text = "Bad"
        } else if (kommuneListe[pos].aqiVal?.toInt() ?: 0  > 400) {
            viewHolder.status.setBackgroundResource(R.drawable.ic_outline_sentiment_very_dissatisfied_24)
            viewHolder.statusValue.text = "Horrible"
        }

        for(i in favorittList) {
            if (kommuneListe[pos].kommuneNavn == i.kommuneNavn) {
                viewHolder.favorittB.isChecked = true
            }
        }

        viewHolder.cardView.setOnClickListener {
            valgtKommune = kommuneListe[pos]
            (context as AppCompatActivity).supportActionBar!!.title = "STATISTIKK"// ${(valgtKommune.kommuneNavn)?.toUpperCase()}"
            val transaction = context.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, StatsFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        viewHolder.favorittB.setOnClickListener {
            if (viewHolder.favorittB.isChecked) {
                favorittList.add(kommuneListe[pos])
            } else {
                favorittList.remove(kommuneListe[pos])
            }
        }
    }

    override fun getItemCount(): Int {
        return kommuneListe.size
    }
}