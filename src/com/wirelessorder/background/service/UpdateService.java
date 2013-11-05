package com.wirelessorder.background.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.wirelessorder.R;
import com.wirelessorder.util.FileHelper;
import com.wirelessorder.util.ThreadPoolUtils;

public class UpdateService extends Service {
	// 提示语
	private String updateMsg;
	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private UpdateHandler updateHandler;
	private HandlerThread mHandlerThread;
	private int progress;
	private boolean interceptFlag = false;
	private FileHelper fileHelper;
	private String fileURL;
	private String fileName;
	private AlertDialog noticeDialog;
	private AlertDialog downloadDialog;
	private Runnable mdownApkRunnable;
	private File apkFile;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		fileHelper = new FileHelper();
		mHandlerThread = new HandlerThread("UpdateService");
		mHandlerThread.start();
		updateHandler = new UpdateHandler(mHandlerThread.getLooper(), this);
		mdownApkRunnable = new DownloadRunnable();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
//		if (MyApplication.getInstance().user.readyToUpdate) {
			updateMsg = intent.getStringExtra("msg");
			fileURL = intent.getStringExtra("url");
			fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
			showNoticeDialog();
//		} else {
//			stopSelf();
//		}
		return super.onStartCommand(intent, flags, startId);
	}

	public UpdateService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private static class UpdateHandler extends Handler {
		WeakReference<UpdateService> mService;

		public UpdateHandler(Looper looper, UpdateService service) {
			super(looper);
			mService = new WeakReference<UpdateService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			UpdateService theService = mService.get();

			// TODO Auto-generated method stub
			switch (msg.what) {
			case DOWN_UPDATE:
				theService.mProgress.setProgress(theService.progress);
				break;
			case DOWN_OVER:
				theService.downloadDialog.dismiss();
				theService.installApk();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(UpdateService.this);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setCancelable(false);
		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				MyApplication.getInstance().user.readyToUpdate = false;
				dialog.dismiss();
				stopSelf();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(UpdateService.this);
		builder.setTitle("软件版本更新");

		final LayoutInflater inflater = LayoutInflater.from(UpdateService.this);
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.progressbar,
				null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		downloadDialog.show();
		ThreadPoolUtils.execute(mdownApkRunnable);
	}

	private void installApk() {

		if (!fileHelper.hasSD()) {
			apkFile = getApplicationContext().getFileStreamPath(fileName);
		}
		if (apkFile != null && apkFile.exists()) {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(apkFile),
					"application/vnd.android.package-archive");
			UpdateService.this.startActivity(intent);
			UpdateService.this.stopSelf();
		}

	}

	private class DownloadRunnable implements Runnable {

		@Override
		public void run() {
			try {
				URL url = new URL(fileURL);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				if (fileHelper.hasSD()) {
					apkFile = fileHelper.createStockFile(fileName);
				} else {
					apkFile = null;
				}
				FileOutputStream fos;
				if (!fileHelper.hasSD()) {
					fos = getApplicationContext().openFileOutput(
							fileName,
							Context.MODE_WORLD_READABLE
									| Context.MODE_WORLD_WRITEABLE);

				} else {
					fos = new FileOutputStream(apkFile);
				}

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					updateHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						updateHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	};

}
