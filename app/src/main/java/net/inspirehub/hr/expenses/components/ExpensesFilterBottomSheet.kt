package net.inspirehub.hr.expenses.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesFilterBottomSheet(
    tempFromDate: LocalDate?,
    tempToDate: LocalDate?,
    selectedStatuses: Set<String>,
    onFromDateClick: () -> Unit,
    onToDateClick: () -> Unit,
    onStatusChange: (Set<String>) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    onDateReset: () -> Unit,
    onStatusReset: () -> Unit,
    expenses: Boolean = true,
    attachmentFilter: Boolean? = null,
    onAttachmentFilterChange: (Boolean?) -> Unit = {}
) {
    val colors = appColors()

    var dateError by remember { mutableStateOf(false) }
    var showDateSection by remember { mutableStateOf(tempFromDate != null || tempToDate != null) }
    var showAttachmentSection by remember { mutableStateOf(attachmentFilter != null) }
    var showStatusSection by remember { mutableStateOf(selectedStatuses.isNotEmpty()) }


    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
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
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = colors.tertiaryColor,
                    )
                }
            }

            if (expenses) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showDateSection,
                        onCheckedChange = {
                            showDateSection = it
                            if (!it) {
                                onDateReset()
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.tertiaryColor,
                            checkmarkColor = colors.onSecondaryColor,
                            uncheckedColor = colors.onBackgroundColor
                        )
                    )

                    Text(
                        text = stringResource(R.string.filter_by_date),
                        color = colors.onBackgroundColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (showDateSection) {
                    ExpensesDateRangeRow(
                        fromDate = tempFromDate,
                        toDate = tempToDate,
                        onFromDateClick = {
                            onFromDateClick()
                            dateError = false
                        },
                        onToDateClick = {
                            onToDateClick()
                            dateError = false
                        }
                    )
                }


                if (dateError) {
                    Text(
                        text = stringResource(R.string.please_check_the_date_you_entered),
                        color = colors.error,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    )
                }

                Spacer(Modifier.height(35.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showStatusSection,
                    onCheckedChange = {
                        showStatusSection = it
                        if (!it) {
                            onStatusReset()
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.tertiaryColor,
                        checkmarkColor = colors.onSecondaryColor,
                        uncheckedColor = colors.onBackgroundColor
                    )
                )

                Text(
                    text = stringResource(R.string.filter_by_status),
                    color = colors.onBackgroundColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showStatusSection) {
                ExpenseFlowRow(
                    selectedStatuses = selectedStatuses,
                    onStatusChange = onStatusChange,
                    expenses = expenses
                )
            }

            if (expenses) {
                Spacer(Modifier.height(35.dp))

                Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showAttachmentSection,
                    onCheckedChange = {
                        showAttachmentSection = it
                        if (!it) {
                            onAttachmentFilterChange(null)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.tertiaryColor,
                        checkmarkColor = colors.onSecondaryColor,
                        uncheckedColor = colors.onBackgroundColor
                    )
                )

                Text(
                    text = stringResource(R.string.filter_by_attachment),
                    color = colors.onBackgroundColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }}

            if (showAttachmentSection) {
                AttachmentFlowRow(
                    selected = attachmentFilter,
                    onChange = {
                        onAttachmentFilterChange(it)
                    }
                )
            }

            Spacer(Modifier.height(40.dp))

            ResetAndApplyButtons(
                onReset = onReset,
                onApply = {
                    val from = tempFromDate ?: LocalDate.now()
                    val to = tempToDate ?: LocalDate.now()

                    if (from.isAfter(to)) {
                        dateError = true
                    } else {
                        dateError = false
                        onApply()
                    }
                }
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}