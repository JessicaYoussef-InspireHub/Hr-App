package net.inspirehub.hr.lunch.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallLoading
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.Supplier
import net.inspirehub.hr.lunch.data.fetchSuppliers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersFilterBottomSheet(
    context: Context,
    token: String,
    onApply: (List<Int>) -> Unit
) {

    val colors = appColors()
    var showSheet by remember { mutableStateOf(false) }
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var selectedSuppliers by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val isAllSelected = selectedSuppliers.all { it }


    LaunchedEffect(showSheet) {
        if (showSheet && suppliers.isEmpty()) {
            isLoading = true
            val result = fetchSuppliers(context = context, token = token)
            suppliers = result
            selectedSuppliers = List(result.size) { false }
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .background(colors.surfaceContainerHigh, CircleShape)
            .clickable { showSheet = true }
    ) {
        Icon(
            imageVector = Icons.Default.LocalCarWash,
            contentDescription = stringResource(R.string.filter_by_suppliers),
            tint = colors.onBackgroundColor,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = colors.surfaceContainerHigh,
                windowInsets = WindowInsets(0)
            )
            {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .padding(horizontal = 8.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd,
                    ) {
                        IconButton(onClick = { showSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = colors.tertiaryColor,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.filter_by_suppliers),
                            color = colors.onBackgroundColor,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold
                        )


                        if (!isLoading)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isAllSelected,
                                    onCheckedChange = { checked ->
                                        selectedSuppliers = List(suppliers.size) { checked }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = colors.tertiaryColor,
                                        checkmarkColor = colors.onSecondaryColor,
                                        uncheckedColor = colors.onBackgroundColor
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.select_all),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.onBackgroundColor,
                                )
                            }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    val maxHeightDp = 200.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = maxHeightDp)
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier.verticalScroll(scrollState)
                        ) {
                    if (isLoading) {
                        SmallLoading()
                    } else {

                        suppliers.forEachIndexed { index, supplier ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp, horizontal = 12.dp)
                                        .clickable {
                                            selectedSuppliers =
                                                selectedSuppliers.toMutableList().also {
                                                    it[index] = !it[index]
                                                }
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Name",
                                                tint = colors.onBackgroundColor,
                                                modifier = Modifier.size(25.dp)
                                            )
                                            Text(
                                                text = supplier.name,
                                                color = colors.onBackgroundColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )

                                        }

                                        Spacer(modifier = Modifier.height(5.dp))


                                        Row(
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "Phone",
                                                tint = colors.onBackgroundColor,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .padding(start = 4.dp)
                                            )
                                            Text(
                                                text = supplier.phone,
                                                color = colors.onBackgroundColor,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "Address",
                                                tint = colors.onBackgroundColor,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .padding(start = 2.dp)
                                            )
                                            Text(
                                                text = supplier.address,
                                                color = colors.onBackgroundColor,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }

                                    Checkbox(
                                        checked = selectedSuppliers.getOrElse(index) { false },
                                        onCheckedChange = { isChecked ->
                                            selectedSuppliers =
                                                selectedSuppliers.toMutableList().also {
                                                    it[index] = isChecked
                                                }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            uncheckedColor = colors.tertiaryColor,
                                            checkedColor = colors.tertiaryColor,
                                            checkmarkColor = colors.onSecondaryColor
                                        )
                                    )
                                }
                                if (index != suppliers.size - 1) {
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        color = colors.surfaceColor
                                    )
                                }
                            }
                        }}}}
                        Spacer(modifier = Modifier.height(20.dp))

                        SuppliersFilterActions(
                            onApply = {
                                val selectedSupplierIds = suppliers
                                    .filterIndexed { index, _ ->
                                        selectedSuppliers.getOrElse(index) { false }
                                    }
                                    .map { it.id }

                                onApply(selectedSupplierIds)
                                showSheet = false
                            },
                            onDiscard = {
                                selectedSuppliers = List(suppliers.size) { false }
                                onApply(emptyList())
                                showSheet = false
                            }
                        )
                    }
                }
            }
        }
}