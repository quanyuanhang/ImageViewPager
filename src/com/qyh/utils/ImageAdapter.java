package com.qyh.utils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.qyh.constant.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class ImageAdapter {
	static int flag = 1;

	public static void readBitmap(final String url, final String fileName,
			final Handler handler, final int num) {

		String savePath = Constant.savePath;
		final String srcPath = savePath + fileName;
		final FileUtils fileUtils = new FileUtils();
		new Thread() {
			@Override
			public void run() {
				InputStream inputStream = null;
				HttpURLConnection httpURLConnection = null;
				try {
					URL url1 = new URL(url);
					if (url1 != null) {
						httpURLConnection = (HttpURLConnection) url1
								.openConnection();
						// 设置网络超时时间
						httpURLConnection.setConnectTimeout(3000);
						httpURLConnection.setDoInput(true);
						httpURLConnection.setRequestMethod("GET");
						int responseCode = httpURLConnection.getResponseCode();
						if (responseCode == 200) {
							inputStream = httpURLConnection.getInputStream();
						}
					}
					File file = fileUtils.write2SDFromInput("UCDownloads/",
							fileName, inputStream);
				
				} catch (Exception e) {
					// TODO: handle exception
					Message message = handler.obtainMessage(num + 10, null);
					handler.sendMessage(message);
				} finally {
					File file = new File(srcPath);
					Bitmap bitmap = null;
					if (file.exists()) {
						bitmap = BitmapFactory.decodeFile(srcPath);
					} else {
						Message message = handler.obtainMessage(num + 10, null);
						handler.sendMessage(message);
					}
					file.delete();
					Message message = handler.obtainMessage(num + 10, bitmap);
					handler.sendMessage(message);
				}
			}
		}.start();

	}

}
