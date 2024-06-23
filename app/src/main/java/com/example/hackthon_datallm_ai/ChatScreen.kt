package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Model.ViewModelChat
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import com.example.hackthon_datallm_ai.geminidatamanager.Resource


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(
    navController: NavController,
    viewmodel: ViewModelChat,
    chatViewModel: ChatViewModel
) {


    val conversation = viewmodel.conversation.collectAsState()


    val listState = rememberLazyListState()
    LaunchedEffect(conversation.value.size) {
        listState.animateScrollToItem(conversation.value.size)
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {

            CenterAlignedTopAppBar(
                actions = {
                    IconButton(onClick = { navController.navigate("datashow") }) {
                        Image(
                            painter = painterResource(id = R.drawable.datatable),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row {
                        Text(
                            "DataGenie",
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        Image(
                            painter = painterResource(id = R.drawable.robot),
                            contentDescription = "robo",
                            Modifier
                                .size(42.dp)
                                .padding(bottom = 5.dp)
                        )
                    }
                }
            )
        }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (messages, chatBox) = createRefs()
            val aichat by chatViewModel.chatdata.observeAsState()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .constrainAs(messages) {
                        top.linkTo(parent.top)
                        bottom.linkTo(chatBox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                contentPadding = PaddingValues(16.dp)
            ) {
                val initialValue = chatViewModel.chatdata.value


                items(conversation.value) { message ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(if (message.isFromMe) Alignment.End else Alignment.Start)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 48f,
                                        topEnd = 48f,
                                        bottomStart = if (message.isFromMe) 48f else 0f,
                                        bottomEnd = if (message.isFromMe) 0f else 48f
                                    )
                                )
                                .background(
                                    if (message.isFromMe) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        MaterialTheme.colorScheme.onTertiary
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            if (message.isFromMe) {
                                Text(text = message.text)
                            } else {
                                Crossfade(targetState = aichat, label = "") { state ->
                                    when (state) {
                                        is Resource.Loading -> {
                                            if (conversation.value.last() == message) {
                                                AnimatedLoadingGradient()
                                            } else {
                                                Text(text = message.text)
                                            }
                                        }

                                        is Resource.Stop -> {
                                            Text(text = message.text)
                                        }

                                        is Resource.Success -> {
                                            Text(text = message.text)
                                        }

                                        null -> {
                                            Text(text = message.text)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }



            ChatBox(
                { message ->
                    if (message.isNotBlank()) {
                        viewmodel.sendChat(message)
                    } else {
                        viewmodel.sendChat("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(chatBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(
    onSendChatClickListener: (String) -> Unit,
    modifier: Modifier
) {
    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
    Row(modifier = modifier.padding(16.dp)) {
        TextField(
            value = chatBoxValue,
            onValueChange = { newText ->
                chatBoxValue = newText
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Type something", color = MaterialTheme.colorScheme.onSecondary)
            }
        )
        IconButton(
            onClick = {
                val msg = chatBoxValue.text

                if (msg.isNotBlank()) {
                    onSendChatClickListener(msg)
                    chatBoxValue = TextFieldValue("") // Clear text input after sending
                }
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.secondary)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}


@Composable
fun AnimatedLoadingGradient(
) {
    val geminiPrimaryColor = MaterialTheme.colorScheme.surface
    val geminiContainerColor = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
                .animatedGradient(
                    primaryColor = geminiPrimaryColor,
                    containerColor = geminiContainerColor
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
                .animatedGradient(
                    primaryColor = geminiPrimaryColor,
                    containerColor = geminiContainerColor
                )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(fraction = 0.7f)
                .animatedGradient(
                    primaryColor = geminiPrimaryColor,
                    containerColor = geminiContainerColor
                )
        )
    }
}

fun Modifier.animatedGradient(
    primaryColor: Color,
    containerColor: Color
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")
    val colors = listOf(
        primaryColor,
        containerColor,
        primaryColor
    )
    val offsetXAnimation by transition.animateFloat(
        initialValue = -size.width.toFloat(),
        targetValue = size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "gradientAnimation"
    )
    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(x = offsetXAnimation, y = 0f),
            end = Offset(x = offsetXAnimation + size.width.toFloat(), y = size.height.toFloat())
        ),
        shape = RoundedCornerShape(20.dp)
    )
        .onGloballyPositioned {
            size = it.size
        }
}