package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun OrderNowButton(
    onClick: () -> Unit
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
        onClick = {
            onClick()
        },
    ){
        Text(text = "Order Now",
            fontWeight = FontWeight.Bold)


    }

}