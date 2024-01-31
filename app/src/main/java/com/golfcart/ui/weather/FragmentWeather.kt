package com.golfcart.ui.weather

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentWeatherBinding
import com.golfcart.databinding.ItemDaysBinding
import com.golfcart.databinding.ItemTimeBinding
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.model.weather.WeatherResponse
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.peeking_layout.PeekingLinearLayoutManager
import com.golfcart.utils.village_constant.VillageConstants.PNG
import com.golfcart.utils.village_constant.VillageConstants.WEATHER_APP
import com.golfcart.utils.village_constant.VillageConstants.WEATHER_IMAGE_URL
import com.golfcart.utils.village_constant.VillageConstants.WEATHER_UNITS
import com.golfcart.utils.village_constant.VillageConstants.WEATHER_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import java.text.DecimalFormat

class FragmentWeather : VillageFragment<FragmentWeatherBinding, WeatherViewModel>() {

    var dailyAdapter: RecyclerViewGenericAdapter<WeatherResponse.WeatherList, ItemTimeBinding>? =
        null
    var dailyList = ArrayList<WeatherResponse.WeatherList>()

    var weeklyAdapter: RecyclerViewGenericAdapter<WeatherResponse.WeatherList, ItemDaysBinding>? =
        null
    var weeklyList = ArrayList<WeatherResponse.WeatherList>()

    val decimalFormat = DecimalFormat("##")

    override fun showBottomBar(): Boolean? {
        return true
    }

    override fun getCurrentFragment(): Fragment? {
        return this@FragmentWeather
    }

    override fun showToolbar(): Boolean? {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration? {
        return ToolbarConfiguration(
            false, true, getString(R.string.weather), false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.weather
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_weather
    }

    override fun getViewModel(): WeatherViewModel {
        val vm: WeatherViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setdailyAdapter()
        setWeekAdapter()
        fetchLocation()

    }

    private fun getWeatherInfo(
        latitude: Double?,
        longitude: Double?,
        units: String?,
        appid: String?
    ) {
        lifecycleScope.launch {
            val url = WEATHER_URL +
                    "lat=${latitude}&" +
                    "lon=${longitude}&" +
                    "units=${units}&" +
                    "appid=${appid}"

            getViewModel().getWeatherInfo(getContainerActivity(), url)
            getViewModel().weatherLiveData.observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiResponse.Status.LOADING -> {
                        showLoader()
                    }

                    ApiResponse.Status.SUCCESS -> {
                        updateUi(it)
                        hideLoader()
                        removeObserver(getViewModel().weatherMutableLiveData)
                    }

                    ApiResponse.Status.ERROR -> {
                        hideLoader()
                        handleError(it.error)
                        removeObserver(getViewModel().weatherMutableLiveData)
                    }
                }
            }
        }
    }

    private fun updateUi(weatherResponse: ApiResponse<WeatherResponse>) {
        getViewDataBinding()!!.apply {
            val response = weatherResponse.data
            tvTodayTemperature.text = decimalFormat.format(response?.list?.get(0)?.main?.temp).toString() + "" +"\u2109 "
            tvCityName.text = "${response?.city!!.name}"
            tvWeatherType.text = "${response.list?.get(0)?.weather?.get(0)?.main}"

            dailyList.clear()
            dailyList.addAll(response.list as ArrayList<WeatherResponse.WeatherList>)
            dailyAdapter!!.notifyDataSetChanged()

            filterWeeklyList(response.list as ArrayList<WeatherResponse.WeatherList>)
        }
    }

    private fun filterWeeklyList(fullWeeklyList: java.util.ArrayList<WeatherResponse.WeatherList>) {
        weeklyList.clear()
        fullWeeklyList.forEachIndexed { index, weatherList ->
            if (index == 0) {
                weeklyList.add(weatherList)
            } else {
                val predate: String = fullWeeklyList.get(index - 1).dt_txt!!.substring(8, 10)
                val dt: String = fullWeeklyList.get(index).dt_txt!!.substring(8, 10)
                if (dt != predate) {
                    weeklyList.add(weatherList)
                }
            }
        }

        weeklyAdapter?.notifyDataSetChanged()
    }

