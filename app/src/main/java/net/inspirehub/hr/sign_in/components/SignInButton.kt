package net.inspirehub.hr.sign_in.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun SignInButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val colors = appColors()

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.tertiaryColor,
            contentColor = colors.onSecondaryColor,
            disabledContainerColor = colors.tertiaryColor.copy(alpha = 0.4f),
            disabledContentColor = colors.onSecondaryColor.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(stringResource(R.string.sign_in) ,
            fontWeight = FontWeight.Bold)
    }
}