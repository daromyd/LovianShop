package com.example.lovianshop

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lovianshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        private const val BASE_URL = "https://shop.lovian.love/"
        private const val ERR_DESC = "net::ERR_INTERNET_DISCONNECTED"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingWebView()

        binding.webView.loadUrl(BASE_URL)

        binding.swipe.setOnRefreshListener {
            binding.webView.reload()
            hideError()
            binding.swipe.isRefreshing = false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun settingWebView(){
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true)

        binding.webView.apply {
            webViewClient = object: WebViewClient(){
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request?.url.toString().startsWith(BASE_URL) || request?.url.toString().startsWith("https://accounts.google.com")){
                        return false
                    }

                    Intent(Intent.ACTION_VIEW, Uri.parse(request?.url.toString())).apply {
                        startActivity(this)
                    }
                    return true
                }

                @SuppressLint("NewApi")
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)

                    if (error?.description.toString() == ERR_DESC){
                        showError()
                    }
                }
            }

            webChromeClient = object : WebChromeClient(){
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    Log.d("alert", message.toString())
                    val dialogBuilder = AlertDialog.Builder(context)

                    dialogBuilder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                            result?.confirm()
                        }

                    val alert = dialogBuilder.create()
                    alert.show()

                    return true
                }
            }

            settings.cacheMode
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.domStorageEnabled = true
            settings.userAgentString = "LovianShop"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.url.toString() == BASE_URL){
            super.onBackPressed()
        }
        if (binding.webView.canGoBack()){
            binding.webView.goBack()
        }
    }

    private fun showError(){
        binding.webView.visibility = View.GONE
        binding.linearError.visibility = View.VISIBLE

    }

    private fun hideError(){
        binding.linearError.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE
    }

}