package com.example.childpikmain

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
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
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

var imageLoader: ImageLoader? = null
var CURRENT_PAGE = 1
var NUM_COLLECTIONS: Int? = null
var NUM_PAGES: Int? = null



class Main : FragmentActivity() {
    /*var imageLoader: ImageLoader? = null
    var CURRENT_PAGE = 1
    var NUM_COLLECTIONS: Int? = null
    var NUM_PAGES: Int? = null*/
    var btnNext: ImageView? = null
    var btnPrev: ImageView? = null
    var btnSound: ImageView? = null
    private var button_anim: Animation? = null
    var button_check_anim: Animation? = null
    var goCheckingImage: ImageView? = null
    var goCheckingImageFone: ImageView? = null
    var handler: Handler? = null
    //private val interstitial: InterstitialAd? = null
    var mPlayer: MediaPlayer? = null
    private var mViewPager: ViewPager2? = null
    var rotate: Animation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NUM_COLLECTIONS = Integer.valueOf(intent.getIntExtra("collection", 1))
        imageLoader = ImageLoader.getInstance()
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(applicationContext))
        button_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_anim)
        rotate = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_circle)
        button_check_anim = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.button_check_anim
        )
        goCheckingImage = findViewById(R.id.goCheckingImage) as ImageView
        goCheckingImageFone = findViewById(R.id.goCheckingImageFone) as ImageView
        btnNext = findViewById(R.id.btnNext) as ImageView
        btnPrev = findViewById(R.id.btnPrev) as ImageView
        btnSound = findViewById(R.id.btnSound) as ImageView
        btnPrev!!.visibility = View.INVISIBLE
        //TODO Реклама
        /*val bundle2 = Bundle()
        bundle2.putString("npa", "1")
        interstitial = InterstitialAd(this)
        interstitial.adUnitId = resources.getString(android.R.string.admob_interstitial)
        interstitial.loadAd(
            Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, bundle2).build()
        )*/
        try {
            if (NUM_COLLECTIONS?.toInt() == 1) {
                NUM_PAGES =
                    Integer.valueOf(resources.getStringArray(R.array.collection_1_names).size)
            }
            if (NUM_COLLECTIONS?.toInt() == 2) {
                NUM_PAGES =
                    Integer.valueOf(resources.getStringArray(R.array.collection_2_names).size)
            }

            mViewPager = findViewById(R.id.pager) as ViewPager2
            mViewPager!!.adapter = ScreenSlidePagerAdapter(this)

            mViewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                //override fun onPageScrolled(i: Int, f: Float, i2: Int) {}
                override fun onPageSelected(i: Int) {
                    CURRENT_PAGE = Integer.valueOf(i + 1)
                    //val unused = CURRENT_PAGE
                    if (CURRENT_PAGE == 1) {
                        btnPrev!!.visibility = View.INVISIBLE
                    } else if (CURRENT_PAGE == NUM_PAGES) {
                        //TODO Сейчас если дошел до конца, идет проверка знаний. Надо это сделать отдельным меню, а картинки накчинать сначала
                        goCheckingImageFone!!.startAnimation(rotate)
                        goCheckingImage!!.startAnimation(button_check_anim)
                        goCheckingImage!!.visibility = View.VISIBLE
                        goCheckingImageFone!!.visibility = View.VISIBLE
                        btnNext!!.visibility = View.INVISIBLE
                        btnSound!!.visibility = View.INVISIBLE
                    } else {
                        goCheckingImageFone!!.clearAnimation()
                        goCheckingImage!!.clearAnimation()
                        goCheckingImage!!.visibility = View.INVISIBLE
                        goCheckingImageFone!!.visibility = View.INVISIBLE
                        btnNext!!.visibility = View.VISIBLE
                        btnSound!!.visibility = View.VISIBLE
                        btnPrev!!.visibility = View.VISIBLE
                    }
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
        /*val bundle = Bundle()
        bundle.putString("npa", "1")
        val adView: AdView? = findViewById(R.id.adView) as AdView?
        val build: AdRequest =
            Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, bundle).build()
        if (adView != null) {
            adView.loadAd(build)
        }*/
    }

    override fun onPause() {
        super.onPause()
        destroySound()
    }

    fun playSound() {
        destroySound()
        try {
            handler = Handler(Looper.getMainLooper())
            handler!!.postDelayed({
                var valueOf = if (NUM_COLLECTIONS!!.toInt() == 1) Integer.valueOf(
                    resources.obtainTypedArray(R.array.collection_1_namesound)
                        .getResourceId(
                            CURRENT_PAGE - 1, 0
                        )
                ) else null
                if (NUM_COLLECTIONS!!.toInt() == 2) {
                    valueOf = Integer.valueOf(
                        resources.obtainTypedArray(R.array.collection_2_namesound)
                            .getResourceId(
                                CURRENT_PAGE - 1, 0
                            )
                    )
                }
                mpClear()
                //val main = this@Main
                mPlayer = MediaPlayer.create(applicationContext, valueOf!!.toInt())
                //val unused = main.mPlayer
                mPlayer!!.start()

                handler!!.postDelayed({
                    var valueOf = if (NUM_COLLECTIONS!!.toInt() == 1) Integer.valueOf(
                        resources.obtainTypedArray(R.array.collection_1_golos)
                            .getResourceId(
                                CURRENT_PAGE - 1, 0
                            )
                    ) else null
                    if (NUM_COLLECTIONS!!.toInt() == 2) {
                        valueOf = Integer.valueOf(
                            resources.obtainTypedArray(R.array.collection_2_golos)
                                .getResourceId(
                                    CURRENT_PAGE - 1, 0
                                )
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

    inner class ScreenSlidePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            val fragment = PageFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putInt("collection", NUM_COLLECTIONS!!.toInt())
            fragment.arguments = bundle
            return  fragment

        }
        override fun getItemCount(): Int = NUM_PAGES!!.toInt()
        //if (items.isNotEmpty()) Int.MAX_VALUE else 0
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
            val arguments: Bundle? = getArguments()
            val i: Int = arguments!!.getInt("position")
            val i2:Int = arguments!!.getInt("collection")
            if (i2 == 1) {
                imageResource = Integer.valueOf(R.array.collection_1_pictures)
                textResource = Integer.valueOf(R.array.collection_1_names)
            }
            if (i2 == 2) {
                imageResource = Integer.valueOf(R.array.collection_2_pictures)
                textResource = Integer.valueOf(R.array.collection_2_names)
            }
            if (i == getResources().getStringArray(textResource!!.toInt()).size - 1) {
                linearLayout.visibility = View.INVISIBLE
            } else {
                linearLayout.visibility = View.VISIBLE
            }
            val str: String = getResources().getStringArray(textResource!!.toInt()).get(i)
            textView.text = str
            try {
                textView.text = Html.fromHtml(
                    "<font color=red>" + str[0] + "</font><font color=white>" + str.substring(1) + "</font>"
                )
            } catch (unused: Exception) {
            }
            val imageLoader: ImageLoader? = imageLoader
            if (imageLoader != null) {
                imageLoader.displayImage(
                    "drawable://" + getResources().obtainTypedArray(imageResource!!.toInt())
                        .getResourceId(i, 0), imageView
                )
            }
            return viewGroup2
        }
    }
}