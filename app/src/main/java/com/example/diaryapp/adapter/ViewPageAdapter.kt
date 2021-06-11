package com.example.diaryapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter(
    var listFragment: MutableList<Fragment>,
    var fragmentManager: FragmentManager,
    life: Lifecycle
) : FragmentStateAdapter(fragmentManager,life) {
    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
    fun setRight(fragment: Fragment, position: Int) {
        var f = listFragment.find {
            it == fragment
        }
        if (f == null) {
            listFragment.set(position, fragment)
        }
        notifyItemChanged(position)
    }

    fun setLeft(fragment: Fragment, position: Int) {
        var f = listFragment.find {
            it == fragment
        }
        if (f == null) {
            listFragment.set(position, fragment)
        }
        notifyItemChanged(position)
    }

    fun reloadFragment(position: Int) {
        var fragmentManager = fragmentManager.beginTransaction()
        fragmentManager.remove(listFragment[position])
        fragmentManager.commitNow()
    }
}