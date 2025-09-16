package com.besosn.app.presentation.ui.articles

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.besosn.app.R
import com.besosn.app.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment(R.layout.fragment_article_detail) {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        bindArticle()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun bindArticle() {
        val article = ArticleDataSource.getArticleById(args.articleId)
        if (article == null) {
            Toast.makeText(requireContext(), R.string.article_not_found, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        with(binding) {
            tvArticleTitle.text = article.title
            tvArticleContent.text = article.content
            bgImage.setImageResource(article.imageRes)
            bgImage.contentDescription = article.title
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
