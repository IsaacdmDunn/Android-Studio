package com.example.staffshelper

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class SettingsData(
    val darkMode: Boolean,
    val otherStuff: Boolean) : Serializable {

    companion object {
        fun getBooks(filename: String, context: Context): ArrayList<SettingsData>{
            val settingsList = ArrayList<SettingsData>();

            try {
                val inputStream = context.assets.open(filename)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                val json = JSONObject(String(buffer, Charsets.UTF_8))
                val settings = json.getJSONArray("Settings")
                for (i in 0 until settings.length())

                    settingsList.add(SettingsData(
                       settings.getJSONObject(0).getBoolean("darkMode"),
                       settings.getJSONObject(0).getBoolean("otherStuff")
                ));

            }
            catch (e: JSONException){
                e.printStackTrace()
            }
            return settingsList;
        }
    }
}