/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.systemui.keyguard.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.systemui.res.R
import com.android.systemui.shared.R as SharedR

/** Provides a container for all keyguard ui content. */
class KeyguardRootView(
    context: Context,
    attrs: AttributeSet?,
) :
    ConstraintLayout(
        context,
        attrs,
    ) {
    var aodFpOnly: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            invalidate()
            notifySubtreeAccessibilityStateChangedIfNeeded()
        }

    private var depthWallpaperOverClock = false

    fun setDepthWallpaperOverClock(enabled: Boolean) {
        if (depthWallpaperOverClock == enabled) return
        depthWallpaperOverClock = enabled
        for (index in 0 until childCount) {
            updateWidgetLayer(getChildAt(index))
        }
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (depthWallpaperOverClock) updateWidgetLayer(child)
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        if (depthWallpaperOverClock && isWidget(child.id)) child.translationZ = 0f
    }

    private fun updateWidgetLayer(child: View) {
        if (isWidget(child.id)) {
            child.translationZ = if (depthWallpaperOverClock) 2f else 0f
        }
    }

    private fun isWidget(id: Int): Boolean =
        when (id) {
            R.id.keyguard_app_widgets,
            R.id.keyguard_widgets,
            R.id.keyguard_info_widgets,
            R.id.keyguard_independent_info_widgets,
            R.id.keyguard_slice_view,
            R.id.keyguard_weather_area,
            R.id.keyguard_weather,
            R.id.default_weather_image,
            R.id.default_weather_text,
            SharedR.id.bc_smartspace_view,
            SharedR.id.date_smartspace_view,
            SharedR.id.date_smartspace_view_large,
            SharedR.id.weather_smartspace_view,
            SharedR.id.weather_smartspace_view_large -> true
            else -> false
        }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        if (aodFpOnly && child.id != R.id.device_entry_icon_view) return false
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun isTransformedTouchPointInView(
        x: Float,
        y: Float,
        child: View,
        outLocalPoint: PointF?,
    ): Boolean {
        if (aodFpOnly && child.id != R.id.device_entry_icon_view) return false
        return super.isTransformedTouchPointInView(x, y, child, outLocalPoint)
    }

    override fun addChildrenForAccessibility(outChildren: ArrayList<View>) {
        if (!aodFpOnly) {
            super.addChildrenForAccessibility(outChildren)
            return
        }
        if (accessibilityNodeProvider != null) return
        val icon = findViewById<View>(R.id.device_entry_icon_view) ?: return
        if (icon.visibility != VISIBLE) return
        if (icon.includeForAccessibility()) {
            outChildren.add(icon)
        } else {
            icon.addChildrenForAccessibility(outChildren)
        }
    }
}
