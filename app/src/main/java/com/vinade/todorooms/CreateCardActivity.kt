package com.vinade.todorooms


import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import java.util.*

class CreateCardActivity : AppCompatActivity() {
    private lateinit var db :DataBase
    private lateinit var roomID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        supportActionBar?.hide()
        config()

        setContentView(R.layout.activity_create_card)
        super.onCreate(savedInstanceState)
        initDatabase()
        roomID = intent.getStringExtra("roomID").toString()
        val title = findViewById<EditText>(R.id.card_create_title)
        val text_card = findViewById<EditText>(R.id.card_create_text)
        val btnBack = findViewById<Button>(R.id.card_create_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }
        val btnDone = findViewById<Button>(R.id.card_create_done).setOnClickListener {
            val card = Card(UUID.randomUUID().toString(), title.text.toString(), text_card.text.toString())
            db.writeNewCard(roomID, card)
        }


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
    private fun initDatabase(){
      db = DataBase()
      db.initDatabase()
    }


}