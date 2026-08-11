package net.inspirehub.hr.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.check_in_out.data.LocalAttendanceReminderManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import net.inspirehub.hr.InfoIcon

@Composable
fun AttendanceReminderCard() {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }

    var isReminderEnabled by remember { mutableStateOf(sharedPref.isAttendanceReminderEnabled()) }
    var showReminderDialog by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.attendance_reminder),
            color = colors.tertiaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        colors.inverseOnSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = colors.onSecondaryColor
            )
        ) {
            Column {
                SettingsItem(
                    label = stringResource(R.string.attendance_reminder),
                    icon = Icons.Default.NotificationsActive,
                    onClick = {
                        if (!isReminderEnabled) {
                            showReminderDialog = true
                        } else {
                            isReminderEnabled = false

                            LocalAttendanceReminderManager.stop(
                                context
                            )
                        }
                    },
                    trailingIcon = {
                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showReminderDialog = true
                                } else {
                                    isReminderEnabled = false

                                    LocalAttendanceReminderManager.stop(
                                        context
                                    )
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onSecondaryColor,
                                checkedTrackColor = colors.tertiaryColor,
                                uncheckedThumbColor = colors.onSecondaryColor,
                                uncheckedTrackColor = colors.onBackgroundColor,
                                uncheckedBorderColor = colors.transparent,
                                checkedBorderColor = colors.transparent
                            )
                        )
                    }
                )
            }
        }
    }

    if (showReminderDialog) {

        MyDialog(
            onConfirm = {
                showReminderDialog = false
                isReminderEnabled = true
                LocalAttendanceReminderManager.start(context)
            },

            onDismiss = {
                showReminderDialog = false
                isReminderEnabled = false
            },
            title = stringResource(R.string.attendance_reminder),
            subtitle = "",
            subtitleContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.attendance_reminder_intro))
                            append("\n\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.tertiaryColor
                                )
                            ) {
                                append(stringResource(R.string.check_out_reminder_title))
                            }
                            append(" ")
                            append(stringResource(R.string.check_out_reminder_description))

                            append("\n\n")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.tertiaryColor
                                )
                            ) {
                                append(stringResource(R.string.check_in_reminder_title))
                            }
                            append(" ")
                            append(stringResource(R.string.check_in_reminder_description))
                            append("\n")
                        },
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        color = colors.onBackgroundColor
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.tertiaryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.Top
                        ) {
                            InfoIcon()

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(
                                    R.string.attendance_reminder_no_api
                                ),
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButtonText = stringResource(R.string.ok),

            dismissButtonText = stringResource(R.string.cancel)
        )
    }
}