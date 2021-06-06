package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType

@Composable
fun MediaPreviewCard(navController: NavController, mediaPreview: MediaPreview) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (mediaPreview.type == MediaType.VIDEO) {
                    navController.navigate("video/${mediaPreview.mediaId}")
                } else if(mediaPreview.type == MediaType.IMAGE){
                    navController.navigate("image/${mediaPreview.mediaId}")
                }
            }
        ) {
            Box(modifier = Modifier.height(150.dp), contentAlignment = Alignment.BottomCenter) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberCoilPainter(mediaPreview.previewPic),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(color = Color.White),
                    LocalContentColor provides Color.White
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (plays, likes, type) = createRefs()

                        Row(modifier = Modifier.constrainAs(plays) {
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.play_icon), null)
                            Text(text = mediaPreview.watchs)
                        }

                        Row(modifier = Modifier.constrainAs(likes) {
                            start.linkTo(plays.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.like_icon), null)
                            Text(text = mediaPreview.likes)
                        }

                        Row(modifier = Modifier.constrainAs(type) {
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (mediaPreview.type) {
                                    MediaType.VIDEO -> "视频"
                                    MediaType.IMAGE -> "图片"
                                }
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = mediaPreview.title, fontWeight = FontWeight.Bold, maxLines = 1)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = mediaPreview.author, maxLines = 1)
                }
            }
        }
    }
}