package com.shail_singh.a7minworkout

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shail_singh.a7minworkout.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flEnd?.setOnClickListener {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val historyDao = (application as WorkoutApp).db.historyDao()

        lifecycleScope.launch {
            addRecord(historyDao)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun addRecord(historyDao: HistoryDao) {
        val cal = Calendar.getInstance()
        val dateTime = cal.time
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val dateString = sdf.format(dateTime)

        lifecycleScope.launch {
            historyDao.insert(HistoryEntity(dateString))
        }
    }
}