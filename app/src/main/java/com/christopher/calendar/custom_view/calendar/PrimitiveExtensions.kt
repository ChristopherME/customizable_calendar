package com.christopher.calendar.custom_view.calendar

/**
 * Created by Christopher Elias on 18/02/2020.
 * christopher.mike.96@gmail
 *
 * Peru Apps
 * Lima, Peru.
 **/


/**
 * Format the current integer number with leading zeros.
 * This only works if the extension is used for numbers with max length = 2.
 *
 * @sample 2.toTimeFormat() = "02"
 * @sample 10.toTimeFormat() = "10"
 */
fun Int.toTimeFormat() = String.format("%02d", this)