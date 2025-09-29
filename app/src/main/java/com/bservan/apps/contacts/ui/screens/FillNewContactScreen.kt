package com.bservan.apps.contacts.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.data.model.db.ContactsDatabase
import com.bservan.apps.contacts.domain.repository.ContactRepositoryImpl
import com.bservan.apps.contacts.ui.state.ContactViewModel
import com.bservan.apps.contacts.ui.state.ContactsViewModel
import com.bservan.apps.contacts.ui.theme.ContactsBlue
import com.bservan.apps.contacts.ui.theme.ContactsTheme
import com.bservan.apps.contacts.ui.theme.ContactsPhotoPlaceholder
import com.bservan.apps.contacts.ui.widget.BottomPanel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FillNewContactScreen(
    contactsViewModel: ContactsViewModel,
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val state = viewModel.state
    val shape = RoundedCornerShape(16.dp)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var show by rememberSaveable { mutableStateOf(false) }
    var contactAdded by rememberSaveable { mutableStateOf(false) }
    var doneVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(contactAdded) {
        if (contactAdded) {
            doneVisible = true
            delay(2500L)
            doneVisible = false
        }
    }

    AnimatedVisibility(
        visible = doneVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            ContactAddDoneScreen()
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(onTap = {
                    //keyboardController?.hide()
                    focusManager.clearFocus()
                })
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Cancel", color = ContactsBlue)
                }
                Text(
                    text = "New Contact",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                TextButton(
                    onClick = {
                        contactsViewModel.createContact(
                            firstName = viewModel.state.name.trim(),
                            lastName = viewModel.state.lastName.trim(),
                            phoneNumber = viewModel.state.phoneNumber.trim(),
                            image = viewModel.state.image
                        )
                        contactAdded = true
                        viewModel.clear()
                        viewModel.viewModelScope.launch {
                            delay(2500)
                            onDismiss()
                        }
                    },
                    enabled = !state.name.isEmpty() && !state.lastName.isEmpty() && !state.phoneNumber.isEmpty(),
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = ContactsBlue,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Done")
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = if (viewModel.state.image != null) rememberAsyncImagePainter(viewModel.state.image) else painterResource(R.drawable.ic_contact_placeholder),
                        tint = if (viewModel.state.image != null) ContactsPhotoPlaceholder else Color.Unspecified,
                        contentDescription = null
                    )
                    TextButton(
                        onClick = {
                            show = true
                        },
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = ContactsBlue,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "Add Photo",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(12.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = { Text("First Name", color = Color.LightGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChanged,
                placeholder = { Text("Last Name", color = Color.LightGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                placeholder = { Text("Phone Number", color = Color.LightGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomPanel(
                visible = show,
                onDismiss = { show = false },
                heightRatio = 0.1f,
                disableBackground = false
            ) {
                AddPhotoScreen(Modifier.fillMaxWidth(), viewModel, onDismiss = { show = false })
            }
        }
    }
}
