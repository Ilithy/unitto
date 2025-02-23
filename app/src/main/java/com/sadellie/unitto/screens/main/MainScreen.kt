/*
 * Unitto is a unit converter for Android
 * Copyright (c) 2022-2022 Elshan Agaev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sadellie.unitto.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sadellie.unitto.R
import com.sadellie.unitto.data.NavRoutes.SETTINGS_SCREEN
import com.sadellie.unitto.data.units.AbstractUnit
import com.sadellie.unitto.screens.common.AnimatedTopBarText
import com.sadellie.unitto.screens.main.components.Keyboard
import com.sadellie.unitto.screens.main.components.TopScreenPart
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    navControllerAction: (String) -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    var launched: Boolean by rememberSaveable { mutableStateOf(false) }

    val mainScreenUIState = viewModel.mainFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                title = { AnimatedTopBarText(launched) },
                actions = {
                    IconButton(onClick = { navControllerAction(SETTINGS_SCREEN) }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.open_settings_description)
                        )
                    }
                },
                // Makes the background of the top bar transparent, by default uses secondary color
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        content = { padding ->
            PortraitMainScreenContent(
                modifier = Modifier.padding(padding),
                unitFrom = viewModel.unitFrom,
                unitTo = viewModel.unitTo,
                portrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT,
                mainScreenUIState = mainScreenUIState.value,
                navControllerAction = { navControllerAction(it) },
                swapMeasurements = { viewModel.swapUnits() },
                processInput = { viewModel.processInput(it) },
                deleteDigit = { viewModel.deleteDigit() }
            ) { viewModel.clearInput() }
        }
    )

    LaunchedEffect(Unit) {
        /**
         * 1.5 seconds is enough for user to see "Hello" in app bar title. Also, since we are using
         * Unit as key, "Hello" will be switched to app name only when composable is not getting
         * recomposed for 1.5 seconds.
         */
        delay(1500)
        launched = true
    }
}

@Composable
private fun PortraitMainScreenContent(
    modifier: Modifier,
    unitFrom: AbstractUnit,
    unitTo: AbstractUnit,
    portrait: Boolean = true,
    mainScreenUIState: MainScreenUIState = MainScreenUIState(),
    navControllerAction: (String) -> Unit = {},
    swapMeasurements: () -> Unit = {},
    processInput: (String) -> Unit = {},
    deleteDigit: () -> Unit = {},
    clearInput: () -> Unit = {},
) {
    if (portrait) {
        Column(
            modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TopScreenPart(
                modifier = Modifier,
                inputValue = mainScreenUIState.inputValue,
                calculatedValue = mainScreenUIState.calculatedValue,
                outputValue = mainScreenUIState.resultValue,
                unitFrom = unitFrom,
                unitTo = unitTo,
                loadingDatabase = mainScreenUIState.isLoadingDatabase,
                loadingNetwork = mainScreenUIState.isLoadingNetwork,
                networkError = mainScreenUIState.showError,
                onUnitSelectionClick = navControllerAction,
                swapUnits = swapMeasurements
            )
            // Keyboard which takes half the screen
            Keyboard(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                addDigit = processInput,
                deleteDigit = deleteDigit,
                clearInput = clearInput,
            )
        }
    } else {
        Row(
            modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TopScreenPart(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                inputValue = mainScreenUIState.inputValue,
                calculatedValue = mainScreenUIState.calculatedValue,
                outputValue = mainScreenUIState.resultValue,
                unitFrom = unitFrom,
                unitTo = unitTo,
                loadingDatabase = mainScreenUIState.isLoadingDatabase,
                loadingNetwork = mainScreenUIState.isLoadingNetwork,
                networkError = mainScreenUIState.showError,
                onUnitSelectionClick = navControllerAction,
                swapUnits = swapMeasurements
            )

            // Keyboard which takes half the screen
            Keyboard(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                addDigit = processInput,
                deleteDigit = deleteDigit,
                clearInput = clearInput,
            )
        }
    }
}
