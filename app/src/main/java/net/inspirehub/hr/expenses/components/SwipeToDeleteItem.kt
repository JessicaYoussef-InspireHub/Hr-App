package net.inspirehub.hr.expenses.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(
    expense: ExpenseItem,
    onDelete: () -> Unit,
    navController: NavController,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val colors = appColors()
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true
                false
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
                    .background(colors.error)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    color = colors.onBackgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        content = {
            ExpenseItemCard(
                expense ,
                navController ,
                isSelectionMode,
                isSelected,
                onSelect)
        }
    )


    if (showDialog) {
        AlertDialog(
            containerColor = colors.surfaceVariant,
            onDismissRequest = { showDialog = false },

            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = colors.tertiaryColor,
                            modifier = Modifier
                                .clickable { showDialog = false }
                        )
                    }
                    Text(
                        stringResource(R.string.delete_confirmation),
                        color = colors.tertiaryColor,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },

            text = {
                Text(
                    text = stringResource(R.string.are_you_sure_you_want_to_delete_this_expense),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onBackgroundColor
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colors.onSecondaryColor,
                            containerColor = colors.tertiaryColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            onDelete()
                            showDialog = false
                        }
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.inverseOnSurface,
                            contentColor = colors.onSecondaryColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            showDialog = false
                            scope.launch {
                                dismissState.reset()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }
}