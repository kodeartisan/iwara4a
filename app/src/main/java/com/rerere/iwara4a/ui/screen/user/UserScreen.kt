package com.rerere.iwara4a.ui.screen.user

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.user.UserData
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.TabItem
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun UserScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        userViewModel.load(userId)
    }

    Scaffold(
        topBar = {
            TopBar(navController, userViewModel)
        }
    ) {
        if (userViewModel.isLoaded()) {
            UserInfo(navController, userViewModel.userData)
        } else if(userViewModel.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                }
            }
        } else if(userViewModel.error){
            Box(modifier = Modifier.fillMaxSize().noRippleClickable { userViewModel.load(userId) }, contentAlignment = Alignment.Center) {
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
                    Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun UserInfo(navController: NavController, userData: UserData) {
    Column {
        // 用户信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberCoilPainter(userData.pic),
                            contentDescription = null
                        )
                    }

                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = userData.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PINK
                        )
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(
                                text = "注册日期: ${userData.joinDate}"
                            )
                            Text(
                                text = "最后在线: ${userData.lastSeen}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = userData.about, maxLines = 5)
                }
            }
        }
        // 评论/ 视频 / 图片
        val pagerState = rememberPagerState(pageCount = 3)
        com.rerere.iwara4a.ui.public.TabRow {
            TabItem(pagerState = pagerState, index = 0, text = "评论")
            TabItem(pagerState = pagerState, index = 1, text = "发布的视频")
            TabItem(pagerState = pagerState, index = 2, text = "发布的图片")
        }
        HorizontalPager(modifier = Modifier.fillMaxWidth().weight(1f), state = pagerState) {
            when(it){
                0 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
                2 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, userViewModel: UserViewModel) {
    FullScreenTopBar(
        title = {
            Text(text = if(userViewModel.isLoaded()) userViewModel.userData.username else "用户信息")
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        }
    )
}