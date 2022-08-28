package com.example.airquality

//API
data class Areas(val zone: String?, val municipality: String?, val area: String?, val description: String?)

//Api
data class Aqis(val index: Number?, val fromValue: Number?, val toValue: Number?, val color: String?, val text: String?, val shortDescription: String?, val description: String?, val advice: String?)
data class Statistics(val component: String?, val unit: String?, val aqis: List<Aqis>?)

data class Stasjon(
    val id: Number?,
    val zone: String?,
    val municipality: String?,
    val area: String?,
    val station: String?,
    val eoi: String?,
    val type: String?,
    val component: String?,
    val fromTime: String?,
    val toTime: String?,
    val value: Number?,
    val unit: String?,
    val latitude: Number?,
    val longitude: Number?,
    val timestep: Number?,
    val index: Number?,
    val color: String?,
    val isValid: Boolean?)

data class StasjonerValgtKommune(val id: Number?, val zone: String?, val municipality: String?, val area: String?, val station: String?, val type: String?, val eoi: String?, val latitude: Number?, val longitude: Number?, val owner: String?, val status: String?, val description: String?, val firstMeasurment: String?, val lastMeasurment: String?, val components: String?, val isVisible: Boolean?)

//Main
data class Adapter(val kommuneNavn: String?, val fargekode: String?, val vaer: String?, val beskrivelse: String?, val ikonID: Int?, val aqiVal: Number?, var fav: Boolean? = false)
//data class Statistics(val text: String?, val shortDescription: String?, val description: String?, val advice: String?)
data class Luftkvalitet(val stasjon: String?, val verdinavn: String?, val verdi: String?)
data class BaseLuftkvalitet(val Liste: MutableList<Luftkvalitet>)

//Proxy Weather API
data class Base(val type: String?, val geometry: Geometry?, val properties: Properties?)

data class Data(val instant: Instant?, val next_1_hours: Next_1_hours?)

data class Details(val air_temperature: Number?, val precipitation_rate: Number?, val relative_humidity: Number?, val wind_from_direction: Number?, val wind_speed: Number?, val wind_speed_of_gust: Number?)

data class Geometry(val type: String?, val coordinates: List<Number>?)

data class Instant(val details: Details?)

data class Meta(val updated_at: String?, val units: Units?, val radar_coverage: String?)

data class Next_1_hours(val summary: Summary?, val details: Details?)

data class Properties(val meta: Meta?, val timeseries: List<TimeSeries>?)

data class Summary(val symbol_code: String?)

data class TimeSeries(val time: String?, val data: Data?)

data class Units(val air_temperature: String?, val precipitation_amount: String?, val precipitation_rate: String?, val relative_humidity: String?, val wind_from_direction: String?, val wind_speed: String?, val wind_speed_of_gust: String?)
