package com.universalcalculator.ui.ads

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.universalcalculator.R

// Ad unit IDs are managed centrally in AdConfig.kt

@Composable
fun NativeAdBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    // Load the Native Ad once
    DisposableEffect(Unit) {
        val adLoader = AdLoader.Builder(context, AdConfig.NATIVE_AD_UNIT_ID)
            .forNativeAd { ad: NativeAd ->
                nativeAd?.destroy() // Destroy previous ad if there was one
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Handle failure if needed
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
        }
    }

    // Display the Native Ad using AndroidView when loaded
    nativeAd?.let { ad ->
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { ctx ->
                val adView = LayoutInflater.from(ctx).inflate(R.layout.native_ad_layout, null) as NativeAdView
                
                // Locate the views
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)

                // Bind the views
                adView.headlineView = headlineView
                adView.mediaView = mediaView
                adView.callToActionView = callToActionView

                // Set the specific data
                headlineView.text = ad.headline
                
                if (ad.callToAction == null) {
                    callToActionView.visibility = android.view.View.INVISIBLE
                } else {
                    callToActionView.visibility = android.view.View.VISIBLE
                    callToActionView.text = ad.callToAction
                }

                // Call the AdMob setNativeAd method to finish registration
                adView.setNativeAd(ad)

                adView
            },
            update = { adView ->
                // Ensure the view is always updated if the state changes
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                
                headlineView.text = ad.headline
                if (ad.callToAction == null) {
                    callToActionView.visibility = android.view.View.INVISIBLE
                } else {
                    callToActionView.visibility = android.view.View.VISIBLE
                    callToActionView.text = ad.callToAction
                }
                adView.setNativeAd(ad)
            }
        )
    }
}
