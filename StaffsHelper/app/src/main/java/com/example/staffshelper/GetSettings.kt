package com.example.staffshelper

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class GetSettings {
    val darkmode = false;
    val otherSettings = false;

    companion object {
        fun getBooks(filename: String, context: Context): ArrayList<GetSettings>{
            val settings = ArrayList<GetSettings>();

            try {
                val inputStream = context.assets.open(filename)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                val json = JSONObject(String(buffer, Charsets.UTF_8))
                //val setting = json.getJSONArray("Settings")

                   // settings.add(GetSettings(
                    //    setting.getJSONObject(0).getString("darkMode")
                   // ));

            }
            catch (e: JSONException){
                e.printStackTrace()
            }
            return settings;
        }
    }
}