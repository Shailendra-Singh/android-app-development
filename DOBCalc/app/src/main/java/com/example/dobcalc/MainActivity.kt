package com.example.dobcalc

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var tvSelectedDate: TextView? = null
    private var tvAgeInMinutes: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDatePicker: Button = findViewById<Button>(R.id.btnDatePicker)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvAgeInMinutes = findViewById(R.id.tvAgeInMinutes)
        btnDatePicker.setOnClickListener {
            clickDatePicker()
        }
    }

    private fun clickDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                Toast.makeText(this, "$year/$month/$dayOfMonth", Toast.LENGTH_LONG).show()
                var selectedDateStr = "${month + 1}/$dayOfMonth/$year"
                var sdf = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                val selectedDate = sdf.parse(selectedDateStr)
                selectedDate?.let {
                    tvSelectedDate?.text = selectedDateStr
                    val selectedDateInMinutes = selectedDate.time / 60000
                    val currentDateInMinutes = sdf.parse(sdf.format(System.currentTimeMillis()))
                    currentDateInMinutes?.let {
                        val differenceInMinutes = (currentDateInMinutes.time / 60000) - selectedDateInMinutes
                        tvAgeInMinutes?.text = differenceInMinutes.toString()
                    }
                }
            },
            year, month, day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpd.show()
    }
}