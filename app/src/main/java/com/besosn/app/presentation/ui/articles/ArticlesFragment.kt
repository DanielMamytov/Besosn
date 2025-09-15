package com.besosn.app.presentation.ui.articles

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentArticlesBinding

class ArticlesFragment : Fragment(R.layout.fragment_articles) {

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticlesBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
//        binding.btnOpenArticle.setOnClickListener {
//            findNavController().navigate(R.id.action_articlesFragment_to_articleDetailFragment)
//        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
