package com.inkspire.ebookreader.ui.setting.bookcategory

import com.inkspire.ebookreader.domain.model.Category

sealed interface BookCategorySettingAction {
    data class ChangeChipState(val chip: Category) : BookCategorySettingAction
    data class AddCategory(val category: Category) : BookCategorySettingAction
    data object DeleteCategory : BookCategorySettingAction
    data object ResetChipState : BookCategorySettingAction
}