package com.example.myfragments.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.NavHostFragment
import com.example.myfragments.R
import com.example.myfragments.core.base.BaseFragment
import com.example.myfragments.databinding.FragmentStartBinding


class StartFragment : Fragment() {

lateinit var vievBinding: FragmentStartBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vievBinding = FragmentStartBinding.inflate(inflater,container,false)
        return vievBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = resources.getColor(R.color.clear)
        val animated = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_splash)
        vievBinding.splashLogo.animation = animated
        Handler(Looper.myLooper()!!).postDelayed({
            NavHostFragment.findNavController(this).navigate(R.id.endFragment)
        },3000)

    }



}