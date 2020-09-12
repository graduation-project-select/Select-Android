package com.konkuk.select.activity

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.konkuk.select.R
import kotlinx.android.synthetic.main.activity_add_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*

class AddCodiActivity : AppCompatActivity() {

    private val IMAGEVIEW_TAG="드래그 이미지"
    private val TAG = "DragClickListener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi)
        setToolBar()
        
        // 드래그 앤 드롭
        dragImg.tag = IMAGEVIEW_TAG
        dragImg.setOnLongClickListener {
            var item = ClipData.Item(it.tag as CharSequence)
            var mimeTypes:Array<String> = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            var data = ClipData(it.tag.toString(), mimeTypes, item)
            var shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
            it.visibility = View.INVISIBLE

            true
        }

        codi_canvas.setOnDragListener(dragAndDropEventListener)
        bottom_view.setOnDragListener(dragAndDropEventListener)
    }

    fun setToolBar(){
        toolbar.title_tv.text = getString(R.string.activity_title_addCodi)
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "완료"

        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_tv.setOnClickListener {
            var nextIntent = Intent(this, AddCodiRegisterActivity::class.java)
            startActivity(nextIntent)
            finish() // 뒤로가기 해야되니깐 finish 하면 안되는데 일단..!
        }
    }

    val dragAndDropEventListener = View.OnDragListener { v, event ->
        when(event.action){
            // 이미지를 드래그 시작될 때
            DragEvent.ACTION_DRAG_STARTED->{
                Log.d(TAG, "ACTION_DRAG_STARTED")
            }
            // 드래그한 이미지를 옮기려는 지역으로 들어왔을 때
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d(TAG, "ACTION_DRAG_ENTERED")
            }
            // 드래그한 이미지가 영역을 빠져나갈때
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d(TAG, "ACTION_DRAG_EXITED")
            }
            // 이미지를 드래그해서 드랍시켰을 때
            DragEvent.ACTION_DROP -> {
                Log.d(TAG, "ACTION_DROP")
                when(v){
                    codi_canvas -> {
                        dropAtCodiCanvas(v,event)
                    }
                    bottom_view -> {
                        dropAtBottomView(v,event)
                    }
                    else -> {
                        v.visibility = View.VISIBLE
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d(TAG, "ACTION_DRAG_ENDED")
            }


            else -> {}
        }
        true
    }

    fun dropAtCodiCanvas(v:View, event:DragEvent){
        Log.d(TAG, "여기는 codi_canvas")
        var view:View = event.localState as View
        var viewgroup = view.parent as ViewGroup
        viewgroup.removeView(view)
        Toast.makeText(this,"이미지가 codi_canvas에 드랍되었습니다.", Toast.LENGTH_SHORT).show()

        var containView:ConstraintLayout = v as ConstraintLayout
        view.x = 100F
        view.y = 100F
        v.addView(view)
        view.visibility = View.VISIBLE
    }

    fun dropAtBottomView(v:View, event:DragEvent){
        Log.d(TAG, "여기는 bottom_view")
        var view:View = event.localState as View
        var viewgroup = view.parent as ViewGroup
        viewgroup.removeView(view)
        Toast.makeText(this,"이미지가 bottom_view에 드랍되었습니다.", Toast.LENGTH_SHORT).show()
        view.x = 300F
        view.y = 100F
        var containView:ConstraintLayout = v as ConstraintLayout
        v.addView(view)
        view.visibility = View.VISIBLE
    }
}