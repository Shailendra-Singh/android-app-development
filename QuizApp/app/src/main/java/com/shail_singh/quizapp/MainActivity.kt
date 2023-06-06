package com.shail_singh.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart: Button = findViewById<Button>(R.id.btnStart)
        val etNameInput: EditText = findViewById<EditText>(R.id.etNameInput)
        btnStart.setOnClickListener {
            if (etNameInput.text.isEmpty())
                Toast.makeText(this, "Please enter your name!", Toast.LENGTH_LONG).show()
            else {
                val questionsActivityIntent: Intent =
                    Intent(this, QuizQuestionsActivity::class.java)
                questionsActivityIntent.putExtra(Constants.USER_NAME, etNameInput?.text.toString())
                startActivity(questionsActivityIntent)
                finish()
            }
        }
    }
}