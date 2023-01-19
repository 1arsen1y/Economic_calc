package com.example.calc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.calc.R
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

fun getMap(givenString: String): Map<Int, Double> {
    var res = mutableMapOf<Int, Double>()
    var current_string = ""
    val len = givenString.length
    for (j in 0..2) {
        res[j] = 0.0
    }
    var i = 0
    while (i < len) {
        if (givenString[i].equals('P')) {
            if (i + 2 < len && givenString[i + 1].equals('^') && givenString[i + 2].equals('2')) {
                if (current_string == "+" || current_string == "-" || current_string.isEmpty()) {
                    current_string += "1"
                }
                if (current_string[0].equals('+')) {
                    current_string = current_string.drop(1)
                }
                val cur_value = current_string.toDouble();
                i += 2
                res[2] = cur_value
                current_string = ""
            } else {
                if (current_string == "+" || current_string == "-" || current_string.isEmpty()) {
                    current_string += "1"
                }
                if (current_string[0].equals('+')) {
                    current_string = current_string.drop(1)
                }
                val cur_value = current_string.toDouble();
                current_string = ""
                res[1] = cur_value
            }
        } else if (i == len - 1 || givenString[i + 1].equals('+') || givenString[i + 1].equals('-')) {
            current_string += givenString[i]
            if (current_string.equals('+') || current_string.equals('-') || current_string.isEmpty()) {
                current_string += "1"
            }
            val cur_value = current_string.toDouble();
            res[0] = cur_value
            current_string = ""
        } else {
            current_string += givenString[i]
        }
        i += 1
    }
    return res
}

fun getStrWithoutSpace(givenString: String): String {
    var res = ""
    for (el in givenString) {
        if (!el.equals(' ')) {
            if (el.equals('p')) {
                res += 'P'
            } else {
                res += el
            }
        }
    }
    return res
}

fun solveOfEquation(indexes: List<Double>): Double {
    val a = indexes[2]
    val b = indexes[1]
    val c = indexes[0]
    if (a == 0.0) {
        if (b == 0.0) {
            return 1e12
        }
        return -c / b
    }
    val d = b * b - 4 * a * c
    if (d < 0) {
        return 1e12
    }
    return (-b + sqrt(d)) / (2 * a)
}

fun cutString(str: String): String {
    var res = ""
    var i = 0
    while (!str[i].equals('.')) {
        res += str[i]
        i += 1
    }
    res += '.'
    var cnt = 0
    i += 1
    while (cnt < 3 && i < str.length) {
        cnt += 1
        res += str[i]
        i += 1
    }
    return res
}

class DemandFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button: Button = view.findViewById(R.id.button_go_calc)

        val input_Qs: EditText = view.findViewById(R.id.input_Qs)
        val input_Qd: EditText = view.findViewById(R.id.input_Qd)

        val outputP: TextView = view.findViewById(R.id.outputP)
        val outputQ: TextView = view.findViewById(R.id.outputQ)
        val outputE: TextView = view.findViewById(R.id.outputE)

        button.setOnClickListener {
            if (input_Qs.text.isEmpty() || input_Qd.text.isEmpty()) {
                Toast.makeText(
                    view.context, "Одно из полей пустое",
                    Toast.LENGTH_LONG
                ).show()
            } else {
//                Toast.makeText(
//                    view.context, "Успешно",
//                    Toast.LENGTH_LONG
//                ).show()
                val Qs_value = getStrWithoutSpace(input_Qs.text.toString())
                val Qd_value = getStrWithoutSpace(input_Qd.text.toString())
                val indexesQs = getMap(Qs_value)
                val indexesQd = getMap(Qd_value)
                val res = mutableListOf(0.0, 0.0, 0.0)
                for (i in 0..2) {
                    res[i] += indexesQd[i].toString().toDouble()
                    res[i] -= indexesQs[i].toString().toDouble()
                }
                val res_Qd =
                    indexesQd[0].toString() + ", " + indexesQd[1].toString() + ", " + indexesQd[2].toString()
                val res_Qs =
                    indexesQs[0].toString() + ", " + indexesQs[1].toString() + ", " + indexesQs[2].toString()
                val res_indexes =
                    res[0].toString() + ", " + res[1].toString() + ", " + res[2].toString()
                Toast.makeText(
                    view.context, res_Qs + "\n" + res_Qd + "\n" + res_indexes,
                    Toast.LENGTH_LONG
                ).show()
                val p = solveOfEquation(res)
                if (p == 1e12) {
                    Toast.makeText(
                        view.context, "ОШИБКА!!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val q = indexesQd[2].toString().toDouble() * p * p + indexesQd[1].toString()
                        .toDouble() * p + indexesQd[0].toString().toDouble()
                    outputP.setText(cutString(p.toString()))
                    outputQ.setText(cutString(q.toString()))
                    if (q == 0.0) {
                        outputE.setText("-")
                    } else {
                        val del = p / q
                        val e = del * (2 * indexesQd[2].toString().toDouble() * p + indexesQd[1].toString()
                                .toDouble())
                        outputE.setText(cutString(e.toString()))
                    }
                }
            }
        }
    }
}