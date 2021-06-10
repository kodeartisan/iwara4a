package com.rerere.iwara4a.ui.screen.search

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.public.QueryParamSelector
import com.rerere.iwara4a.util.noRippleClickable

@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            FullScreenTopBar(
                title = {
                    Text(text = "搜索")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        val result = searchViewModel.pager.collectAsLazyPagingItems()

        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            SearchBar(searchViewModel, result)
            Result(navController, searchViewModel, result)
        }
    }
}

@Composable
private fun Result(
    navController: NavController,
    searchViewModel: SearchViewModel,
    list: LazyPagingItems<MediaPreview>
) {
    if (list.loadState.refresh !is LoadState.Error) {
        Crossfade(searchViewModel.query) {
            if (it.isNotBlank()) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(list.loadState.refresh == LoadState.Loading),
                    onRefresh = { list.refresh() }
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        item {
                            QueryParamSelector(
                                queryParam = searchViewModel.searchParam,
                                onChangeSort = {
                                    searchViewModel.searchParam.sortType = it
                                    list.refresh()
                                },
                                onChangeFilters = {
                                    searchViewModel.searchParam.filters = it
                                    list.refresh()
                                }
                            )
                        }

                        items(list) {
                            MediaPreviewCard(navController, it!!)
                        }

                        when (list.loadState.append) {
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
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .noRippleClickable { list.retry() }
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // 也许可以加个搜索推荐？
            }
        }
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { list.refresh() }, contentAlignment = Alignment.Center
        ) {
            Text(text = "加载错误，点击重新尝试搜索", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchRecommend(text: String, onClick: (text: String) -> Unit) {
    Box(modifier = Modifier
        .padding(horizontal = 8.dp)
        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
        .clickable { onClick(text) }
        .padding(4.dp)) {
        Text(text = text)
    }
}

@Composable
private fun SearchBar(searchViewModel: SearchViewModel, list: LazyPagingItems<MediaPreview>) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp, shape = RoundedCornerShape(6.dp)) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchViewModel.query,
                    onValueChange = { searchViewModel.query = it.replace("\n", "") },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "搜索视频和图片")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (searchViewModel.query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.query = "" }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchViewModel.query.isBlank()) {
                                Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                            } else {
                                focusManager.clearFocus()
                                list.refresh()
                            }
                        }
                    )
                )
            }
            IconButton(onClick = {
                if (searchViewModel.query.isBlank()) {
                    Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                } else {
                    focusManager.clearFocus()
                    list.refresh()
                }
            }) {
                Icon(Icons.Default.Search, null)
            }
        }
    }
}