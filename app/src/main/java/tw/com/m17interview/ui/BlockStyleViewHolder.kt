package tw.com.m17interview.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tw.com.m17interview.R
import tw.com.m17interview.viewmodel.ItemViewModel
import tw.com.m17interview.databinding.GithubUserProfileBlockStyleBinding

class BlockStyleViewHolder(private val binding: GithubUserProfileBlockStyleBinding) : ViewHolder(binding.root) {

    companion object {
        /**
         * Factory for creating [BlockStyleViewHolder]
         *
         * @param parent
         */
        fun inflate(parent: ViewGroup): BlockStyleViewHolder {

            val binding:GithubUserProfileBlockStyleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.github_user_profile_block_style, parent,
                false)

            return BlockStyleViewHolder(binding)
        }
    }

    override fun bind(vm: ItemViewModel) {
        binding.users = vm.data
        binding.executePendingBindings()
    }
}