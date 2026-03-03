package com.example.freshauctionapp.network

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/* object 키워드로 NetworkModule 객체를 생성
   - 공공DB 서버에 요청할 OkHttp Request 객체 생성
   - OkHttp3
     . HTTP 통신을 보다 쉽게 할 수 있도록 다양한 기능을 제공해주는 Android 및 Java 용 라이브러리
     . Square에서 제공하는 오픈소스 라이브러리로 동기, 비동기 방식 모두 제공
     . OkHttp를 이용하여 원하는 데이터 부분만 뽑아 별도의 파싱 없이 바로 사용 가능
 */
object NetworkModule {
    private val keyValue = "Your Service key(공공 DB 인증 key)"

    /* OkHttp 요청을 전송할 OkHttpClient 객체(서버에 검색 요청시 사용) 생성 */
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES) // readTimeout(읽기 지연시간) 설정
            .build()
    }

    /* HttpUrl 객체(REST API 요청을 전송할 URL 객체) 생성 함수
       - HttpUrl.Builder()를 이용하여 HttpUrl 객체(URI 구조) 정의
       - 공공DB API 명세에 맞춰 선언
    */
    fun makeHttpUrl(scode: String, date: String, amount: String): HttpUrl {
        //http://apis.data.go.kr/B552895/StatsInfoService/getDailyAdjStatsInfo
        val result =  HttpUrl.Builder()
            .scheme("http")
            .host("apis.data.go.kr")
            .addPathSegment("B552895")
            .addPathSegment("StatsInfoService")
            .addPathSegment("getDailyAdjStatsInfo")
            .addQueryParameter("delng_de", date.replace("-", "")) //통계 기간(-를 삭제)
            .addQueryParameter("numOfRows", amount)//한 페이지 결과수
            .addQueryParameter("pageNo", "1")//페이지 번호
            .addQueryParameter("std_prdlst_code", scode)//과일/야채 소분류(060103)
            .addQueryParameter("_returnType", "json")//리턴타입
            .build()

        return result
    }

    /* OkHttp Request 객체 생성 함수
       - Request.Builder()를 사용하여 요청을 위한 Request 생성
       - url: 요청서버 url
       - get/post: 전송방식
    */
    fun makeHttprequest(httpUrl: HttpUrl): Request {

        Log.i("URL","$httpUrl&serviceKey=$keyValue")//요청 url

        return Request.Builder()
            /*공공 DB 서버의 문제로 인해 하드코딩하여 KEY값을 넣음
            * - serviceKey에 %가 포함되어 빌더에 설정할 수 없기 때문에 하드 코딩
            * - %가 다른 용도로 사용되고 있기 때문에 키값 인식이 안되는 문제 발생 */
            //.url(httpUrl.toString() + "&serviceKey=$keyValue")
            .url("$httpUrl&serviceKey=$keyValue")
            .get().build()
    }
}
