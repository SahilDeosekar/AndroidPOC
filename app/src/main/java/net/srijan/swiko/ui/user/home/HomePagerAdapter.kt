package net.srijan.swiko.ui.user.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val FIRST_TAB_INDEX = 0
const val SECOND_TAB_INDEX = 1
const val THIRD_TAB_INDEX = 2
const val FOURTH_TAB_INDEX = 3

class HomePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        FIRST_TAB_INDEX to { FirstFragment() },
        SECOND_TAB_INDEX to { SecondFragment() },
        THIRD_TAB_INDEX to { ThirdFragment() },
        FOURTH_TAB_INDEX to { FourthFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}