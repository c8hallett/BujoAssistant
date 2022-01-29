package com.hallett.taskassistant.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.formatters.Formatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.kodein.di.compose.rememberInstance

@Composable
fun ColumnScope.ScopeSelectorContent(
    scopeType: ScopeType,
    scopes: Flow<PagingData<Scope>>,
    onScopeTypeSelected: (ScopeType) -> Unit,
    onScopeSelected: (Scope?) -> Unit,
) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.TopStart)) {
        ActiveScopeTypeSelector(
            scopeType = scopeType,
            isExpanded = isExpanded,
            setIsExpanded = setIsExpanded,
            onScopeTypeSelected = onScopeTypeSelected,
        )
    }

    ScopeList(
        selectableScopes = scopes,
        onScopeSelected = onScopeSelected
    )
}

@Composable
fun ActiveScopeTypeSelector(
    scopeType: ScopeType,
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    onScopeTypeSelected: (ScopeType) -> Unit,
) {
    Row(
        horizontalArrangement = SpaceBetween,
        modifier = Modifier.fillMaxWidth()){

        val expandIconId = "expandArrowId"
        val scopeTypeLabel = AnnotatedString.Builder().apply {
            append("During the ")
            pushStyle(SpanStyle(color = MaterialTheme.colors.secondaryVariant, fontWeight = FontWeight.Bold))
            append(scopeType.name)
            appendInlineContent(expandIconId, if(isExpanded) "[expand]" else "[collapse]")
            pop()
            append(" of")
        }
        val inlineIcon = mapOf(
            expandIconId to InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                )
            ) {
                when(isExpanded) {
                    true -> Icon(Icons.Default.ExpandLess,"",tint = MaterialTheme.colors.secondary)
                    false -> Icon(Icons.Default.ExpandMore,"",tint = MaterialTheme.colors.secondary)
                }
            }
        )

        Text(
            text = scopeTypeLabel.toAnnotatedString(),
            inlineContent = inlineIcon,
            modifier = Modifier.clickable { setIsExpanded(!isExpanded) }
        )

        ScopeTypeDropDownMenu(
            isExpanded = isExpanded,
            onDismiss = { setIsExpanded(false) },
            onScopeTypeSelected = onScopeTypeSelected
        )
    }
}

@Composable
fun ScopeTypeDropDownMenu(
    isExpanded: Boolean = false,
    onDismiss: () -> Unit,
    onScopeTypeSelected: (ScopeType) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
    ) {
        ScopeType.values().forEach { scopeType ->
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onScopeTypeSelected(scopeType)
                }
            ) {
                Text(scopeType.name, modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically))
            }
        }
    }
}

@Composable
fun ScopeList(selectableScopes: Flow<PagingData<Scope>>, onScopeSelected: (Scope) -> Unit) {
    val lazyScopes = selectableScopes.collectAsLazyPagingItems()
    LazyColumn(
        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 0.dp ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        items(lazyScopes) { selectableScope ->
            when(selectableScope){
                null -> Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.Gray)
                )
                else -> ScopeListItem(scope = selectableScope, onScopeSelected = onScopeSelected)
            }
        }
    }
}

@Composable
fun ScopeListItem(scope: Scope, onScopeSelected: (Scope) -> Unit) {
    val labelFormatter: Formatter<Scope?, String> by rememberInstance(tag = Formatter.SIMPLE_DATE)
    val secondaryLabelFormatter: Formatter<Scope?, String> by rememberInstance(tag = Formatter.OFFSET_LABEL)
    val extraPaddingFormatter: Formatter<Scope?, Dp> by rememberInstance(tag = Formatter.EXTRA_PADDING)

    Card(
        backgroundColor = Color.LightGray,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onScopeSelected(scope) }
    ) {
        Row(
            horizontalArrangement = SpaceBetween,
            modifier = Modifier
                .padding(vertical = 12.dp + extraPaddingFormatter.format(scope), horizontal = 12.dp)
        ){
            Text(
                labelFormatter.format(scope),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.h6,
            )
            Text(
                secondaryLabelFormatter.format(scope),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle2,
            )
        }
    }
}

