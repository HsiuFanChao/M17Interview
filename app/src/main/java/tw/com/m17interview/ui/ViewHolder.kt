package tw.com.m17interview.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import tw.com.m17interview.viewmodel.ItemViewModel

abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(vm: ItemViewModel) {

    }
}