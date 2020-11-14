package com.konkuk.select.fragment

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.konkuk.select.R
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.getCodi
import kotlinx.android.synthetic.main.dialog_today_codi.view.*
import kotlinx.android.synthetic.main.fragment_mypage_calendar.*
import java.util.*

class MypageCalendarFragment : Fragment() {

    lateinit var codiAlertDialogView:View
    lateinit var codiAlertDialogBuilder:AlertDialog.Builder
    lateinit var codiAlertDialog:AlertDialog

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
        initCodiDialog()
    }

    fun getCodiDataOfSelectedDate(year: Int, month:Int, date:Int){
        Fbase.CODI_REF.whereEqualTo("uid", Fbase.uid)
            .whereEqualTo("year", year)
            .whereEqualTo("month", month)
            .whereEqualTo("date", date)
            .get().addOnSuccessListener {
                Log.d("달력에 해당하는 옷", "${it.documents}")
                if(it.documents.isEmpty()){
                    Toast.makeText(activity, "등록된 코디가 없습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    showCodiDialog(getCodi(it.documents[0]))
                }
            }
    }

    fun initCodiDialog(){
        codiAlertDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_today_codi, RelativeLayout(activity))
        codiAlertDialogBuilder = AlertDialog.Builder(activity, R.style.ThemeOverlay_AppCompat_Dialog)
            .setView(codiAlertDialogView)
        codiAlertDialog = codiAlertDialogBuilder.create()

        // (cardCornerRadius 주면 뒤에 흰배경이 보임)
//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//            codiAlertDialogView.cv_wrapper.background.alpha = 0;
//        } else {
//            codiAlertDialogView.cv_wrapper
//                .setBackgroundColor(ContextCompat.getColor(codiAlertDialogView.cv_wrapper.context, android.R.color.transparent))
//        }
    }

    fun showCodiDialog(selectedCodi: Codi){
        var headerStr = "${selectedCodi.year}년 ${selectedCodi.month}월 ${selectedCodi.date}일 코디"

        codiAlertDialogView.tv_headerTitle.text = headerStr
        Glide.with(codiAlertDialogView.tv_headerTitle.context)
            .load(selectedCodi.imgUri).into(codiAlertDialogView.iv_codi)

        codiAlertDialog.show()
    }

}
