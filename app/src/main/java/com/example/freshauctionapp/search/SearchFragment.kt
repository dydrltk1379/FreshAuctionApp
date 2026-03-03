package com.example.freshauctionapp.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshauctionapp.R
import com.example.freshauctionapp.database.DatabaseModule
import com.example.freshauctionapp.databinding.FragmentSearchBinding
import com.example.freshauctionapp.model.Fruits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    var selectedFruit: String? = null
    var selectedDate: String? = null

    /* DatabaseModule.getDatabase(싱글톤)를 이용하여 데이터베이스를 가져오기 */
    private val database by lazy {
        DatabaseModule.getDatabase(requireContext())
    }

    /* SearchAdapter 생성  */
    private val searchAdapter by lazy { SearchAdapter(database.freshDao()) }

    /* todo2 - 사용자가 클릭할때 분류(과일)를 선택하는 Dialog 설정
       - alertDialog를 사용하여 과일을 선택하는 Dialog 설정
       - AlertDialog에 setItems(컬렉션, 리스너)를 사용하면 SingleChoice Dialog를 만들 수 있음
       - setItems(CharSequence[] items, final OnClickListener listener)
     */
    /* -----------------------Alert Dialog------------------------*/
    /* 과일 선택을 위한 Dialog 설정. */
    private val alertDialog by lazy {
        /* Dialog의 builder 클래스를 초기화 */
        val builder = AlertDialog.Builder(requireContext())
        /* builder에 Dialog 제목을 설정 */
        builder.setTitle("농산물을 선택해주세요.")

        /* 분류 Dialog에서 과일을 선택하면, 해당 과일의 상수명을 반환하도록 builder 설정
            - map: 컬렉션 내 인자를 변환하여 반환
            - toTypedArray(): 컬렉션을 배열로 변환
            - OnClickListener를 통해 선택된 아이템의 index를 넘겨 받아, Fruits.values()[index]로
              선택한 과일의 상수명을 selectedFruit에 저장
        */
        builder.setItems(Fruits.values().map { it.holder }.toTypedArray()) { _, index ->
            /*  선택한 Enum의 키값(상수명)을 String 형태로 리턴 */
            with(Fruits.values()[index]) {
                selectedFruit = this.name//상수명(APPLE)

                /* 선택한 과일명을 선택창에 표시 */
                binding.textType.text = this.holder//과일명(사과)
            }
            Log.i("FRESH", "which: $index - $selectedFruit")//0 - APPLE

            /* 검색버튼의 색상을 변경하는 함수 호출 */
            checkCondition()

            //changeInputTextBydate()
        }

        /* builder에 취소 버튼을 설정(이벤트는 Null로 설정) */
        builder.setNegativeButton("취소", null)

        /* AlertDialog를 생성하여 반환 */
        builder.create()
    }
    /* -----------------------Alert Dialog end ------------------------*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* 리사이클러뷰의 구분선(Divider)를 설정 */
        DividerItemDecoration(
            requireContext(),
            LinearLayoutManager(requireContext()).orientation
        ).run {
            //리사이클러뷰(list_save)에 구분선(Divider) 추가
            binding.listSearch.addItemDecoration(this)
        }

        //검색 농산물, 날짜 초기화
        selectedFruit= null
        selectedDate= null

        /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
        binding.listSearch.adapter = searchAdapter
        binding.listSearch.layoutManager = LinearLayoutManager(requireContext())

        /* todo1 - 사용자가 layout_type([농산물을선택해주세요] 레이아웃)을 클릭한 경우 이벤트 처리
            - [농산물을 선택해주세요] 레이아웃을 클릭하면 항목을 선택하는 Dialog를 출력하는 AlertDialog를 실행
        */
        binding.layoutType.setOnClickListener { alertDialog.show() }

        //todo3 - 검색 날짜 Dialog를 띄워주기
        /*[날짜를 선택하세요] 레이아웃을 클릭했을경우 */
        binding.layoutDate.setOnClickListener {
            /* Calender 객체 생성(현재날자를 기준으로) */
            val currentCaldenar =
                Calendar.getInstance().apply { time = Date(System.currentTimeMillis()) }

            /* 날짜 선택을 위한  DatePickerDialog 띄우기 */
            DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    currentCaldenar.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.run {
                        /* 선택한 데이터를 2022-01-01와 같은 형식으로 가져오기 */
                        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                        /* 날짜 선택 여부를 체크하여 처리하기 위한 함수 호출
                           - 선택한 날짜를 선택창에 표시 */
                        changeInputTextBydate()
                    }
                },
                /* DatePickerDialog의 Date를 오늘 날짜로 초기화*/
                currentCaldenar.get(Calendar.YEAR),
                currentCaldenar.get(Calendar.MONTH),
                currentCaldenar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        /* PagingData 빌드 및 생성(LiveData)
          - 페이징 구성을 정의한 PagingConfig를 기반으로 LiveData<PagingData>를 생성
          - PagingConfig(pageSize, initialKey,...)
            .pageSize: 각 페이지에 로드할 데이터 수(필수 매개변수)
          - FreshDao에 정의한 DataSource.Factory를 이용하여 생성한 DataSource 를,
            asPagingSourceFactory()를 이용하여 LiveData<PagingData> 형태로 변환
       */
        val pageLiveData = Pager(
            PagingConfig(20), 1,
            database.freshDao().loadSaveItems().asPagingSourceFactory(Dispatchers.IO)
        )

        /* pagingData(LiveData)에 observe() 메서드로 Observer 객체 등록(데이터 변경시 자동 호출)
         - LiveData를 관찰하여 PageData에 변경이 있을 시 Adapter에 전달
         - owner : Observer를 제어하는 LifecycleOwner
         - Observer : LiveDta 로부터 이벤트를 수신하는 Observer 객체(데이터가 변경되면 호출됨)
       */
        pageLiveData.liveData.observe(viewLifecycleOwner) {

            /* 변경된 pagingData를 NoteAdapter에 전달
              - pagingData를 submit하면 NoteAdapter 의 DIFF_CALLBACK은
                기존에 pagingData가 존재하면 그 차이를 비교한 후 리사이클러뷰를 새로운 페이지로 갱신
           */
            lifecycleScope.launch { searchAdapter.submitData(it) }
        }

        /* todo4 - 사용자가 검색 버튼(btn_search)을 클릭했을경우 이벤트 처리 */
        binding.btnSearch.setOnClickListener {
            /* 과일과 날짜를 선택했는지 검증
               - selectedFruit: 과일의 상수명(APPLE, GRAPE, WATERMELON, ...)
               - selectedDate:  날짜(YYYY-MM-DD)  */
            if (selectedDate == null || selectedFruit == null) {
                /* 선택이 안된 경우 에러메세지를 띄우기*/
                Toast.makeText(requireContext(), "분류와 날짜를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                Log.i("SELECT_DATE", selectedDate!!)
                Log.i("SELECT_FRUIT", selectedFruit!!)

                /* 검색 요청을 위한 과일/일자/수량을 Bundle 객체에 ResultFragemtnt에 전달
                  - ResultFragment로 화면 전환 */
                findNavController().navigate(R.id.action_searchFragment_to_resultFragment,
                    Bundle().apply {
                        putString("SELECT_DATE", selectedDate)
                        putString("SELECT_FRUIT", selectedFruit)
                        putString("RESULT_AMOUNT",
                            view.findViewById<RadioButton>(binding.radioLayout.checkedRadioButtonId).tag.toString()
                        )
                    })
            }
        }
    }

    /* 날짜 선택 여부를 체크하여 선택한 날짜를 선택창에 표시하는 함수*/
    private fun changeInputTextBydate() {
        /* 분류와 날짜를 모두 선택하면 검색버튼의 색상을 변경하는 함수호출 */
        checkCondition()
        /*Date를 선택하면 날짜 선택창에 날짜 표시*/
        selectedDate?.let { binding.txtGongpan.text = it }
    }

    /* 분류와 날짜를 모두 선택하면 검색버튼과 글자의 색상을 변경하는 함수 */
    private fun checkCondition() {
        if (selectedDate != null && selectedFruit != null) {
            binding.btnSearch.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            binding.btnSearch.setTextColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
