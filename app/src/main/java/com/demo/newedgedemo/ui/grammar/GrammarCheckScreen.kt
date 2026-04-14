package com.demo.newedgedemo.ui.grammar

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.newedgedemo.R
import com.demo.newedgedemo.ui.theme.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarCheckScreen(
    viewModel: GrammarViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccessMessage()
        }
    }

    BackHandler(enabled = !state.isInitialState || state.correctedText != null) {
        viewModel.setInitialState(true)
    }

    LaunchedEffect(state.isInitialState) {
        if (!state.isInitialState && state.errors.isEmpty() && state.correctedText == null) {
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        } else if (state.isInitialState) {
            keyboardController?.hide()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            Surface(color = PrimaryBlue) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    BannerAdView(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White))
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.grammar_check), color = Color.White, fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (!state.isInitialState || state.correctedText != null) {
                                    viewModel.setInitialState(true)
                                } else {
                                    onBackClick()
                                }
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Help action */ }) {
                                Icon(Icons.Outlined.HelpOutline, contentDescription = stringResource(R.string.help), tint = Color.White)
                            }
                        },
                        windowInsets = WindowInsets(0.dp),
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = PrimaryBlue
                        )
                    )
                }
            }
        },
        bottomBar = {
            Surface(color = Color.White, modifier = Modifier.navigationBarsPadding()) {
                GrammarBottomNavigation()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isInitialState) {
                InitialInputCard(
                    viewModel = viewModel,
                    state = state,
                    focusRequester = focusRequester,
                    modifier = Modifier.weight(1f)
                )
            } else {
                ResultsFlow(
                    viewModel = viewModel,
                    state = state,
                    focusRequester = focusRequester
                )
            }
            if (!state.isInitialState) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InitialInputCard(
    viewModel: GrammarViewModel,
    state: GrammarUiState,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
            Box(modifier = Modifier.weight(1f)) {
                TextField(
                    value = state.textFieldValue,
                    onValueChange = { viewModel.onTextChanged(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                viewModel.setInitialState(false)
                            }
                        },
                    textStyle = GrammarInputTextStyle,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = { Text(stringResource(R.string.tap_to_type_english), color = Color.LightGray, fontSize = 28.sp) }
                )
                
                if (state.textFieldValue.text.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearText() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear), tint = Color.Gray)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = if (isLandscape) 4.dp else 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton(icon = Icons.Default.Keyboard, label = stringResource(R.string.keyboard), isLandscape = isLandscape, isEnabled = true) {
                    viewModel.setInitialState(false)
                }
                ActionButton(icon = Icons.Default.Mic, label = stringResource(R.string.voice), isLandscape = isLandscape, isEnabled = false) {
                    Toast.makeText(context, context.getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show()
                }
                ActionButton(icon = Icons.Default.PhotoCamera, label = stringResource(R.string.camera), isLandscape = isLandscape, isEnabled = false) {
                    Toast.makeText(context, context.getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show()
                }
                ActionButton(icon = Icons.Default.ContentPaste, label = stringResource(R.string.paste), isLandscape = isLandscape, isEnabled = true) {
                    val clipboardText = clipboardManager.getText()?.text
                    if (clipboardText != null) {
                        viewModel.appendText(clipboardText)
                    } else {
                        Toast.makeText(context, context.getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Button(
                onClick = {
                    if (viewModel.checkInternetStatus()) {
                        keyboardController?.hide()
                        viewModel.checkGrammar()
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(if (isLandscape) 40.dp else 56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp), color = Color.White)
                    } else {
                        Spacer(modifier = Modifier.width(20.dp)) 
                        Text(
                            text = stringResource(R.string.fix_errors),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = if (isLandscape) 12.sp else 14.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, isLandscape: Boolean,
                 isEnabled: Boolean, onClick: () -> Unit) {
    val size = if (isLandscape) 36.dp else 64.dp
    val iconSize = if (isLandscape) 20.dp else 28.dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (!isEnabled) KeyboardButtonBg else PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (!isEnabled) PrimaryBlue else Color.White,
                modifier = Modifier.size(iconSize)
            )
            if (label == stringResource(R.string.keyboard)) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = if (isLandscape) 2.dp else 4.dp)
                )
            }
        }
        if (!isLandscape) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 12.sp, color = Color.Black)
        }
    }
}

@Composable
fun ResultsFlow(
    viewModel: GrammarViewModel,
    state: GrammarUiState,
    focusRequester: FocusRequester
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Input Card (Compressed)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = state.textFieldValue,
                    onValueChange = { viewModel.onTextChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                        .focusRequester(focusRequester),
                    textStyle = GrammarInputTextStyle,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(stringResource(R.string.enter_text_here), color = Color.LightGray, fontSize = 28.sp)
                    }
                )
                
                if (state.textFieldValue.text.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearText() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear), tint = Color.Gray)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.errors.isNotEmpty()) {
                    Surface(color = MistakeBackground, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = stringResource(R.string.mistakes, state.errors.size),
                            color = MistakeText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(43.dp)
                                .clip(CircleShape)
                                .background(ActiveInputBoxButtonBg),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = {
                                viewModel.setInitialState(true)
                            }) {
                                Icon(painterResource(R.drawable.keyboard_hide_300), contentDescription = stringResource(R.string.keyboard), tint = IconPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(43.dp)
                                .clip(CircleShape)
                                .background(ActiveInputBoxButtonBg),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = {
                                val clipboardText = clipboardManager.getText()?.text
                                if (clipboardText != null) {
                                    viewModel.appendText(clipboardText)
                                } else {
                                    Toast.makeText(context, context.getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(painterResource(R.drawable.content_copy), contentDescription = stringResource(R.string.paste), tint = IconPrimary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                if (state.correctedText == null) {
                    Button(
                        onClick = {
                            if (viewModel.checkInternetStatus()) {
                                keyboardController?.hide()
                                viewModel.checkGrammar()
                            } else {
                                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (state.errors.isEmpty()) stringResource(R.string.fix_errors) else stringResource(R.string.fix_errors),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(43.dp)
                                .clip(CircleShape)
                                .background(ActiveInputBoxButtonBg),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = {
                                val clipboardText = clipboardManager.getText()?.text
                                if (clipboardText != null) {
                                    viewModel.appendText(clipboardText)
                                } else {
                                    Toast.makeText(context, context.getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(painterResource(R.drawable.content_copy), contentDescription = stringResource(R.string.paste), tint = IconPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(43.dp)
                                .clip(CircleShape)
                                .background(ActiveInputBoxButtonBg),
                            contentAlignment = Alignment.Center
                        ){
                            Row {
                                IconButton(onClick = { /* Share */ }) {
                                    Icon(painterResource(R.drawable.share_300), contentDescription = stringResource(R.string.share), tint = IconPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (state.correctedText != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = state.correctedText!!,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp).padding(8.dp),
                    fontSize = 28.sp,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(ActiveInputBoxButtonBg),
                        contentAlignment = Alignment.Center
                    ){
                        IconButton(onClick = { /* Speak */ }) {
                            Icon(painterResource(R.drawable.sound_animation), contentDescription = stringResource(R.string.speak), tint = IconPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(ActiveInputBoxButtonBg),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            val clipboardText = state.correctedText
                            if (clipboardText != null) {
                                clipboardManager?.setText(clipboardText)
                                Toast.makeText(context, context.getString(R.string.clipboard_copied), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(painterResource(R.drawable.content_copy), contentDescription = stringResource(R.string.paste), tint = IconPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(ActiveInputBoxButtonBg),
                        contentAlignment = Alignment.Center
                    ){
                        Row {
                            IconButton(onClick = { /* Share */ }) {
                                Icon(painterResource(R.drawable.share_300), contentDescription = stringResource(R.string.share), tint = IconPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { 
                keyboardController?.hide()
                viewModel.clearText() 
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryBlue)
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.new_text), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun GrammarBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        val items = listOf(
            Triple(stringResource(R.string.bottom_nav_check), Icons.Default.TextFields, true),
            Triple(stringResource(R.string.bottom_nav_dictionary), Icons.Outlined.MenuBook, false),
            Triple(stringResource(R.string.bottom_nav_home), Icons.Outlined.Home, false),
            Triple(stringResource(R.string.bottom_nav_history), Icons.Outlined.History, false),
            Triple(stringResource(R.string.bottom_nav_favorites), Icons.Outlined.FavoriteBorder, false)
        )

        items.forEach { (label, icon, selected) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                selected = selected,
                onClick = { },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MistakeText,
                    selectedTextColor = MistakeText,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                //adUnitId = "ca-app-pub-6370111532336036/9475790138"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
