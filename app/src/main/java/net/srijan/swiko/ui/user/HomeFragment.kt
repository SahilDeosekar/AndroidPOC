package net.srijan.swiko.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import net.srijan.swiko.databinding.FragmentHomeBinding
import net.srijan.swiko.ui.user.home.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val tabLayout = binding.homeTabLayout
        val viewPager = binding.homeViewPager
        viewPager.adapter = HomePagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        return binding.root
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            FIRST_TAB_INDEX -> "First"
            SECOND_TAB_INDEX -> "Second"
            THIRD_TAB_INDEX -> "Third"
            FOURTH_TAB_INDEX -> "Fourth"
            else -> null
        }
    }

}