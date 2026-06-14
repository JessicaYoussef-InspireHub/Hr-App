package net.inspirehub.hr

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmallButtons(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    isLoading: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    equalWeight: Boolean = false
) {
    val colors = appColors()

    if (isLoading) {
        Box(
            modifier = Modifier
                .clickable(enabled = false) {}
        ) {
            SmallLoading()
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement =
                if (equalWeight) Arrangement.Center
                else Arrangement.End
        ) {
            Button(
                onClick = { onConfirm() },
                modifier =
                    if (equalWeight) Modifier.weight(1f)
                    else Modifier,
                colors = ButtonDefaults.buttonColors(
                    contentColor = colors.onSecondaryColor,
                    containerColor = colors.tertiaryColor
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    confirmButtonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (dismissButtonText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDismiss() },
                    modifier =
                        if (equalWeight) Modifier.weight(1f)
                        else Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.inverseOnSurface,
                        contentColor = colors.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        dismissButtonText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}