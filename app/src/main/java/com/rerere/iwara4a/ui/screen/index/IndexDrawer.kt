package com.rerere.iwara4a.ui.screen.index

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun IndexDrawer(navController: NavController, indexViewModel: IndexViewModel) {
    fun isLoading() = indexViewModel.loadingSelf

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp),
            elevation = 8.dp,
            color = MaterialTheme.colors.primaryVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Profile Pic
                Box(modifier = Modifier.padding(horizontal = 32.dp)){
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("login")
                            }
                    ) {
                        val painter = rememberCoilPainter(indexViewModel.self.profilePic)
                        Image(modifier = Modifier.fillMaxSize(), painter = painter, contentDescription = null)
                    }
                }

                // Profile Info
                Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) {
                    // UserName
                    Text(
                        text = if(isLoading()) "加载中" else indexViewModel.self.nickname,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    // Email
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = indexViewModel.email)
                    }
                }
            }
        }

        // Navigation List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Surface(Modifier.fillMaxSize()) {

            }
        }
    }
}