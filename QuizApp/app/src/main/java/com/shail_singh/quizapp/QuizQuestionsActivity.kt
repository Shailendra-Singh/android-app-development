package com.shail_singh.quizapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private var currentQuestionPos: Int = 1
    private var questionsList: ArrayList<Question>? = null
    private var selectedOptionId: Int = 0
    private var hasSelectedAnOption: Boolean = false

    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null

    private var tvOptionA: TextView? = null
    private var tvOptionB: TextView? = null
    private var tvOptionC: TextView? = null
    private var tvOptionD: TextView? = null

    private var btnSubmit: Button? = null

    private var userName: String? = null
    private var correctAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        // Assign UI Items
        progressBar = findViewById(R.id.progress_bar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)

        tvOptionA = findViewById(R.id.tv_option_a)
        tvOptionB = findViewById(R.id.tv_option_b)
        tvOptionC = findViewById(R.id.tv_option_c)
        tvOptionD = findViewById(R.id.tv_option_d)

        tvOptionA?.setOnClickListener(this)
        tvOptionB?.setOnClickListener(this)
        tvOptionC?.setOnClickListener(this)
        tvOptionD?.setOnClickListener(this)

        btnSubmit = findViewById(R.id.btn_submit)
        btnSubmit?.setOnClickListener(this)

        questionsList = Constants.getQuestions()

        userName = intent.getStringExtra(Constants.USER_NAME)

        populateQuestion()
        defaultOptionsView()
    }

    private fun populateQuestion() {
        hasSelectedAnOption = false
        defaultOptionsView()
        val questionObj: Question = questionsList!![currentQuestionPos - 1]
        tvQuestion?.text = questionObj.question
        ivImage?.setImageResource(questionObj.image)
        progressBar?.progress = currentQuestionPos
        tvProgress?.text = "$currentQuestionPos/${progressBar?.max}"
        tvOptionA?.text = questionObj.optionA
        tvOptionB?.text = questionObj.optionB
        tvOptionC?.text = questionObj.optionC
        tvOptionD?.text = questionObj.optionD

        if (currentQuestionPos == questionsList!!.size) btnSubmit?.text = "FINISH"
        else btnSubmit?.text = "SUBMIT"

    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        tvOptionA?.let { options.add(0, it) }
        tvOptionB?.let { options.add(1, it) }
        tvOptionC?.let { options.add(2, it) }
        tvOptionD?.let { options.add(3, it) }

        for (option in options) {
            option.setTextColor(resources.getColor(R.color.main_screen_name_text_color, theme))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedOptionsView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        selectedOptionId = selectedOptionNum
        tv.setTextColor(resources.getColor(R.color.main_screen_welcome_text_color, theme))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_option_a -> tvOptionA?.let { selectedOptionsView(it, 1) }
            R.id.tv_option_b -> tvOptionB?.let { selectedOptionsView(it, 2) }
            R.id.tv_option_c -> tvOptionC?.let { selectedOptionsView(it, 3) }
            R.id.tv_option_d -> tvOptionD?.let { selectedOptionsView(it, 4) }
            R.id.btn_submit -> {
                if (selectedOptionId == 0) {
                    if (hasSelectedAnOption) currentQuestionPos++
                    when {
                        currentQuestionPos <= questionsList!!.size -> populateQuestion()
                        else -> {
                            val resultsActivityIntent = Intent(this, ResultsActivity::class.java)
                            resultsActivityIntent.putExtra(Constants.USER_NAME, userName)
                            resultsActivityIntent.putExtra(
                                Constants.CORRECT_ANSWERS,
                                correctAnswers
                            )
                            resultsActivityIntent.putExtra(
                                Constants.TOTAL_QUESTIONS,
                                questionsList!!.size
                            )
                            startActivity(resultsActivityIntent)
                            finish()
                        }
                    }
                } else {
                    val question = questionsList?.get(currentQuestionPos - 1)

                    if (question!!.correctAnswer != selectedOptionId) answerView(
                        selectedOptionId,
                        R.drawable.wrong_option_border_bg
                    )
                    else correctAnswers++

                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    if (currentQuestionPos == questionsList!!.size) btnSubmit?.text = "FINISH"
                    else btnSubmit?.text = "NEXT"

                    selectedOptionId = 0
                    hasSelectedAnOption = true
                }
            }
        }
    }

    private fun answerView(ans: Int, drawableView: Int) {
        when (ans) {
            1 -> tvOptionA?.background = ContextCompat.getDrawable(this, drawableView)
            2 -> tvOptionB?.background = ContextCompat.getDrawable(this, drawableView)
            3 -> tvOptionC?.background = ContextCompat.getDrawable(this, drawableView)
            4 -> tvOptionD?.background = ContextCompat.getDrawable(this, drawableView)
        }
    }
}