package net.inspirehub.hr.expenses.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.AnalyticAccount
import net.inspirehub.hr.expenses.data.fetchAnalyticAccounts


@SuppressLint("SuspiciousIndentation", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticDistribution(
    onDistributionChange: (Map<Int, Int>) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var lines by remember { mutableStateOf(listOf("100")) }
    val colors = appColors()
    var analyticAccounts by remember { mutableStateOf(listOf<AnalyticAccount>()) }
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    var isLoading by remember { mutableStateOf(true) }
    var selectedDistributions by remember { mutableStateOf(listOf<String>()) }
    val allowedIds = sharedPref.getAllowedLocationsIds()
    var tempDistributions by remember { mutableStateOf(selectedDistributions.toMutableList()) }
    val totalPercentage = lines.mapNotNull { it.toFloatOrNull() }.sum()

    LaunchedEffect(Unit) {
        val token = sharedPref.getToken()
        if (!token.isNullOrEmpty()) {
            val accounts = fetchAnalyticAccounts(token, context)
            analyticAccounts = accounts
        }
        isLoading = false
    }

    TextField(
        value = selectedDistributions.joinToString(" | "),
        onValueChange = {},
        readOnly = true,
        placeholder = {
            Text(
                stringResource(R.string.analytic_distribution_small),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackgroundColor,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = colors.onBackgroundColor,
                modifier = Modifier.size(28.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        if (change.pressed) {
                            showSheet = true
                            change.consume()
                        }
                    }
                }
            },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.transparent,
            unfocusedContainerColor = colors.transparent,

            focusedIndicatorColor = colors.tertiaryColor,
            unfocusedIndicatorColor = colors.tertiaryColor,

            focusedTextColor = colors.onBackgroundColor,
            unfocusedTextColor = colors.onBackgroundColor
        ),
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.tertiaryColor
        ),
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
                Text(
                    text = stringResource(R.string.analytic_distribution),
                    color = colors.onBackgroundColor,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.projects) + " " + "(${totalPercentage.toInt()}%)",
                            color = colors.onBackgroundColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .weight(1f)
                        )
                        Text(
                            stringResource(R.string.percentage),
                            color = colors.onBackgroundColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        lines.forEachIndexed { index, percentage ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                AnalyticDistributionTextField(
                                    value = tempDistributions.getOrNull(index) ?: "",
                                    onValueChange = {
                                        val newList = tempDistributions.toMutableList()
                                        if (newList.size <= index) {
                                            repeat(index - newList.size + 1) { newList.add("") }
                                        }
                                        newList[index] = it
                                        tempDistributions = newList
                                    },
                                    placeholderText = " ",
                                    showIcon = true,
                                    loading = isLoading,
                                    modifier = Modifier.weight(1f),
                                    dropdownItems = analyticAccounts
                                        .filter { it.company_id != null && allowedIds.contains(it.company_id) }
                                        .map { it.name })

                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AnalyticDistributionTextField(
                                        value = percentage,
                                        onValueChange = {
                                            val newList = lines.toMutableList()
                                            newList[index] = it
                                            lines = newList
                                        },
                                        placeholderText = " ",
                                        showIcon = false,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Percent,
                                            contentDescription = "Percent",
                                            tint = colors.onBackgroundColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        IconButton(
                                            onClick = {
                                                val newLines = lines.toMutableList()
                                                val newDistributions = selectedDistributions.toMutableList()

                                                newLines.removeAt(index)
                                                newDistributions.removeAt(index)
                                                lines = newLines
                                                selectedDistributions = newDistributions
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                tint = colors.onBackgroundColor,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = colors.surfaceColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            stringResource(R.string.add_a_line),
                            color = colors.tertiaryColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    lines = lines + "100"
                                    selectedDistributions = selectedDistributions + ""
                                }
                        )
                      Button(
                          colors = ButtonDefaults.buttonColors(
                              contentColor = colors.onSecondaryColor,
                              containerColor = colors.tertiaryColor
                          ),
                          shape = RoundedCornerShape(10.dp),
                            onClick = {
                                selectedDistributions = tempDistributions.toList()

                                val result = mutableMapOf<Int, Int>()

                                selectedDistributions.forEachIndexed { index, name ->

                                    val account = analyticAccounts.find { it.name == name }

                                    val percentage = lines.getOrNull(index)?.toIntOrNull()

                                    if (account != null && percentage != null) {
                                        result[account.id] = percentage
                                    }
                                }
                                onDistributionChange(result)

                                showSheet = false
                            }
                        ) {
                          Text(
                              stringResource(R.string.apply),
                              fontSize = 15.sp,
                              fontWeight = FontWeight.SemiBold
                          )
                        }
                    }
                }
            }
        }
    }
}