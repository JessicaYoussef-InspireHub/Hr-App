package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun PaidBy(
    initialPaidBy: String?,
    onPaymentModeChange: (String) -> Unit
) {
    val colors = appColors()
    var selectedOption by remember { mutableStateOf("") }

    val employeeText = stringResource(R.string.employee)
    val companyText = stringResource(R.string.company)

    LaunchedEffect(initialPaidBy) {
        selectedOption = when (initialPaidBy) {
            "company", "company_account" -> companyText
            else -> employeeText
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            RadioButton(
                selected = selectedOption == employeeText,
                onClick = {
                    selectedOption = employeeText
                    onPaymentModeChange("employee")
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.tertiaryColor,
                    unselectedColor = colors.onBackgroundColor,
                )
            )
            Text(
                stringResource(R.string.employee),
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)

        ) {
            RadioButton(
                selected = selectedOption == companyText,
                onClick = {
                    selectedOption = companyText
                    onPaymentModeChange("company_account")
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.tertiaryColor,
                    unselectedColor = colors.onBackgroundColor,
                )
            )
            Text(
                stringResource(R.string.company),
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}