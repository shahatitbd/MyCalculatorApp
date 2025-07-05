package com.example.mycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                CalculatorUI()
            }
        }
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        val result = object : Any() {
            fun eval(): Double {
                val tokens = expression.replace("×", "*").replace("÷", "/")
                return tokens.toDoubleOrNull() ?: 0.0
            }
        }.eval()
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun safeEval(expression: String): String {
    return try {
        val result = expressionEngine(expression)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun expressionEngine(expr: String): Double {
    val clean = expr.replace("[^0-9+\\-*/.]".toRegex(), "")
    val tokens = clean.split("(?<=[-+*/])|(?=[-+*/])".toRegex())
    val stack = mutableListOf<Double>()
    var currentOp = "+"
    for (token in tokens) {
        val t = token.trim()
        if (t.isEmpty()) continue
        if (t in listOf("+", "-", "*", "/")) {
            currentOp = t
        } else {
            val num = t.toDoubleOrNull() ?: 0.0
            when (currentOp) {
                "+" -> stack.add(num)
                "-" -> stack.add(-num)
                "*" -> stack[stack.lastIndex] *= num
                "/" -> stack[stack.lastIndex] /= num
            }
        }
    }
    return stack.sum()
}


@Composable
fun CalculatorUI() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }

    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf(".", "0", "DEL", "+"),
        listOf("C", "=")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header Text
            Text(
                text = "Developed by Md. Shahabuddin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // History
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                history.takeLast(10).reversed().forEach {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = result,
                    fontSize = 28.sp,
                    color = Color.Yellow,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { symbol ->
                        CalculatorButton(symbol) {
                            when (symbol) {
                                "=" -> {
                                    result = try {
                                        val res = safeEval(expression)
                                        val formatted = "$expression = $res"
                                        history.add(formatted)
                                        "= $res"
                                    } catch (e: Exception) {
                                        "Error"
                                    }
                                }

                                "C" -> {
                                    expression = ""
                                    result = ""
                                }

                                "DEL" -> {
                                    if (expression.isNotEmpty()) {
                                        expression = expression.dropLast(1)
                                    }
                                }

                                else -> {
                                    expression += symbol
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    val isOperator = text in listOf("/", "*", "-", "+", "=", "DEL")

    Box(
        modifier = Modifier
            .padding(6.dp)
            .size(80.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(if (isOperator) Color(0xFF64B5F6) else Color(0xFFF5F5F5))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOperator) Color.White else Color.Black
        )
    }
}


