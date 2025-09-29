@file:OptIn(ExperimentalMaterial3Api::class)
package com.bservan.apps.contacts.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bservan.apps.contacts.data.model.Contact
import com.bservan.apps.contacts.ui.state.ContactsViewModel
import kotlin.math.roundToInt
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.ui.state.ToastDispatcher

@Composable
fun SearchHistory(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent searches",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )

            TextButton(
                onClick = onClearHistory,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Clear all",
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(history) { historyItem ->
                SearchHistoryItem(
                    text = historyItem,
                    onClick = { onHistoryItemClick(historyItem) }
                )
            }
        }
    }
}

@Composable
fun SearchHistoryItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Recent search",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SearchableContactList(viewModel: ContactsViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf(listOf<String>()) }

    val contacts = viewModel.contacts.collectAsState().value

    val groupedContacts = remember(searchQuery, contacts) {
        derivedStateOf {
            contacts.filter { contact ->
                if (searchQuery.isBlank()) {
                    true
                } else {
                    val fullName = "${contact.state.name} ${contact.state.lastName}"
                    fullName.contains(searchQuery, ignoreCase = true) ||
                    contact.state.phoneNumber.contains(searchQuery, ignoreCase = true)
                }
            }.groupBy { contact ->
                contact.state.name.first().uppercase()
            }.toSortedMap()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearClick = { searchQuery = "" },
            onFocusChange = { focused -> isSearchFocused = focused },
            onSearch = { query ->
                if (query.isNotBlank() && !searchHistory.contains(query)) {
                    searchHistory = (listOf(query) + searchHistory).take(10)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearchFocused && searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
            SearchHistory(
                history = searchHistory,
                onHistoryItemClick = { historyItem ->
                    searchQuery = historyItem
                    isSearchFocused = false
                },
                onClearHistory = { searchHistory = emptyList() }
            )
        } else {
            val totalContacts = groupedContacts.value.values.sumOf { it.size }
            if (totalContacts > 0) {

                Spacer(modifier = Modifier.height(8.dp))

                GroupedContactList(
                    groupedContacts = groupedContacts.value,
                    onEditContact = { contact -> println("Contact edited: $contact") },
                    onDeleteContact = { contact ->
                        viewModel.deleteContact(contact.id)
                        ToastDispatcher.show("User is deleted!")
                    }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_not_found),
                        tint = Color.Unspecified,
                        contentDescription = null
                    )
                    Text(
                        text = "No Results",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The user you are looking for could not be found."
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
            },
        placeholder = { Text("Search contacts...", color = Color.LightGray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(query)
                focusManager.clearFocus()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.LightGray,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        )
    )
}

@Composable
fun GroupedContactList(
    groupedContacts: Map<String, List<Contact>>,
    onEditContact: (Contact) -> Unit,
    onDeleteContact: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        groupedContacts.forEach { (letter, contacts) ->
            item(key = "header_$letter") {
                Text(
                    text = letter,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }

            items(contacts.sorted(), key = { contact -> "${letter}_${contact.id}" }) { contact ->
                SwipeableContactItem(contact = contact, onEdit = onEditContact, onDelete = onDeleteContact)
            }
        }
    }
}

@Composable
fun SwipeableContactItem(
    contact: Contact,
    onEdit: (Contact) -> Unit,
    onDelete: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var itemWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    val actionButtonWidth = with(density) { 80.dp.toPx() }
    val maxSwipeDistance = actionButtonWidth * 2

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 300),
        label = "swipe_animation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size -> itemWidth = size.width }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
                    .clickable { onEdit(contact) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit contact",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                    )
                    .clickable { onDelete(contact) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete contact",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = when {
                                offsetX < -maxSwipeDistance / 3 -> -maxSwipeDistance
                                offsetX > maxSwipeDistance / 3 -> 0f
                                else -> 0f
                            }
                        }
                    ) { _, dragAmount ->
                        val newOffset = offsetX + dragAmount
                        offsetX = newOffset.coerceIn(-maxSwipeDistance, 0f)
                    }
                }
                .clickable {
                    if (offsetX != 0f) {
                        offsetX = 0f
                    }
                },
            colors = CardDefaults.cardColors().copy(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            ContactItemContent(contact = contact)
        }
    }
}

@Composable
fun ContactItemContent(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.state.name.first().uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${contact.state.name} ${contact.state.lastName}",
                fontWeight = FontWeight.Medium
            )
            Text(
                text = contact.state.phoneNumber,
            )
        }
    }
}
