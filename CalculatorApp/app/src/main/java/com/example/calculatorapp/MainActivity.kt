package com.example.calculatorapp

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), OnClickListener {

    private var tvInput: TextView? = null
    private var lastOperator: Char = '+'
    private var isLastInputAnOperator: Boolean = false
    private var operandA: String = ""
    private var operandB: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInput = findViewById(R.id.tvInput)
        registerButtons()
    }

    private fun registerButtons() {
        // Number Buttons
        findViewById<Button>(R.id.btn0).setOnClickListener(this)
        findViewById<Button>(R.id.btn1).setOnClickListener(this)
        findViewById<Button>(R.id.btn2).setOnClickListener(this)
        findViewById<Button>(R.id.btn3).setOnClickListener(this)
        findViewById<Button>(R.id.btn4).setOnClickListener(this)
        findViewById<Button>(R.id.btn5).setOnClickListener(this)
        findViewById<Button>(R.id.btn6).setOnClickListener(this)
        findViewById<Button>(R.id.btn7).setOnClickListener(this)
        findViewById<Button>(R.id.btn8).setOnClickListener(this)
        findViewById<Button>(R.id.btn9).setOnClickListener(this)
        // Operator Buttons
        findViewById<Button>(R.id.btnDivide).setOnClickListener(this)
        findViewById<Button>(R.id.btnMultiply).setOnClickListener(this)
        findViewById<Button>(R.id.btnSubtract).setOnClickListener(this)
        findViewById<Button>(R.id.btnPlus).setOnClickListener(this)
        findViewById<Button>(R.id.btnPercent).setOnClickListener(this)
        // Action Buttons
        findViewById<Button>(R.id.btnPeriod).setOnClickListener(this)
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener(this)
        findViewById<Button>(R.id.btnAC).setOnClickListener(this)
        findViewById<Button>(R.id.btnEquals).setOnClickListener(this)
    }

    private fun onClickDigitButton(button: Button) {
        var text: String = button.text.toString()
        if (tvInput?.text == "0" && text == "0") tvInput?.text = "0"
        else {
            if (tvInput?.text.toString() == "0") tvInput?.text = text
            else tvInput?.append(text)
        }
        isLastInputAnOperator = false
    }

    private fun onClickOperatorButton(button: Button) {
        lastOperator = button.text.toString()[0]
        operandA = tvInput?.text.toString()
        tvInput?.text = ""
        isLastInputAnOperator = true
    }

    private fun onClickEqualButton(button: Button) {
        if (!isLastInputAnOperator) {
            operandB = tvInput?.text.toString()
            var operandAVal: Double = operandA.toDouble()
            var operandBVal: Double = operandB.toDouble()
            var result: Double = 0.0
            when (lastOperator) {
                '+' -> result = operandAVal + operandBVal
                '-' -> result = operandAVal - operandBVal
                'X' -> result = operandAVal * operandBVal
                '/' -> if (operandBVal == 0.0) {
                    Toast.makeText(
                        this, "Second operand cannot be zero!", Toast.LENGTH_SHORT
                    ).show()
                } else result = operandAVal / operandBVal

                '%' -> if (operandBVal == 0.0) {
                    Toast.makeText(
                        this, "Second operand cannot be zero!", Toast.LENGTH_SHORT
                    ).show()
                } else result = operandAVal % operandBVal
            }


            if (operandA.contains(".") || operandB.contains(".")) tvInput?.text = result.toString()
            else tvInput?.text = result.toInt().toString()
        }
    }

    private fun onClickSignToggleButton(button: Button) {
        var text: String = tvInput?.text.toString()
        if (text.startsWith("-")) text = text.substring(1, text.length)
        else text = "-$text"

        tvInput?.text = text
    }

    override fun onClick(view: View?) {
        view?.let {
            (view as Button).let {
                when (it.id) {
                    R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                    R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                    -> onClickDigitButton(it)

                    R.id.btnDivide, R.id.btnMultiply, R.id.btnSubtract, R.id.btnPercent, R.id.btnPlus -> onClickOperatorButton(
                        it
                    )

                    R.id.btnAC -> tvInput?.text = "0"

                    R.id.btnPeriod -> if (!tvInput?.text.toString()
                            .contains(".")
                    ) tvInput?.append(".")

                    R.id.btnPlusMinus -> onClickSignToggleButton(it)

                    R.id.btnEquals -> onClickEqualButton(it)
                }
            }
        }
    }
}