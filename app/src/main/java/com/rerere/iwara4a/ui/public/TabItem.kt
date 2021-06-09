package com.rerere.iwara4a.ui.public

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.rerere.iwara4a.util.noRippleClickable
import kotlinx.coroutines.launch

@Composable
fun TabRow(content: @Composable ()->Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun TabItem(pagerState: PagerState, index: Int, text: String) {
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