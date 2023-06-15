package com.shail_singh.a7minworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shail_singh.a7minworkout.databinding.ActivityExerciseBinding
import com.shail_singh.a7minworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition: Int = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.ENGLISH)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language specified is not supported!")
                }
            } else {
                Log.e("TTS", "Initialization of Text to Speech service failed!")

            }
        })

        val soundURI = Uri.parse(Constants.ROOT_PROJECT_LOCATION + R.raw.press_start)
        player = MediaPlayer.create(applicationContext, soundURI)
        player?.isLooping = false

        binding?.tbExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setRestView()
        setUpExerciseStatusRecyclerView()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        // Action on YES
        dialogBinding.btnDialogYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }

        // Action on No
        dialogBinding.btnDialogNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun setUpExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setRestView() {
        try {
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        nextExerciseActivityVoiceAlert("Rest now for ${Constants.REST_DURATION / 1000} seconds.")
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
        nextExerciseActivityVoiceAlert("Perform. ${currentExercise.getName()}")

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
                    exerciseList!![currentExercisePosition].setIsSelected(true)
                    exerciseAdapter!!.notifyDataSetChanged()
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
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    if (currentExercisePosition < exerciseList!!.size - 1) setRestView()
                    else {
                        finish()
                        val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                        startActivity(intent)
                    }
                }

            }.start()
    }

    private fun nextExerciseActivityVoiceAlert(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onBackPressed() {
        customDialogForBackButton()
        this.onBackPressedDispatcher.onBackPressed()
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
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }

        if (player != null) player!!.stop()

        binding = null
    }
}