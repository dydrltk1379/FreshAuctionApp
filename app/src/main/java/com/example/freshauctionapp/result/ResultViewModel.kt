package com.example.freshauctionapp.result

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshauctionapp.model.FreshData
import com.example.freshauctionapp.model.FreshWrapper
import com.example.freshauctionapp.model.Fruits
import com.example.freshauctionapp.model.SaveItem
import com.example.freshauctionapp.network.NetworkModule
import com.example.freshauctionapp.util.SingleLiveEvent
import com.example.freshauctionapp.database.DatabaseModule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gildor.coroutines.okhttp.await

//ResultViewModel
class ResultViewModel() : ViewModel() {

    // JSON 라이브러리 모시(moshi) 플러그인 가져오기
    private val moshi: Moshi by lazy {
        //data class를 JSON처럼 다룰 수 있도록 해주는 Moshi 플러그인
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    //notificationMsg LiveData 선언 - SingleLiveEvent() 선언
    val notificationMsg: SingleLiveEvent<String> = SingleLiveEvent()
    //val notificationMsg: MutableLiveData<String> = MutableLiveData()

    /* resultList LiveData 선언 - SingleLiveEvent()  */
    private val resultList: SingleLiveEvent<List<FreshData>> = SingleLiveEvent()
    //private val resultList: MutableLiveData<List<FreshData>> = MutableLiveData()

    //resultList() getter 선언
    fun resultList(): LiveData<List<FreshData>> = resultList as MutableLiveData<List<FreshData>>

    //검색 결과를 SaveItem 테이블과 Fresh 테이블에 저장하는 함수
    fun saveResult(context: Context, saveName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            /* SaveItem 테이블에 경락가격 저장 */
            DatabaseModule.getDatabase(context).freshDao().insertSave(
                SaveItem(id = null, saveTitle = saveName)//saveName: "2022-06-01 사과 검색결과"
            ).run {
                //Fresh 테이블에 경락가격정보 저장
                resultList.value?.let { data ->
                    data.forEach { it.saveId = this }

                    Log.i("SAVERESULT", "data: $data")

                    DatabaseModule.getDatabase(context).freshDao().insertFresh(data)
                }
            }
        }
    }//end of saveResult

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.message?.let { Log.e("error", it) }
        notificationMsg.postValue(exception.message)
    }

    //Request 객체 생성 함수
    fun loadDataFromURL(
        selectDate: String,
        selectFruit: String,
        resultAmount: String
    ) {
        /* Request 객체 생성 */
        val httpUrl = NetworkModule.makeHttprequest(
            /* 분류코드(소분류), 검색일자, 검색수량을 인자로 HttpUrl 객체 생성 함수를 호출하여
               HttpUrl 객체 생성 */
            NetworkModule.makeHttpUrl(
                scode = Fruits.valueOf(selectFruit).scode,
                date = selectDate,
                amount = resultAmount
            )
        )

        Log.i("HTTP", httpUrl.toString())

        /* Coroutine을 이용하여 IO 스레드에서 경락가격정보서비스 서버에 요청  */
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
             Log.i("FRESH", httpUrl.url.toString())

            /* response(응답) 객체 - 경락가격정보 검색 요청 및 응답받기
               - client 객체의 newCall() 메서드 인자로 Request 객체를 전달하여 실행(요청) */
            val response = NetworkModule.client.newCall(httpUrl).await()

            /*  response(json) 데이터를 FreshData 형태로 맵핑 */
            val freshData =
                response.body?.string()?.let { mappingStringToFresh(it) } ?: emptyList()

            //freshData가 없으면 notificationMsg(LiveData) 저장
            if(freshData.isEmpty()){
                notificationMsg.postValue("데이터가 존재하지 않습니다.")
            }

            //resultList(LiveData) 저장
            resultList.postValue(freshData)
        }
    }//end of loadDataFromURL

    //Moshi(JSON 라이브러리)를 이용 FreshData 형태로 맵핑하기 위한 함수
    private fun mappingStringToFresh(jsonBody: String): List<FreshData> {
        // JSON 데이터를 데이터 클래스(FreshWrapper)에 맞게 맵핑하는 어댑터를 생성
        val freshStringToJsonAdapter = moshi.adapter(FreshWrapper::class.java)
        val freshResponse = freshStringToJsonAdapter.fromJson(jsonBody)//파싱

        Log.i("LIST", "$freshResponse")
        //I/LIST: FreshWrapper(list=[FreshData(id=null, saveId=null, lname=과실류, mname=사과,..)])

        return freshResponse?.list ?: emptyList()
    }//end of mappingStringToFresh
}
