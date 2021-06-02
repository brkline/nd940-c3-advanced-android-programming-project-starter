package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*



class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        getIntentExtras()
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun getIntentExtras() {
        if (intent.hasExtra(DOWNLOAD_STATUS))
            if (intent.getBooleanExtra(DOWNLOAD_STATUS, false)) {
                status.text = getString(R.string.download_success_text)
                status.setTextColor(getColor(R.color.download_success_color))
            } else {
                status.text = getString(R.string.download_error_text)
                status.setTextColor(getColor(R.color.download_error_color))
            }
        if (intent.hasExtra(DOWNLOAD_NAME)) {
            name.text = intent.getStringExtra(DOWNLOAD_NAME)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
