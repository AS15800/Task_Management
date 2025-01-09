package com.voidentertainment.taskmanagementapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class TaskListFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper

    //Weather information
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        weatherTextView = view.findViewById(R.id.weatherTextView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocationAndWeather()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)

        databaseHelper = DatabaseHelper (requireContext())

        //Load Tasks from the database
        val tasks = databaseHelper.getAllData().entries.mapIndexed { index, entry ->
            Task(id = index + 1, title = entry.key, items = entry.value)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = DataAdapter(tasks, requireActivity())


        val imageView = view.findViewById<ImageView>(R.id.addNewTasksButton)
        imageView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, TaskFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun getCurrentLocationAndWeather(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let{
                fetchWeatherData(it.latitude, it.longitude)
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(latitude, longitude, "PLEASE INPUT YOUR OPENWEATHER API KEY HERE")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        val temp = it.main.temp
                        val description = it.weather[0].description.capitalize()
                        weatherTextView.text = "Temp: $temp°C\n$description"
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherTextView.text = "Failed to get weather"
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndWeather()
        }
    }
}
