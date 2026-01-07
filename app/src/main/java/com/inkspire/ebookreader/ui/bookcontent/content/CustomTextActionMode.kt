package com.inkspire.ebookreader.ui.bookcontent.content

import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.ui.geometry.Rect
import com.inkspire.ebookreader.R

class CustomTextActionMode  (
    val onActionModeDestroy: (() -> Unit)? = null,
    var view: View,
    var rect: Rect = Rect.Zero,
    var onCleanTextRequested: (() -> Unit)? = null,
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        requireNotNull(menu) { "onCreateActionMode requires a non-null menu" }
        requireNotNull(mode) { "onCreateActionMode requires a non-null mode" }

        onCleanTextRequested?.let { addMenuItem(menu, MenuItemOption.CleanText,mode.menuInflater) }
        return true
    }
    //    fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
//        if (mode == null || menu == null) return false
//        updateMenuItems(menu)
//        return true
//    }
    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        if (mode == null || menu == null) return false
        updateMenuItems(mode,menu)
        // Force revalidate the custom view layout
//        for (i in 0 until menu.size()) {
//            val menuItem = menu.getItem(i)
//            menuItem.actionView?.requestLayout()
//            updateMenuItems(mode,menu)
//        }
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            MenuItemOption.CleanText.id -> onCleanTextRequested?.invoke()
            else -> return false
        }
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        onActionModeDestroy?.invoke()
    }

//    fun onDestroyActionMode() {
//        onActionModeDestroy?.invoke()
//    }

    private fun updateMenuItems(mode: ActionMode, menu: Menu) {
        addOrRemoveMenuItem(mode, menu, MenuItemOption.CleanText, onCleanTextRequested)
    }

    private fun addMenuItem(menu: Menu, item: MenuItemOption, menuInflater: MenuInflater) {
        // Inflate custom layout for menu item
//        getMenuInflater
//        menuInflater.inflate(R.menu.menu_item_custom, menu)
//        val inflater = LayoutInflater.from(view.context) // Use the current view's context
//        val customView = inflater.inflate(R.menu.menu_item_custom, null)
//
//        // Set the icon and text dynamically if needed
//        val iconView = customView.findViewById<ImageView>(R.id.menu_item_icon)
//        val textView = customView.findViewById<TextView>(R.id.menu_item_text)
//        iconView.setImageResource(item.iconResource)
//        textView.setText(item.titleResource)
//
//        // Set the custom view to the menu item
//        menu.add(Menu.NONE, item.id, Menu.NONE, null)
//            .setActionView(customView)
//            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        // Inflate the custom layout for the popup menu
//        val inflater = LayoutInflater.from(view.context)
//        val layout = inflater.inflate(R.layout.menu_item_custom, anchorView.rootView as ViewGroup, false)
//
//        // Find and configure the icon and text in the inflated layout
//        val iconView = layout.findViewById<ImageView>(R.id.menu_item_icon)
//        val textView = layout.findViewById<TextView>(R.id.menu_item_text)
//
//        // Set the text and icon (you can modify it as needed)
//        iconView.setImageResource(R.drawable.ic_clean_text) // Your icon resource
//        textView.setText(R.string.CleanText) // Your string resource
//
//        // Create the PopupWindow with the custom layout
//        popupWindow = PopupWindow(
//            layout,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true // Make it focusable
//        )
//
//        // Handle clicking on the popup item
//        layout.setOnClickListener {
//            onCleanTextRequested?.invoke() // Call the callback when the item is clicked
//            popupWindow?.dismiss() // Dismiss the popup
//        }
//
//        // Show the popup window anchored to the view
//        popupWindow?.showAsDropDown(anchorView)

    }
    @Suppress("SameParameterValue")
    private fun addOrRemoveMenuItem(mode : ActionMode,menu: Menu,  item: MenuItemOption, callback: (() -> Unit)?) {
        when {
            callback != null && menu.findItem(item.id) == null -> addMenuItem(
                menu,
                item,
                mode.menuInflater
            )
            callback == null && menu.findItem(item.id) != null -> menu.removeItem(item.id)
        }
    }
}

enum class MenuItemOption(val id: Int) {
    CleanText(0);

    val titleResource: Int
        get() =
            when (this) {
                CleanText -> R.string.app_name
            }

    val iconResource: Int
        get() =
            when (this) {
                CleanText -> R.drawable.ic_text_cancel
            }
    val order = id
}