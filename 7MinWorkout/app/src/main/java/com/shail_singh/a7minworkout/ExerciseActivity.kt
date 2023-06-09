package com.shail_singh.a7minworkout

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shail_singh.a7minworkout.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        binding?.tbExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        setRestView()
    }

    private fun setRestView() {
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.ivExerciseImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setExerciseView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.ivExerciseImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        val currentExercise = exerciseList!![currentExercisePosition]
        binding?.ivExerciseImage?.setImageResource(currentExercise.getImage())
        binding?.tvExerciseName?.text = currentExercise.getName()

        setExerciseProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress
        restTimer =
            object : CountDownTimer(Constants.REST_DURATION, Constants.TIME_INTERVAL_DURATION) {
                override fun onTick(millisUntilFinished: Long) {
                    restProgress++
                    var remainingTime = (Constants.REST_DURATION / 1000).toInt() - restProgress
                    binding?.progressBar?.progress = remainingTime
                    binding?.tvTimer?.text = remainingTime.toString()
                }

                override fun onFinish() {
                    currentExercisePosition++
                    setExerciseView()
                }

            }.start()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress
        exerciseTimer =
            object : CountDownTimer(Constants.EXERCISE_DURATION, Constants.TIME_INTERVAL_DURATION) {
                override fun onTick(millisUntilFinished: Long) {
                    exerciseProgress++
                    var remainingTime =
                        (Constants.EXERCISE_DURATION / 1000).toInt() - exerciseProgress
                    binding?.progressBarExercise?.progress = remainingTime
                    binding?.tvTimerExercise?.text = remainingTime.toString()
                }

                override fun onFinish() {
                    if (currentExercisePosition < exerciseList!!.size - 1) setRestView()
                    else Toast.makeText(
                        this@ExerciseActivity, "All exercises done", Toast.LENGTH_LONG
                    ).show()
                }

            }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        binding = null
    }
}