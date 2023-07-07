package com.arshapshap.events.presentation.screens.calendar.timelinelayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import com.arshapshap.events.R

// used code from https://github.com/r3za13/android-timeline-schedule-view
// the library was no longer supported,
// and I also needed to fix a few bugs,
// so I didn't implement it in gradle
/**
 * @author R3ZA13 (Reza Abedini)
 * @since 9/12/18
 */
class TimeLineLayout : ScrollView {
    private lateinit var horizontalScrollView: HorizontalScrollView

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    {
        horizontalScrollView = HorizontalScrollView(context).apply {
            id = R.id.horizontal_scroll_view_id
            addView(TimeLineLayoutGroup(context,attrs).apply { id = R.id.timeline_layout_group_id })
        }
        addView(horizontalScrollView)
        post {
            horizontalScrollView.requestLayout()
        }
    }

    fun addEvent(child: EventView?) {
        (horizontalScrollView.getChildAt(0) as ViewGroup).addView(child)
    }

    fun clearEvents() {
        (horizontalScrollView.getChildAt(0) as ViewGroup).removeAllViews()
    }
}