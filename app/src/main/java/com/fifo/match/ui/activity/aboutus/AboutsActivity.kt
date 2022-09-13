package com.fifo.match.ui.activity.aboutus

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.fifo.match.R
import com.fifo.match.network.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_aboutus.*

@AndroidEntryPoint
class AboutsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)

        if (intent.hasExtra(Constants.WEB_FLAG)){
        val urlData  = intent.getStringExtra(Constants.WEB_FLAG)
        callWebView(urlData!!)
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun callWebView(url: String) {
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false)
    }

}