package tw.com.m17interview.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tw.com.m17interview.R
import tw.com.m17interview.viewmodel.ItemViewModel
import tw.com.m17interview.databinding.GithubUserProfileBannerStyleBinding

class BannerStyleViewHolder(private val binding: GithubUserProfileBannerStyleBinding) : ViewHolder(binding.root) {

    companion object {
        /**
         * Factory for creating [BannerStyleViewHolder]
         *
         * @param parent
         */
        fun inflate(parent: ViewGroup): BannerStyleViewHolder {

            val binding: GithubUserProfileBannerStyleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.github_user_profile_banner_style, parent,
                false)

            return BannerStyleViewHolder(binding)
        }
    }

    override fun bind(vm: ItemViewModel) {
        binding.users = vm.data
        binding.executePendingBindings()
    }
}