    private fun setdailyAdapter() {
        getViewDataBinding()!!.apply {

            if (dailyAdapter == null) {

                dailyAdapter = RecyclerViewGenericAdapter(dailyList, R.layout.item_time,
                    object : RecyclerCallback<ItemTimeBinding, WeatherResponse.WeatherList> {
                        override fun bindData(
                            binder: ItemTimeBinding,
                            model: WeatherResponse.WeatherList,
                            position: Int,
                            itemView: View?
                        ) {
                            binder.apply {
                                val url = WEATHER_IMAGE_URL + model.weather?.get(0)?.icon.toString() + PNG

                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val url1 = URL(url)
                                        if (model.bitMap == null) {
                                            val bmp = BitmapFactory.decodeStream(
                                                url1.openConnection().getInputStream()
                                            )
                                            launch(Dispatchers.Main) {
                                                addBitmapInDailyList(model, position, bmp)
                                                ivDaily.setImageBitmap(bmp)
                                            }
                                        } else {
                                            ivDaily.setImageBitmap(dailyList.get(position).bitMap)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        showToast(e.localizedMessage)
                                    }
                                }

                                tvTime.text = getContainerActivity().timeFormatChangeIn24(model.dt_txt) ?: "--"
                                tvTemperature.text = decimalFormat.format(model.main?.temp)

                            }
                        }
                    })
            }

            val peekingLinearLayoutManager =
                PeekingLinearLayoutManager(getContainerActivity(), RecyclerView.HORIZONTAL, false)
            peekingLinearLayoutManager.setRatio(0.25f)
            rvDaily.layoutManager = peekingLinearLayoutManager
            rvDaily.adapter = dailyAdapter
        }
    }

    private fun addBitmapInDailyList(
        model: WeatherResponse.WeatherList,
        position: Int,
        bmp: Bitmap
    ) {
        dailyList[position].bitMap = bmp
    }

    private fun addBitmapInWeeklyList(
        model: WeatherResponse.WeatherList,
        position: Int,
        bmp: Bitmap
    ) {
        weeklyList[position].bitMap = bmp
    }

    private fun setWeekAdapter() {
        getViewDataBinding()!!.apply {
            if (weeklyAdapter == null) {

                weeklyAdapter = RecyclerViewGenericAdapter(weeklyList, R.layout.item_days,
                    object : RecyclerCallback<ItemDaysBinding, WeatherResponse.WeatherList> {
                        override fun bindData(
                            binder: ItemDaysBinding,
                            model: WeatherResponse.WeatherList,
                            position: Int,
                            itemView: View?
                        ) {
                            binder.apply {
                                setWeeklyImage(binder, model, position)
                                tvTime.text =
                                    getContainerActivity().timeFormatChangeInDays(model.dt_txt)
                                        ?: "--"
                                tvTemperature.text = decimalFormat.format(model.main?.temp_max)
                                    .toString() + " " + decimalFormat.format(model.main?.temp_min)
                            }
                        }
                    })
            }

            val peekingLinearLayoutManager =
                PeekingLinearLayoutManager(getContainerActivity(), RecyclerView.HORIZONTAL, false)
            peekingLinearLayoutManager.setRatio(0.25f)
            rvWeekly.layoutManager = peekingLinearLayoutManager
            rvWeekly.adapter = weeklyAdapter
        }
    }

    private fun setWeeklyImage(
        binder: ItemDaysBinding,
        model: WeatherResponse.WeatherList,
        position: Int
    ) {
        binder.apply {
            lifecycleScope.launch(Dispatchers.IO) {

                val url =
                    WEATHER_IMAGE_URL + model.weather?.get(0)?.icon.toString() + PNG
                try {
                    val url1 = URL(url)
                    if (model.bitMap == null) {
                        val bmp = BitmapFactory.decodeStream(url1.openConnection().getInputStream())
                        launch(Dispatchers.Main) {
                            addBitmapInWeeklyList(model, position, bmp)
                            ivDaily.setImageBitmap(bmp)
                        }
                    } else {
                        ivDaily.setImageBitmap(weeklyList.get(position).bitMap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(e.localizedMessage)
                }
            }
        }
    }

    var oneTime = true
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    oneTime = false
                    getWeatherInfo(lat as Double, lng as Double, WEATHER_UNITS, WEATHER_APP)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchLocation()
    }

}