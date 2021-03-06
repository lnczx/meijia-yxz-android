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

package com.umeng.comm.ui.adapters;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.comm.ui.adapters.viewholders.FeedItemViewHolder;
import com.umeng.comm.ui.adapters.viewholders.ReceivedCommentViewHolder;

import java.util.Date;

public class LikeMeFeedAdapter1 extends ReceivedCommentAdapter {

    public LikeMeFeedAdapter1(Context context) {
        super(context);
    }

    @Override
    protected void setItemData(int position, ReceivedCommentViewHolder viewHolder, View rootView) {
        super.setItemData(position, viewHolder, rootView);
        viewHolder.mCommentContent.setText(stringToNameTv());
    }

    protected String stringToNameTv() {
        return " " + ResFinder.getString("umeng_comm_liked_you");
    }

}
