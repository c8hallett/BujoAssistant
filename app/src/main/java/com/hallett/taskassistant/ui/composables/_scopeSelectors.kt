package com.hallett.taskassistant.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.corndux.EnterScopeSelection
import com.hallett.taskassistant.corndux.ScopeSelectionCancelled
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.corndux.SelectNewScopeType
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.UpdateTaskScope
import com.hallett.taskassistant.ui.formatters.Formatter
import kotlinx.coroutines.flow.Flow
import org.kodein.di.compose.rememberInstance

@Composable
fun ActiveScopeTypeSelector(
    scopeType: ScopeType,
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    onScopeTypeSelected: (ScopeType) -> Unit,
) {
    Row(
        horizontalArrangement = SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {

        ScopeTypeSelectorLabel(
            scopeType = scopeType,
            isExpanded = isExpanded,
            setIsExpanded = setIsExpanded
        )

        ScopeTypeDropDownMenu(
            isExpanded = isExpanded,
            onDismiss = { setIsExpanded(false) },
            onScopeTypeSelected = onScopeTypeSelected
        )
    }
}

@Composable
fun ScopeTypeSelectorLabel(
    scopeType: ScopeType,
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val expandIconId = "expandArrowId"
    val scopeTypeLabel = AnnotatedString.Builder().apply {
        append("during the ")
        pushStyle(
            SpanStyle(
                color = MaterialTheme.colors.secondaryVariant,
                fontWeight = FontWeight.Bold
            )
        )
        append(scopeType.name)
        appendInlineContent(expandIconId, if (isExpanded) "[expand]" else "[collapse]")
        pop()
        append(" of")
    }
    val inlineIcon = mapOf(
        expandIconId to InlineTextContent(
            Placeholder(
                width = MaterialTheme.typography.h5.fontSize,
                height = MaterialTheme.typography.h5.fontSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            when (isExpanded) {
                true -> Icon(Icons.Default.ExpandLess, "", tint = MaterialTheme.colors.secondary)
                false -> Icon(Icons.Default.ExpandMore, "", tint = MaterialTheme.colors.secondary)
            }
        }
    )

    Text(
        text = scopeTypeLabel.toAnnotatedString(),
        inlineContent = inlineIcon,
        modifier = modifier.then(Modifier.clickable { setIsExpanded(!isExpanded) }),
        style = MaterialTheme.typography.h5
    )

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
        modifier = Modifier.background(color = MaterialTheme.colors.secondaryVariant),
    ) {
        ScopeType.values().forEach { scopeType ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.secondaryVariant),
                onClick = {
                    onScopeTypeSelected(scopeType)
                    onDismiss()
                }
            ) {
                Text(
                    scopeType.name,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun InactiveScopeLabel(
    scope: Scope?,
    onLabelClicked: () -> Unit
) {
    val labelFormatter: Formatter<Scope?, String> by rememberInstance(tag = Formatter.SIMPLE_LABEL)

    Text(
        text = labelFormatter.format(scope),
        modifier = Modifier.clickable { onLabelClicked() },
        style = MaterialTheme.typography.h5,
        color = MaterialTheme.colors.onSurface
    )
}


@Composable
fun ScopeList(selectableScopes: Flow<PagingData<Scope>>, onScopeSelected: (Scope) -> Unit) {
    val lazyScopes = selectableScopes.collectAsLazyPagingItems()
    LazyColumn(
        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 0.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lazyScopes) { selectableScope ->
            when (selectableScope) {
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
        ) {
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
fun ScopeSelection(
    state: TaskAssistantState,
    dispatchAction: (TaskAssistantAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
    ) {

        when (val scopeSelectionInfo = state.scopeSelectionInfo) {
            null -> InactiveScopeLabel(
                scope = state.scope,
                onLabelClicked = { dispatchAction(EnterScopeSelection) }
            )
            else -> ActiveScopeSelectionContent(
                scopeSelectionInfo = scopeSelectionInfo,
                dispatchAction = dispatchAction
            )
        }
    }
}


@Composable
fun ColumnScope.ActiveScopeSelectionContent(
    scopeSelectionInfo: ScopeSelectionInfo,
    dispatchAction: (TaskAssistantAction) -> Unit,
) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }

    Row(horizontalArrangement = SpaceBetween) {
        ScopeTypeSelectorLabel(
            scopeType = scopeSelectionInfo.scopeType,
            isExpanded = isExpanded,
            setIsExpanded = setIsExpanded,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = { dispatchAction(UpdateTaskScope(null)) },
        ) {
            Icon(Icons.Default.Delete, "remove scope", tint = MaterialTheme.colors.error)
        }
        IconButton(
            onClick = { dispatchAction(ScopeSelectionCancelled) },
        ) {
            Icon(Icons.Default.Cancel, "cancel edit", tint = MaterialTheme.colors.error)
        }
        ScopeTypeDropDownMenu(
            isExpanded = isExpanded,
            onDismiss = { setIsExpanded(false) },
            onScopeTypeSelected = { dispatchAction(SelectNewScopeType(it)) }
        )
    }
    ScopeList(
        selectableScopes = scopeSelectionInfo.scopes,
        onScopeSelected = { dispatchAction(UpdateTaskScope(it)) }
    )
}

@Composable
@Preview
fun ScopeListItemPreview() {
    val scopeGenerator: IScopeGenerator by rememberInstance()

    ScopeListItem(
        scope = scopeGenerator.generateScope(),
        onScopeSelected = {}
    )
}

@Composable
@Preview
fun ExpandedActiveScopeTypeSelectorPreview() {
    ActiveScopeTypeSelector(
        scopeType = ScopeType.DAY,
        isExpanded = true,
        setIsExpanded = {},
        onScopeTypeSelected = {},
    )
}

@Composable
@Preview
fun CollapsedActiveScopeTypeSelectorPreview() {
    ActiveScopeTypeSelector(
        scopeType = ScopeType.DAY,
        isExpanded = false,
        setIsExpanded = {},
        onScopeTypeSelected = {},
    )
}
