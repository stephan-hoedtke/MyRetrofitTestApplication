package com.example.myretrofittestapplication

import android.util.Log
import javax.inject.Inject

// Constructor-injected, because Hilt needs to know how to
// provide instances of AnalyticsServiceImpl, too.
class AnalyticsServiceImpl @Inject constructor() : AnalyticsService {
    override fun analyticsMethod() {
        // "Not yet implemented"
        Log.d("ANALYTICS", "Analytics method called.")
    }
}

