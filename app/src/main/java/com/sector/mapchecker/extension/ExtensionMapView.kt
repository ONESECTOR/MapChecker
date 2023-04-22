package com.sector.mapchecker.extension

import android.graphics.PointF
import com.sector.mapchecker.model.Mark
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider

fun MapView.zoomIn(value: Int = 1){
    this.zoomChange(value)
}

fun MapView.zoomOut(value: Int = 1){
    this.zoomChange(-1 * value)
}

fun MapView.zoomChange(different: Int = 1){
    map.move(
        CameraPosition(map.cameraPosition.target, map.cameraPosition.zoom + different, 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 0.5F), null
    )
}

fun MapView.zoomSet(value: Int = 1){
    map.move(
        CameraPosition(map.cameraPosition.target, value.toFloat(), 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 0.5F), null
    )
}

fun MapView.addMarks(
    markCoordinates: List<Mark>,
    clickListener: MapObjectTapListener,
    markView: ViewProvider
) {
    for (mark in markCoordinates) {
        this.map.mapObjects.addPlacemark(
            Point(mark.latitude, mark.longitude)
        ).apply {
            this.setView(markView)
            this.isDraggable = false
            this.addTapListener(clickListener)
            this.setIconStyle(IconStyle().setAnchor(PointF(0.5f, 1.0f)))
        }
    }
}