package com.example.primarydetail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetail.databinding.ActivityMainBinding
import com.example.primarydetail.posts.ui.PostListAdapter
import com.example.primarydetail.posts.ui.PostLookup
import com.example.primarydetail.posts.ui.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAdapter: PostListAdapter
    private var mActionMode: ActionMode? = null
    private lateinit var mSelectionTracker: SelectionTracker<Long>
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.settings -> {

                    }

                    else -> return false
                }
                return true
            }
        })

        // Initialize the RecyclerView adapter
        mAdapter = PostListAdapter(onItemSelected = { long ->
            markRead(long)
            val intent = Intent(this, DetailActivity::class.java)
            val bundle = bundleOf("postId" to long)
            intent.putExtras(bundle)
            startActivity(intent)
        })

        /**
         * Configure the RecyclerView
         */
        binding.postList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // Initialize the SelectionTracker
        mSelectionTracker = SelectionTracker.Builder(
            "selection",
            binding.postList,
            RecyclerViewIdKeyProvider(binding.postList),
            PostLookup(binding.postList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        // Make sure to set the tracker in the adapter to the tracker here
        mAdapter.mTracker = mSelectionTracker

        // Add an observer to start action mode and set the title of the toolbar to the
        // number of items selected using plurals
        mSelectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val count: Int = mSelectionTracker.selection.size()
                    if (count == 0) {
                        mActionMode?.finish()
                        mActionMode = null
                    } else {
                        if (mActionMode == null) {
                            mActionMode =
                                startSupportActionMode(actionModeCallback)
                        }
                        mActionMode?.title =
                            resources.getQuantityString(R.plurals.count_selected, count, count)
                    }
                }
            })

        viewModel.serverPosts()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest {
                    mAdapter.submitList(it)
                }
            }
        }
    }

    /**
     * Mark posts as read
     *
     * @param postIds A list of IDs of posts to mark as read
     */
    fun markRead(postIds: List<Long>) {
        viewModel.markRead(postIds)
    }

    /**
     * Mark post as read
     *
     * @param postId The ID of the post to mark as read
     */
    private fun markRead(postId: Long) {
        viewModel.markRead(postId)
    }

    /**
     * Delete posts
     *
     * @param postIds A list of IDs of posts to delete
     */
    fun deletePosts(postIds: List<Long>) {
        viewModel.deletePosts(postIds)
    }

    /**
     * Get the selected items into a list of long
     *
     * @return A list of IDs
     */
    private fun getSelection(): List<Long> {
        val selection = mSelectionTracker.selection

        val ids = LongArray(selection.size())
        var i = 0
        for (id in selection)
            ids[i++] = id!!

        return ids.asList()
    }

    // Callback for support action mode
    private val actionModeCallback = object : ActionMode.Callback {

        // Show the menu defined in R.menu.action
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.action, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            // Delete or mark read the posts based on the action selected...
            when (item?.itemId) {
                R.id.delete -> {
                    deletePosts(getSelection())
                }

                R.id.markRead -> {
                    markRead(getSelection())
                }
            }
            // ...and get rid of action mode afterwards
            onDestroyActionMode(mode)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mActionMode?.finish()
            mActionMode = null
            mSelectionTracker.clearSelection()
        }
    }
}

class RecyclerViewIdKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {

    override fun getKey(position: Int): Long {
        return recyclerView.adapter?.getItemId(position)
            ?: throw IllegalStateException("RecyclerView adapter is not set!")
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}
