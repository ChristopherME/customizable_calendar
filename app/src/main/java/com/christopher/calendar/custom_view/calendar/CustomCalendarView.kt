package com.christopher.calendar.custom_view.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.christopher.calendar.R
import java.lang.ref.WeakReference
import java.text.DateFormatSymbols
import java.util.*

/**
 * Created by Christopher Elias on 18/02/2020.
 * christopher.mike.96@gmail
 *
 * Peru Apps
 * Lima, Peru.
 **/
class CustomCalendarView @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val _inflater  by lazy {
        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    private val _rootView by lazy {
        _inflater.inflate(R.layout.layout_calendar, this, true)
    }
    private val _currentCalendar by lazy {
        Calendar.getInstance()
    }
    private val _getPreviousMonthTextView by lazy {
        _rootView.findViewById<TextView>(R.id.iv_previous_month)
    }
    private val _getNextMonthTextView by lazy {
        _rootView.findViewById<TextView>(R.id.iv_next_month)
    }
    companion object {
        private const val DAY_OF_THE_WEEK_LAYOUT = "dayOfTheWeekLayout"
        private const val DAY_OF_THE_MONTH_LAYOUT = "dayOfTheMonthLayout"
        private const val DAY_OF_THE_MONTH_BACKGROUND = "dayOfTheMonthBackground"
        private const val DAY_OF_THE_MONTH_CIRCLE_IMAGE = "dayOfTheMonthHaveDate"
        private const val DAY_OF_THE_MONTH_TEXT = "dayOfTheMonthText"
    }
    private lateinit var _dayOfTheWeekViewContainer : ViewGroup

    private var calendarListener : WeakReference<CustomCalendarListener>? = null
    private var selectedDateTagId : String? = null
    /**
     * The following variables are just for validations
     * like painting or clear the current date when the.
     *
     * [_initialMonthValue] the initial month value when [CustomCalendarView] is first created.
     */
    private var _initialMonthValue = 0
    private var _currentMonthSelectedDateTagId: String? = null
    private val _datesOnCurrentMonth by lazy {
        arrayListOf<Int>()
    }

    private val onDayOfMonthClickListener = OnClickListener { dayView ->
        clearDayBackground()
        // Extract day selected
        val dayOfTheMonthContainer = dayView as ViewGroup
        val viewTag = dayOfTheMonthContainer.tag as String
        selectedDateTagId = viewTag.substring(DAY_OF_THE_MONTH_LAYOUT.length, viewTag.length)

        val dayOfTheMonthBackground : RelativeLayout = dayView.findViewWithTag(
            DAY_OF_THE_MONTH_BACKGROUND + selectedDateTagId)
        dayOfTheMonthBackground.setRoundCorners(100f, android.R.color.transparent)
        dayOfTheMonthBackground.setStroke(3, R.color.colorAccent)

        // Extract the day from the text
        val dayOfTheMonthText = dayView.findViewWithTag<TextView>(DAY_OF_THE_MONTH_TEXT + selectedDateTagId).text.toString().toInt()
        val currentYear = _currentCalendar.get(Calendar.YEAR)
        val currentMonth = _currentCalendar.get(Calendar.MONTH) + 1

        calendarListener?.get()?.onDayClicked(dayOfTheMonthText, "$currentYear-${currentMonth.toTimeFormat()}-${dayOfTheMonthText.toTimeFormat()}")
    }

    /**
     * Modify according to your needs. Event for preview.
     */
    init {
        if(!isInEditMode) {
            createViews()
            updateView()
            markCurrentDay()
            setUpUpdateCalendarListeners()
        }
    }

    /**
     * This have to be set on a class that implements [CustomCalendarListener].
     */
    fun setCalendarListener(calendarListener: CustomCalendarListener) {
        this.calendarListener = WeakReference(calendarListener)
    }

    /**
     * Use this on onDestroy() for Activities or onDestroyView() for fragments. Or if you set listener on "onResume" you should clear it on onStop.
     */
    fun clearListener() {
        calendarListener?.clear()
    }

    /**
     * Mark dots on the given day(number).
     * @param days
     */
    fun loadDays(days: List<Int>) {
        clearDatesDots()
        days.forEach { day -> markDateOnDay(day) }
    }

