package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.comment.CommentPosterType
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable

@Composable
fun CommentItem(navController: NavController, comment: Comment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.5.dp, Color.Gray), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                navController.navigate("user/${comment.authorId}")
                            },
                        painter = rememberCoilPainter(comment.authorPic),
                        contentDescription = null
                    )
                }
                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.padding(end = 8.dp).noRippleClickable {
                                navController.navigate("user/${comment.authorId}")
                            },
                            text = comment.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        when (comment.posterType) {
                            CommentPosterType.OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            CommentPosterType.SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(comment.date)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = comment.content)
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                comment.reply.forEach {
                    CommentItem(navController, it)
                }
            }
        }
    }
}