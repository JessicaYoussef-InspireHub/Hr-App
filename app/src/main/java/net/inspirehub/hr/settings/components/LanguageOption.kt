package net.inspirehub.hr.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@Composable
fun LanguageOption(
    flag: String,
    title: String,
    isNew: Boolean = false
) {
    val color = appColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = flag, fontSize = 25.sp)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = color.onBackgroundColor
        )


        if (isNew) {
            Surface(
                color = color.tertiaryColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.is_new),
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    color = color.tertiaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}