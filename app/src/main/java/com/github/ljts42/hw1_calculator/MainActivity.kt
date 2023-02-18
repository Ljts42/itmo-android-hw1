package com.github.ljts42.hw1_calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.ljts42.hw1_calculator.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var hasDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listOf(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9
        ).forEach { initNumber(it) }

        listOf(
            binding.buttonAdd, binding.buttonSub, binding.buttonMul, binding.buttonDiv
        ).forEach { initOperation(it) }

        binding.buttonDot.setOnClickListener {
            if (binding.prevText.text.isEmpty() || binding.prevText.text.last() in "+-*/") {
                if (binding.resultText.text.last() == '-') {
                    binding.resultText.append("0")
                }
                if (!hasDot) {
                    binding.resultText.append(binding.buttonDot.text)
                }
            } else {
                binding.prevText.append(binding.resultText.text)
                binding.resultText.text = "0."
            }
            hasDot = true
        }

        binding.buttonClear.setOnClickListener {
            binding.prevText.text = ""
            binding.resultText.text = "0"
            hasDot = false
        }

        binding.buttonDel.setOnClickListener {
            if (binding.resultText.text.length == 1) {
                if (binding.prevText.text.isEmpty()) {
                    binding.resultText.text = "0"
                } else if (binding.prevText.text.last() in "+-*/") {
                    binding.resultText.text = binding.prevText.text.last().toString()
                    binding.prevText.text = binding.prevText.text.dropLast(1)
                } else {
                    binding.resultText.text = binding.prevText.text
                    binding.prevText.text = ""
                    hasDot = binding.buttonDot.text in binding.resultText.text
                }
            } else {
                if (binding.resultText.text.last() == '.') {
                    hasDot = false
                }
                binding.resultText.text = binding.resultText.text.dropLast(1)
            }
        }

        binding.buttonCopy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(binding.resultText.text, binding.resultText.text)
            clipboard.setPrimaryClip(clip)
        }

        binding.buttonEq.setOnClickListener {
            if (binding.prevText.text.isNotEmpty()) {
                if (binding.prevText.text.last() in "+-*/") {
                    if (binding.resultText.text.last() == '.') {
                        binding.resultText.append("0")
                    }
                    val left =
                        binding.prevText.text.dropLast(1).toString().replace(binding.buttonDot.text.toString(), ".")
                            .toBigDecimal()
                    val right = binding.resultText.text.toString().replace(binding.buttonDot.text.toString(), ".")
                        .toBigDecimal()
                    val res = when (binding.prevText.text.last()) {
                        '*' -> left.multiply(right)
                        '-' -> left.subtract(right)
                        '/' -> {
                            if (right.compareTo(BigDecimal.ZERO) == 0) {
                                BigDecimal.ZERO
                            } else {
                                left.divide(right, 5, RoundingMode.HALF_UP)
                            }
                        }
                        else -> left.plus(right)
                    }
                    binding.resultText.text =
                        DecimalFormat("0.#####").format(res).replace(".", binding.buttonDot.text.toString())
                } else {
                    binding.resultText.text = binding.prevText.text
                }
                hasDot = '.' in binding.resultText.text
                binding.prevText.text = ""
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(HAS_DOT, hasDot)
        outState.putString(PREV_LINE, binding.prevText.text.toString())
        outState.putString(RESULT_LINE, binding.resultText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        hasDot = savedInstanceState.getBoolean(HAS_DOT)
        binding.prevText.text = savedInstanceState.getString(PREV_LINE)
        binding.resultText.text = savedInstanceState.getString(RESULT_LINE)
    }

    companion object {
        private const val HAS_DOT = "ljts42.MainActivity.has_dot"
        private const val PREV_LINE = "ljts42.MainActivity.prev_line"
        private const val RESULT_LINE = "ljts42.MainActivity.result_line"
    }

    private fun initNumber(button: Button) {
        button.setOnClickListener {
            if (binding.resultText.text.toString() == "0") {
                binding.resultText.text = button.text
            } else if (binding.prevText.text.isEmpty() || binding.prevText.text.last() in "+-*/") {
                binding.resultText.append(button.text)
            } else {
                binding.prevText.append(binding.resultText.text.last().toString())
                binding.resultText.text = button.text
            }
        }
    }

    private fun initOperation(button: Button) {
        button.setOnClickListener {
            if (binding.resultText.text.toString() == "-") {
                binding.resultText.text = "0"
            }
            if (binding.prevText.text.isEmpty()) {
                binding.prevText.text = binding.resultText.text
                hasDot = false
            } else if (binding.prevText.text.last() in "+-*/") {
                if (binding.resultText.text.last() == '.') {
                    binding.resultText.append("0")
                }
                val left =
                    binding.prevText.text.dropLast(1).toString().replace(binding.buttonDot.text.toString(), ".")
                        .toBigDecimal()
                val right = binding.resultText.text.toString().replace(binding.buttonDot.text.toString(), ".")
                    .toBigDecimal()
                val res = when (binding.prevText.text.last()) {
                    '*' -> left.multiply(right)
                    '-' -> left.subtract(right)
                    '/' -> {
                        if (right.compareTo(BigDecimal.ZERO) == 0) {
                            BigDecimal.ZERO
                        } else {
                            left.divide(right, 5, RoundingMode.HALF_UP)
                        }
                    }
                    else -> left.plus(right)
                }
                binding.prevText.text =
                    DecimalFormat("0.#####").format(res).replace(".", binding.buttonDot.text.toString())
            }
            binding.resultText.text = button.text
        }
    }
}