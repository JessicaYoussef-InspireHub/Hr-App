package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun NumberOfItems(){
    val colors = appColors()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerHigh , shape = RoundedCornerShape(50.dp))
    ){
        Row (
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
        ){
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = "Restaurant",
                tint = colors.tertiaryColor,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                stringResource(R.string.you_have_items_in_your_list),
                color = colors.tertiaryColor)
        }
    }
}
