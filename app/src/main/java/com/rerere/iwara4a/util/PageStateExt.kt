package com.rerere.iwara4a.util

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlin.math.roundToInt

@ExperimentalPagerApi
val PagerState.currentVisualPage: Int
    get() {
        if(currentPageOffset != 0f){
            return (currentPage + currentPageOffset.roundToInt()).coerceIn(0 until pageCount)
        }
        return this.currentPage
    }