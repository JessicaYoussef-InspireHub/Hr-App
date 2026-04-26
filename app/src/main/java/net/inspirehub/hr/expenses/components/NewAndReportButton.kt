package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun NewAndReportButton(
    onCreateReport: () -> Unit,
    viewReport: () -> Unit,
    onNewExpenses: () -> Unit,
    onUpload: () -> Unit,
    is17Version: Boolean
) {
    val colors = appColors()
    var expandedNew by remember { mutableStateOf(false) }
    var expandedReport by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = { expandedNew = !expandedNew },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                contentColor = colors.onSecondaryColor,
                containerColor = colors.tertiaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                stringResource(R.string.is_new),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expandedNew,
            onDismissRequest = { expandedNew = false },
            modifier = Modifier.background(colors.surfaceContainerHigh)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.upload),
                        color = colors.onBackgroundColor
                    )
                },
                onClick = {
                    expandedNew = false
                    onUpload()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.new_expense),
                        color = colors.onBackgroundColor
                    )
                },
                onClick = {
                    expandedNew = false
                    onNewExpenses()
                }
            )
        }

        if (is17Version) {
        Button(
            onClick = { expandedReport = !expandedReport },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.surfaceColor,
                contentColor = colors.onBackgroundColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                stringResource(R.string.reports),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }}

        DropdownMenu(
            expanded = expandedReport,
            onDismissRequest = { expandedReport = false },
            offset = DpOffset(x = (-12).dp, y = (0).dp),
            modifier = Modifier.background(colors.surfaceContainerHigh)
        ) {
            DropdownMenuItem(
                text = { Text(
                    stringResource(R.string.create_report),
                    color = colors.onBackgroundColor
                ) },
                onClick = {
                    expandedReport = false
                    onCreateReport()
                }
            )
            DropdownMenuItem(
                text = { Text(
                    stringResource(R.string.view_reports),
                    color = colors.onBackgroundColor
                ) },
                onClick = {
                    expandedReport = false
                    viewReport()
                }
            )
        }
    }
}