package com.universalcalculator.ui.ads

/**
 * Centralized AdMob configuration.
 * Swap these IDs here when moving between test/production environments.
 *
 * Test IDs (safe to use during development):
 *   Banner  → ca-app-pub-3940256099942544/6300978111
 *   Native  → ca-app-pub-3940256099942544/2247696110
 */
object AdConfig {

    // ── AdMob App ID ───────────────────────────────────────────────────────
    // NOTE: The App ID must be set in AndroidManifest.xml (NOT here).
    // Open app/src/main/AndroidManifest.xml and replace the value of
    // com.google.android.gms.ads.APPLICATION_ID with your real AdMob App ID.

    // ── Banner Ad ──────────────────────────────────────────────────────────
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // ── Native Ad ─────────────────────────────────────────────────────────
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
}
