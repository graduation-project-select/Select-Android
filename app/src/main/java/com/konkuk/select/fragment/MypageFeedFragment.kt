package com.konkuk.select.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.konkuk.select.R
import com.konkuk.select.adpater.MyPageFeedAdapter
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_mypage_feed.*


class MypageFeedFragment : Fragment() {

    lateinit var MyPageFeedAdapter:MyPageFeedAdapter
    var feedList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getFeedList()
        MyPageFeedAdapter = MyPageFeedAdapter(feedList)
        rv_feeds.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        rv_feeds.adapter = MyPageFeedAdapter
    }

    fun getFeedList(){
        Fbase.db.collection("feedImgUri")
            .get().addOnSuccessListener {
                feedList.clear()
                for(doc in it.documents){
                    feedList.add(doc["uri"].toString())
                }
                MyPageFeedAdapter.notifyDataSetChanged()
            }
    }

}
