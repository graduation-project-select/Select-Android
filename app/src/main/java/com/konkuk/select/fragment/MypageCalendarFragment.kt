package com.konkuk.select.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.konkuk.select.R
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_mypage_calendar.*
import java.util.*

class MypageCalendarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage_calendar, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cv_calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->
            getCodiDataOfSelectedDate(i, i2+1, i3)
        }
    }

    fun getCodiDataOfSelectedDate(year: Int, month:Int, date:Int){
        Fbase.CODI_REF.whereEqualTo("uid", Fbase.uid)
            .whereEqualTo("year", year)
            .whereEqualTo("month", month)
            .whereEqualTo("date", date)
            .get().addOnSuccessListener {
                Log.d("달력에 해당하는 옷", "${it.documents}")
                for(doc in it.documents){
                    Log.d("옷들", "${doc.id}")
                }
            }
    }

}
