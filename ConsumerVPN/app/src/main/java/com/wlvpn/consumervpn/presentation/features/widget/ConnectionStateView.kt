package com.wlvpn.consumervpn.presentation.features.widget

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.util.bindView

class ConnectionStateView : FrameLayout {

    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.view_connection_state, this, true)
        textBlink = AnimationUtils.loadAnimation(context, R.anim.alpha_blinking)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_connection_state, this, true)
        textBlink = AnimationUtils.loadAnimation(context, R.anim.alpha_blinking)
    }

    enum class ConnectionAnimationState(val animated: Boolean) {
        DISCONNECTED(false),
        CONNECTED(true),
        CONNECTING(true)
    }

    private val circleSlice: ImageView by bindView(R.id.rotating_circle_slice)
    private val connectionStateText: TextView by bindView(R.id.connection_state_text)
    private var textBlink: Animation? = null

    var state = ConnectionAnimationState.DISCONNECTED
        private set

    fun setConnectionState(state: ConnectionAnimationState) {
        this.state = state
        connectionStateText.clearAnimation()

        when (this.state) {
            ConnectionAnimationState.DISCONNECTED -> {

            }
            ConnectionAnimationState.CONNECTED -> {
                connectionStateText.visibility = View.GONE
                circleSlice.setImageResource(R.drawable.anim_connection_complete)
            }
            ConnectionAnimationState.CONNECTING -> {
                connectionStateText.visibility = View.VISIBLE
                if (textBlink != null) {
                    connectionStateText.startAnimation(textBlink)
                }
                circleSlice.setImageResource(R.drawable.anim_connection_rotate)
            }
        }

        if (this.state.animated) {
            val drawable = circleSlice.drawable
            if (drawable is Animatable) {
                (drawable as Animatable).start()
            }
        }
    }
}