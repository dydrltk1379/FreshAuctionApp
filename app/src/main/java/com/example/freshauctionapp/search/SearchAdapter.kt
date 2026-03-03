package com.example.freshauctionapp.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.freshauctionapp.R
import com.example.freshauctionapp.databinding.ListItemSaveitemBinding
import com.example.freshauctionapp.model.FreshDao
import com.example.freshauctionapp.model.SaveItem

/* 검색화면(SearchFragment)에서 저장된 데이터를 리사이클러뷰에 보여주는 어댑터
   - PagingDataAdapter: 페이징 데이터(PagingData)를 RecyclerView에 표시하기 위한 기본 클래스
   - 선언 시 SearchAdapter가 PagingDataAdapter를 상속하고, 초기화시 DIFF_CALLBACK 객체를 넘겨줌
   - PagingData는 LiveData로 observe 중, 변경이 발생할 때마다 submitData()를 이용하여
     변경된 pagingData를 이곳 SearchAdapter로 전달되며, 어댑터는 DiffUtil 객체를 이용하여
     전달된 new PagingData와 기존 old PagingData를 비교하여 UI 갱신(변경된 PagingData만 갱신)
* */
class SearchAdapter(val freshDao: FreshDao) :
    PagingDataAdapter<SaveItem, SearchAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemSaveitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(getItem(position))
        Log.d("GETITEM", "${getItem(position)}, SearchAdapter")
        //SaveItem(id=1, saveTitle=2020-06-19 사과 검색결과)
    }

    //뷰홀더 클래스 선언
    inner class ItemViewHolder(private val binding: ListItemSaveitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(saveItem: SaveItem?) {

            /* local DB에 저장된 경락가격정보의 목록을 리사이클러뷰로 보여줄 아이템뷰의 텍스트를 설정
               -  SaveItem 테이블에 저장된 경락가격정보 타이틀(saveTitle)을 itemView.txt_save_subject에 설정 */
            binding.txtSaveSubject.text = saveItem?.saveTitle

            //삭제 버튼 클릭하면 DB에서 해당 SaveItem 삭제
            binding.btnDelete.setOnClickListener {
                saveItem?.id?.let { freshDao.deleteSaveData(it) }
            }

            /* 검색결과 아이템뷰(2022-01-15 사과 검색결과)를 클릭하면, 내비게이션을 통해 saveFragment로
               화면(view)을 전환하여 DB에 저장된 검색 결과를 화면에 출력
               - 해당 saveItem의 id를 "SAVE_ID"로 Bundle 객체에 저장하여 saveFragment에 전달  */
            binding.txtSaveSubject.setOnClickListener {
                /* saveFragment로 화면 전환.*/
                Navigation.findNavController(itemView).navigate(
                    R.id.action_searchFragment_to_saveFragment,
                    Bundle().apply {
                        putLong("SAVE_ID", saveItem!!.id!!)
                    })
            }
        }
    }

    /* DiffUtil Callback 구현
     - page가 load될 때 callback을 수신하고, background thread에서 DiffUtil을 사용해,
       두 객체간의 차이점을 찾고 업데이트 되어야 할 pagingData를 반환
     - 기존에 PagedList 가 존재하면 그 차이를 비교한 후 리사이클러뷰를 새로운 페이지로 갱신
     - areItemsTheSame() : 두 객체가 같은 항목인지 비교
     - areContentsTheSame() : 두 항목의 데이터가 같은지 비교 */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SaveItem>() {
            override fun areItemsTheSame(oldConcert: SaveItem, newConcert: SaveItem): Boolean =
                oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: SaveItem, newConcert: SaveItem): Boolean =
                oldConcert.id == newConcert.id
        }
    }
}
