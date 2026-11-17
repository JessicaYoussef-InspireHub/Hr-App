package net.inspirehub.hr.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors


@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = appColors()
    NavigationDrawerItem(
        label = { Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        ) },
        icon = { Icon(icon, contentDescription = label) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = colors.tertiaryColor.copy(alpha = 0.12f),
            unselectedContainerColor = colors.transparent,
            selectedIconColor = colors.tertiaryColor,
            selectedTextColor = colors.tertiaryColor,
            unselectedIconColor = colors.onBackgroundColor,
            unselectedTextColor = colors.onBackgroundColor
        )
    )
}