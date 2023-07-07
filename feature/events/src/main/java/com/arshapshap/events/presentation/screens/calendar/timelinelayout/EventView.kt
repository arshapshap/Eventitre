package com.arshapshap.events.presentation.screens.calendar.timelinelayout

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.arshapshap.events.R
import kotlin.math.roundToInt

// used code from https://github.com/r3za13/android-timeline-schedule-view
// the library was no longer supported,
// and I also needed to fix a few bugs,
// so I didn't implement it in gradle
/**
 * @author R3ZA13 (Reza Abedini)
 * @since 18/11/18
 */
class EventView : FrameLayout {

    var startTime = 0f
    var endTime = 0f
    private lateinit var event: EventTimelineModel
    private var marginBetweenItems: Int = 0
    private var layoutResourceId: Int = 0

    private var onClick: (event: EventTimelineModel) -> Unit = { _ -> }
    private var setupView: (view: View) -> Unit = {}

    constructor(
        context: Context, event: EventTimelineModel, itemsMargin: Int = 1,
        layoutResourceId: Int = R.layout.item_event_on_timeline,
        setupView: (view: View) -> Unit = {},
        onItemClick: (event: EventTimelineModel) -> Unit = { _ -> }
    ) : super(context) {
        this.event = event
        this.startTime = event.startTime
        this.endTime = event.endTime
        this.marginBetweenItems = itemsMargin.toPx()
        this.layoutResourceId = layoutResourceId
        this.setupView = setupView
        this.onClick = onItemClick
        //visibility = View.INVISIBLE
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    {
        throw IllegalAccessException("Adding EventView from layout not supported in this version.")
    }


    private fun init() {
        setPadding(marginBetweenItems, marginBetweenItems, marginBetweenItems, marginBetweenItems)

        val view = View.inflate(context, layoutResourceId, parent as? ViewGroup)
        view.setOnClickListener {
            onClick(event)
        }
        addView(view)

        setupView(view)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val oneHourHeight = ((parent as TimeLineLayoutGroup).eachHourHeightInDp).toPx()
        val minDelta = ((parent as TimeLineLayoutGroup).minimumHeightEachSellPercentage)
        val calculatedHeight: Float = if (endTime - startTime < minDelta) {
            oneHourHeight * minDelta
        } else {
            (endTime - startTime) * oneHourHeight
        }

        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(calculatedHeight.roundToInt(), MeasureSpec.EXACTLY)
        )


    }

    fun getEventTime() = startTime to endTime

    private fun Float.toPx(): Int {
        val r = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, r.displayMetrics).toInt()
    }

    private fun Int.toPx(): Int {
        val r = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), r.displayMetrics).toInt()
    }
}