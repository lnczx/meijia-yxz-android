/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.comm.ui.fragments;

import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.ui.adapters.FeedAdapter;
import com.umeng.comm.ui.adapters.LikeMeFeedAdapter;
import com.umeng.comm.ui.adapters.LikeMeFeedAdapter1;
import com.umeng.comm.ui.adapters.ReceivedCommentAdapter;
import com.umeng.comm.ui.adapters.viewholders.ReceivedCommentViewHolder;
import com.umeng.comm.ui.imagepicker.adapters.CommonAdapter;
import com.umeng.comm.ui.imagepicker.presenter.impl.CommentPostedPresenter;
import com.umeng.comm.ui.imagepicker.presenter.impl.FeedListPresenter;
import com.umeng.comm.ui.imagepicker.presenter.impl.LikeMePresenter;

/**
 * 我的消息页面的Like我的Fragment
 */
public class LikedMeFragment1 extends CommentReceivedFragment1 {

    @Override
    protected FeedListPresenter createPresenters() {
        return new LikeMePresenter(this);
    }

    protected CommonAdapter<FeedItem, ReceivedCommentViewHolder> createListViewAdapter() {
        return new LikeMeFeedAdapter1(getActivity());
    }
}
