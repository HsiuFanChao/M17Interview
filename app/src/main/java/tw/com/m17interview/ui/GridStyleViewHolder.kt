package tw.com.m17interview.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tw.com.m17interview.R
import tw.com.m17interview.viewmodel.ItemViewModel
import tw.com.m17interview.databinding.GithubUserProfileGridStyleBinding

class GridStyleViewHolder(private val binding: GithubUserProfileGridStyleBinding) :
    ViewHolder(binding.root) {

    companion object {
        /**
         * Factory for creating [GridStyleViewHolder]
         *
         * @param parent
         */
        fun inflate(parent: ViewGroup): GridStyleViewHolder {

            val binding: GithubUserProfileGridStyleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.github_user_profile_grid_style, parent,
                false)

            return GridStyleViewHolder(binding)
        }
    }

    override fun bind(vm: ItemViewModel) {
        binding.users = vm.data
        binding.executePendingBindings()
    }
}