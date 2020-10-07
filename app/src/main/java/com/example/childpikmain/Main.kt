package com.example.childpikmain

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration


class Main : FragmentActivity() {
    var imageLoader: ImageLoader? = null
    var CURRENT_PAGE = 1
    var NUM_COLLECTIONS: Int? = null
    var NUM_PAGES: Int? = null
    lateinit var prefs: SharedPreferences

    var btnSound: ImageView? = null
    private var buttonAnim: Animation? = null
    var buttonCheckAnim: Animation? = null
    var goCheckingImage: ImageView? = null
    var goCheckingImageBack: ImageView? = null
    private var handler: Handler? = null

    private var mPlayer: MediaPlayer? = null
    private var mViewPager: ViewPager2? = null
    var rotate: Animation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        NUM_COLLECTIONS = intent.getIntExtra("collection", 1)
        imageLoader = ImageLoader.getInstance()
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(applicationContext))
        buttonAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_anim)
        rotate = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_circle)
        buttonCheckAnim = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.button_check_anim
        )
        goCheckingImage = findViewById<ImageView>(R.id.goCheckingImage)
        goCheckingImageBack = findViewById<ImageView>(R.id.goCheckingImageFone) as ImageView
        btnSound = findViewById(R.id.btnSound)
        try {
            if (NUM_COLLECTIONS == 1) {
                NUM_PAGES =
                    resources.getStringArray(R.array.collection_1_names).size
            }
            if (NUM_COLLECTIONS == 2) {
                NUM_PAGES =
                   resources.getStringArray(R.array.collection_2_names).size
            }

            mViewPager = findViewById(R.id.pager) as ViewPager2
            mViewPager!!.adapter = ScreenSlidePagerAdapter(this)

            mViewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                //override fun onPageScrolled(i: Int, f: Float, i2: Int) {}

                override fun onPageSelected(i: Int) {
                    Log.d("TEST i", i.toString())

                    CURRENT_PAGE = i + 1
                    //val unused = CURRENT_PAGE
                    /*if (CURRENT_PAGE == NUM_PAGES) {
                        //TODO Сейчас если дошел до конца, идет проверка знаний. Надо это сделать отдельным меню, а картинки накчинать сначала
                        goCheckingImageBack!!.startAnimation(rotate)
                        goCheckingImage!!.startAnimation(buttonCheckAnim)
                        goCheckingImage!!.visibility = View.VISIBLE
                        goCheckingImageBack!!.visibility = View.VISIBLE
                        btnSound!!.visibility = View.INVISIBLE
                    } else {*/
                        goCheckingImageBack!!.clearAnimation()
                        this@Main.goCheckingImage!!.clearAnimation()
                        this@Main.goCheckingImage!!.visibility = View.INVISIBLE
                        goCheckingImageBack!!.visibility = View.INVISIBLE
                        btnSound!!.visibility = View.VISIBLE
                    //}
                }

                override fun onPageScrollStateChanged(i: Int) {
                    if (i == 2) {
                        destroySound()
                    }
                    if (i == 0) {
                        playSound()
                    }
                }
            })
        } catch (e: java.lang.Exception) {
            //errorShow("Main.onCreate error - " + e.message)
            Log.i("Log", "ViewPager error - " + e.message)
        }

    }

    override fun onResume() {
        super.onResume()
        handler = Handler(Looper.getMainLooper())
        handler!!.postDelayed({ playSound() }, 1500)
    }

    override fun onPause() {
        super.onPause()
        destroySound()
    }

    fun btnBack(view: View) {
        view.startAnimation(buttonAnim)
        destroySound()
        finish()
    }


    fun btnSound(view: View) {
        view.startAnimation(buttonAnim)
        playSound()
    }


    // не совсем понял, что дает аннотация Reycle и что она подавляет и стоит ли его использовать в конкретном контексте
    fun playSound() {
        destroySound()
        try {
            handler = Handler(Looper.getMainLooper())
            Log.d("TEST palysound CURRENT_PAGE", CURRENT_PAGE.toString())
            handler!!.postDelayed({
                var valueOf = if (NUM_COLLECTIONS!!.toInt() == 1)
                    resources.obtainTypedArray(R.array.collection_1_namesound)
                        .getResourceId(
                            CURRENT_PAGE - 1, 0
                        )
                else null
                if (NUM_COLLECTIONS!!.toInt() == 2) {
                    valueOf =
                        resources.obtainTypedArray(R.array.collection_2_namesound)
                            .getResourceId(
                                CURRENT_PAGE - 1, 0
                            )

                }
                mpClear()
                //val main = this@Main
                mPlayer = MediaPlayer.create(applicationContext, valueOf!!.toInt())
                //val unused = main.mPlayer
                mPlayer!!.start()

                handler!!.postDelayed({
                    var valueOf = if (NUM_COLLECTIONS!!.toInt() == 1)
                        resources.obtainTypedArray(R.array.collection_1_golos)
                            .getResourceId(
                                CURRENT_PAGE - 1, 0
                            )
                    else null
                    if (NUM_COLLECTIONS!!.toInt() == 2) {
                        valueOf =
                            resources.obtainTypedArray(R.array.collection_2_golos)
                                .getResourceId(
                                    CURRENT_PAGE - 1, 0
                                )

                    }
                    mpClear()
                    mPlayer = MediaPlayer.create(applicationContext, valueOf!!.toInt())
                    //val unused = mPlayer
                    mPlayer?.start()
                }, 2500)
            }, 500)
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

    private fun mpClear() {
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

    inner class ScreenSlidePagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            Log.d("TEST position ", (position).toString())
            Log.d("TEST CURRENT_PAGE ", (CURRENT_PAGE).toString())

            val fragment = PageFragment()
            val bundle = Bundle()

            bundle.putInt("position", position)
            bundle.putInt("collection", NUM_COLLECTIONS!!.toInt())
            fragment.arguments = bundle
            return fragment

        }

        override fun getItemCount(): Int = NUM_PAGES!!.toInt()
    }

    class PageFragment : Fragment() {
        private var imageResource: Int? = null
        private var textResource: Int? = null

        override fun onCreateView(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            bundle: Bundle?
        ): View {
            val viewGroup2 =
                layoutInflater.inflate(R.layout.pager_fragment, viewGroup, false) as ViewGroup
            val textView = viewGroup2.findViewById<View>(R.id.text) as TextView
            val imageView = viewGroup2.findViewById<View>(R.id.image) as ImageView
            val linearLayout = viewGroup2.findViewById<View>(R.id.textLayout) as LinearLayout
            val arguments: Bundle = arguments!!
            var position: Int = arguments.getInt("position")
            val collection: Int = arguments.getInt("collection")


            if (collection == 1) {
                imageResource = R.array.collection_1_pictures
                textResource = R.array.collection_1_names
            }
            if (collection == 2) {
                imageResource = R.array.collection_2_pictures
                textResource = R.array.collection_2_names
            }
            Log.d("TEST position ", (position).toString())

            if (PreferenceManager.getDefaultSharedPreferences(activity)
                    .getBoolean("ShowImageText", true)
            ) {
                if (position == getResources().getStringArray(textResource!!.toInt()).size - 1) {
                    linearLayout.visibility = View.INVISIBLE
                } else {
                    linearLayout.visibility = View.VISIBLE
                }
                val str: String = resources.getStringArray(textResource!!.toInt()).get(position)
                textView.text = str
            } else {
                linearLayout.visibility = View.INVISIBLE
            }
            val imageLoader: ImageLoader? = ImageLoader.getInstance()
            if (imageLoader != null) {
                imageLoader.displayImage(
                    "drawable://" + getResources().obtainTypedArray(imageResource!!.toInt())
                        .getResourceId(position, 0), imageView
                )
            }
            return viewGroup2
        }
    }
}