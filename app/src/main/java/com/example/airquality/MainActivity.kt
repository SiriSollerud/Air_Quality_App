package com.example.airquality

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AirQuality)
        setContentView(R.layout.activity_main)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.bottom_navigation) //might have to be initalized before
        bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    supportActionBar!!.title = "SØK"
                    openFragment(SearchFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_fav -> {
                    supportActionBar!!.title = "FAVORITTER"
                    openFragment(FavFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_info -> {
                    supportActionBar!!.title = "INFO"
                    openFragment(InfoFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        supportActionBar!!.title = "SØK"
        openFragment(SearchFragment.newInstance())
    }
    // Fragment function
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}