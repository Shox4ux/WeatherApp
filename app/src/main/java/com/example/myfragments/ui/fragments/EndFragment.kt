package com.example.myfragments.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myfragments.R
import com.example.myfragments.core.base.BaseFragment
import com.example.myfragments.core.util.ViewModelProviderFactory
import com.example.myfragments.core.util.connection.ConnectionLiveData
import com.example.myfragments.databinding.FragmentEndBinding
import com.example.myfragments.ui.activity.MainVM
import com.example.myweatherapp.core.models.oneDay.ApiResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class EndFragment : BaseFragment() {
    private lateinit var vievBinding: FragmentEndBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     var num:Int =0

    @Inject
    lateinit var viewModelProvider: ViewModelProviderFactory
    val mainVM: MainVM by viewModels { viewModelProvider }

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val API_KEY = "6d285f96857cddb24759a48dce263198"

    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vievBinding = FragmentEndBinding.inflate(inflater,container,false)
            return vievBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkNetWork()
        callRetrofit()
    }


    private fun checkNetWork(){
        connectionLiveData.observe(requireActivity()){
            if (it){
                if (num<1){
                    if (num<1){
                        num+=1
                        getCurrentLocation()
                    }
                }

                val anim = AnimationUtils.loadAnimation(requireContext(),R.anim.disappear_anim)
                vievBinding.netWarning.animation = anim
                Handler(Looper.myLooper()!!).postDelayed({
                    vievBinding.netWarning.visibility = View.GONE
                },2000)

            }
            else{
                vievBinding.netWarning.visibility = View.VISIBLE
                val anim = AnimationUtils.loadAnimation(requireContext(),R.anim.appear_anim)
                vievBinding.netWarning.animation = anim
            }
        }
    }



    private fun callRetrofit(){
        vievBinding.searchCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mainVM.callCityWeatherData(vievBinding.searchCity.text.toString(), API_KEY)
                val view = requireActivity().currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    vievBinding.searchCity.clearFocus()

                }
                true
            } else false

        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }



    private fun getCurrentLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result

                    if (location == null) {
                        checkNetWork()
                    } else {
                        fetchCurrentLocation(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }

                }

            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                checkPermissions()
            }

        } else {
            //request permission anew
            requestPermissions()
        }

    }

    private fun fetchCurrentLocation(latitude: String, longitude: String) {
        vievBinding.pbLoading.visibility = View.VISIBLE
        mainVM.callCurrentWeatherData(latitude,longitude, API_KEY)
        mainVM.requestLiveData.observe(requireActivity()) {
            setDataOnView(it!!)
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "NewApi")
    private fun setDataOnView(body: ApiResponse) {
        val sdf = SimpleDateFormat("dd/MM//yyyy hh:mm")
        val currentData = sdf.format(Date())
        vievBinding.tvDateAndTime.text = currentData

        vievBinding.tvDayMaxTemp.text =
            "Day " + body.main?.temp_max?.let { kelvinToCelcius(it) } + "°C"
        vievBinding.tvDayMinTemp.text =
            "Night " + body.main?.temp_min?.let { kelvinToCelcius(it) } + "°C"
        vievBinding.tvTemp.text = "" +body.main?.temp?.let { kelvinToCelcius(it) } + "°C"
        vievBinding.tvFeelsLike.text =
            "Feels like " + body.main?.feels_like?.let { kelvinToCelcius(it) } + "°C"
        vievBinding.tvWeatherType.text = body.weather?.get(0)?.main as String
        vievBinding.tvSunrise.text =
            body.sys?.sunrise?.let { timeStampToLocalDate(it.toLong()) }
        vievBinding.tvSunset.text =
            body.sys?.sunset?.let { timeStampToLocalDate(it.toLong()) }
        vievBinding.tvPressure.text = body.main?.pressure?.toString()
        vievBinding.tvHumidity.text = body.main?.humidity?.toString() + " %"
        vievBinding.tvWindSpeed.text = body.wind?.speed.toString() + " m/s"

        vievBinding.tvTempFarenhite.text =
            "" + ((body.main?.temp?.let { kelvinToCelcius(it) })!!.times(1.8).plus(32).roundToInt()) +"°"

        vievBinding.searchCity.setText(body.name)

        updateUI(body.weather[0].id)


    }

    private fun updateUI(id: Int?) {

        when (id) {
            in 200..232 -> {
               requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.thunderstorm)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.thunderstorm))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.thunderstorm))
                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.thunderstrom_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.thunderstrom_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.thunderstrom_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.thunderstrom_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.thunderstrom)
            }
            in 300..321 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.drizzle)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.drizzle))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.drizzle))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drizzle_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drizzle_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drizzle_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.drizzle_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.drizzle)
            }
            in 500..531 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.rain)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.rain))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.rain))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.rainy_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.rainy_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.rainy_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.rainy_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.raining)
            }
            in 600..622 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.snow)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.snow))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.snow))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.snow_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.snow_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.snow_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.snow_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.snow)
            }
            in 700..781 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.atmosphere)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.atmosphere))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.atmosphere))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.mist_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.mist_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.mist_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.mist_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.mist)
            }
            in 799..800 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.clear)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.clear))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.clear))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.clear_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.clear_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.clear_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.clear_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.clear)
            }
            in 801..804 -> {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().window.statusBarColor = resources.getColor(R.color.clouds)
                vievBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.clouds))
                vievBinding.netWarning.setBackgroundColor(resources.getColor(R.color.clouds))

                vievBinding.rlSumLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cloud_bg)
                vievBinding.llMainBgAbove.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cloud_bg)
                vievBinding.llMainBgBelow.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cloud_bg)
                vievBinding.ivWeatherBg.setImageResource(R.drawable.cloud_bg)
                vievBinding.ivWeatherIcon.setImageResource(R.drawable.clouds)
            }
        }
        vievBinding.pbLoading.visibility = View.GONE
        vievBinding.rlMainLayout.visibility = View.VISIBLE


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timeStampToLocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()
    }

    private fun kelvinToCelcius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permissions Granted", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Permissions Denied", Toast.LENGTH_SHORT).show()
        }

    }





}