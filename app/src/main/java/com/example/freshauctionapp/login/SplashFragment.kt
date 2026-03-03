package com.example.freshauctionapp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freshauctionapp.R
import com.example.freshauctionapp.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Splash Fragment
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //fragment_splash 뷰 inflate
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //lifecycleScope: activity나 fragment의 lifecycle에 맞춰 코루틴 동작
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)

            withContext(Dispatchers.Main) {
                /* 현재 로그인한 사용자 가져오기(파이어베이스 로그인 인증)
                   - 로그인 user가 있으면  global 액션으로 바로 searchFragment로 이동하고
                   - 로그인 user가 없으면(null) global 액션으로 바로 loginFragment로 이동
                 */
                FirebaseAuth.getInstance().currentUser?.let {
                    findNavController().navigate(R.id.action_global_searchFragment)
                } ?: kotlin.run {
                    findNavController().navigate(R.id.action_global_loginFragment)
                }
            }//end of withContext
        }//end of ifecycleScope.launch
    }//end of onViewCreated

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}//end of SplashFragment

