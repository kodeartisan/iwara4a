package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.rerere.iwara4a.model.index.MediaQueryParam
import com.rerere.iwara4a.model.index.SortType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.title

@Composable
fun QueryParamSelector(
    queryParam: MediaQueryParam,
    onChangeSort: (sort: SortType) -> Unit,
    onChangeFilters: (filters: List<String>) -> Unit
) {
    val sortDialog = remember {
        MaterialDialog()
    }
    sortDialog.build {
        title("选择排序条件")
        listItemsSingleChoice(
            list = SortType.values().map { it.name },
            onChoiceChange = {
                onChangeSort(SortType.values()[it])
                sortDialog.hide()
            },
            initialSelection = queryParam.sortType.ordinal,
        )
        buttons {
            positiveButton("确定"){
                sortDialog.hide()
            }
        }
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "排序:  ")
            Box(
                modifier = Modifier
                    .clickable { sortDialog.show() }
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(2.dp))
                    .padding(4.dp)
            ) {
                Text(text = queryParam.sortType.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    QueryParamSelector(
        queryParam = MediaQueryParam(SortType.DATE, listOf("created:2021")),
        onChangeSort = { /*TODO*/ }) {

    }
}