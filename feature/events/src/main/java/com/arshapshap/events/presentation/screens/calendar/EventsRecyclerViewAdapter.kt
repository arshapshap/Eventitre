package com.arshapshap.events.presentation.screens.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arshapshap.common.extensions.formatDayToString
import com.arshapshap.events.databinding.ItemEventBinding
import com.arshapshap.events.domain.models.Event

internal class EventsRecyclerViewAdapter(
    private var list: List<Event> = listOf(),
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder>() {

    internal class ViewHolder(
        private val binding: ItemEventBinding,
        private val onClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(event: Event) {
            with(binding) {
                nameTextView.text = event.name
                descriptionTextView.text = event.description
                dateStartTextView.text = event.dateStart.formatDayToString()
                dateFinishTextView.text = event.dateFinish.formatDayToString()

                root.setOnClickListener {
                    onClick.invoke(event)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Event>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            binding = getBinding(parent),
            onClick = onItemClick
        )

    override fun getItemCount(): Int =
        list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    private fun getBinding(parent: ViewGroup): ItemEventBinding =
        ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
}