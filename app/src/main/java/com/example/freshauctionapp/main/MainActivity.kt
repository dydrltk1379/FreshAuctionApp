package com.example.freshauctionapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.freshauctionapp.R
import com.example.freshauctionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val controller = findNavController(R.id.navigation_host)

        /*Navigation 설정 */
        NavigationUI.setupActionBarWithNavController(this, controller,
            /* splashFragment, loginFragment, searchFragment, userFragment에서 뒤로가기(<-)를 표시하지 않음*/
            AppBarConfiguration.Builder(R.id.splashFragment, R.id.loginFragment, R.id.searchFragment, R.id.userFragment).build()
        )

        /* menu id(menu_bottom.xml)와 navigation graph(nav_graph.xml)의 대상 id와 매핑
            - menu id와 대상 id를 같게 선언해야 함
            - bottomNavigation의 menu id와 fragment id를  자동으로 맵핑
            - bottomNavigation: activity_main.xml의 BottomNavigationView id
         */
        NavigationUI.setupWithNavController(binding.bottomNavigation, controller)

        controller.addOnDestinationChangedListener { _, destination, _ ->
            /* 현재 화면이 searchFragment나 userFragment 일때만 bottomNavigation 표시 */
            if (arrayListOf(R.id.searchFragment, R.id.userFragment).contains(destination.id)) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    /* AppBar(툴바)에서 뒤로가기(up) 버튼(<-) 눌렀을 때,
      뒤로 이동하도록 onSupportNavigateUp를 오버라이드하여 구현  */
    override fun onSupportNavigateUp() = findNavController(R.id.navigation_host).navigateUp()
}
