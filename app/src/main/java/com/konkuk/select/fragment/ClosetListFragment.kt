package com.konkuk.select.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.*
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.konkuk.select.R
import com.konkuk.select.activity.AddClosetActivity
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.adpater.ClosetListBlockAdapter
import com.konkuk.select.model.Closet
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.fragment_closet_list.*
import kotlinx.android.synthetic.main.fragment_closet_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class ClosetListFragment(val ctx: Context) : Fragment() {

    lateinit var closetListBlockAdapter:ClosetListBlockAdapter
    var closetList:ArrayList<Closet> = ArrayList()

    val TAG = "ClosetListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_closet_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingToolBar()
        settingAdapter()
        settingOnClickListener()
        getClosetData()
    }

    private fun settingToolBar(){
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            fragmentManager?.let {
                it.beginTransaction()
                    .replace(R.id.ll, ClosetFragment(ctx))
                    .commit()
            }
        }
    }

    private fun settingAdapter(){
        rv_closet_list.layoutManager = GridLayoutManager(ctx, 2)
        closetListBlockAdapter = ClosetListBlockAdapter(ctx, closetList)
        rv_closet_list.adapter = closetListBlockAdapter
        closetListBlockAdapter.itemClickListener = object :ClosetListBlockAdapter.OnItemClickListener{
            override fun onClickItem(
                holder: ClosetListBlockAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                fragmentManager?.let {
                    it.beginTransaction()
                        .replace(R.id.ll, ClosetFragment(ctx).apply {
                            arguments = Bundle().apply {
                                putString("closetId", data.id)
                                putString("closetTitle", data.name)
                            }
                        })
                        .commit()
                }
            }

            override fun onClickShareBtn(
                holder: ClosetListBlockAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                sendShareLink(data)
            }
        }
    }

    private fun settingOnClickListener() {
        addClosetBtn.setOnClickListener {
            startActivity(Intent(ctx, AddClosetActivity::class.java))
        }
    }

    private fun getClosetData(){
        val closetRef  = Fbase.db.collection("closets").whereEqualTo("uid", Fbase.uid)
        closetRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null) {
                closetList.clear()
                for(doc in documentSnapshot.documents){
                    val closetObj = Fbase.getCloset(doc)
                    closetList.add(closetObj)
                }
                closetListBlockAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun sendShareLink(closetObj:Closet){
        Toast.makeText(ctx, "옷장 공유 ${closetObj.id}", Toast.LENGTH_SHORT).show()

//        val sendIntent = Intent(Intent.ACTION_SEND)
//        val title: String = "Share this photo with"
//        val chooser: Intent = Intent.createChooser(sendIntent, title)

        // Verify the original intent will resolve to at least one activity
//        if (sendIntent.resolveActivity(packageManager) != null) {
//            startActivity(chooser)
//        }
        // kakaoLink()
    }

    fun kakaoLink() {
        val params = FeedTemplate
            .newBuilder(
                ContentObject.newBuilder(
                    "디저트 사진",
                    "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com").build()
                )
                    .setDescrption("아메리카노, 빵, 케익")
                    .build()
            )
            .setSocial(
                SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                    .setSharedCount(30).setViewCount(40).build()
            )
            .addButton(
                ButtonObject(
                    "웹에서 보기",
                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl(
                        "https://developers.kakao.com"
                    ).build()
                )
            )
            .addButton(
                ButtonObject(
                    "앱에서 보기", LinkObject.newBuilder()
                        .setWebUrl("'https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()
                )
            )
            .build()

        val serverCallbackArgs: MutableMap<String, String> =
            HashMap()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendDefault(
            ctx,
            params,
            serverCallbackArgs,
            object : ResponseCallback<KakaoLinkResponse?>() {
                override fun onFailure(errorResult: ErrorResult) {
                    Log.e("Kakao", errorResult.toString())
                }

                override fun onSuccess(result: KakaoLinkResponse?) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                }
            })
    }
}