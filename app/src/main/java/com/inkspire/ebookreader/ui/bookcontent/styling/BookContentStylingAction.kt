package com.inkspire.ebookreader.ui.bookcontent.styling

sealed interface BookContentStylingAction {
    data class UpdateSelectedColorSet(val index: Int) : BookContentStylingAction
    data class UpdateBackgroundColor(val color: Int) : BookContentStylingAction
    data class UpdateTextColor(val color: Int) : BookContentStylingAction
    data class UpdateSelectedFontFamilyIndex(val index: Int) : BookContentStylingAction
    data class UpdateFontSize(val fontSize: Int) : BookContentStylingAction
    data class UpdateLineSpacing(val lineSpacing: Int) : BookContentStylingAction
    data object UpdateTextIndent : BookContentStylingAction
    data object UpdateTextHighlight : BookContentStylingAction
    data object UpdateImagePaddingState : BookContentStylingAction
}