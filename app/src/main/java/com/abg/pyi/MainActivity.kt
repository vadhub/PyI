package com.abg.pyi


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.abg.pyi.editor.ICodeEditorActions
import com.abg.pyi.ui.LessonFragment
import com.abg.pyi.ui.LessonsPagerFragment
import com.abg.pyi.ui.ModulesFragment
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, ModulesFragment.newInstance())
            }
        }

        //demo-banner-yandex
        val mBanner = findViewById<BannerAdView>(R.id.adView)
        mBanner.setAdUnitId("demo-banner-yandex")
        mBanner.setAdSize(getAdSize(mBanner))
        val adRequest: AdRequest = AdRequest.Builder().build()
        mBanner.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Получаем текущий фрагмент
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is ICodeEditorActions) {
            when (item.itemId) {
                R.id.findMenu -> currentFragment.findAndReplace()
                R.id.comment -> currentFragment.commentSelected()
                R.id.un_comment -> currentFragment.unCommentSelected()
                R.id.clearText -> currentFragment.clearText()
                R.id.toggle_relative_line_number -> currentFragment.toggleRelativeLineNumber()
                R.id.undo -> currentFragment.undo()
                R.id.redo -> currentFragment.redo()
                else -> {
                    if (item.groupId == R.id.group_themes) {
                        currentFragment.changeTheme(item.itemId)
                        return true
                    }
                    return super.onOptionsItemSelected(item)
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateToLesson(moduleId: Int, lessonId: Int) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, LessonFragment.newInstance(moduleId, lessonId))
            addToBackStack(null)
        }
    }

    fun navigateToModule(moduleId: Int) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, LessonsPagerFragment.newInstance(moduleId))
            addToBackStack(null)
        }
    }

    private fun getAdSize(mBanner: BannerAdView): BannerAdSize {
        val displayMetrics = resources.displayMetrics
        var adWidthPixels = mBanner.width
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels
        }
        val adWidth = (adWidthPixels / displayMetrics.density).roundToInt()
        return BannerAdSize.stickySize(this, adWidth)
    }

}
