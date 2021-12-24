package com.vinade.todorooms


import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class CreateCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        supportActionBar?.hide()
        config()

        setContentView(R.layout.activity_create_card)
        super.onCreate(savedInstanceState)


    }

    private fun config() {
        findViewById<View>(android.R.id.content).transitionName = "transition_floating_btn"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        val transform = MaterialContainerTransform()
        transform.addTarget(android.R.id.content)
        transform.setAllContainerColors(Color.WHITE)
        transform.duration = 500
        transform.fadeMode = MaterialContainerTransform.FADE_MODE_IN
        transform.interpolator = FastOutSlowInInterpolator()
        transform.pathMotion = MaterialArcMotion()
        window.sharedElementEnterTransition = transform
        window.sharedElementReturnTransition = transform
    }


}