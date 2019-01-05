/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */

package net.jayschwa.android.preference

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.app.FragmentManager
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.AttributeSet

import java.util.Objects
import java.util.Objects.isNull

/**
 * @author Jay Weisskopf
 */
class SliderPreference : DialogPreference {

    var mValue: Float = 0.toFloat()
    var mSeekBarValue: Int = 0
    private var mSummaries: Array<CharSequence>? = null

    // clamp to [MINIMUM, MAXIMUM]
    var value: Float
        get() = mValue
        set(newValue) {
            var value = newValue
            value = Math.max(MINIMUM, Math.min(value, MAXIMUM))
            if (shouldPersist()) {
                persistFloat(value)
            }
            if (value != mValue) {
                mValue = value
                notifyChanged()
            }
        }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs)
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setup(context, attrs)
    }

    private fun setup(context: Context, attrs: AttributeSet) {
        dialogLayoutResource = R.layout.slider_preference_dialog
        val a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference)
        try {
            setSummary(a.getTextArray(R.styleable.SliderPreference_android_summary))
        } catch (e: Exception) {
            // Do nothing
        }

        a.recycle()
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a!!.getFloat(index, MINIMUM)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        value = if (restoreValue) getPersistedFloat(mValue) else defaultValue as Float
    }

    override fun getSummary(): CharSequence? {
        return mSummaries.run {
            if (this == null || isEmpty()) {
                super.getSummary()
            } else {
                var index = (mValue * size).toInt()
                index = Math.min(index, size - 1)
                this[index]
            }
        }
    }

    private fun setSummary(summaries: Array<CharSequence>) {
        mSummaries = summaries
    }

    override fun setSummary(summary: CharSequence) {
        super.setSummary(summary)
        mSummaries = null
    }

    override fun setSummary(summaryResId: Int) {
        try {
            setSummary(context.resources.getTextArray(summaryResId))
        } catch (e: Exception) {
            super.setSummary(summaryResId)
        }

    }

    companion object {

        val MAXIMUM = 1.0f
        val MINIMUM = 0.0f
        const val SEEKBAR_RESOLUTION = 10000

        // As in PreferenceFragmentCompat, because we want to ensure that at most one dialog is showing.
        private const val DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG"

        fun onDisplayPreferenceDialog(preferenceFragment: PreferenceFragmentCompat,
                                      preference: Preference): Boolean {

            if (preference is SliderPreference) {
                // getChildFragmentManager() will lead to looking for target fragment in the child
                // fragment manager.
                val fragmentManager = Objects.requireNonNull<FragmentManager>(preferenceFragment.fragmentManager)
                if (fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
                    val dialogFragment = SliderPreferenceDialogFragmentCompat.newInstance(preference.getKey())
                    dialogFragment.setTargetFragment(preferenceFragment, 0)
                    dialogFragment.show(fragmentManager, DIALOG_FRAGMENT_TAG)
                }
                return true
            }

            return false
        }
    }
}
