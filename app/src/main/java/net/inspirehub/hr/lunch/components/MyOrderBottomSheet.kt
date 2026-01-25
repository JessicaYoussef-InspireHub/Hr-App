package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderBottomSheet(
    onOrderSuccess: () -> Unit
) {

    val colors = appColors()
    var showSheet by remember { mutableStateOf(false) }



    Box(
        modifier = Modifier
            .background(colors.surfaceContainerHigh, CircleShape)
            .clickable { showSheet = true }
    ) {
        Icon(
            imageVector = Icons.Default.Fastfood,
            contentDescription = "MyOrder",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                    Text(
                        text = stringResource(R.string.my_order),
                        color = colors.onBackgroundColor,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))


                    OrderRow()
                    HorizontalDivider(
                        modifier = Modifier.padding(8.dp),
                        color = colors.surfaceColor
                    )
                    OrderRow()
                    HorizontalDivider(
                        modifier = Modifier.padding( 8.dp),
                        color = colors.surfaceColor
                    )
                    OrderRow()
                    HorizontalDivider(
                        modifier = Modifier.padding(8.dp),
                        color = colors.surfaceColor
                    )
                    OrderRow()
                    HorizontalDivider(
                        modifier = Modifier.padding(8.dp),
                        color = colors.surfaceColor
                    )
                    OrderRow()
                    HorizontalDivider(
                        modifier = Modifier.padding( 8.dp),
                        color = colors.surfaceColor
                    )
                    OrderRow()

                    Spacer(modifier = Modifier.height(20.dp))

                    TotalPrice(stringResource(R.string.total), "110.0 EGP")

                    Spacer(modifier = Modifier.height(20.dp))

                    OrderNowButton(
                        onClick = {
                            showSheet = false
                            onOrderSuccess()
                        }
                    )
                }
            }
        }
    }
}