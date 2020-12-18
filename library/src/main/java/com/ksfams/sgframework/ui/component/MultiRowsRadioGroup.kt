package com.ksfams.sgframework.ui.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 *
 * A RadioGroup allow multiple rows layout,
 * will use the RadioButton's OnCheckedChangeListener
 *
 * Create Date  12/18/20
 * @version 1.00    12/18/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/18/20     신규 개발.
 */

class MultiRowsRadioGroup @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null) : RadioGroup(context, attrs) {

    private var checking = false

    init {
        setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) {
                if (parent === this@MultiRowsRadioGroup && child is ViewGroup) {
                    for (radioButton in getRadioButtonFromGroup(child)) {
                        radioButton.setOnCheckedChangeListener(null)
                    }
                }
            }

            override fun onChildViewAdded(parent: View?, child: View?) {
                if (parent === this@MultiRowsRadioGroup && child is ViewGroup) {
                    for (radioButton in getRadioButtonFromGroup(child)) {
                        var id = radioButton.id
                        // generates an id if it's missing
                        if (id == View.NO_ID) {
                            id =
                                    if (Build.VERSION.SDK_INT >= 17) View.generateViewId() else radioButton.hashCode()
                            radioButton.id = id
                        }

                        if (radioButton.isChecked) {
                            check(id)
                        }

                        radioButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(buttonView: CompoundButton,
                                                          isChecked: Boolean) {
                                if (isChecked) {
                                    radioButton.setOnCheckedChangeListener(null)
                                    check(buttonView.id)
                                    radioButton.setOnCheckedChangeListener(this)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    override fun check(id: Int) {
        if (checking) return

        checking = true
        super.check(id)
        checking = false
    }

    private fun getRadioButtonFromGroup(group: ViewGroup?): ArrayList<RadioButton> {
        if (group == null) return ArrayList()
        val list: ArrayList<RadioButton> = ArrayList()
        getRadioButtonFromGroup(group, list)
        return list
    }

    private fun getRadioButtonFromGroup(group: ViewGroup,
                                        list: ArrayList<RadioButton>) {
        for (i in 0 until group.childCount) {
            val child = group.getChildAt(i)
            if (child is RadioButton) {
                list.add(child)
            } else if (child is ViewGroup) {
                getRadioButtonFromGroup(child, list)
            }
        }
    }
}