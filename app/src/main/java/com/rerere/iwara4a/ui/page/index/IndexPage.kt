package com.rerere.iwara4a.ui.page.index

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import com.rerere.iwara4a.R

@Composable
fun IndexPage(navController: NavController){
    Scaffold(
        topBar = {
            TopBar()
        }
    ) {
        Text(text = "??")
    }
}

@Composable
private fun TopBar(){
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(text = stringResource(R.string.app_name))
        }
    )
}