@Composable
@Preview
fun ScopeSelectorPreview() {
    Column {
        ScopeSelectorContent(
            scopeType = ScopeType.DAY,
            scopes = flowOf(),
            onScopeTypeSelected = {},
            onScopeSelected = {}
        )
    }
}


@Composable
fun ScopeSelection(
    scope: Scope?,
    scopeType: ScopeType,
    isSelectActive: Boolean,
    setSelectActive: (Boolean) -> Unit,
    scopes: Flow<PagingData<Scope>>,
    onScopeTypeSelected: (ScopeType) -> Unit,
    onScopeSelected: (Scope?) -> Unit,
) {
    fun onScopeSelectedWithDismiss(scope: Scope?) {
        onScopeSelected(scope)
        setSelectActive(false)
    }

    Column(modifier = Modifier
        .animateContentSize()
        .fillMaxWidth()) {
        ScopeSelectButton(
            scope = scope,
            isSelectActive = isSelectActive,
            setSelectActive = setSelectActive,
            onScopeSelected = ::onScopeSelectedWithDismiss
        )

        if(isSelectActive) {
            ScopeSelectorContent(
                scopeType = scopeType,
                scopes = scopes,
                onScopeTypeSelected = onScopeTypeSelected,
                onScopeSelected = ::onScopeSelectedWithDismiss
            )
        }
    }
}

@Composable
fun ColumnScope.ScopeSelectButton(
    scope: Scope?,
    isSelectActive: Boolean,
    setSelectActive: (Boolean) -> Unit,
    onScopeSelected: (Scope?) -> Unit,
) {
    val labelFormatter: Formatter<Scope?, String> by rememberInstance(tag = Formatter.SIMPLE_DATE)

    data class ScopeSelectInfo(
        val bgColor: Color,
        val textColor: Color,
        val icon: ImageVector,
        val contentDescription: String,
    )

    val info: ScopeSelectInfo = when (isSelectActive) {
        true -> ScopeSelectInfo(
            bgColor = Color.Transparent,
            textColor = MaterialTheme.colors.error,
            icon = Icons.Default.Close,
            contentDescription = "cancel scope selection",
        )
        false -> ScopeSelectInfo(
            bgColor = Color.Transparent,
            textColor = MaterialTheme.colors.secondaryVariant,
            icon = Icons.Default.Reply,
            contentDescription = "select scope",
        )
    }
    when(scope){
        null -> Button(
            onClick = { setSelectActive(!isSelectActive) },
            colors = ButtonDefaults.textButtonColors(contentColor = info.textColor),
            modifier = Modifier.align(Alignment.End),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(labelFormatter.format(scope), color = info.textColor)
                Icon(info.icon, contentDescription = info.contentDescription, tint = info.textColor)
            }
        }
        else -> Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            // delete button
            Button(
                onClick = { onScopeSelected(null) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Icon(Icons.Default.Delete, "remove scope")
            }
            Button(
                onClick = { setSelectActive(!isSelectActive) },
                colors = ButtonDefaults.textButtonColors(contentColor = info.textColor),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(labelFormatter.format(scope), color = info.textColor)
                    Icon(info.icon, contentDescription = info.contentDescription, tint = info.textColor)
                }
            }
        }
    }
}

@Composable
@Preview
fun ScopeSelectionButtonActive() {
    Column {
        ScopeSelectButton(
            scope = null,
            isSelectActive = false,
            setSelectActive = {},
            onScopeSelected = {}
        )
    }
}

@Composable
@Preview
fun ScopeSelectionButtonInactive() {
    Column {
        ScopeSelectButton(
            scope = null,
            isSelectActive = true,
            setSelectActive = {},
            onScopeSelected = {}
        )
    }
}

