package net.inspirehub.hr.notifications.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun NoNotifications() {
    val colors = appColors()

    Column(
        modifier = Modifier.fillMaxSize()
            .background(colors.onSecondaryColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.no_notifications),
            contentDescription = stringResource(R.string.no_notifications_yet),
            modifier = Modifier.height(250.dp).width(250.dp)
        )
        Text(
            text = stringResource(R.string.no_notifications_yet),
            color = colors.tertiaryColor ,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}