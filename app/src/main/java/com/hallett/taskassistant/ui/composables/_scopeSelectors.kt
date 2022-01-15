package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.formatters.Formatter
import kotlinx.coroutines.flow.Flow
import org.kodein.di.compose.rememberInstance

@ExperimentalMaterialApi
@Composable
fun ScopeSelectorScreen(
    scopes: Flow<PagingData<Scope>>,
    modalState: ModalBottomSheetState,
    onScopeTypeSelected: (ScopeType) -> Unit,
    onScopeSelected: (Scope?) -> Unit,
    onFullyExpandedContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val expandedContent: @Composable () -> Unit = when(modalState.currentValue) {
        ModalBottomSheetValue.Expanded -> onFullyExpandedContent
        else -> {{ }}
    }
    ModalBottomSheetLayout(
        sheetState = modalState,
        sheetContent = {
            ScopeSelectorContent(
                scopes = scopes,
                onScopeTypeSelected = onScopeTypeSelected,
                onScopeSelected = onScopeSelected,
                extraContent = expandedContent
            )
        },
        content = content
    )
}

@Composable
fun ScopeSelectorContent(
    scopes: Flow<PagingData<Scope>>,
    onScopeTypeSelected: (ScopeType) -> Unit,
    onScopeSelected: (Scope?) -> Unit,
    extraContent: @Composable () -> Unit
) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()){
        extraContent()

        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)) {
            Row(
                horizontalArrangement = SpaceBetween,
                modifier = Modifier.fillMaxWidth()){
                Button(
                    onClick = { setIsExpanded(!isExpanded) }
                ) {
                    Text("Select New Scope")
                }
                IconButton(onClick = { onScopeSelected(null) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove scope")
                }
            }
            ScopeTypeSelector(
                isExpanded = isExpanded,
                onDismiss = { setIsExpanded(false) },
                onScopeTypeSelected = onScopeTypeSelected
            )
        }

        ScopeList(
            selectableScopes = scopes,
            onScopeSelected = onScopeSelected
        )
    }
}

@Composable
fun ScopeTypeSelector(
    isExpanded: Boolean = false,
    onDismiss: () -> Unit,
    onScopeTypeSelected: (ScopeType) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(),
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
        contentPadding = PaddingValues(12.dp),
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
                style = MaterialTheme.typography.h5,
            )
            Text(
                secondaryLabelFormatter.format(scope),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle2,
            )
        }
    }
}

