package com.yalantis.colormatchtabs.colormatchtabs

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.ActionBar
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by anna on 10.05.17.
 */
class ColorTabView : LinearLayout, View.OnClickListener {

    companion object {
        private const val NORMAL_VIEWS_MARGIN = 16
        private const val RADIUS = 22
        private const val TAB_MAX_WIDTH = 132
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initColorTabView()
    }

    internal var tab: ColorTab? = null
        set(value) {
            field = value
            updateView()
        }

    internal var parentLayout: ColorMatchTabLayout? = null


    fun initColorTabView() {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        isClickable = true
        setBackgroundColor(Color.TRANSPARENT)
        initViews()
        this.setOnClickListener(this@ColorTabView)
    }

    private fun initViews() {
        iconView = ImageView(context)
        iconView.setBackgroundColor(Color.TRANSPARENT)
        addView(iconView)
        textView = TextView(context)
        addView(textView)
    }

    private lateinit var textView: TextView
    private lateinit var iconView: ImageView

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        // This view masquerades as an action bar tab.
        event.className = ActionBar.Tab::class.java.name
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        // This view masquerades as an action bar tab.
        info.className = ActionBar.Tab::class.java.name
    }

    override fun onMeasure(origWidthMeasureSpec: Int, origHeightMeasureSpec: Int) {
        val specWidthSize = MeasureSpec.getSize(origWidthMeasureSpec)
        val specWidthMode = MeasureSpec.getMode(origWidthMeasureSpec)
        val maxWidth = parentLayout?.tabMaxWidth

        val widthMeasureSpec: Int
        val heightMeasureSpec = origHeightMeasureSpec  - dpToPx(4)
        if (maxWidth ?: 0 > 0 && (specWidthMode == MeasureSpec.UNSPECIFIED || specWidthSize > maxWidth ?: 0)) {
            // If we have a max width and a given spec which is either unspecified or
            // larger than the max width, update the width spec using the same mode
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(if (tab?.isSelected ?: false) dpToPx(TAB_MAX_WIDTH) else maxWidth ?: 0, MeasureSpec.EXACTLY)
        } else {
            // Else, use the original width spec
            widthMeasureSpec = origWidthMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        iconView.setPadding(dpToPx(NORMAL_VIEWS_MARGIN), 0, dpToPx(NORMAL_VIEWS_MARGIN), 0)
        textView.setPadding(0, 0, dpToPx(NORMAL_VIEWS_MARGIN), 0)
        if(clickedTabView != null) {
            parentLayout?.tabStrip?.animateDrawTab(clickedTabView)
            Log.e("OnLayout", "work")
        }
    }

    fun updateView() {
        val colorTab = tab
        if (tab?.isSelected ?: false) {
            textView.visibility = View.VISIBLE
            textView.text = colorTab?.text
            textView.setTextColor(Color.BLACK)
            textView.requestLayout()
        } else {
            textView.visibility = View.GONE
        }
        if (colorTab?.icon != null) {
            iconView.setImageDrawable(colorTab.icon)
            iconView.requestLayout()
        }

        requestLayout()
    }

    internal var clickedTabView: ColorTabView? = null

    override fun onClick(v: View?) {
        Log.e("OnClick", "start")
        Log.e("OnClick", parentLayout?.tabStrip?.isAnimate.toString())
        if(!(parentLayout?.tabStrip?.isAnimate ?: false)) {
            Log.e("OnClick", "work")
            val clickedTabView = v as ColorTabView?
            parentLayout?.select(clickedTabView?.tab)
            this.clickedTabView = clickedTabView
        }
    }

    private fun dpToPx(dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }

}