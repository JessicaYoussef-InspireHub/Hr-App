package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import net.inspirehub.hr.lunch.data.DatabaseProvider
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.inspirehub.hr.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFavorite() {
    val colors = appColors()
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val favorites by db.favoriteLunchDao()
        .getAllFavoritesFlow()
        .collectAsState(initial = emptyList())
    var showClearDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()




    Box(
        modifier = Modifier
            .background(colors.surfaceContainerHigh, CircleShape)
            .clickable { showSheet = true }
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "favorite",
            tint = colors.onBackgroundColor,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )


        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = colors.surfaceContainerHigh,
                windowInsets = WindowInsets(0)
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .padding(horizontal = 8.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd,
                    ) {
                        IconButton(onClick = { showSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = colors.tertiaryColor,
                            )
                        }
                    }

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = stringResource(R.string.my_favorite),
                            color = colors.onBackgroundColor,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                        if (!favorites.isEmpty())
                        Text(
                            text = stringResource(R.string.clear_favorites),
                            color = colors.onBackgroundColor,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 10.dp)
                                .clickable { showClearDialog = true },
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (favorites.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_favorites_yet),
                                color = colors.tertiaryColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        FavoriteCard()

                    }
                }
            }
            if (showClearDialog) {
                ClearFavoritesDialog(
                    onDismiss = { showClearDialog = false },
                    onConfirm = {
                        coroutineScope.launch {
                            db.favoriteLunchDao().deleteAllFavorites()
                        }
                        showClearDialog = false
                    }
                )
            }

        }
    }
}