package com.wifiview.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.molink.demotsl.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wifiview.config.VeDate;
import com.wifiview.images.ViewPagerFixed;
import com.wifiview.photoview.PhotoView;
import com.wifiview.photoview.PhotoViewAttacher.OnPhotoTapListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.support.v4.view.PagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
//import android.support.v4.view.ViewPager;
import androidx.viewpager.widget.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class ImagePagerActivity extends Activity
{
	private ViewPagerFixed mViewPager;
	private static String TAG = "ImagePagerActivity";

	private TextView txtcountTextView;
	private ImageView iv_ImagePager_Actionbar_Back;
	private ImageView iv_ImagePager_Actionbar_Photo;
	private ImageView iv_ImagePager_Actionbar_More;
	private ImageView iv_Actionbar_Bottom_Info;
	private ImageView iv_Actionbar_Bottom_Delete;
	private LinearLayout layout_Actionbar_Bottom;

	SamplePagerAdapter mViewPagerAdapter;

	private Timer mTimer;
	private ActionbarTimerTask mTimerTask;

	Bitmap defaultbmp;
	DisplayImageOptions options;// jar包声明配置
	protected ImageLoader imageLoader = ImageLoader.getInstance();// jar包声明图片类

	private int currItem = 0;
	private int screenWidth;
	private int screenHeight;
	private PopupWindow pWindow;
	private SimpleAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagepager);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		defaultbmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		imageLoader.init(ImageLoaderConfiguration.createDefault(ImagePagerActivity.this));

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(300)).build();

		mTimer = new Timer(true);

		widgetInit();
	}

	private void widgetInit()
	{
		Log.e(TAG, "images size:" + PlaybackActivity.photoList.size());

		mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
		mViewPagerAdapter = new SamplePagerAdapter();
		mViewPager.setAdapter(mViewPagerAdapter);

		int item = getIntent().getIntExtra("position", 0);
		mViewPager.setCurrentItem(item);
		currItem = item;

		layout_Actionbar_Bottom = (LinearLayout) findViewById(R.id.layout_Actionbar_Bottom);

		iv_ImagePager_Actionbar_Back = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_Back);
		iv_ImagePager_Actionbar_Back.setOnClickListener(clickListener);
		iv_ImagePager_Actionbar_Photo = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_Photo);
		iv_ImagePager_Actionbar_Photo.setOnClickListener(clickListener);
		iv_ImagePager_Actionbar_More = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_More);
		iv_ImagePager_Actionbar_More.setOnClickListener(clickListener);
		iv_Actionbar_Bottom_Info = (ImageView) findViewById(R.id.iv_Actionbar_Bottom_Info);
		iv_Actionbar_Bottom_Info.setOnClickListener(clickListener);
		iv_Actionbar_Bottom_Delete = (ImageView) findViewById(R.id.iv_Actionbar_Bottom_Delete);
		iv_Actionbar_Bottom_Delete.setOnClickListener(clickListener);

		txtcountTextView = (TextView) findViewById(R.id.txt_imagecount);
		txtcountTextView.setText((item + 1) + "/" + String.valueOf(PlaybackActivity.photoList.size()));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				txtcountTextView.setText(String.valueOf(position + 1) + "/" + String.valueOf(PlaybackActivity.photoList.size()));
				currItem = position;
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}
		});

	}

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.iv_ImagePager_Actionbar_Back:
			case R.id.iv_ImagePager_Actionbar_Photo:
				onBackPressed();
				break;
			case R.id.iv_ImagePager_Actionbar_More:
				layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
				startActionbarTimer();
				break;
			case R.id.iv_Actionbar_Bottom_Info:
				if (PlaybackActivity.photoList.size() > 0)
				{
					initPopupPhotoinfo();
					popupWindowPhotoinfo();
				} else
				{
					onBackPressed();
				}
				break;
			case R.id.iv_Actionbar_Bottom_Delete:
				if (PlaybackActivity.photoList.size() > 0)
				{
					deleteFileOper(currItem);
				} else
				{
					onBackPressed();
				}
				break;
			default:
				break;
			}
		}
	};

	class SamplePagerAdapter extends PagerAdapter
	{
		private LayoutInflater inflater;

		SamplePagerAdapter()
		{
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount()
		{
			return PlaybackActivity.photoList.size();
		}

		/***
		 * 直接使用notifyDataSetChanged是无法更新，需要同时重写getItemPosition返回常量 POSITION_NONE
		 * (此常量为viewpager带的)。
		 */
		@Override
		public int getItemPosition(Object object)
		{
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position)
		{
			View imageLayout = inflater.inflate(R.layout.item_imagepager, container, false);
			final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.image);

			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			final String imgUrl = Uri.fromFile(new File(PlaybackActivity.photoList.get(position))).toString();
			imageLoader.displayImage(imgUrl, photoView, options, new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
					String message = null;
					switch (failReason.getType())
					{
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
					}
					Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
				{
					spinner.setVisibility(View.GONE);
				}
			});
			photoView.setOnPhotoTapListener(new OnPhotoTapListener()
			{

				@Override
				public void onPhotoTap(View view, float x, float y)
				{
					// TODO Auto-generated method stub
					layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
					startActionbarTimer();
				}
			});
			((ViewPager) container).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == object;
		}

	}

	private void initPopupPhotoinfo()
	{
		LayoutInflater mInflater = (LayoutInflater) ImagePagerActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View mView = mInflater.inflate(R.layout.popup_photo_info, null);
		pWindow = new PopupWindow(mView, screenWidth * 4 / 5, screenHeight * 3 / 5);
		ListView mListView = (ListView) mView.findViewById(R.id.listview_Popup_Photoinfo);
		mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item_photoinfo, new String[] { "title", "info" }, new int[] { R.id.title, R.id.info });
		mListView.setAdapter(mAdapter);
	}

	private void popupWindowPhotoinfo()
	{
		pWindow.setFocusable(true);
		pWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
		pWindow.setOutsideTouchable(true);
		pWindow.showAtLocation(mViewPager, Gravity.CENTER, 0, 0);
		pWindow.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				// TODO Auto-generated method stub

			}
		});
	}

	private List<Map<String, Object>> getData()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> photo = new HashMap<String, Object>();
		int index = PlaybackActivity.photoList.get(currItem).lastIndexOf("/");
		String photoName = PlaybackActivity.photoList.get(currItem).substring(index + 1);
		photo.put("title", this.getString(R.string.str_imagepager_filename));
		photo.put("info", photoName);
		list.add(photo);

		photo = new HashMap<String, Object>();
		String photoRoute = PlaybackActivity.photoList.get(currItem).substring(0, index);
		photo.put("title", this.getString(R.string.str_imagepager_filepath));
		photo.put("info", photoRoute);
		list.add(photo);

		photo = new HashMap<String, Object>();
		long timeLastChange = new File(PlaybackActivity.photoList.get(currItem)).lastModified();
		Log.e(TAG, "time:" + timeLastChange);
		Date date = new Date(timeLastChange);
		String timeDate = VeDate.dateToStrLong(date);
		photo.put("title", this.getString(R.string.str_imagepager_time));
		photo.put("info", timeDate);
		list.add(photo);

		photo = new HashMap<String, Object>();
		File photoFile = new File(PlaybackActivity.photoList.get(currItem));
		float kbSize = (float) photoFile.length() / 1024; // 此步骤小数有6位
		if (kbSize < 800)
		{
			DecimalFormat mFormat = new DecimalFormat(".00"); // 此步骤为保留2位小数
			String kbSizeStr = mFormat.format(kbSize);
			photo.put("title", this.getString(R.string.str_imagepager_filesize));
			photo.put("info", kbSizeStr + " KB");
		} else
		{
			float mbSize = (float) kbSize / 1024;
			DecimalFormat mFormat = new DecimalFormat(".00"); // 此步骤为保留2位小数
			String mbSizeStr = mFormat.format(mbSize);
			photo.put("title", this.getString(R.string.str_imagepager_filesize));
			photo.put("info", mbSizeStr + " MB");
		}
		list.add(photo);

		photo = new HashMap<String, Object>();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(PlaybackActivity.photoList.get(currItem));
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		photo.put("title", this.getString(R.string.str_imagepager_resolution));
		photo.put("info", bmpWidth + " × " + bmpHeight);
		list.add(photo);

		photo = new HashMap<String, Object>();
		int typeIndex = PlaybackActivity.photoList.get(currItem).lastIndexOf(".");
		String typeString = PlaybackActivity.photoList.get(currItem).substring(typeIndex);
		photo.put("title", this.getString(R.string.str_imagepager_filetype));
		photo.put("info", "MJEPG/" + typeString);
		list.add(photo);

		return list;
	}

	private void deleteFileOper(final int position)
	{
		File file = new File(PlaybackActivity.photoList.get(position));
		if (file.exists())
		{
			file.delete();
			PlaybackActivity.photoList.remove(position);
			mViewPagerAdapter.notifyDataSetChanged();
			Intent intent = new Intent(PlaybackActivity.ACTION_DELETE_PHOTO);
			intent.putExtra("deletePosition", 0);
			sendBroadcast(intent);
			txtcountTextView.setText((currItem + 1) + "/" + String.valueOf(PlaybackActivity.photoList.size()));
		}
	}

	private Handler handler = new Handler(new Handler.Callback()
	{

		@Override
		public boolean handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 0:
				layout_Actionbar_Bottom.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}
			return true;
		}
	});

	// start the timer task
	private void startActionbarTimer()
	{
		if (mTimer != null)
		{
			if (mTimerTask != null)
			{
				mTimerTask.cancel();
			}
		}

		mTimerTask = new ActionbarTimerTask();
		mTimer.schedule(mTimerTask, 2500);
	};

	// what the timer task do
	private class ActionbarTimerTask extends TimerTask
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
			startActionbarTimer();
		}
		return super.onKeyDown(keyCode, event);
	}

}
