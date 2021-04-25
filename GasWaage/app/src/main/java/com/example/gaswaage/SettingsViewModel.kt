package com.example.gaswaage

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    lateinit var prefs: SharedPreferences

    fun setPref(activity: MainActivity) {
        prefs = activity.getPreferences(Context.MODE_PRIVATE)
    }

    companion object {
        var singelton: SettingsViewModel = SettingsViewModel()
    }

    fun writeSetting(key: String, value: String){
        with(prefs.edit()){
            putString(key, value)
            apply()
        }
    }

    fun writeSetting(key: String, value: Int){
        with(prefs.edit()){
            putInt(key, value)
            apply()
        }
    }

    fun getStringSetting(key: String): String?{
        return prefs.getString(key, null)
    }

    fun removeSetting(key: String){
        with(prefs.edit()){
            remove(key)
            commit()
        }
    }

    fun getIntSetting(key: String): Int{
        return prefs.getInt(key, 0)
    }

}