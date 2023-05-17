package com.example.staffshelper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.style.BackgroundColorSpan
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.PrintWriter
import java.io.Serializable
import java.io.Writer
import java.nio.charset.Charset

//class for retriving and saving settings data
class SettingsData(
    val darkMode: Boolean,
    val textSize: Int,
    val txtColour: String,
    //val bgColor: String     idk why it wont do 4
    ) : Serializable {

    companion object {
        //gets settings from json file
        fun getSettings(filename: String, context: Context): ArrayList<SettingsData>{
            val settingsList = ArrayList<SettingsData>();

            try {
                //open file
                val inputStream = context.assets.open(filename)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                //parse into json object
                val json = JSONObject(String(buffer, Charsets.UTF_8))

                //get standard settings
                val settings = json.getJSONArray("Settings")
                    settingsList.add(SettingsData(
                       settings.getJSONObject(0).getBoolean("darkMode"),
                       settings.getJSONObject(0).getInt("textSize"),
                       settings.getJSONObject(0).getString("textColour")//,
                      // settings.getJSONObject(i).getString("backgroundColor2")
                ))

            }
            catch (e: JSONException){
                e.printStackTrace()
            }
            return settingsList;
        }

        //saves settings (broken)
        fun setSettings(filename: String, context: Context){
            var path: String = context.filesDir.absolutePath
            var file = File(path + "/" + filename)

            val inputStream = context.assets.open(filename)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val settingJsonData = JSONObject(String(buffer, Charsets.UTF_8))
            val settings = SettingsData(true, 38, "none")

            settingJsonData.getJSONArray("Settings")

            settingJsonData.getJSONArray("Settings").put(true)
            settingJsonData.getJSONArray("Settings").put(38)
            settingJsonData.getJSONArray("Settings").put("none")


            var jsonString: String = settingJsonData.toString()
            var outputStream: FileOutputStream = context.openFileOutput(filename, MODE_PRIVATE)

            //var outputStream = FileWriter(file)
            outputStream.write(jsonString.toByteArray())
            outputStream.close()

            try {

            }
            catch (e:Exception){
                print(e.message)
            }


        }
    }
}