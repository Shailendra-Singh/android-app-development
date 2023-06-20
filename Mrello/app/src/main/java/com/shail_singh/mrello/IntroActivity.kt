package com.shail_singh.mrello

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val typeFace: Typeface =
            Typeface.createFromAsset(assets, "kablammo-regular-variable-font.ttf")
        val view: TextView = findViewById(R.id.tv_intro_brand_name)
        view.typeface = typeFace
    }
}