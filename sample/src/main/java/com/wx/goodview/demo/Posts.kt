/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.goodview.demo

import android.view.View
import android.widget.TextView
import com.wx.goodview.GoodView

/**
 * 示例帖子：首页预览和信息流页共用
 */
class Post(
    val author: String,
    val time: String,
    val content: String,
    var likes: Int,
    var stars: Int,
    var shares: Int,
    val tag: String? = null,
    var liked: Boolean = false,
    var starred: Boolean = false,
)

/**
 * 把帖子绑定到 item_post 布局：点赞与收藏可以取消，转发只累加；每次操作都在按钮上方飘字
 *
 * @author venshine
 */
object PostBinder {

    fun bind(root: View, post: Post, goodView: GoodView) {
        val context = root.context
        root.findViewById<TextView>(R.id.post_avatar).text = post.author.take(1).uppercase()
        root.findViewById<TextView>(R.id.post_author).text = post.author
        root.findViewById<TextView>(R.id.post_time).text = post.time
        root.findViewById<TextView>(R.id.post_content).text = post.content
        root.findViewById<TextView>(R.id.post_tag).apply {
            text = post.tag
            visibility = if (post.tag == null) View.GONE else View.VISIBLE
        }

        val like = root.findViewById<TextView>(R.id.action_like)
        val star = root.findViewById<TextView>(R.id.action_star)
        val share = root.findViewById<TextView>(R.id.action_share)
        fun render() {
            like.text = post.likes.toString()
            like.setTextColor(context.colorOf(if (post.liked) R.color.accent_ink else R.color.text_sub))
            like.setStartIcon(icon(like, if (post.liked) R.drawable.ic_heart_fill else R.drawable.ic_heart,
                if (post.liked) R.color.accent_ink else R.color.text_sub))
            star.text = post.stars.toString()
            star.setTextColor(context.colorOf(if (post.starred) R.color.gold else R.color.text_sub))
            star.setStartIcon(icon(star, if (post.starred) R.drawable.ic_star_fill else R.drawable.ic_star,
                if (post.starred) R.color.gold else R.color.text_sub))
            share.text = post.shares.toString()
            share.setStartIcon(icon(share, R.drawable.ic_share, R.color.text_sub))
            like.contentDescription = context.getString(R.string.post_like) + " " + post.likes
            star.contentDescription = context.getString(R.string.post_star) + " " + post.stars
            share.contentDescription = context.getString(R.string.post_share) + " " + post.shares
        }

        like.setOnClickListener {
            post.liked = !post.liked
            post.likes += if (post.liked) 1 else -1
            render()
            Pop.text(goodView, it, if (post.liked) "+1" else "-1",
                context.colorOf(if (post.liked) R.color.accent_ink else R.color.text_dim))
            if (post.liked) it.pulse(1.12f)
        }
        star.setOnClickListener {
            post.starred = !post.starred
            post.stars += if (post.starred) 1 else -1
            render()
            Pop.text(goodView, it, context.getString(if (post.starred) R.string.pop_starred else R.string.pop_unstarred),
                context.colorOf(if (post.starred) R.color.gold else R.color.text_dim), 13)
            if (post.starred) it.pulse(1.12f)
        }
        share.setOnClickListener {
            post.shares++
            render()
            Pop.text(goodView, it, context.getString(R.string.pop_shared), context.colorOf(R.color.blue), 13)
        }
        render()
    }

    private fun icon(view: TextView, id: Int, color: Int) = view.context.tintedIcon(id, view.context.colorOf(color), 20f)
}
