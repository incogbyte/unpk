package com.r0df.unpk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CommandsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commands)

        supportActionBar?.title = "ADB Commands (HELP)"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}