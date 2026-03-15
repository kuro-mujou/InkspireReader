package com.inkspire.ebookreader.ui.bookcontent.root

sealed interface BookContentDataEvent {
    data class SendToastAfterFilter(val amount: Int) : BookContentDataEvent
}