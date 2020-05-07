package tw.com.m17interview.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import tw.com.m17interview.R

@BindingAdapter("avatarUrl")
fun bindImageFromUrl(view: ImageView, avatarUrl: String?) {
    if (!avatarUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(avatarUrl)
            .placeholder(R.drawable.github_user_profile_avatar_placeholder)
            .into(view)
    }
}