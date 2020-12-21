package com.ksfams.sgframework.ui.dialog.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.gone
import com.ksfams.sgframework.extensions.inflate
import com.ksfams.sgframework.extensions.visible
import com.ksfams.sgframework.ui.dialog.listener.OnItemClickedListener


/**
 *
 * Dialog List Adapter
 *
 * Create Date 2020-02-03
 * @version    1.00 2020-02-03
 * @since   1.00
 * @see
 * @author    강성훈(ssogaree@gmail.com)
 * Revision History
 * who			when				what
 * ssogaree		2020-02-03		    신규 개발.
 */

class DialogListAdapter(val context: Context,
                        val isCheckMode: Boolean = false,
                        override val itemList: Array<String>,
                        override var itemClickedListener: OnItemClickedListener?) : RecyclerView.Adapter<DialogListAdapter.DialogListViewHolder>(), DialogAdapter {

    private var mSelectedItems = SparseBooleanArray()

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogListViewHolder {
        val view = context.inflate(R.layout.dialog_list_item_row, parent, false)
        return DialogListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogListViewHolder, position: Int) {
        holder.bind(position)

        itemClickedListener?.let { listener ->
            holder.itemView.setOnClickListener { view ->
                listener.onClicked(view, position, getSelectedItemPositions())
            }
        }
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    override fun isSelected(position: Int): Boolean {
        return mSelectedItems.get(position, false)
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    override fun toggleSelection(position: Int) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position)
        } else {
            mSelectedItems.put(position, true)
        }
    }

    /**
     * default 로 선택된 items selected
     *
     * @param selectedItems List<Int>
     */
    override fun setDefaultSelection(selectedItems: List<Int>) {
        selectedItems.forEach {
            mSelectedItems.put(it, true)
        }
    }

    /**
     * Clear the selection status for all items
     */
    override fun clearSelection() {
        mSelectedItems.clear()
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    override fun getSelectedItemCount(): Int {
        return mSelectedItems.size()
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items
     */
    override fun getSelectedItems(): List<String> {
        val items: MutableList<String> = ArrayList(mSelectedItems.size())
        getSelectedItemPositions().forEach {
            items.add(itemList[it])
        }
        return items
    }

    /**
     * Indicates the list of selected item positions
     *
     * @return List of selected items ids
     */
    override fun getSelectedItemPositions(): List<Int> {
        val items: MutableList<Int> = ArrayList(mSelectedItems.size())
        for (i in 0 until mSelectedItems.size()) {
            items.add(mSelectedItems.keyAt(i))
        }
        return items
    }


    inner class DialogListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val checkBox = itemView.findViewById<ImageView>(R.id.common_dialog_check)
        private val itemText = itemView.findViewById<TextView>(R.id.common_dialog_text)
        private val divider = itemView.findViewById<ImageView>(R.id.divider_line)

        fun bind(position: Int) {

            val item = itemList[position]

            // 다중 선택이 아니면 초기화 처리 후, item 선택 처리함.
            if (!isCheckMode) {
                clearSelection()
            }

            if (isSelected(position)) {
                checkBox.visible()
            } else {
                checkBox.gone()
            }

            // divider line 설정 (마지막포지션에는 보여주지않는다)
            if (itemList.size == (position + 1)) {
                divider.gone()
            } else {
                divider.visible()
            }

            // 항목의 내용
            itemText.text = item
        }
    }
}