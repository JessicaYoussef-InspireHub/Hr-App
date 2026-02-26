package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun OrderNowButton(
    onClick: () -> Unit,
    isLoading: Boolean = false
){
    val colors = appColors()

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.tertiaryColor,
            contentColor = colors.onSecondaryColor
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = { if (!isLoading) onClick() }
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
                Text(
                    text = stringResource(R.string.order_now),
                    fontWeight = FontWeight.Bold
                )
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FullLoading()
                }
            }
        }
    }
}