package com.code.myweather.data.network.responses
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class ForecastResponse{
     @SerializedName("list")
     var list: List<Forecast>? = null

     class City {
         @SerializedName("sunset")
         var sunset = 0
         @SerializedName("sunrise")
         var sunrise = 0
         @SerializedName("timezone")
         var timezone = 0
         @SerializedName("population")
         var population = 0
         @SerializedName("country")
         var country: String? = null
         @SerializedName("coord")
         var coord: Coord? = null
         @SerializedName("name")
         var name: String? = null
         @SerializedName("id")
         var id = 0

     }

     class Coord {
         @SerializedName("lon")
         var lon = 0.0
         @SerializedName("lat")
         var lat = 0.0

     }

    class Forecast() : Parcelable {
        @SerializedName("weather")
        var weather: List<CityDailyResponse.Weather>? =
                null
        @SerializedName("clouds")
        var clouds: CityDailyResponse.Clouds? = null
        @SerializedName("sys")
        var sys: CityDailyResponse.Sys? = null
        @SerializedName("wind")
        var wind: CityDailyResponse.Wind? = null
        @SerializedName("dt")
        var dt = 0
        @SerializedName("main")
        var main: CityDailyResponse.Main? = null
        @SerializedName("coord")
        var coord: CityDailyResponse.Coord? = null
        @SerializedName("name")
        var name: String? = null
        @SerializedName("id")
        var id = 0

        constructor(parcel: Parcel) : this() {
            dt = parcel.readInt()
            name = parcel.readString()
            id = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(dt)
            parcel.writeString(name)
            parcel.writeInt(id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Forecast> {
            override fun createFromParcel(parcel: Parcel): Forecast {
                return Forecast(parcel)
            }

            override fun newArray(size: Int): Array<Forecast?> {
                return arrayOfNulls(size)
            }
        }

    }

     class Wind {
         @SerializedName("speed")
         var speed = 0.0

     }

     class Weather {
         @SerializedName("icon")
         var icon: String? = null
         @SerializedName("description")
         var description: String? = null
         @SerializedName("main")
         var main: String? = null
         @SerializedName("id")
         var id = 0

     }

     class Main {
         @SerializedName("temp_kf")
         var tempKf = 0.0
         @SerializedName("humidity")
         var humidity = 0
         @SerializedName("grnd_level")
         var grndLevel = 0
         @SerializedName("sea_level")
         var seaLevel = 0
         @SerializedName("pressure")
         var pressure = 0
         @SerializedName("temp_max")
         var tempMax = 0.0
         @SerializedName("temp_min")
         var tempMin = 0.0
         @SerializedName("feels_like")
         var feelsLike = 0.0
         @SerializedName("temp")
         var temp = 0.0

     }
 }