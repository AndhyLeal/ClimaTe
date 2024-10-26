package com.example.climate

import android.content.res.Resources.Theme
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.time.LocalTime


//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        var bt_search_city = findViewById<Button>(R.id.bt_search_city)
        bt_search_city.setOnClickListener() {
            searchDataWeather()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDayTime(): Boolean {
        val currentTime = LocalTime.now()
        val startOfDay = LocalTime.of(6, 0) // 06:00
        val endOfDay = LocalTime.of(18, 0) // 18:00
        return currentTime.isAfter(startOfDay) && currentTime.isBefore(endOfDay)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun searchDataWeather() {
        var tempValue = findViewById<TextView>(R.id.txt_temp)
        var climaValue = findViewById<TextView>(R.id.txt_clima)
        var city = findViewById<TextView>(R.id.txt_search_city)
        var cityNameVisible = findViewById<TextView>(R.id.txt_city)
        var imageViewPrevision = findViewById<ImageView>(R.id.img_load)
        var APIKEYWEATHER = ""
        Thread {
            //Busca de dados em processo paralelo
            var url =
                URL("https://api.openweathermap.org/data/2.5/weather?q=${city.text}&appid=$APIKEYWEATHER")

            var conn = url.openConnection() as HttpsURLConnection

            try {
                val data = conn.inputStream.bufferedReader().readText()
                val obj = JSONObject(data)

                //Pegando os valores
                val cityName = obj.getString("name")
                val tempCity = obj.getJSONObject("main").getDouble("temp")
                val weatherArray = obj.getJSONArray("weather")
                val tempClima = weatherArray.getJSONObject(0).getInt("id")
                val fromKelvinForCelsius = (tempCity - 273.15).toInt()
                runOnUiThread {
                    tempValue.setText("$fromKelvinForCelsius" + "Cº")
                    cityNameVisible.setText("$cityName")
                    when (tempClima) {
                        //Group 2xx: Thunderstorm

                        200 -> {
                            climaValue.text = getString(R.string.thunderstorm_light_rain)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }

                        201 -> {
                            climaValue.text = getString(R.string.thunderstorm_rain)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }

                        202 -> {
                            climaValue.text = getString(R.string.thunderstorm_heavy_rain)
                            imageViewPrevision.setImageResource(R.drawable.heavy_rain)
                        }

                        210 -> {
                            climaValue.text = getString(R.string.light_thunderstorm)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }

                        211 -> {
                            climaValue.text = getString(R.string.thunderstorm)
                            imageViewPrevision.setImageResource(R.drawable.heavy_rain)
                        }

                        212 -> {
                            climaValue.text = getString(R.string.heavy_thunderstorm)
                            imageViewPrevision.setImageResource(R.drawable.heavy_rain)
                        }

                        221 -> {
                            climaValue.text = getString(R.string.ragged_thunderstorm)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }

                        230 -> {
                            climaValue.text = getString(R.string.thunderstorm_light_drizzle)
                            imageViewPrevision.setImageResource(R.drawable.drizzle_rain)
                        }

                        231 -> {
                            climaValue.text = getString(R.string.thunderstorm_drizzle)
                            imageViewPrevision.setImageResource(R.drawable.drizzle_rain)
                        }

                        232 -> {
                            climaValue.text = getString(R.string.thunderstorm_heavy_drizzle)
                            imageViewPrevision.setImageResource(R.drawable.drizzle_rain)
                        }


                        //Group 3xx: Drizzle


                        //Group 5xx: Rain
                        500 -> {
                            climaValue.text = getString(R.string.light_rain)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }


                        501 -> {
                            climaValue.text = getString(R.string.moderate_rain)
                            imageViewPrevision.setImageResource(R.drawable.rainy_day_view)
                        }

                        502 -> {
                            climaValue.text = getString(R.string.heavy_intensity_rain)
                            imageViewPrevision.setImageResource((R.drawable.rainy_day_view))
                        }


                        //Group 7 e 8
                        711 -> {
                            climaValue.text = getString(R.string.smoke)
                            if (tempCity > 35) {
                                imageViewPrevision.setImageResource(R.drawable.hot)
                            }
                        }

                        800 -> {
                            climaValue.text = getString((R.string.clear_sky))
                            if (isDayTime() && tempCity > 35) {
                                imageViewPrevision.setImageResource(R.drawable.hot)
                            }
                            if(isDayTime() && tempCity < 35){
                                imageViewPrevision.setImageResource((R.drawable.sunny_view))
                            } else if(!isDayTime()){
                                imageViewPrevision.setImageResource(R.drawable.clear_sky)
                            }


                        }
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tempValue.text = "Erro"
                    cityNameVisible.text = "Cidade não localizada"
                }
            } finally {
                conn.disconnect()
            }
        }.start()
    }


}