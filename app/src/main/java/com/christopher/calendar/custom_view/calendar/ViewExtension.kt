package com.christopher.calendar.custom_view.calendar

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import org.jetbrains.annotations.NotNull

/**
 * Created by Christopher Elias on 18/02/2020.
 * christopher.mike.96@gmail
 *
 * Peru Apps
 * Lima, Peru.
 **/


/**
 * Add round corners to view background.
 *
 * @param cornerRadius (viewCornerRadius) goes from 0 to 100. [cornerRadius] is proportional to the view height and width.
 *                     It is applied to the 4 corners of the view.
 * @param backgroundColorId (viewBackgroundColor) the color resource ID.
 *                example: R.color.someColor
 *
 * @throws android.content.res.Resources.NotFoundException if the given [backgroundColorId]
 *         does not exist.
 */
fun View.setRoundCorners(@NotNull cornerRadius: Float,
                         @NotNull @ColorRes backgroundColorId: Int) {
    background = GradientDrawable().apply {
        // if corner radius is smaller than 0, no round effect is applied to the
        // view background corners
        if (cornerRadius > 0) {
            this.cornerRadius = cornerRadius
        }
        // if color is null, means no background color... "transparent".
        setColor(ContextCompat.getColor(context, backgroundColorId))
    }
}


/**
 * Add stroke to view background.
 *
 * @param strokeWidth width of the stroke.
 * @param strokeColorID the color resource ID. Example: R.color.someColor
 *
 * @throws Exception if background is not [GradientDrawable].
 * [setStroke] has te be used only if view background is [GradientDrawable]
 * if not, it will probably crash.
 *
 * @throws android.content.res.Resources.NotFoundException if the given [strokeColorID]
 *         does not exist.
 */
fun View.setStroke(@NotNull strokeWidth: Int,
                   @NotNull @ColorRes strokeColorID: Int) {
    background = if (background == null) {
        GradientDrawable().also {
            if (strokeWidth > 0) {
                // If stroke width is smaller than 0, means no stroke at all
                it.setStroke(strokeWidth, ContextCompat.getColor(context, strokeColorID))
            }
        }
    } else {
        (background as GradientDrawable).also {
            if (strokeWidth > 0) {
                // If stroke width is smaller than 0, means no stroke at all
                it.setStroke(strokeWidth, ContextCompat.getColor(context, strokeColorID))
            }
        }
    }
}