package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.unit.dp
import com.bluelinelabs.conductor.Controller
import com.example.app.utils.MultiDevicePreview
import com.example.app.utils.TextStyles.body1


class ExampleController : Controller() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = ComposeView(inflater.context).apply {
        setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ExampleScreen(
                onCloseClicked = { /*TODO*/ },
            )
        }
        isClickable = true
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExampleScreen(
    onCloseClicked: () -> Unit,
) {
    BottomSheet(
        title = "BottomSheet title",
        lightMode = false,
        onClosedClicked = onCloseClicked,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Content of the BottomSheet",
            style = body1,
            color = White,
        )
    }
}

@MultiDevicePreview
@Composable
fun ExampleScreenPreview() {
    ExampleScreen(
        onCloseClicked = {},
    )
}
