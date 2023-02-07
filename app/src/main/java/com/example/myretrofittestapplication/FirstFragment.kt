package com.example.myretrofittestapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myretrofittestapplication.databinding.FragmentFirstBinding
import com.stho.beaver.ui.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment(R.layout.fragment_first) {

    private val binding: FragmentFirstBinding by viewBinding()

    private lateinit var handler: Handler

    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        observeFacts()
        callDataApi()
    }


    private fun observeFacts() {
        viewModel.catFactsLD.observe(viewLifecycleOwner) { fact -> onUpdateFact(fact) }
    }

    private fun onUpdateFact(fact: CatFact) {
        binding.textviewFirst.text = fact.fact
    }

    private fun callDataApi() {
//        lifecycleScope.launchWhenResumed {
//            val controller = MyDataController()
//            controller.start()
//        }
//
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                uiStateFlow.collect { uiState ->
//                    updateUi(uiState)
//                }
//            }
//        }
        val runnableCode = Runnable {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.fetchFact()
            }
        }

        handler.postDelayed(runnableCode, 100)
    }
}

