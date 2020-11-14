package com.konkuk.select.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.konkuk.select.R
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.USERS_REF
import kotlinx.android.synthetic.main.fragment_my_page.*


class MyPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(contentLayout)
        bottomSheetBehavior.isHideable = false

//        var backLayout = ll.findViewById<ConstraintLayout>(R.id.back_layout)
//        backLayout.viewTreeObserver.addOnGlobalLayoutListener {
//            bottomSheetBehavior.peekHeight = (backLayout.height - (mytag1.y + mytag1.height+ mytag1.marginBottom)).toInt()
//            content_body.minimumHeight = bottomSheetBehavior.peekHeight - content_header.height
//        } // TODO 계속  backLayout null 오류;;

        // viewPager
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false


        btn_feed.setOnClickListener {
            viewPager.currentItem = 0
            setHeaderTextColor()
        }
        btn_codi.setOnClickListener {
            viewPager.currentItem = 1
            setHeaderTextColor()
        }
        btn_calendar.setOnClickListener {
            viewPager.currentItem = 2
            setHeaderTextColor()
        }

        viewPager.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            setHeaderTextColor()
        }
    }

    fun setHeaderTextColor() {
        when(viewPager.currentItem){
            0 -> {
                btn_feed.setTextColor(ContextCompat.getColor(btn_feed.context, R.color.colorPrimary))
                btn_codi.setTextColor(ContextCompat.getColor(btn_codi.context, R.color.colorText))
                btn_calendar.setTextColor(ContextCompat.getColor(btn_calendar.context, R.color.colorText))
            }
            1 -> {
                btn_feed.setTextColor(ContextCompat.getColor(btn_feed.context, R.color.colorText))
                btn_codi.setTextColor(ContextCompat.getColor(btn_codi.context, R.color.colorPrimary))
                btn_calendar.setTextColor(ContextCompat.getColor(btn_calendar.context, R.color.colorText))
            }
            2 -> {
                btn_feed.setTextColor(ContextCompat.getColor(btn_feed.context, R.color.colorText))
                btn_codi.setTextColor(ContextCompat.getColor(btn_codi.context, R.color.colorText))
                btn_calendar.setTextColor(ContextCompat.getColor(btn_calendar.context, R.color.colorPrimary))
            }
        }
    }

    fun getUserInfo(){
        Fbase.uid?.let {
            USERS_REF.document(it).get().addOnSuccessListener {userObj ->
                if(userObj.exists()){
                    userObj.get("name")?.let {
                        tv_userNickname.text = it.toString()
                    }

                    userObj.get("tag1")?.let{
                        if(it.toString() != ""){
                            mytag1.text = "# $it"
                        }else{
                            mytag1.text = ""
                        }
                    }
                    userObj.get("tag2")?.let{
                        if(it.toString() != ""){
                            mytag2.text = "# $it"
                        }else{
                            mytag2.text = ""
                        }
                    }
                    if(mytag1.text == "" && mytag2.text == ""){
                        mytag1.text = "#_나의_대표_스타일_태그를_추가해보세요"
                    }
                    if(userObj.get("tag1") == null){
                        mytag1.text = ""
                    }
                    if(userObj.get("tag2") == null){
                        mytag2.text = ""
                    }

                }
            }
        }
    }


    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> MypageFeedFragment()
                1 -> MypageCodiFragment()
                2 -> MypageCalendarFragment()
                else->MypageFeedFragment()
            }
        }
    }

}
