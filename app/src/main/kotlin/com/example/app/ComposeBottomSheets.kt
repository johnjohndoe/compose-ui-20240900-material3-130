package com.example.app

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.app.BottomSheetState.HIDE
import com.example.app.BottomSheetState.INITIAL
import com.example.app.BottomSheetState.SHOW
import com.example.app.utils.MultiDevicePreview
import com.example.app.utils.TextStyles
import com.example.app.utils.TextStyles.body1
import com.example.app.utils.bottomSheetMaxWidth
import com.example.app.utils.roundedCornerSize4
import com.example.app.utils.spacingR2
import com.example.app.utils.spacingR4
import com.example.app.utils.spacingR6
import com.example.app.utils.spacingR8
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Full screen half transparent background. Used by modal [androidx.compose.ui.window.Dialog].
 */
@Composable
fun OpaqueBackground(
    onClicked: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.black_opaque))
            .clickable { onClicked() },
        contentAlignment = Alignment.Center,
        content = content
    )
}

enum class BottomSheetState {
    INITIAL,
    SHOW,
    HIDE
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    bottomSheetState: BottomSheetState = SHOW,
    title: String,
    lightMode: Boolean,
    initialState: ModalBottomSheetValue = Hidden,
    onClosedClicked: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialState,
        skipHalfExpanded = true,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.8f)
    )
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val navigationBarInsetPx = WindowInsets.navigationBars.getBottom(density)
    val safeDrawingInsetPx = WindowInsets.safeDrawing.getBottom(density)
    val currentBottomInsetPx = max(navigationBarInsetPx, safeDrawingInsetPx)
    var maxSeenBottomInsetPx by remember { mutableIntStateOf(0) }
    val minimumBottomInsetPx = with(density) { 24.dp.roundToPx() }
    val effectiveBottomInsetDp =
        with(density) { max(maxSeenBottomInsetPx, minimumBottomInsetPx).toDp() }

    val foregroundColor = if (lightMode) R.color.rich_black90 else R.color.snow_white
    val backgroundColor = if (lightMode) R.color.snow_white else R.color.background_sheet_dark
    val dividerColor = if (lightMode) R.color.lavender_gray else R.color.rich_black70

    //We wrap this modal in a box so that we can modify the width of the sheet for tablets, we have to process the clicks in this area ourselves
    OpaqueBackground(
        onClicked = { scope.launch { modalBottomSheetState.hide() } }
    ) {
        if (bottomSheetState != INITIAL) {
            ModalBottomSheetLayout(
                modifier = Modifier.widthIn(max = bottomSheetMaxWidth()),
                scrimColor = colorResource(R.color.full_transparency), //Handled by the OpaqueBackground as we need bottomSheets that don't match the scrim width
                sheetState = modalBottomSheetState,
                sheetBackgroundColor = colorResource(backgroundColor),
                sheetShape = RoundedCornerShape(
                    topStart = roundedCornerSize4(),
                    topEnd = roundedCornerSize4()
                ),
                sheetContent = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { }
                            )
                            .padding(bottom = effectiveBottomInsetDp + spacingR6())
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = spacingR4(),
                                    top = spacingR2(),
                                    bottom = spacingR2()
                                ),
                            verticalAlignment = CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = title,
                                color = colorResource(foregroundColor),
                                style = TextStyles.header2
                            )
                            IconButton(
                                onClick = { scope.launch { modalBottomSheetState.hide() } },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = colorResource(foregroundColor),
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_close),
                                    contentDescription = stringResource(R.string.default_close),
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colorResource(dividerColor))
                        )

                        Column(
                            modifier = Modifier.padding(vertical = spacingR4())
                        ) {
                            content()
                        }
                    }
                }
            ) {
                LaunchedEffect(bottomSheetState) {
                    when (bottomSheetState) {
                        HIDE -> modalBottomSheetState.hide()
                        else -> modalBottomSheetState.show()
                    }
                }

                LaunchedEffect(currentBottomInsetPx, bottomSheetState) {
                    if (bottomSheetState == SHOW) {
                        maxSeenBottomInsetPx = max(maxSeenBottomInsetPx, currentBottomInsetPx)
                    } else {
                        maxSeenBottomInsetPx = 0
                    }
                }

                LaunchedEffect(currentBottomInsetPx, bottomSheetState) {
                    if (bottomSheetState == SHOW && modalBottomSheetState.currentValue != Hidden) {
                        modalBottomSheetState.show()
                    }
                }

                LaunchedEffect(modalBottomSheetState.targetValue) {
                    if (modalBottomSheetState.targetValue == Hidden) {
                        onClosedClicked()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@MultiDevicePreview
@Composable
private fun BottomSheetPreview() {
    BottomSheet(
        title = "BottomSheet title",
        lightMode = true,
        onClosedClicked = {},
        initialState = Expanded,
        content = {
            Row(
                modifier = Modifier.padding(vertical = spacingR8()),
            ) {
                Text(text = "Your content goes here.", style = body1)
            }
        }
    )
}
