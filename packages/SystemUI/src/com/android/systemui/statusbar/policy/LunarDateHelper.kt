package com.android.systemui.statusbar.policy

import android.icu.util.ChineseCalendar
import android.icu.util.TimeZone
import android.icu.util.ULocale
import java.util.Date
import java.util.Locale

object LunarDateHelper {
    const val SETTING_KEY = "qs_header_lunar_date"

    fun formatLunarDate(time: Date): String {
        val cal = ChineseCalendar(
            TimeZone.getTimeZone("Asia/Ho_Chi_Minh"),
            ULocale.getDefault(),
        )
        cal.time = time
        val day = cal.get(ChineseCalendar.DAY_OF_MONTH)
        val month = cal.get(ChineseCalendar.MONTH) + 1
        val isLeap = cal.get(ChineseCalendar.IS_LEAP_MONTH) == 1
        val leapSuffix = if (isLeap) "N" else ""
        val suffix = if (Locale.getDefault().language == "vi") "ÂL" else "Lunar"
        return "$day/$month$leapSuffix $suffix"
    }

    fun appendLunarDate(baseText: String, time: Date, enabled: Boolean): String {
        if (!enabled || baseText.isEmpty()) return baseText
        return "$baseText • ${formatLunarDate(time)}"
    }
}
