package com.rerere.iwara4a.ui.screen.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.video.VideoDetail
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.public.ExoPlayer
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.util.noRippleClickable
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@SuppressLint("WrongConstant")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoScreen(
    navController: NavController,
    videoId: String,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val orientation = LocalScreenOrientation.current
    val context = LocalContext.current as Activity

    // 判断视频是否加载了
    fun isVideoLoaded() =
        videoViewModel.videoDetail != VideoDetail.LOADING && !videoViewModel.error && !videoViewModel.isLoading

    fun getTitle() =
        if (videoViewModel.isLoading) "加载中" else if (isVideoLoaded()) videoViewModel.videoDetail.title else if (videoViewModel.error) "加载失败" else "视频页面"

    val videoLink = if (isVideoLoaded()) videoViewModel.videoDetail.videoLinks[0].toLink() else ""

    // 加载视频
    LaunchedEffect(Unit) {
        videoViewModel.loadVideo(videoId)
    }

    // 响应旋转
    LaunchedEffect(orientation) {
        if (isVideoLoaded()) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                context.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            } else {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
    }

    // 处理返回
    BackHandler(isVideoLoaded() && orientation == Configuration.ORIENTATION_LANDSCAPE) {
        context.requestedOrientation = Configuration.ORIENTATION_PORTRAIT
    }


    Scaffold(
        topBar = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                FullScreenTopBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = getTitle(), maxLines = 1)
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Player
            ExoPlayer(
                modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .requiredHeightIn(max = 210.dp)
                        .background(Color.Black)
                else
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                videoLink = videoLink
            )

            when {
                isVideoLoaded() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        VideoInfo(navController, videoViewModel.videoDetail)
                    }
                }
                videoViewModel.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(text = "加载中")
                        }
                    }
                }
                videoViewModel.error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { videoViewModel.loadVideo(videoId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .padding(10.dp)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.anime_4),
                                    contentDescription = null
                                )
                            }
                            Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun TabItem(pagerState: PagerState, index: Int, text: String) {
    val coroutineScope = rememberCoroutineScope()
    val selected = pagerState.currentPage == index
    Box(
        modifier = Modifier
            .noRippleClickable { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CompositionLocalProvider(LocalContentAlpha provides if (selected) ContentAlpha.high else ContentAlpha.disabled) {
                Text(text = text)
            }

            AnimatedVisibility(selected) {
                Spacer(
                    modifier = Modifier
                        .width(32.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colors.onBackground)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun VideoInfo(navController: NavController, videoDetail: VideoDetail) {
    val pagerState = rememberPagerState(pageCount = 2, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(pagerState, 0, "简介")
            TabItem(pagerState, 1, "评论")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth(),
                state = pagerState
            ) {
                when (it) {
                    0 -> VideoDescription(navController ,videoDetail)
                    1 -> CommentPage()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun VideoDescription(navController: NavController, videoDetail: VideoDetail) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            // 视频简介
            Card(modifier = Modifier.padding(8.dp), elevation = 4.dp) {
                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .padding(16.dp)
                ) {
                    // 作者信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 作者头像
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = rememberCoilPainter(videoDetail.authorPic),
                                contentDescription = null
                            )
                        }

                        // 作者名字
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = videoDetail.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = Color(0xfff45a8d)
                        )
                    }
                    // 视频信息
                    Row(Modifier.padding(vertical = 4.dp)) {
                        Text(text = "播放: ${videoDetail.watchs} 喜欢: ${videoDetail.likes}")
                    }

                    var expand by remember {
                        mutableStateOf(false)
                    }
                    Crossfade(expand) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                                // TODO: 解析URL
                                Text(
                                    text = videoDetail.description,
                                    maxLines = if (expand) 10 else 1
                                )
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .noRippleClickable { expand = !expand }
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    modifier = Modifier.size(20.dp),
                                    onClick = { expand = !expand }) {
                                    Icon(
                                        imageVector = if (it) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // 更多视频
        item {
            Text(
                text = "该作者的其他视频:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(videoDetail.moreVideo) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            println(it.id)
                            navController.navigate("video/${it.id}")
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(5.dp))) {
                        Image(
                            modifier = Modifier.fillMaxHeight(),
                            painter = rememberCoilPainter(it.pic),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight
                        )
                    }

                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                    ) {
                        Text(text = it.title, fontWeight = FontWeight.Bold)
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(text = "播放: ${it.watchs} 喜欢: ${it.likes}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentPage() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(100) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp), elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Text(text = "哈哈哈: $it", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}