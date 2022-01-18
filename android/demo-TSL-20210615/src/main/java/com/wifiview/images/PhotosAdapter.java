package com.wifiview.images;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.molink.demotsl.R;
import com.wifiview.images.MyImageView.OnMeasureListener;
import com.wifiview.images.NativeImageLoader.NativeImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.TextView;

/**
 * adapter for photos
 * @author Tony
 *
 */
public class PhotosAdapter extends BaseAdapter
{
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public PhotosAdapter(Context context, List<String> list, GridView mGridView)
	{
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ViewHolder viewHolder;
		String path = list.get(position);
		// String folderName = new File(path).getParentFile().getName();
		// String folderPath =
		// Environment.getExternalStorageDirectory().toString() + "/" +
		// folderName + "/";
		// String imageName = path.substring(folderPath.length());
		String imageName = new File(path).getName();
		// Log.e("", imageName);

		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.item_gridview_photos, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.child_tv);

			// 用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener()
			{

				@Override
				public void onMeasureSize(int width, int height)
				{
					mPoint.set(width, height);
				}
			});

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			viewHolder.mTextView.setText("");
		}
		viewHolder.mImageView.setTag(path);

		// 利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack()
		{

			@Override
			public void onImageLoader(Bitmap bitmap, String path)
			{
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if (bitmap != null && mImageView != null)
				{
					mImageView.setImageBitmap(bitmap);
					String imageName = new File(path).getName();
					viewHolder.mTextView.setText(imageName);
				}
			}
		});

		if (bitmap != null)
		{
			viewHolder.mImageView.setImageBitmap(bitmap);
			viewHolder.mTextView.setText(imageName);
		} else
		{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			viewHolder.mTextView.setText("");
		}

		return convertView;
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public List<Integer> getSelectItems()
	{
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue())
			{
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public static class ViewHolder
	{
		public MyImageView mImageView;
		public TextView mTextView;
	}

}
