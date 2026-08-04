package net.inspirehub.hr

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GeneralIcon(
    imageVector: ImageVector,
    contentDescription: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.size(40.dp),
    tint: Color = appColors().onBackgroundColor
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun CloseIcon(
    onClick: () -> Unit,
    contentDescription: String? = stringResource(R.string.close)
) {
    val colors = appColors()

    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = contentDescription,
            tint = colors.tertiaryColor
        )
    }
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    tint: Color = appColors().tertiaryColor,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        modifier = modifier
            .size(32.dp)
            .clickable {
                onClick()
            },
        tint = tint
    )
}

@Composable
fun AddIcon(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.size(14.dp),
    onClick: () -> Unit = {}
){
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "add",
        tint = colors.tertiaryColor,
        modifier = modifier.clickable { onClick() }
    )
}


@Composable
fun ArrowDropDownIcon(
    expanded: Boolean,
    tint: Color = appColors().onBackgroundColor
) {

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300),
        label = "ArrowRotation"
    )

    Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = if (expanded) {
            "Close dropdown"
        } else {
            "Open dropdown"
        },
        tint = tint,
        modifier = Modifier
            .size(28.dp)
            .rotate(rotation)
    )
}

@Composable
fun KeyboardArrowLeftIcon(
    onClick: () -> Unit
) {
    val color = appColors()
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "Previous Month",
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() },
        tint = color.onBackgroundColor
    )
}

@Composable
fun DeleteIcon(
    onClick: () -> Unit,
    tint: Color,
    size: Int = 28
) {

    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete),
            tint = tint,
            modifier = Modifier.size(size.dp)
        )
    }
}
@Composable
fun KeyboardArrowRightIcon(
    onClick: () -> Unit
) {
    val color = appColors()

    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Next Month",
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() },
        tint = color.onBackgroundColor
    )
}

@Composable
fun StarIcon(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = appColors()

    Icon(
        imageVector = if (isFavorite)
            Icons.Filled.Star
        else
            Icons.Outlined.StarBorder,
        contentDescription = if (isFavorite)
            "Remove from favorites"
        else
            "Add to favorites",
        tint = if (isFavorite)
            colors.tertiaryColor
        else
            colors.onBackgroundColor,
        modifier = Modifier
            .size(20.dp)
            .then(modifier)
            .clickable { onClick() }
    )
}

@Composable
fun SearchIcon(
    tint: Color
) {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search",
        tint = tint
    )
}

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier
) {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Select",
        tint = colors.onSecondaryColor,
        modifier = modifier
    )
}

@Composable
fun SendIcon(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (enabled)
        Icons.AutoMirrored.Filled.Send
    else
        Icons.Default.Check

    val rotation = if (enabled) -30f else 0f
    val colors = appColors()


    Icon(
        imageVector = icon,
        contentDescription = if (enabled) "Send" else "Already Submitted",
        modifier = Modifier
            .size(22.dp)
            .rotate(rotation)
            .clickable(enabled = enabled) {
                onClick()
            },
        tint = colors.tertiaryColor,
    )
}

@Composable
fun FilterAltIcon(
    isActive: Boolean
) {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.FilterAlt,
        contentDescription = "Filter",
        tint = if (isActive) colors.tertiaryColor else colors.onBackgroundColor,
        modifier = Modifier
            .size(50.dp)
            .padding(10.dp)
    )
}

@Composable
fun ChecklistIcon(
    onClick: () -> Unit
) {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Checklist,
        contentDescription = "Select",
        tint = colors.onSecondaryColor,
        modifier = Modifier
            .size(40.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
fun RemoveIcon() {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Remove,
        contentDescription = "minus",
        tint = colors.tertiaryColor,
        modifier = Modifier.size(14.dp)
    )
}

@Composable
fun FingerprintIcon(
    onClick: () -> Unit
){
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Fingerprint,
        contentDescription = stringResource(R.string.fingerprint_authentication),
        modifier = Modifier
            .size(200.dp)
            .clickable {
               onClick()
            },
        tint = colors.tertiaryColor
    )
}

@Composable
fun PasswordVisibilityIcon(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    val colors = appColors()

    val image =
        if (isVisible) Icons.Filled.Visibility
        else Icons.Filled.VisibilityOff

    IconButton(onClick = onClick) {
        Icon(
            imageVector = image,
            contentDescription = if (isVisible)
                "Hide password"
            else
                "Show password",
            tint = colors.tertiaryColor
        )
    }
}

@Composable
fun ArrowRightAlt(){
    val colors = appColors()

    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
        contentDescription = "",
        modifier = Modifier
            .size(20.dp),
        tint = colors.onBackgroundColor
    )
}

@Composable
fun StatusCircleIcon(
    imageVector: ImageVector,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                tint.copy(alpha = 0.12f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun AttachFileIcon() {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.AttachFile,
        contentDescription = "Attachments",
        tint = colors.tertiaryColor,
        modifier = Modifier
            .size(18.dp)
    )
}

@Composable
fun GridViewIcon(
    color: Color ,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.GridView,
        contentDescription = "GridView",
        tint = color,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Composable
fun ViewListIcon(
    color: Color ,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ViewList,
        contentDescription = "ViewList",
        tint = color ,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Composable
fun ViewAgendaIcon(
    color: Color ,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.ViewAgenda,
        contentDescription = "ViewAgenda",
        tint = color,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Composable
fun ScheduleIcon(
    size : Int = 16
) {
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = stringResource(R.string.hours_worked),
        tint = colors.onBackgroundColor,
        modifier = Modifier.size(size.dp)

    )
}

@Composable
fun DescriptionIcon(
    size: Dp = 18.dp
){
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.Description,
        contentDescription = "has Permission",
        tint = colors.outline,
        modifier = Modifier.size(size)
    )
}

@Composable
fun DateRangeIcon(){
    val colors = appColors()

    Icon(
        imageVector = Icons.Default.DateRange,
        contentDescription = "DateRange",
        tint = colors.onBackgroundColor ,
        modifier = Modifier.size(18.dp)
    )
}