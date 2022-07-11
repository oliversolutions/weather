package com.oliversolutions.dev.weather.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.oliversolutions.dev.weather.R

@BindingAdapter("temperature")
fun bindTemperature(textView: TextView, weatherTempMap: MutableMap<String, String>?) {
    if (weatherTempMap != null) {
        textView.text = weatherTempMap["temp"] + " º c"
    }
}

@BindingAdapter("weatherImage")
fun bindWeatherImage(imageView: ImageView, weatherArray: Array<MutableMap<String,String>>?) {
    if (weatherArray != null) {
        when (weatherArray[0]["id"]?.toInt()) {
            800 -> imageView.setImageResource(R.drawable.sunny)
            in 200..299 -> imageView.setImageResource(R.drawable.thunderstorm)
            in 300..399 -> imageView.setImageResource(R.drawable.drizzle)
            in 500..599 -> imageView.setImageResource(R.drawable.raining)
            in 600..699 -> imageView.setImageResource(R.drawable.snow)
            in 700..799 -> imageView.setImageResource(R.drawable.thunderstorm)
            in 801..899 -> imageView.setImageResource(R.drawable.clouds)
        }
    }
}

@BindingAdapter("android:fadeVisible")
fun setFadeVisible(view: View, visible: Boolean? = true) {
    if (view.tag == null) {
        view.tag = true
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        view.animate().cancel()
        if (visible == true) {
            if (view.visibility == View.GONE)
                view.fadeIn()
        } else {
            if (view.visibility == View.VISIBLE)
                view.fadeOut()
        }
    }
}