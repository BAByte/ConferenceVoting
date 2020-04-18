package com.cvte.maxhub.mvvmsample.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cvte.maxhub.mvvmsample.R

/**
 * 自定义DataBinding的Adapter
 */
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("imageFromID")
fun bindImageFromID(view: ImageView, id: Int?) {
    id?.let {
        Glide.with(view.context)
            .load(id)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}