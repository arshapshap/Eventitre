package com.arshapshap.events.presentation.screens.calendar.timelinelayout

import com.arshapshap.common.domain.models.Event
import com.arshapshap.common.extensions.addHours
import com.arshapshap.common.extensions.formatDateTimeToString
import com.arshapshap.common.extensions.formatTimeToString
import com.arshapshap.common.extensions.getTimeInFloat
import java.util.Date


class EventTimelineModel(event: Event, selectedDate: Date) {

    var title: String = ""
    var startTimeString: String = ""
    var endTimeString: String = ""
    var startTime = 0f
    var endTime = 0f

    init {
        this.title = event.name
        this.startTime = event.dateStart.getTimeInFloat()
        this.endTime = event.dateFinish.getTimeInFloat()
        this.startTimeString = event.dateStart.formatTimeToString()
        this.endTimeString = event.dateFinish.formatTimeToString()

        if (event.dateStart.before(selectedDate)) {
            this.startTime = 0f
            this.startTimeString = event.dateStart.formatDateTimeToString()
        }
        if (!event.dateFinish.before(selectedDate.addHours(24))) {
            this.endTime = 24f
            this.endTimeString = event.dateFinish.formatDateTimeToString()
        }
    }
}