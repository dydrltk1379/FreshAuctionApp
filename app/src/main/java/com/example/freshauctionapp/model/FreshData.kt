package com.example.freshauctionapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

//검색 결과(json string)를 FreshData 형태로 맵핑하기 위한 FreshWrapper 클래스(Moshi)
data class FreshWrapper(
    val list: List<FreshData>
)

//SaveItem Entity
@Entity(tableName = "SaveItem")
data class SaveItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var saveTitle: String
)

//Fresh Entity
@Entity(tableName = "Fresh",
    foreignKeys = [//외래키 설정
        ForeignKey(
        // 참조한 부모 Entity(SaveItem) 의 parentColumns(id)이 삭제될 경우 이를 참조하는 자식 Entity(Fresh)의 childColumns(saveId)도 함께 삭제
        onDelete = ForeignKey.CASCADE,
        entity = SaveItem::class,         // 외래키가 참조할 부모 Entity
        parentColumns = arrayOf("id"),   // 참조할 부모 Entity의 Key(필드명)
        childColumns = arrayOf("saveId") // 참조한 부모의 Key를 저장할 자식 Entity의 Key(parentColumns 개수와 동일하게 설정)
    )]
)
data class FreshData(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var saveId: Long? = null,

    /* 속성(필드)에 JSON 인코딩 방식 정의(Moshi-kotlin)
       - JSON 데이터의 name = "lclas_nm"을 필드 lname에 할당
     */
    @Json(name = "lclas_nm")//대분류명
    var lname: String,
    @Json(name = "mlsfc_nm")//중분류명
    var mname: String,
    @Json(name = "std_prdlst_nm")//표준품목명_구코드
    var sname: String,
    @Json(name = "std_unit_new_nm")//표준단위신규코드명
    var uname: String,
    @Json(name = "std_qlity_new_nm")//표준품질신규코드명
    var grade: String,
    @Json(name = "cpr_nm")//법인명_구코드
    var cprName: String,
    @Json(name = "sbid_pric_max")//최고가격
    var maxPrice: String,
    @Json(name = "sbid_pric_min")//최저가격
    var minPrice: String,
    @Json(name = "sbid_pric_avg")//평균경락가격_산술
    var avgPrice: String,
    @Json(name = "delng_qy")//거래수량
    var tradeAmt: String
)


