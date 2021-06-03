package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.screen.index.IndexViewModel

@Composable
fun IndexPage(indexViewModel: IndexViewModel){
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(Modifier.fillMaxSize()) {
            items(100){
                Card(modifier = Modifier.fillMaxWidth().height(100.dp).padding(12.dp), elevation = 4.dp) {
                    
                }
            }
        }
    }
}