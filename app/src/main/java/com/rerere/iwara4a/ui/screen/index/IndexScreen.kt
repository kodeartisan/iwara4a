package com.rerere.iwara4a.ui.screen.index

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.screen.index.page.IndexPage
import com.rerere.iwara4a.util.currentVisualPage
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = 3, initialPage = 1)
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar() },
        bottomBar = {
            BottomBar(pagerState = pagerState)
        },
        drawerContent = {
            IndexDrawer(navController)
        }
    ) {
        HorizontalPager(modifier = Modifier
            .fillMaxSize()
            .padding(it), state = pagerState) {
            when (it) {
                0 -> {
                    IndexPage(indexViewModel)
                }
                1 -> {
                    IndexPage(indexViewModel)
                }
                2 -> {
                    IndexPage(indexViewModel)
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(text = stringResource(R.string.app_name))
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
                Icon(painter = painterResource(R.drawable.index_icon), contentDescription = null)
            },
            label = {
                Text(text = "主页")
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