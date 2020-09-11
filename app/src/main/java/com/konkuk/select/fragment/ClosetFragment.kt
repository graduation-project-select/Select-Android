package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesTwoByTwoAdapter
import kotlinx.android.synthetic.main.fragment_closet.*

class ClosetFragment(var ctx:Context) : Fragment() {

    lateinit var twoBytwoAdapter: ClothesTwoByTwoAdapter
    var temp_imgs:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_closet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        for(i in 0..10)
            temp_imgs.add("")
        rv.layoutManager = GridLayoutManager(ctx,2)
        twoBytwoAdapter = ClothesTwoByTwoAdapter(temp_imgs)
        rv.adapter = twoBytwoAdapter
        twoBytwoAdapter.itemClickListener = object:ClothesTwoByTwoAdapter.OnItemClickListener{
            override fun OnClickItem(
                holder: ClothesTwoByTwoAdapter.ImageHolder,
                view: View,
                data: String,
                position: Int
            ) {
                Toast.makeText(ctx, "data: ${data}", Toast.LENGTH_SHORT).show()
            }

        }
    }

}
