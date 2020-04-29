package com.prin.notes

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prin.notes.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContext(this)
    }

    companion object{
        var keyHashedString: String? = null
        private lateinit var context: Context
        fun setContext(con: Context) {
            context =con
        }

        fun getContext() : Context {
            return context
        }
    }

}
