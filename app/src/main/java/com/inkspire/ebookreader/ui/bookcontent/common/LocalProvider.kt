package com.inkspire.ebookreader.ui.bookcontent.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSViewModel
import com.inkspire.ebookreader.ui.setting.SettingViewModel

val LocalDataViewModel = staticCompositionLocalOf<BookContentDataViewModel> {
    error("DataViewModel not provided")
}
val LocalDrawerViewModel = staticCompositionLocalOf<DrawerViewModel> {
    error("DrawerViewModel not provided")
}
val LocalStylingViewModel = staticCompositionLocalOf<BookContentStylingViewModel> {
    error("StylingViewModel not provided")
}
val LocalChapterContentViewModel = staticCompositionLocalOf<BookChapterContentViewModel> {
    error("ChapterContentViewModel not provided")
}
val LocalTableOfContentViewModel = staticCompositionLocalOf<TableOfContentViewModel> {
    error("TableOfContentViewModel not provided")
}
val LocalNoteViewModel = staticCompositionLocalOf<NoteViewModel> {
    error("NoteViewModel not provided")
}
val LocalBookmarkViewModel = staticCompositionLocalOf<BookmarkViewModel> {
    error("BookmarkListViewModel not provided")
}
val LocalTopBarViewModel = staticCompositionLocalOf<BookContentTopBarViewModel> {
    error("TopBarViewModel not provided")
}
val LocalBottomBarViewModel = staticCompositionLocalOf<BookContentBottomBarViewModel> {
    error("BottomBarViewModel not provided")
}
val LocalBottomBarTTSViewModel = staticCompositionLocalOf<BottomBarTTSViewModel> {
    error("BottomBarTTSViewModel not provided")
}
val LocalBottomBarAutoScrollViewModel = staticCompositionLocalOf<BottomBarAutoScrollViewModel> {
    error("BottomBarAutoScrollViewModel not provided")
}
val LocalSettingViewModel = staticCompositionLocalOf<SettingViewModel> {
    error("SettingViewModel not provided")
}
val LocalAutoScrollViewModel = staticCompositionLocalOf<AutoScrollViewModel> {
    error("AutoScrollViewModel not provided")
}
val LocalTTSViewModel = staticCompositionLocalOf<TTSViewModel> {
    error("TTSViewModel not provided")
}
val LocalCombineActions = staticCompositionLocalOf<CombineActions> {
    error("CombineActions not provided")
}