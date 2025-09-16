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
        setArticleClickListeners()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun setArticleClickListeners() {
        val articleViews = listOf(
            binding.articleItemOne,
            binding.articleItemTwo,
            binding.articleItemThree,
            binding.articleItemFour,
            binding.articleItemFive,
            binding.articleItemSix,
            binding.articleItemSeven,
            binding.articleItemEight,
            binding.articleItemNine,
            binding.articleItemTen,
            binding.articleItemEleven,
            binding.articleItemTwelve,
        )

        ArticleDataSource.articles.zip(articleViews).forEach { (article, container) ->
            container.setOnClickListener {
                val direction =
                    ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(
                        article.id,
                    )
                findNavController().navigate(direction)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
