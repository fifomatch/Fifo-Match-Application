package com.fifo.match.ui.activity.survey.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SurveyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments: MutableList<Fragment> = ArrayList()
    fun add(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}