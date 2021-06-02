package com.rerere.iwara4a.ui.page.index

import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import com.rerere.iwara4a.R

@Composable
fun IndexPage(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()){
    Scaffold(
        topBar = {
            TopBar()
        }
    ) {
        Button(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "LOGIN")
        }
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