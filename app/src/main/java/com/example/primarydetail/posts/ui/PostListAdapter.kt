package com.example.primarydetail.posts.ui

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetail.databinding.PostListItemBinding
import com.example.primarydetail.posts.domain.model.Post

class PostListAdapter(private val onItemSelected: (Long) -> Unit) :
    ListAdapter<Post, PostListAdapter.ViewHolder>(PostListDiff()) {

    // Declare the SelectionTracker
    var mTracker: SelectionTracker<Long>? = null

    var mSelected: Int = RecyclerView.NO_POSITION

    // Make sure to specify it has stable IDs
    init {
        setHasStableIds(true)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            PostListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // Very important to override this, as the id of the post is the unique ID
    // in the RecyclerView, not the position
    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        // When long pressing an item, it will add it to the SelectionTracker
        // We'll pass whether the item is selected to the bind
        mTracker?.let {
            holder.bind(item, it.isSelected(item.id))
        }

        with(holder.itemView) {
            tag = item
        }
    }

    inner class ViewHolder(
        private val binding: PostListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the post object to the post variable in post_list_content.xml
         *
         * @param post THe post to bind
         * @param isActivated Whether or not the SelectionTracker has it as selected
         *
         * isActivated is connected to android:state_activated in item_selector.xml
         * If it's true, is makes the row a different color since we set android:background
         * to item_selector in post_list_item.xml
         */
        fun bind(post: Post, isActivated: Boolean) {
            itemView.isActivated = isActivated
            itemView.isSelected = absoluteAdapterPosition == mSelected
            binding.postTitle.text = post.title
            val typeface = if (post.read) Typeface.NORMAL else Typeface.BOLD
            binding.postTitle.setTypeface(null, typeface)
            binding.root.setOnClickListener {
                notifyItemChanged(mSelected)
                mSelected = absoluteAdapterPosition
                notifyItemChanged(mSelected)
                onItemSelected(post.id)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long = itemId
                override fun getPosition(): Int = absoluteAdapterPosition
            }
    }
}

/**
 * Compares the differences between the old list and new list whenever the RecyclerView changes
 * Need to perform the proper animations when removing items from the list or marking them
 * as read
 */
private class PostListDiff : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.title == newItem.title
                && oldItem.read == newItem.read
    }
}
