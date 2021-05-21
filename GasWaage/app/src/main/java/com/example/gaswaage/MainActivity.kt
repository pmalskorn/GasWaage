package com.example.gaswaage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSettings()
    }

    fun initSettings(){
        with(SettingsViewModel.singelton){
            setPref(this@MainActivity)
        }

    }

    override fun onStop() {
        super.onStop()
        finishAndRemoveTask()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_calibrate -> {
                this.findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_global_calibrateFragment
                )
                true
            }
            R.id.action_settings -> {
                this.findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_global_settingsFragment
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}