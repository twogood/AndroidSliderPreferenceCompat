package net.jayschwa.android.preference

import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.SeekBar

class SliderPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    private val preference: SliderPreference
        get() = super.getPreference() as SliderPreference

    override fun onBindDialogView(view: View) {
        preference.mSeekBarValue = (preference.mValue * SliderPreference.SEEKBAR_RESOLUTION).toInt()
        val seekBar = view.findViewById<View>(R.id.slider_preference_seekbar) as SeekBar
        seekBar.max = SliderPreference.SEEKBAR_RESOLUTION
        seekBar.progress = preference.mSeekBarValue
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    preference.mSeekBarValue = progress
                }
            }
        })
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        val newValue = preference.mSeekBarValue.toFloat() / SliderPreference.SEEKBAR_RESOLUTION
        if (positiveResult && preference.callChangeListener(newValue)) {
            preference.value = newValue
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(preferenceKey: String): SliderPreferenceDialogFragmentCompat {
            val fragment = SliderPreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preferenceKey)
            fragment.arguments = bundle
            return fragment
        }


    }
}