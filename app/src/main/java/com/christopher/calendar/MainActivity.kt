package com.christopher.calendar

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.christopher.calendar.custom_view.calendar.CustomCalendarListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomCalendarListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customCalendar.setCalendarListener(this)
        loadSomeFakeDays()
    }

    /**
     * Send some day numbers in order to "draw dots" on the given days
     * to show the user, that the day can contain something like an event or etc.
     */
    private fun loadSomeFakeDays() {
        val fakeDays = listOf(11, 17, 24)
        customCalendar.loadDays(fakeDays)
    }

    @SuppressLint("SetTextI18n")
    override fun onDayClicked(dayOfTheMonth: Int, fullDate: String) {
        tvDate.text = "- DAY OF THE MONTH: $dayOfTheMonth \n- FULL DATE: $fullDate"
        Log.d(TAG, "DAY OF THE MONTH: $dayOfTheMonth - FULL DATE: $fullDate")
    }

    override fun onGetPreviousMonthClicked() {
        Log.d(TAG, "You can perform a request for load events or any other things")
        /*
         * viewModel.loadEvents(month - 1)
         */
        Toast.makeText(this, "onGetPreviousMonthClicked", Toast.LENGTH_SHORT).show()
    }

    override fun onGetNextMonthClicked() {
        Log.d(TAG, "You can perform a request for load events or any other things")
        /*
         * viewModel.loadEvents(month + 1)
         */
        Toast.makeText(this, "onGetNextMonthClicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        customCalendar.clearListener()
        super.onDestroy()
    }
}
