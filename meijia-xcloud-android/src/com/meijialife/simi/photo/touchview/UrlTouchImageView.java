package com.meijialife.simi.photo.touchview;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.meijialife.simi.photo.touchview.InputStreamWrapper.InputStreamProgressListener;

/**
 * @description：自定义有链接可触摸的ImageView
 * @author： kerryg
 * @date:2015年11月12日 
 */
public class UrlTouchImageView extends RelativeLayout {
    protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;

    protected Context mContext;

    public UrlTouchImageView(Context ctx)
    {
        super(ctx);
        mContext = ctx;
        init();

    }
    public UrlTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }
    public TouchImageView getImageView() { return mImageView; }

    @SuppressWarnings("deprecation")
    protected void init() {
        mImageView = new TouchImageView(mContext);
        LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
        mImageView.setLayoutParams(params);
        this.addView(mImageView);
        mImageView.setVisibility(GONE);

        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);
        params = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins(30, 0, 30, 0);
        mProgressBar.setLayoutParams(params);
        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax(100);
        this.addView(mProgressBar);
    }

    public void setUrl(String imageUrl)
    {
        new ImageLoadTask().execute(imageUrl);
    }
    
    public void setScaleType(ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
    }
    
    //No caching load
    public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int totalLen = conn.getContentLength();
                InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
                bis.setProgressListener(new InputStreamProgressListener()
				{					
					@Override
					public void onProgress(float progressValue, long bytesLoaded,
							long bytesTotal)
					{
						publishProgress((int)(progressValue * 100));
					}
				});
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }
        
        @Override
        protected void onPostExecute(Bitmap bitmap) {
        	if (bitmap == null) 
        	{
        		mImageView.setScaleType(ScaleType.CENTER);
//        		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
        		mImageView.setImageBitmap(bitmap);
        	}
        	else 
        	{
        		mImageView.setScaleType(ScaleType.MATRIX);
	            mImageView.setImageBitmap(bitmap);
        	}
            mImageView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			mProgressBar.setProgress(values[0]);
		}
    }
}
