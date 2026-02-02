package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun CasualLeave(
    remainingDays: Double,
) {
    val colors = appColors()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.inversePrimary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style =
                    SpanStyle(
                        fontWeight = FontWeight.Bold ,
                        fontSize = 18.sp)) {
                    append("${stringResource(R.string.this_is_a_casual_leave)} \n")
                }
                append(stringResource(R.string.this_leave_qualifies_as_a_casual_emergency_leave_according_to_egyptian_labor_law_you_have))
                withStyle(style =
                    SpanStyle(
                        fontWeight = FontWeight.Bold ,
                        fontSize = 17.sp)) {
                    append(" ${"%.2f".format(remainingDays)} ${stringResource(R.string.days)}" + " ")
            }
                append(stringResource(R.string.remaining_for_this_year))

            },
            color = colors.scrim,
            fontSize = 15.sp,
        )
    }
}