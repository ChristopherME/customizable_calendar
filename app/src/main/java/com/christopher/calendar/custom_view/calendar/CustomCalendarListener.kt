package com.christopher.calendar.custom_view.calendar

/**
 * Created by Christopher Elias on 18/02/2020.
 * christopher.mike.96@gmail
 *
 * Peru Apps
 * Lima, Peru.
 **/

/**
 * Implement this interface on Activities, Fragments, Dialogs or any other class that you wan't
 * to handles [CustomCalendarListener] functions.
 */
interface CustomCalendarListener {

    /**
     * Fired when user click on a given day.
     * @param dayOfTheMonth day number of the day clicked.
     * @param fullDate dd/MM/yyyy
     */
    fun onDayClicked(dayOfTheMonth: Int, fullDate: String)

    /**
     * This will make the calendar refresh and show current month - 1
     */
    fun onGetPreviousMonthClicked()

    /**
     * This will make the calendar refresh and show current month + 1
     */
    fun onGetNextMonthClicked()
}