package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun SubPage(indexViewModel: IndexViewModel) {
    val subscriptionList = indexViewModel.subscriptionPager.collectAsLazyPagingItems()
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = subscriptionList.loadState.refresh == LoadState.Loading)
    Box(modifier = Modifier.fillMaxSize()) {
        if (subscriptionList.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        subscriptionList.retry()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(160.dp).padding(10.dp).clip(CircleShape)){
                        Image(modifier = Modifier.fillMaxSize(), painter = painterResource(R.drawable.anime_1), contentDescription = null)
                    }
                    Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            SwipeRefresh(state = swipeRefreshState, onRefresh = { subscriptionList.refresh() }) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(subscriptionList) {
                        MediaPreview(it!!)
                    }

                    when (subscriptionList.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(Modifier.size(30.dp))
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = "加载中..."
                                    )
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .noRippleClickable { subscriptionList.retry() }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(modifier = Modifier.size(140.dp).padding(10.dp).clip(CircleShape)){
                                            Image(modifier = Modifier.fillMaxSize(), painter = painterResource(R.drawable.anime_2), contentDescription = null)
                                        }
                                        Text(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            text = "加载失败: ${(subscriptionList.loadState.append as LoadState.Error).error.message}"
                                        )
                                        Text(text = "点击重试")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaPreview(mediaPreview: MediaPreview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.height(150.dp), contentAlignment = Alignment.BottomCenter) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberCoilPainter(mediaPreview.previewPic),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(color = Color.White),
                    LocalContentColor provides Color.White
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (plays, likes, type) = createRefs()

                        Row(modifier = Modifier.constrainAs(plays) {
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.play_icon), null)
                            Text(text = mediaPreview.watchs)
                        }

                        Row(modifier = Modifier.constrainAs(likes) {
                            start.linkTo(plays.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.like_icon), null)
                            Text(text = mediaPreview.likes)
                        }

                        Row(modifier = Modifier.constrainAs(type) {
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (mediaPreview.type) {
                                    MediaType.VIDEO -> "视频"
                                    MediaType.IMAGE -> "图片"
                                }
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = mediaPreview.title, fontWeight = FontWeight.Bold, maxLines = 1)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = mediaPreview.author, maxLines = 1)
                }
            }
        }
    }
}