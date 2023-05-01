package com.example.primarydetail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.window.embedding.ActivityEmbeddingController
import com.example.primarydetail.databinding.ActivityDetailBinding
import com.example.primarydetail.posts.domain.model.Post
import com.example.primarydetail.posts.ui.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var activityEmbeddingController: ActivityEmbeddingController
    private val viewModel: PostViewModel by viewModels()
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityEmbeddingController = ActivityEmbeddingController.getInstance(this)
        setSupportActionBar(binding.toolbar)

        if (!activityEmbeddingController.isActivityEmbedded(this)) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val bundle = intent!!.extras
        val postId = bundle!!.getLong("postId")
        lifecycleScope.launch {
            post = viewModel.postById(postId)

            binding.titleTextView.text = post.title
            binding.bodyTextView.text = post.body
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.delete -> {
                        post.let {
                            viewModel.deletePost(it.id)
                            if (activityEmbeddingController.isActivityEmbedded(this@DetailActivity)) {
                                val intent =
                                    Intent(this@DetailActivity, PlaceholderActivity::class.java)
                                startActivity(intent)
                            } else {
                                finish()
                            }
                        }
                    }

                    android.R.id.home -> {
                        finish()
                    }

                    else -> return false
                }
                return true
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val bundle = intent!!.extras
        val postId = bundle!!.getLong("postId")
        lifecycleScope.launch {
            post = viewModel.postById(postId)

            binding.titleTextView.text = post.title
            binding.bodyTextView.text = post.body
        }
    }
}