    /**
     * Create views for the calendar.
     *
     * Views are created and positioned like if they where some kind of GridLayout.
     * The final view will have 6 horizontal lines x 7 vertical lines = 42 child views in total.
     *
     * Why 42 and not only 35 (5H x 7V)? because in special cases like 05/2020
     * the number of days (child view) will fill 6 horizontal lines to fit in the calendar.
     */
    private fun createViews() {
        _initialMonthValue = _currentCalendar.get(Calendar.MONTH)
        var weekIndex: Int
        for (i in 0..41) {
            weekIndex = i % 7 + 1
            // we are going to grab the day of the week from R.layout.layout_calendar_week
            // and inside the day we are going to inflate a view.
            _dayOfTheWeekViewContainer = _rootView.findViewWithTag(DAY_OF_THE_WEEK_LAYOUT + weekIndex)
            // Create dayOfTheMonth View object from R.layout.layout_calendar_day_of_the_month
            // inflating it inside _dayOfTheWeekViewContainer (LinearLayout (Vertical)).
            val dayOfTheMonthLayoutView: View = _inflater.inflate(R.layout.layout_calendar_day_of_the_month, _dayOfTheWeekViewContainer, false)
            val dayOfTheMonthText : View = dayOfTheMonthLayoutView.findViewWithTag(DAY_OF_THE_MONTH_TEXT)
            val circleImageView: View = dayOfTheMonthLayoutView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE)
            val dayOfTheMonthBackground : View = dayOfTheMonthLayoutView.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND)
            // Set tags to identify them
            val viewIndex = i + 1
            dayOfTheMonthLayoutView.tag = DAY_OF_THE_MONTH_LAYOUT + viewIndex
            dayOfTheMonthText.tag = DAY_OF_THE_MONTH_TEXT + viewIndex
            circleImageView.tag = DAY_OF_THE_MONTH_CIRCLE_IMAGE + viewIndex
            circleImageView.setRoundCorners(100f, R.color.colorAccent)
            dayOfTheMonthBackground.tag = DAY_OF_THE_MONTH_BACKGROUND + viewIndex
            _dayOfTheWeekViewContainer.addView(dayOfTheMonthLayoutView)
        }
    }

    /**
     * Update month title and Days.
     */
    private fun updateView(comeFromListener : Boolean = false) {
        setUpMonthTitleLayout()
        setUpDaysOfMonthLayout()
        setUpDaysInCalendar()
        if (comeFromListener) {
            clearMarkedCurrentDay()
        }
    }

    private fun setUpMonthTitleLayout() {
        val months = DateFormatSymbols(Locale.getDefault()).months
        val currentMonth = months[_currentCalendar.get(Calendar.MONTH)]
        val nextMonth = if (_currentCalendar.get(Calendar.MONTH) == Calendar.DECEMBER){
            months[0]
        } else {
            months[_currentCalendar.get(Calendar.MONTH) + 1]
        }
        _rootView.findViewById<TextView>(R.id.tv_current_month).text = currentMonth
        _rootView.findViewById<TextView>(R.id.tv_next_month).text = nextMonth
    }

    /**
     * Find 42 views created on [createViews] and do the following.
     * 1.- Turn them [View.INVISIBLE]
     * 2.- background transparent
     * 3.- click listener disabled.
     */
    private fun setUpDaysOfMonthLayout() {
        var dayOfTheMonthText: TextView?
        var dayOfTheMonthContainer: ViewGroup?
        //Include the 42 in the loop.
        for (i in 1..42) {
            dayOfTheMonthContainer = _rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i)
            dayOfTheMonthText = _rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i)
            dayOfTheMonthText.isInvisible = true

            // Apply styles
            dayOfTheMonthText?.setBackgroundResource(android.R.color.transparent)
            dayOfTheMonthContainer?.setBackgroundResource(android.R.color.transparent)
            dayOfTheMonthContainer?.setOnClickListener(null)
        }
    }

    private fun setUpDaysInCalendar() {
        val auxCalendar = Calendar.getInstance(Locale.getDefault())
        auxCalendar.time = _currentCalendar.time
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK)
        var dayOfTheMonthText: TextView?
        var dayOfTheMonthContainer: ViewGroup?
        var dayOfTheMonthLayout: ViewGroup?

        // Calculate dayOfTheMonthIndex
        var dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar.firstDayOfWeek)

        run {
            for (i in 1..auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                dayOfTheMonthContainer =_rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + dayOfTheMonthIndex)
                dayOfTheMonthText = _rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex)
                if (dayOfTheMonthText == null) {
                    break
                }
                dayOfTheMonthContainer?.setOnClickListener(onDayOfMonthClickListener)
                dayOfTheMonthText?.visibility = View.VISIBLE
                dayOfTheMonthText?.text = "$i"
                dayOfTheMonthIndex++
            }
        }
        // Last line, the 6th one.
        for (i in 36..42) {
            dayOfTheMonthText =_rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i)
            dayOfTheMonthLayout =_rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i)
            if (dayOfTheMonthText != null && dayOfTheMonthLayout != null)
                if (dayOfTheMonthText!!.isInvisible) {
                    dayOfTheMonthLayout.isGone = true
                } else {
                    dayOfTheMonthLayout.isVisible = true
                }
        }

    }

    /**
     * Set the red circle as background for the current day.
     * Just for visual purposes.
     */
    private fun markCurrentDay() {
        selectedDateTagId = getDayIndexByDate().toString()
        _currentMonthSelectedDateTagId = selectedDateTagId
        val currentDayOfTheMonthBackground : RelativeLayout = findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND + selectedDateTagId)
        currentDayOfTheMonthBackground.setRoundCorners(100f, android.R.color.transparent)
        currentDayOfTheMonthBackground.setStroke(3, R.color.colorAccent)
    }

    /**
     * Shows the red circle dot on a given day to
     * tell the current client that, on that [day] he/she
     * have one or more date/event.
     *
     * @param day day of the month where is a date/event saved.
     */
    private fun markDateOnDay(day: Int) {
        _datesOnCurrentMonth.add(day)
        findViewWithTag<View>(DAY_OF_THE_MONTH_CIRCLE_IMAGE + getDayIndexByDate(day)).isVisible = true
    }

    /**
     * Remove the red circle background from the "old" date.
     * with old we mean the previous day that was with the red circle as background.
     */
    private fun clearDayBackground() {
        if (selectedDateTagId != null) {
            findViewWithTag<RelativeLayout>(DAY_OF_THE_MONTH_BACKGROUND + selectedDateTagId).setBackgroundResource(0)
        }
    }

    /**
     * Initialize click listeners for change to a previous month or the next one and update the whole view.
     */
    private fun setUpUpdateCalendarListeners() {
        _getPreviousMonthTextView.setOnClickListener {
            it.isEnabled = false
            checkNotNull(calendarListener?.get()) { "You must assign a valid CustomCalendarListener first!" }
            clearDatesDots()
            _currentCalendar.add(Calendar.MONTH, -1)
            calendarListener?.get()?.onGetPreviousMonthClicked()
            updateView(true)
            it.isEnabled = true
        }
        _getNextMonthTextView.setOnClickListener {
            it.isEnabled = false
            checkNotNull(calendarListener?.get()) { "You must assign a valid CustomCalendarListener first!" }
            clearDatesDots()
            _currentCalendar.add(Calendar.MONTH, +1)
            calendarListener?.get()?.onGetNextMonthClicked()
            updateView(true)
            it.isEnabled = true
        }
    }

    private fun clearMarkedCurrentDay() {
        if (_initialMonthValue == _currentCalendar.get(Calendar.MONTH)) {
            if (_currentMonthSelectedDateTagId != null) {
                clearDayBackground()
                markCurrentDay()
            }
        } else {
            clearDayBackground()
        }
    }

    /**
     * Hides the red dots from the day's views.
     * This dots where set as visible when the given day have
     * a date or event saved.
     */
    private fun clearDatesDots() {
        if (_datesOnCurrentMonth.isNotEmpty()) {
            _datesOnCurrentMonth.forEach {
                this.run {
                    findViewWithTag<View>(DAY_OF_THE_MONTH_CIRCLE_IMAGE + getDayIndexByDate(it)).isVisible = false
                }
            }
            _datesOnCurrentMonth.clear()
        }
    }

    private fun getWeekIndex(weekIndex: Int, firstDayOfWeek: Int) : Int {
        return if (firstDayOfWeek == 1){
            weekIndex
        } else {
            if (weekIndex == 1){
                7
            } else {
                weekIndex - 1
            }
        }
    }

    private fun getMonthOffset() : Int {
        val tempCalendar = Calendar.getInstance()
            .apply {
                time = _currentCalendar.time
                set(Calendar.DAY_OF_MONTH, 1)
            }
        val firstDayWeekPosition = tempCalendar.firstDayOfWeek
        val dayPosition = tempCalendar.get(Calendar.DAY_OF_WEEK)
        return if (firstDayWeekPosition == 1) {
            dayPosition - 1
        } else {
            if (dayPosition == 1) {
                6
            } else {
                dayPosition - 2
            }
        }
    }

    /**
     * Return the index-position of a day(1..31).
     * to locate it on the calendar.
     *
     * @param otherDay a day to calculate its index in the calendar.
     */
    private fun getDayIndexByDate(otherDay: Int? = null) : Int {
        val monthOffset = getMonthOffset()
        val dayToCalculate = otherDay ?: _currentCalendar.get(Calendar.DAY_OF_MONTH)
        return dayToCalculate + monthOffset
    }

}