package com.shail_singh.a7minworkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shail_singh.a7minworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null
    private var isMetricSelected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarBmiActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding?.rgUnitSelector?.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.rb_unit_metric) {
                setMetricView()
                isMetricSelected = true
            } else {
                setUSSystemView()
                isMetricSelected = false
            }
        }

        binding?.btnBmiCalculate?.setOnClickListener {

            try {
                if (isMetricSelected) {
                    validateMetricUnits()
                    val weightKg: Float = binding?.etInputWeightKg?.text.toString().toFloat()
                    val heightCm: Float = binding?.etInputHeightCm?.text.toString().toFloat()
                    val bmi = calculateBMI(weightKg, heightCm)
                    displayBMIResult(bmi)
                } else {
                    validateUSSystemUnits()
                    val weightLb: Float = binding?.etInputWeightLb?.text.toString().toFloat()
                    val heightFeet: Float = binding?.etInputHeightFeet?.text.toString().toFloat()
                    val heightInch: Float = binding?.etInputHeightInch?.text.toString().toFloat()
                    val bmi = calculateBMI(weightLb, heightFeet, heightInch)
                    displayBMIResult(bmi)
                }
            } catch (e: Exception) {
                binding?.etInputWeightLb?.setText("")
                binding?.etInputHeightFeet?.setText("")
                binding?.etInputHeightInch?.setText("")
                binding?.etInputWeightKg?.setText("")
                binding?.etInputHeightCm?.setText("")
                Toast.makeText(this, "Invalid Input!", Toast.LENGTH_LONG).show()

            }
        }
        setMetricView()
    }

    private fun setUSSystemView() {
        binding?.tilEtWeightLb?.visibility = View.VISIBLE
        binding?.tilEtHeightFeet?.visibility = View.VISIBLE
        binding?.tilEtHeightInch?.visibility = View.VISIBLE

        binding?.tilEtWeightKg?.visibility = View.GONE
        binding?.tilEtHeightCm?.visibility = View.GONE
    }

    private fun setMetricView() {
        binding?.tilEtWeightLb?.visibility = View.GONE
        binding?.tilEtHeightFeet?.visibility = View.GONE
        binding?.tilEtHeightInch?.visibility = View.GONE

        binding?.tilEtWeightKg?.visibility = View.VISIBLE
        binding?.tilEtHeightCm?.visibility = View.VISIBLE
    }

    private fun displayBMIResult(bmi: Float) {
        val bmiLabel: String
        val bmiDesc: String

        when {
            bmi.compareTo(15f) <= 0 -> {
                bmiLabel = "Mortally Underweight"
                bmiDesc = "Danger! You should get help!"
            }

            bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0 -> {
                bmiLabel = "Severely Underweight"
                bmiDesc =
                    "Oops! You really need to take better care of yourself!" + " Consult a dietitian."
            }

            bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0 -> {
                bmiLabel = "Underweight"
                bmiDesc = "You should target to gain healthy weight." + " Eat more calories."
            }

            bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0 -> {
                bmiLabel = "Normal"
                bmiDesc = "Congratulations! You are in good shape."
            }

            bmi.compareTo(25f) > 0 && bmi.compareTo(29.9f) <= 0 -> {
                bmiLabel = "Overweight"
                bmiDesc = "You should try to loose weight. Be in caloric deficit and exercise more."
            }

            else -> {
                bmiLabel = "Obesity"
                bmiDesc = "OMG! You are in a very dangerous condition! Act now!"
            }
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
        binding?.tvBmiValue?.text = bmiValue.toString()
        binding?.tvBmiCategory?.text = bmiLabel.toString()
        binding?.tvBmiDescription?.text = bmiDesc.toString()
        binding?.llTextViewContainer?.visibility = View.VISIBLE
    }

    private fun validateMetricUnits() {
        if (binding?.etInputWeightKg?.text.toString()
                .isEmpty()
        ) binding?.etInputWeightKg?.setText("0")

        if (binding?.etInputHeightCm?.text.toString()
                .isEmpty()
        ) binding?.etInputHeightCm?.setText("0")
    }

    private fun validateUSSystemUnits() {
        if (binding?.etInputWeightLb?.text.toString()
                .isEmpty()
        ) binding?.etInputWeightLb?.setText("0")

        if (binding?.etInputHeightFeet?.text.toString()
                .isEmpty()
        ) binding?.etInputHeightFeet?.setText("0")

        if (binding?.etInputHeightInch?.text.toString()
                .isEmpty()
        ) binding?.etInputHeightInch?.setText("0")
    }

    private fun calculateBMI(weightInKg: Float, heightInCm: Float): Float {
        return (weightInKg / (heightInCm * heightInCm)) * 10000
    }

    private fun calculateBMI(weightInLb: Float, heightInFt: Float, heightInInch: Float): Float {
        val weightInKg = weightInLb / 2.20462f
        val heightInCm = (heightInFt * 12.0f + heightInInch) * 2.54f
        return calculateBMI(weightInKg, heightInCm)
    }
}