package com.abg.pyi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextCode = findViewById<EditText>(R.id.editTextCode)
        val buttonRun = findViewById<Button>(R.id.buttonRun)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        buttonRun.setOnClickListener {
            val code = editTextCode.text.toString()
            if (code.isNotBlank()) {
                val result = executePythonCode(code)
                textViewResult.text = result
            }
        }
    }

    private fun executePythonCode(code: String): String {
        return try {
            val py = Python.getInstance()
            val executor = py.getModule("executor")
            executor.callAttr("execute", code).toString()
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }
}