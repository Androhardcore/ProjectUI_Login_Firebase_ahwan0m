package site.ahwan0m.blogger.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import site.ahwan0m.blogger.R

class MainActivity : AppCompatActivity() {
    var mywebview : WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mywebview = findViewById<WebView>(R.id.webku)
        mywebview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        mywebview!!.loadUrl("https://www.ahwan0m.site/")

        val navbar = findViewById<View>(R.id.navbar) as BottomNavigationView
        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.login_nav -> {
                    val MainActivity = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(MainActivity)
                    finish()
                }

                R.id.about_nav -> {
                    val MainActivity = Intent(applicationContext, DaftarActivity::class.java)
                    startActivity(MainActivity)
                    finish()
                }
            }
            true
        }
    }
}



