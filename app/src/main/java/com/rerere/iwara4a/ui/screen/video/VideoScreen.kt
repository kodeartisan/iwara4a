package com.rerere.iwara4a.ui.screen.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.*
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.public.CommentItem
import com.rerere.iwara4a.ui.public.ExoPlayer
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.TabItem
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable

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
                .navigationBarsWithImePadding()
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
                        VideoInfo(navController, videoViewModel, videoViewModel.videoDetail)
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

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun VideoInfo(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
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
                    0 -> VideoDescription(navController, videoViewModel, videoDetail)
                    1 -> CommentPage(navController, videoViewModel)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun VideoDescription(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val context = LocalContext.current
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
                                .noRippleClickable {
                                    navController.navigate("user/${videoDetail.authorName}")
                                }
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = rememberCoilPainter(videoDetail.authorPic),
                                contentDescription = null
                            )
                        }

                        // 作者名字
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .noRippleClickable {
                                    navController.navigate("user/${videoDetail.authorName}")
                                },
                            text = videoDetail.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = PINK
                        )

                        // 关注
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    videoViewModel.handleFollow { action, success ->
                                        if (action) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "关注了该UP主！ ヾ(≧▽≦*)o" else "关注失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "已取消关注" else "取消关注失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }
                                .background(
                                    if (videoDetail.follow) Color.LightGray else Color(
                                        0xfff45a8d
                                    )
                                )
                                .padding(4.dp),
                        ) {
                            Text(
                                text = if (videoDetail.follow) "已关注" else "+ 关注",
                                color = if (videoDetail.follow) Color.Black else Color.White
                            )
                        }
                    }
                    // 视频信息
                    Row(Modifier.padding(vertical = 4.dp)) {
                        Text(text = "播放: ${videoDetail.watchs} 喜欢: ${videoDetail.likes}")
                    }

                    // 视频介绍
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

                    // 操作按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    videoViewModel.handleLike { action, success ->
                                        if (action) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "点赞大成功！ ヾ(≧▽≦*)o" else "点赞失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "已取消点赞" else "取消点赞失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (videoDetail.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (videoDetail.isLike) Color(0xfff45a8d) else Color.LightGray
                            )
                            Text(text = if (videoDetail.isLike) "已喜欢" else "喜欢")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Subscriptions, null)
                            Text(text = "收藏")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Share, null)
                            Text(text = "分享")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Download, null)
                            Text(text = "下载")
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
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Image(
                            modifier = Modifier.fillMaxHeight(),
                            painter = rememberCoilPainter(it.pic),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight
                        )
                    }

                    Column(
                        modifier = Modifier
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
private fun CommentPage(navController: NavController, videoViewModel: VideoViewModel) {
    val pager = videoViewModel.commentPager.collectAsLazyPagingItems()
    val state = rememberSwipeRefreshState(pager.loadState.refresh == LoadState.Loading)
    if (pager.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { pager.retry() }, contentAlignment = Alignment.Center
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
    } else {
        Column(Modifier.fillMaxSize()) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = state,
                onRefresh = { pager.refresh() }) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (pager.itemCount == 0 && pager.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(text = "暂无评论", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(pager) {
                        CommentItem(navController, it!!)
                    }

                    when (pager.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is LoadState.Error -> {

                        }
                    }
                }
            }
            ReplyBox()
        }
    }
}

@Composable
fun ReplyBox() {
    var content by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier.weight(1f),
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(text = "回复请注意礼仪哦~")
                },
                maxLines = 3,
                label = {
                    Text(text = "评论视频")
                }
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.EmojiEmotions, null)
            }
        }
    }
}