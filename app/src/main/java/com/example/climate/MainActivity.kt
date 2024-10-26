package com.example.climate

import android.content.res.Resources.Theme
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Definindo as variáveis do app
        var bt_search_city = findViewById<Button>(R.id.bt_search_city)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bt_search_city.setOnClickListener() {
            searchDataWeather()
        }
    }

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
                val tempClima = weatherArray.getJSONObject(0).getString("main")
                val fromKelvinForCelsius = (tempCity - 273.15).toInt()
                runOnUiThread {
                    tempValue.setText("$fromKelvinForCelsius" + "Cº")
                    cityNameVisible.setText("$cityName")

                    if(tempClima.equals("Clouds")){
                        climaValue.setText("Parcialmente nublado")
                        imageViewPrevision.setImageResource(R.drawable.sun_clouds_view)

                    }


                }

            } catch (e: Exception){
                e.printStackTrace()
                runOnUiThread {
                    tempValue.text = "Erro"
                    city.text = "Cidade não localizada"
                }
            }

            finally {
                conn.disconnect()
            }
        }.start()
    }


}