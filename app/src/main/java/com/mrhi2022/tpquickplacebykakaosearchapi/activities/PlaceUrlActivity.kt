package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding: ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.wv.webViewClient= WebViewClient()
        binding.wv.webChromeClient = WebChromeClient()

        binding.wv.settings.javaScriptEnabled=true

        var placeUrl:String= intent.getStringExtra("place_url") ?: ""
        binding.wv.loadUrl(placeUrl)
    }

    override fun onBackPressed() {
        if(binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()
    }

}