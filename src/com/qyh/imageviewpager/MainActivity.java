package com.qyh.imageviewpager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.qyh.constant.Constant;
import com.qyh.utils.ImageAdapter;
import com.qyh.utils.ViewPagerAdapter;
import com.qyh.utils.XMLParser;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	

	private ViewPager viewPager;  
	private List<ImageView> imageViews; 

	private String[] titles;  
	private int[] imageResId; 
	private List<View> dots; 

	private TextView tv_title;
	private int currentItem = 0; 
    //DOM XML
	private  final String KEY_IMAGE = "image"; // parent node
	private  final String KEY_ID = "id";
	private  final String KEY_URL = "url";
	ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;


	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
			switch (msg.what - 10) {
			case 0:
				System.out.println("msg.what:" + msg.what);
				if (msg.obj != null) {
					imageViews.get(0).setImageBitmap((Bitmap) msg.obj);
				} else {
					imageViews.get(0).setImageResource(R.drawable.failure);
				}
				viewPager.getAdapter().notifyDataSetChanged();
				break;
			case 1:
				System.out.println("msg.what:" + msg.what);
				if (msg.obj != null) {
					imageViews.get(1).setImageBitmap((Bitmap) msg.obj);
				} else {
					imageViews.get(1).setImageResource(R.drawable.failure);
				}
				viewPager.getAdapter().notifyDataSetChanged();
				break;
			case 2:
				if (msg.obj != null) {
					imageViews.get(2).setImageBitmap((Bitmap) msg.obj);
				} else {
					imageViews.get(2).setImageResource(R.drawable.failure);
				}
				break;
			case 3:
				System.out.println("msg.what:" + msg.what);
				if (msg.obj != null) {
					imageViews.get(3).setImageBitmap((Bitmap) msg.obj);
				} else {
					imageViews.get(3).setImageResource(R.drawable.failure);
				}
				viewPager.getAdapter().notifyDataSetChanged();
				break;
			case 4:
				System.out.println("msg.what:" + msg.what);
				if (msg.obj != null) {
					imageViews.get(4).setImageBitmap((Bitmap) msg.obj);
				} else {
					imageViews.get(4).setImageResource(R.drawable.failure);
				}
				viewPager.getAdapter().notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// **************************************************************************
		try {
		
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(Constant.URL_IMAGE); // getting XML from URL
			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nl = doc.getElementsByTagName(KEY_IMAGE);
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				Element e = (Element) nl.item(i);
				// adding each child node to HashMap key => value
				map.put(KEY_ID, parser.getValue(e, KEY_ID));
				map.put(KEY_URL, parser.getValue(e, KEY_URL));
				// adding HashList to ArrayList
				imageList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "XML parsing failure��", 0).show();
		} 
		// *********************************************************************************

		imageResId = new int[] { 1, 2, 3, 4, 5 };
		titles = new String[imageResId.length];
		titles[0] = "";
		titles[1] = "";
		titles[2] = "";
		titles[3] = "";
		titles[4] = "";

		imageViews = new ArrayList<ImageView>();

		for (int i = 0; i < imageList.size(); i++) {
			ImageAdapter.readBitmap(imageList.get(i).get(KEY_URL),
					getFileName(imageList.get(i).get(KEY_URL)), handler, i);
			ImageView imageViewDefault = new ImageView(this);
			imageViewDefault.setImageResource(R.drawable.loading);
			imageViewDefault.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageViewDefault);

		}

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		dots.add(findViewById(R.id.v_dot3));
		dots.add(findViewById(R.id.v_dot4));

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(titles[0]);

		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager.setAdapter(new ViewPagerAdapter(imageViews, imageResId));
		
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}

	// *******************************************************************************************
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)

			System.exit(0);

		return true;
	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	
	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 4,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // ͨ��Handler�л�ͼƬ
			}
		}

	}

	
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private String getFileName(String path) {
		int start = path.lastIndexOf("/") + 1;
		return path.substring(start);
	}
}
