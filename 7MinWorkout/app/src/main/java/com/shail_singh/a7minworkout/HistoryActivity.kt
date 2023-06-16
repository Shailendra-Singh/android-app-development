package com.shail_singh.a7minworkout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shail_singh.a7minworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistoryActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Workout History"
        }

        binding?.toolbarHistoryActivity?.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        populateHistory(historyDao)
    }

    private fun populateHistory(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.fetchAll().collect { completedDates ->
                if (completedDates.isNotEmpty()) {
                    binding?.tvPlaceholderText?.visibility = View.GONE
                    binding?.rvHistoryRecords?.visibility = View.VISIBLE

                    binding?.rvHistoryRecords?.layoutManager =
                        LinearLayoutManager(this@HistoryActivity)
                    binding?.rvHistoryRecords?.adapter = HistoryItemAdapter(completedDates)
                } else {
                    binding?.tvPlaceholderText?.visibility = View.VISIBLE
                    binding?.rvHistoryRecords?.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}