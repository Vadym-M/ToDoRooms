package com.vinade.todorooms


import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreateCardActivity : AppCompatActivity() {
    private lateinit var db: DataBase
    private lateinit var roomID: String

    @RequiresApi(Build.VERSION_CODES.O)
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

        text_card.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(text_card)
                true
            } else {
                false
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("tag", "RUN RUN RUN")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            showKeyboard(text_card)
        }, 500)

        btnBack.setOnClickListener {
            applicationContext.hideKeyboard(it.rootView)
            onBackPressed()
        }
        val btnDone = findViewById<Button>(R.id.card_create_done).setOnClickListener {

            val currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("H:m dd-MM-yyyy", Locale.ENGLISH))
            val card = Card(
                UUID.randomUUID().toString(),
                title.text.toString(),
                text_card.text.toString(),
                currentDateTime
            )
            db.writeNewCard(roomID, card)
            applicationContext.hideKeyboard(it.rootView)
            onBackPressed()
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

    private fun initDatabase() {
        db = DataBase()
        db.initDatabase()
    }

    fun showKeyboard(edit_text: EditText) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(edit_text.rootView, InputMethodManager.SHOW_IMPLICIT)
        edit_text.requestFocus()

    }

    fun hideKeyboard(edit_text: EditText) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(edit_text.applicationWindowToken, 0)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}