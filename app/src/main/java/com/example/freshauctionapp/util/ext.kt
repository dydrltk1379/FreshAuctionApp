package com.example.freshauctionapp.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/* 화면에 출력되어있는 소프트키보드를 숨김으로 전환
   - 안드로이드 폰에서 제공하는 키보드는 크게 하드웨어 키보드와 소프트 키보드로 구분
   - 소프트 키보드를 제어할 때 InputMethodManage 클래스 사용
     . 키보드를 보여줄 때: inputMethodManager.showSoftInput()
     . 키보드를 감출 때: inputMethodManager.hideSoftInputFromWindow()
 */
fun Fragment.hideKeyboard() {
    /* inputMethodManager 객체 가져오기(SystemService에서 가져옴). */
    val inputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    /* 현재 사용자가 보고있는 current 화면을 가져옴. */
    requireActivity().currentFocus?.let { focusView ->
        /* 화면에 출력되어있는 소프트 키보드를 숨김으로 전환*/
        inputMethodManager.hideSoftInputFromWindow(
            focusView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
