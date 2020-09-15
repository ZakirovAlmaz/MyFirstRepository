package com.example.childpikmain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity


class Menu : AppCompatActivity() {
    private var button_anim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        button_anim = AnimationUtils.loadAnimation(applicationContext,R.anim.button_anim)
    }

    fun clickMenu1(view: View?) {
        if (view != null) {
            openCollection(1, view)
        }
    }

    fun clickMenu2(view: View?) {
        if (view != null) {
            openCollection(2, view)
        }
    }

    private fun openCollection(num: Int, view: View) {
        view.startAnimation(button_anim)
        try {
            val intent = Intent(this, Main::class.java)
            intent.putExtra("collection", num)
            startActivity(intent)
        } catch (e: Exception) {
            Log.d(" ERROR. Menu.openCollection error",e.message.toString())
        }
    }
}