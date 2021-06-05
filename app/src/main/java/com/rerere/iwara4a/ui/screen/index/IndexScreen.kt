package com.rerere.iwara4a.ui.screen.index

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.screen.index.page.ImageListPage
import com.rerere.iwara4a.ui.screen.index.page.SubPage
import com.rerere.iwara4a.ui.screen.index.page.VideoListPage
import com.rerere.iwara4a.util.currentVisualPage
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = 3, initialPage = 1)
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scaffoldState, indexViewModel) },
        bottomBar = {
            BottomBar(pagerState = pagerState)
        },
        drawerContent = {
            IndexDrawer(navController, indexViewModel)
        }
    ) {
        HorizontalPager(
            modifier = Modifier
            .fillMaxSize()
            .padding(it),
            state = pagerState
        ) {
            when (it) {
                0 -> {
                    VideoListPage(indexViewModel)
                }
                1 -> {
                    SubPage(indexViewModel)
                }
                2 -> {
                    ImageListPage(indexViewModel)
                }
            }
        }
    }
}

@Composable
private fun TopBar(scaffoldState: ScaffoldState, indexViewModel: IndexViewModel) {
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Box(modifier = Modifier.size(30.dp).clip(CircleShape)){
                    Image(modifier = Modifier.fillMaxSize(), painter = rememberCoilPainter(indexViewModel.self.profilePic), contentDescription = null)
                }
            }
        }
    )
}

@ExperimentalPagerApi
@Composable
private fun BottomBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    BottomNavigation(modifier = Modifier.navigationBarsPadding()) {
        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 0,
            onClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(0) }
            },
            icon = {
                Icon(painter = painterResource(R.drawable.video_icon), contentDescription = null)
            },
            label = {
                Text(text = "视频")
            }
        )
        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 1,
            onClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(1) }
            },
            icon = {
                Icon(painter = painterResource(R.drawable.subscriptions), contentDescription = null)
            },
            label = {
                Text(text = "关注")
            }
        )
        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 2,
            onClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(2) }
            },
            icon = {
                Icon(painter = painterResource(R.drawable.image_icon), contentDescription = null)
            },
            label = {
                Text(text = "图片")
            }
        )
    }
}