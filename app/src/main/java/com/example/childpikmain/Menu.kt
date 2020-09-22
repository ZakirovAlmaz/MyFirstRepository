package com.example.childpikmain

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager


class Menu : AppCompatActivity() {
    private var button_anim: Animation? = null
    lateinit var prefs: SharedPreferences
    var handler: Handler? = null
    var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        getSupportActionBar()?.hide()
        button_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_anim)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean("PlayFoneMusic", true)){playSound()}
    }

    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("PlayFoneMusic", true)){playSound()}
    }
    fun clickMenu1(view: View?) {
        destroySound()
        if (view != null) {
            openCollection(1, view)
        }
    }

    fun clickMenu2(view: View?) {
        destroySound()
        if (view != null) {
            openCollection(2, view)
        }
    }
    fun clickSettings() {
        destroySound()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openCollection(num: Int, view: View) {
        view.startAnimation(button_anim)
        try {
            val intent = Intent(this, Main::class.java)
            intent.putExtra("collection", num)
            startActivity(intent)
        } catch (e: Exception) {
            Log.d(" ERROR. Menu.openCollection error", e.message.toString())
        }
    }

    fun playSound() {
        destroySound()
        try {
            handler = Handler(Looper.getMainLooper())
            handler!!.postDelayed({
                mpClear()
                mPlayer = MediaPlayer.create(this, R.raw.sound_menu)
                mPlayer!!.start()

            }, 0)
        } catch (e: java.lang.Exception) {
            //errorShow("Main.playSound error - " + e.message)
            Log.i("Log", "Sound generator error - " + e.message)
        }
    }

    fun destroySound() {
        try {
            handler!!.removeCallbacksAndMessages(null as Any?)
        } catch (e: Exception) {
            Log.i("Log", "destroySound - " + e.message)
        }
        mpClear()
    }

    fun mpClear() {
        try {
            if (mPlayer != null) {
                mPlayer!!.stop()
                mPlayer!!.release()
                mPlayer = null
                Log.i("Log", "++++++++++++++++++++ mpClear CLEAR")
            }
        } catch (unused: java.lang.Exception) {
            Log.i("Log", "++++++++++++++++++++ mpClear ERROR")
        }
    }

}