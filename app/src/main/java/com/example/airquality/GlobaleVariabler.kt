package com.example.airquality

import com.github.mikephil.charting.data.BarEntry

//Main
var adapterList = mutableSetOf<Adapter>()
val favorittList = mutableListOf<Adapter>()

//API
lateinit var valgtKommune: Adapter

//statistikk
var stasjonsNavn = mutableListOf<String>()
val aqisList = mutableListOf("CO", "NO", "NO2", "NOx", "O3", "PM1", "PM10", "PM2.5", "SO2")
const val url = "https://api.nilu.no/obs/utd?"
var resultatAqis = mutableListOf<Luftkvalitet>()
var xValues = ArrayList<String>()
var barEntries = ArrayList<BarEntry>()