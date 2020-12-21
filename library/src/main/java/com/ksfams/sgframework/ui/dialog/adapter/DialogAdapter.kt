package com.ksfams.sgframework.ui.dialog.adapter

import com.ksfams.sgframework.ui.dialog.listener.OnItemClickedListener

/**
 *
 * Dialog Adapter
 *
 * Create Date  12/21/20
 * @version 1.00    12/21/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/21/20     신규 개발.
 */

interface DialogAdapter {
    // list item
    val itemList: Array<String>

    // list item Click Listener
    var itemClickedListener: OnItemClickedListener?

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    fun isSelected(position: Int): Boolean

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    fun toggleSelection(position: Int)

    /**
     * default 로 선택된 items selected
     *
     * @param selectedItems List<Int>
     */
    fun setDefaultSelection(selectedItems: List<Int>)

    /**
     * Clear the selection status for all items
     */
    fun clearSelection()

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    fun getSelectedItemCount(): Int

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items
     */
    fun getSelectedItems(): List<String>

    /**
     * Indicates the list of selected item positions
     *
     * @return List of selected items ids
     */
    fun getSelectedItemPositions(): List<Int>
}