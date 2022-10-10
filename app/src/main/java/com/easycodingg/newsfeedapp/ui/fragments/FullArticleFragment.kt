package com.easycodingg.newsfeedapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.easycodingg.newsfeedapp.R
import com.easycodingg.newsfeedapp.databinding.FragmentFullArticleBinding
import com.easycodingg.newsfeedapp.util.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FullArticleFragment: Fragment(R.layout.fragment_full_article) {

    private val binding by viewBinding(FragmentFullArticleBinding::bind)
    private val args: FullArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleUrl = args.articleUrl

        viewLifecycleOwner.lifecycleScope.launch {
            binding.webView.apply {
                webViewClient = object: WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        binding.apply {
                            pbFullArticle.visibility = View.GONE
                            webView.visibility = View.VISIBLE
                        }
                    }
                }
                loadUrl(articleUrl)
            }
        }
    }
}