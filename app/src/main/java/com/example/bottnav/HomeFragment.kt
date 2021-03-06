package com.example.bottnav

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class HomeFragment : Fragment() {
    // 홈

    lateinit var home_tvNick: TextView
    lateinit var home_btnMusic: ImageButton
    lateinit var home_tvDo: TextView
    lateinit var home_tvDone: TextView
    lateinit var tvTip: TextView
    lateinit var NICK: String
    lateinit var ivSprout: ImageView
    lateinit var dbManager: DBManager

    var missionDo: Int = 10
    var missionDone: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        home_tvNick = view.findViewById<TextView>(R.id.home_tvNick)
        home_btnMusic = view.findViewById<ImageButton>(R.id.home_btnMusic)
        home_tvDo = view.findViewById<TextView>(R.id.home_tvDo)
        home_tvDone = view.findViewById<TextView>(R.id.home_tvDone)
        tvTip = view.findViewById<TextView>(R.id.home_tvTip)
        ivSprout = view.findViewById<ImageView>(R.id.home_ivSprout)  //tvDone수에 따라 이미지 바뀌도록 연결

        dbManager = DBManager(view.context)

        //SharedPreferences로 닉네임 가져오기
        val pref: SharedPreferences =
            requireActivity().getSharedPreferences("current", Context.MODE_PRIVATE)
        NICK = pref.getString("nickname", null).toString()
        home_tvNick.text = NICK  //login페이지에서 넘어온 닉네임이 화면에 보임

        missionDo = pref.getInt("aimLevel", 0)  //목표 레벨 (sharedPrefereces사용)가져오기
        home_tvDo.text = missionDo.toString()

        missionDone = dbManager.getLevel()  //현재 레벨 (db 사용)가져오기
        home_tvDone.text = missionDone.toString()

        //음악 버튼
        home_btnMusic.setOnClickListener {
            //실행 중인지 확인하는 코드 추가하기
            if ((activity as MainActivity).isplaying()) { //실행 중이라면 중단
                (activity as MainActivity).mPause()
                home_btnMusic.setImageResource(R.drawable.ic_baseline_music_off_24)
            } else {
                (activity as MainActivity).mStart()
                home_btnMusic.setImageResource(R.drawable.ic_baseline_music_note_24)
            }
        }
        if (!(activity as MainActivity).isplaying()) {  //실행 중이지 않은 상태면 음악이 꺼진 버튼이 보임. (프래그먼트끼리 이동시 오류 해결)
            home_btnMusic.setImageResource(R.drawable.ic_baseline_music_off_24)
        }

        //db에서 팁 배열 가져옴
        val tip = dbManager.getTips("tip")

        //랜덤 수 만들기
        var tipSize = (tip!!.size).toInt()
        val random = (0 until tipSize).random()

        //팁 배열에서 랜덤으로 값 가져와서 text가 바뀜
        tvTip.text = tip[random]


        //친구에게 공유(자랑)할 수 있는 dialog 생성
        val dialog = HomeCharDialog(view.context)

        //커스텀 다이얼로그를 함수로 묶어둠
        fun popupDialog(resChar: Int) {
            dialog.showDialog(resChar)
            dialog.setOnClickedListener(object : HomeCharDialog.ButtonClickListener {
                //공유버튼 클릭시
                override fun onClicked() {  //CharacterDialogFragment에서 생성한 함수 오버라이드를 통해 사용
                    //바텀시트 나옴
                    val bottomSheet = HomeCharBottomSheetFragment()
                    bottomSheet.isCancelable = false  //외부영역 터치로 사라지지 않음
                    bottomSheet.show(childFragmentManager, bottomSheet.tag)
                }
            })
        }

        //목표 레벨에 따라 이미지 바꿈(프래그먼트간 이동에 값 초기화 방지)
        when (missionDo) {
            10 -> {
                ivSprout.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.sprout,
                        view.context.theme
                    )
                )
            }
            30 -> {
                ivSprout.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.tree,
                        view.context.theme
                    )
                )
            }
            60 -> {
                ivSprout.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.appletrees,
                        view.context.theme
                    )
                )
            }
            100 -> {
                ivSprout.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.forest,
                        view.context.theme
                    )
                )
                //모든 미션을 달성한 경우 (100/100인 경우)
                if (missionDone == 100) {
                    ivSprout.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.healthy_earth,
                            view.context.theme
                        )
                    )
                }
            }
        }

        //sharedPreference 수정 (목표 개수)
        val editor = pref.edit()
        fun aimLevelChange(aim: Int) {
            editor.remove("aimLevel")
            editor.putInt("aimLevel", aim)
            editor.apply()
        }

        //목표 성취 개수를 (레벨이)넘은 경우
        if (missionDone >= missionDo) {

            when (missionDone) {  //성취 개수
                in 10..29 -> {   // 10~29개를 달성한 경우
                    missionDo = 30  //다음 달성 목표
                    aimLevelChange(missionDo)    //목표 개수 수정
                    home_tvDo.text = missionDo.toString()  //달성 목표 화면에 반영
                    ivSprout.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.tree,
                            view.context.theme
                        )
                    )  //캐릭터 이미지 바꿈
                    ivSprout.layoutParams.width = 140  //너비 크게 수정
                    popupDialog(R.drawable.tree)
                }
                in 30..59 -> {
                    missionDo = 60  //다음 달성 목표
                    aimLevelChange(missionDo)
                    home_tvDo.text = missionDo.toString()
                    ivSprout.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.appletrees,
                            view.context.theme
                        )
                    )
                    popupDialog(R.drawable.appletrees)
                }
                in 60..99 -> {
                    missionDo = 100  //다음 달성 목표
                    aimLevelChange(missionDo)
                    home_tvDo.text = missionDo.toString()
                    ivSprout.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.forest,
                            view.context.theme
                        )
                    )
                    popupDialog(R.drawable.forest)
                }
                100 -> {
                    home_tvDo.text = missionDo.toString()
                    ivSprout.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.healthy_earth,
                            view.context.theme
                        )
                    )
                    popupDialog(R.drawable.healthy_earth)
                }
            }
        }

        return view
    }
}