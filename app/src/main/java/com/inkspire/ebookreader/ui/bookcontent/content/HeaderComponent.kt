package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.common.LocalChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalNoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.NoteDialog
import com.inkspire.ebookreader.ui.bookcontent.composable.SelectionMenu
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.ui.bookcontent.styling.getHighlightColors
import com.inkspire.ebookreader.util.HeaderTextSizeUtil.calculateHeaderSize
import com.inkspire.ebookreader.util.HighlightUtil
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent(
    index: Int,
    text: String,
    isTTSHighlighted: () -> Boolean,
    highlights: () -> List<Highlight>,
    headerLevel: Int,
    currentChapterIndex: () -> Int,
    onRequestScrollToOffset: (Float) -> Unit,
) {
    val styleVM = LocalStylingViewModel.current
    val noteVM = LocalNoteViewModel.current
    val ttsVM = LocalTTSViewModel.current
    val dataVM = LocalDataViewModel.current
    val contentVM = LocalChapterContentViewModel.current
    val density = LocalDensity.current

    val stylingState by styleVM.state.collectAsStateWithLifecycle()
    val highlightRange by ttsVM.currentHighlightRange.collectAsStateWithLifecycle()
    val readingOffset by ttsVM.currentReadingWordOffset.collectAsStateWithLifecycle()

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var userSelectionRange by remember { mutableStateOf<TextRange?>(null) }
    var showSelectionPopup by remember { mutableStateOf(false) }
    var isOpenDialog by remember { mutableStateOf(false) }

    var layoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var dragStartOffset by remember { mutableStateOf<Offset?>(null) }

    val activeTtsRange by rememberUpdatedState(
        if (isTTSHighlighted()) highlightRange else TextRange.Zero
    )

    val paragraphBgColor = if (isTTSHighlighted()) stylingState.drawerContainerColor else Color.Transparent
    val annotatedText = remember(text) { AnnotatedString(text) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(paragraphBgColor)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = annotatedText,
            style = TextStyle(
                fontSize = calculateHeaderSize(headerLevel, stylingState.stylePreferences.fontSize).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                textAlign = TextAlign.Center,
                color = stylingState.stylePreferences.textColor,
                lineBreak = LineBreak.Paragraph,
                lineHeight = (stylingState.stylePreferences.fontSize + stylingState.stylePreferences.lineSpacing).sp
            ),
            onTextLayout = { textLayoutResult = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    layoutCoordinates = coordinates
                }
                .drawBehind {
                    val layout = textLayoutResult ?: return@drawBehind

                    if (stylingState.stylePreferences.enableHighlight) {
                        highlights().forEach { highlight ->
                            val startOffset = highlight.startOffset.fastCoerceIn(0, text.length)
                            val endOffset = highlight.endOffset.fastCoerceIn(0, text.length)

                            if (startOffset < endOffset) {
                                val rect = mutableListOf<Rect>()
                                val startLine = layout.getLineForOffset(startOffset)
                                val endLine = layout.getLineForOffset(endOffset)

                                for (line in startLine..endLine) {
                                    val left = if (line == startLine) {
                                        layout.getHorizontalPosition(startOffset, true)
                                    } else {
                                        layout.getLineLeft(line)
                                    }
                                    val right = if (line == endLine) {
                                        layout.getHorizontalPosition(endOffset, true)
                                    } else {
                                        layout.getLineRight(line)
                                    }
                                    val actualLeft = minOf(left, right)
                                    val actualRight = maxOf(left, right)
                                    if (actualRight - actualLeft > 1) {
                                        rect.add(
                                            Rect(
                                                left = actualLeft,
                                                top = layout.getLineTop(line),
                                                right = actualRight,
                                                bottom = layout.getLineBottom(line)
                                            )
                                        )
                                    }
                                }
                                val highlightPath = HighlightUtil.createRoundedSelectionPath(
                                    rect = rect,
                                    cornerRadius = 6.dp.toPx(),
                                    snapThreshold = 15.dp.toPx()
                                )

                                drawPath(
                                    path = highlightPath,
                                    color = stylingState.getHighlightColors()[highlight.colorIndex]
                                )
                            }
                        }
                    }

                    if (activeTtsRange.start != activeTtsRange.end) {
                        val ttsPath = layout.getPathForRange(
                            activeTtsRange.start.fastCoerceIn(0, text.length),
                            activeTtsRange.end.fastCoerceIn(0, text.length)
                        )
                        drawPath(
                            path = ttsPath,
                            color = stylingState.textBackgroundColor,
                            blendMode = BlendMode.SrcOver
                        )
                    }

                    userSelectionRange?.let { range ->
                        if (!range.collapsed) {
                            val selectionPath = layout.getPathForRange(range.start, range.end)
                            drawPath(selectionPath, color = Color(0xFF2196F3).copy(alpha = 0.3f))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { startOffset ->
                            val layout = textLayoutResult ?: return@detectDragGesturesAfterLongPress
                            dragStartOffset = startOffset

                            val globalPos = layoutCoordinates?.localToRoot(startOffset) ?: Offset.Unspecified
                            contentVM.onAction(BookChapterContentAction.UpdateGlobalMagnifierCenter(globalPos))

                            val index = layout.getOffsetForPosition(startOffset)
                            val wordRange = layout.getWordBoundary(index)
                            userSelectionRange = TextRange(wordRange.start, wordRange.end)

                            showSelectionPopup = false
                        },
                        onDrag = { change, _ ->
                            val layout = textLayoutResult ?: return@detectDragGesturesAfterLongPress
                            val start = dragStartOffset ?: return@detectDragGesturesAfterLongPress

                            val globalPos = layoutCoordinates?.localToRoot(change.position) ?: Offset.Unspecified
                            contentVM.onAction(BookChapterContentAction.UpdateGlobalMagnifierCenter(globalPos))

                            val startIndex = layout.getOffsetForPosition(start)
                            val endIndex = layout.getOffsetForPosition(change.position)

                            userSelectionRange = TextRange(
                                min(startIndex, endIndex),
                                max(startIndex, endIndex)
                            )
                        },
                        onDragEnd = {
                            contentVM.onAction(BookChapterContentAction.UpdateGlobalMagnifierCenter(Offset.Unspecified))
                            dragStartOffset = null
                            if (userSelectionRange != null && !userSelectionRange!!.collapsed) {
                                showSelectionPopup = true
                            } else {
                                userSelectionRange = null
                            }
                        },
                        onDragCancel = {
                            contentVM.onAction(BookChapterContentAction.UpdateGlobalMagnifierCenter(Offset.Unspecified))
                            dragStartOffset = null
                            userSelectionRange = null
                        }
                    )
                }
                .then(
                    if (userSelectionRange != null) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                userSelectionRange = null
                                showSelectionPopup = false
                            })
                        }
                    } else {
                        Modifier
                    }
                )
        )

        if (showSelectionPopup && userSelectionRange != null && textLayoutResult != null) {
            val range = userSelectionRange!!
            val layout = textLayoutResult!!
            val startRect = layout.getBoundingBox(range.start)
            val popupOffsetY = with(density) { 60.dp.toPx() }
            userSelectionRange?.let { range ->
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(
                        x = startRect.left.toInt(),
                        y = (startRect.top - popupOffsetY).toInt()
                    ),
                    onDismissRequest = { showSelectionPopup = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    SelectionMenu(
                        stylingState = stylingState,
                        onHighlight = { colorIndex ->
                            dataVM.onAction(
                                BookContentDataAction.AddHighlightForParagraph(
                                    HighlightToInsert(
                                        bookId = "",
                                        tocId = currentChapterIndex(),
                                        paragraphIndex = index,
                                        startOffset = range.start,
                                        endOffset = range.end,
                                        colorIndex = colorIndex,
                                        createdTime = System.currentTimeMillis()
                                    )
                                )
                            )
                            showSelectionPopup = false
                        },
                        onClearHighlight = {
                            dataVM.onAction(
                                BookContentDataAction.DeleteHighlightRange(
                                    tocId = currentChapterIndex(),
                                    paragraphIndex = index,
                                    start = range.start,
                                    end = range.end
                                )
                            )
                            showSelectionPopup = false
                        },
                        onAddNote = { isOpenDialog = true },
                        onAddingGlobalRegex = {

                        },
                        onEditSelectedText = {

                        },
                        onCopy = {

                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(showSelectionPopup) {
        if (!showSelectionPopup) {
            userSelectionRange = null
        }
    }

    LaunchedEffect(readingOffset, isTTSHighlighted(), textLayoutResult) {
        if (isTTSHighlighted() && textLayoutResult != null) {
            val layout = textLayoutResult!!
            val coercedWordOffset = { readingOffset.fastCoerceIn(0, layout.layoutInput.text.length) }
            val cursorRect = layout.getCursorRect(coercedWordOffset())
            onRequestScrollToOffset(cursorRect.bottom)
        }
    }

    if (isOpenDialog) {
        val selectedText = userSelectionRange?.let { text.substring(it.start, it.end) } ?: text
        NoteDialog(
            stylingState = stylingState,
            note = selectedText,
            onDismiss = {
                isOpenDialog = false
            },
            onNoteChanged = { noteInput ->
                noteVM.onAction(
                    NoteAction.AddNote(
                        noteBody = selectedText,
                        noteInput = noteInput,
                        tocId = currentChapterIndex(),
                        contentId = index
                    )
                )
                userSelectionRange = null
            }
        )
    }
}