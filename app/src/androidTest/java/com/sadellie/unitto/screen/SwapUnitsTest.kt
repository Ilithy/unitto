package com.sadellie.unitto.screen


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sadellie.unitto.data.preferences.DataStoreModule
import com.sadellie.unitto.data.preferences.UserPreferencesRepository
import com.sadellie.unitto.data.units.AllUnitsRepository
import com.sadellie.unitto.data.units.MyUnitIDS
import com.sadellie.unitto.data.units.database.MyBasedUnitDatabase
import com.sadellie.unitto.data.units.database.MyBasedUnitsRepository
import com.sadellie.unitto.screens.main.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SwapUnitsTest {

    private lateinit var viewModel: MainViewModel
    private val allUnitsRepository = AllUnitsRepository()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = MainViewModel(
            UserPreferencesRepository(DataStoreModule().provideUserPreferencesDataStore(context)),
            MyBasedUnitsRepository(
                Room.inMemoryDatabaseBuilder(
                    context,
                    MyBasedUnitDatabase::class.java
                ).build().myBasedUnitDao()
            ),
            ApplicationProvider.getApplicationContext(),
            allUnitsRepository
        )
    }

    @Test
    fun swapUnits() {
        val mile = allUnitsRepository.getById(MyUnitIDS.mile)
        val kilometer = allUnitsRepository.getById(MyUnitIDS.kilometer)

        viewModel.changeUnitFrom(kilometer)
        viewModel.changeUnitTo(mile)
        viewModel.swapUnits()

        assertEquals(mile, viewModel.unitFrom)
        assertEquals(kilometer,viewModel.unitTo)
    }
}
