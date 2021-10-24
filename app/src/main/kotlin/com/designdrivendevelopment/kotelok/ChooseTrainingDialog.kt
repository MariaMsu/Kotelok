package com.designdrivendevelopment.kotelok

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment

class ChooseTrainingDialog: DialogFragment() {
    var selectedSimulator: String? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater: LayoutInflater? = activity?.layoutInflater
        val view: View? = inflater?.inflate(R.layout.choose_training_dialog, null)
        val cards: ConstraintLayout? = view?.findViewById(R.id.cl_cards)
        val choice: ConstraintLayout? = view?.findViewById(R.id.cl_choice)
        val write: ConstraintLayout? = view?.findViewById(R.id.cl_write)
        val btn: Button? = view?.findViewById(R.id.ok)
        val b: AlertDialog.Builder = AlertDialog.Builder(activity)
        cards?.setOnClickListener{
            cards.setBackgroundColor(resources.getColor(R.color.gray))
            choice?.setBackgroundColor(resources.getColor(R.color.white))
            write?.setBackgroundColor(resources.getColor(R.color.white))
            btn?.visibility = View.VISIBLE
            selectedSimulator = "cards"
        }
        choice?.setOnClickListener{
            cards?.setBackgroundColor(resources.getColor(R.color.white))
            choice.setBackgroundColor(resources.getColor(R.color.gray))
            write?.setBackgroundColor(resources.getColor(R.color.white))
            btn?.visibility = View.VISIBLE
            selectedSimulator = "choice"
        }
        write?.setOnClickListener{
            cards?.setBackgroundColor(resources.getColor(R.color.white))
            choice?.setBackgroundColor(resources.getColor(R.color.white))
            write.setBackgroundColor(resources.getColor(R.color.gray))
            btn?.visibility = View.VISIBLE
            selectedSimulator = "write"
        }
        btn?.setOnClickListener{
            val message = when(selectedSimulator){
                "cards" -> "выбраны карточки"
                "choice" -> "выбрано соответствие"
                "write" -> "выбран ввод слова"
                else -> "чето еще"
            }
            Log.i("search", message)
        }
        b.setView(view)
        return b.create()
    }
}
