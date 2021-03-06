package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CommunityFeedAdapter
import com.konkuk.select.model.Feed
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_community.*

class CommunityFragment : Fragment() {
    private lateinit var communityFeedAdapter:CommunityFeedAdapter
    var myFeedUser: ArrayList<Feed> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myFeedUser.add(Feed("minimal_125", "#미니멀룩 #심플 #캠퍼스룩", "", 130))
        myFeedUser.add(Feed("syoung", "#미니멀룩 #베이직룩", "", 126))
        myFeedUser.add(Feed("leeesun", "#캠퍼스룩 #미니멀룩", "", 320))
        getFeedList()
        settingAdapter()
    }

    private fun settingAdapter() {
        feed_rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        communityFeedAdapter = CommunityFeedAdapter(myFeedUser)
        feed_rv.adapter = communityFeedAdapter
    }

    // temp
    fun getFeedList(){
        Fbase.db.collection("feedImgUri")
            .get().addOnSuccessListener {
                for((i, doc) in it.documents.withIndex()){
                    if(myFeedUser.size < i+1) break
                    myFeedUser[i].img = doc["uri"].toString()
                }
                communityFeedAdapter.notifyDataSetChanged()
            }
    }
}
