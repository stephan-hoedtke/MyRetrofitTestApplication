package com.example.myretrofittestapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myretrofittestapplication.databinding.FragmentSecondBinding
import com.example.myretrofittestapplication.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondFragment : Fragment(R.layout.fragment_second) {

    private val binding: FragmentSecondBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(com.example.myretrofittestapplication.R.id.action_SecondFragment_to_FirstFragment)
        }
    }
}

