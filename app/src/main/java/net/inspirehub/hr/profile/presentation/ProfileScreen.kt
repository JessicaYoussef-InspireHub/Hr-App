package net.inspirehub.hr.profile.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.bottom_app_bar.BottomBar
import net.inspirehub.hr.profile.components.HeaderCard
import net.inspirehub.hr.profile.components.PersonalInformationCard
import net.inspirehub.hr.profile.components.WorkInformationCard
import net.inspirehub.hr.profile.components.WorkLocationCard
import net.inspirehub.hr.profile.data.loadProfileFromPrefs


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val profile = remember { loadProfileFromPrefs(SharedPrefManager(context)) }
    val colors = appColors()

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.my_profile),
                onBackClick = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 5.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            HeaderCard(profile = profile)

            PersonalInformationCard(title = stringResource(R.string.personal_information))
            WorkInformationCard(title = stringResource(R.string.work_information))
            WorkLocationCard(title = stringResource(R.string.work_locations))

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}