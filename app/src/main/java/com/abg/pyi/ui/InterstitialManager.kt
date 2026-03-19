package com.abg.pyi.ui

import android.app.Activity
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

class InterstitialManager(private val activity: Activity) {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    // Тестовый идентификатор (замените на свой, когда получите)
    // Для тестов можно использовать демо-блок Яндекса: 'demo-interstitial-yandex'
    private val adUnitId = "demo-interstitial-yandex" // или ваш реальный ID

    private val adLoader = InterstitialAdLoader(activity).apply {
        setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d("Interstitial", "Ad loaded")
                interstitialAd = ad
                isLoading = false
                // Здесь можно сразу показывать, если нужно
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                Log.e("Interstitial", "Failed to load: ${error.description}")
                isLoading = false
            }
        })
    }

    fun loadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnitId).build()
        adLoader.loadAd(adRequestConfiguration)
    }

    fun showAd(callback: () -> Unit = {}) {
        if (interstitialAd == null) {
            // Если реклама не загружена, просто выполняем callback
            callback()
            return
        }

        interstitialAd?.setAdEventListener(object : InterstitialAdEventListener {
            override fun onAdShown() {
                Log.d("Interstitial", "Ad shown")
            }

            override fun onAdFailedToShow(error: AdError) {
                Log.e("Interstitial", "Failed to show: ${error.description}")
                interstitialAd = null
                callback()
            }

            override fun onAdImpression(impressionData: ImpressionData?) {
                Log.d("Interstitial", "Ad impression")
            }

            override fun onAdDismissed() {
                Log.d("Interstitial", "Ad dismissed")
                interstitialAd = null
                // после закрытия загружаем следующий заранее
                loadAd()
                callback()
            }

            override fun onAdClicked() {
                Log.d("Interstitial", "Ad clicked")
            }
        })

        interstitialAd?.show(activity)
    }
}