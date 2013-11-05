package com.wirelessorder.util.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.wirelessorder.util.FileHelper;

public class DownloaderTask extends AsyncTask<String, Void, String> {
	private FileHelper fileHelper;
	private ProgressDialog mDialog;
	private Context mContext;
	private String fileName;
	private String cookieString;
	private String originalURL;

	public DownloaderTask(Context vContext, String cookie, String originalUrl) {
		fileHelper = new FileHelper();
		this.mContext = vContext;
		cookieString = cookie;
		originalURL = originalUrl;
	}

	public DownloaderTask(Context vContext, String cookie) {
		this(vContext, cookie, null);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url = params[0];
		fileName = url.substring(url.lastIndexOf("/") + 1);
		fileName = URLDecoder.decode(fileName);
		File file;
		try {
			file = fileHelper.createFile(fileName);
			if (file.exists()) {
				// return fileName;
				// 仅在测试时使用
				file = fileHelper.createStockFile(fileName);
			} else {
				file = fileHelper.createStockFile(fileName);
			}
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.setHeader("Cookie", cookieString);
			if (originalURL != null) {
				get.setHeader("Referer", originalURL);
			}
			HttpResponse response = client.execute(get);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				InputStream input = entity.getContent();
				writeToSDCard(file, input);
				input.close();
				return fileName;
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		closeProgressDialog();
		if (result == null) {
			if (!fileHelper.hasSD()) {
				result = "需要SD卡！";
			} else {
				result = "连接错误！请稍后再试！";
			}
			Toast t = Toast.makeText(mContext, result, Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			return;
		}

		Toast t = Toast.makeText(mContext,
				"已保存到" + fileHelper.getStockFilePATH(fileName) + "。",
				Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		File file;
		try {
			file = fileHelper.createStockFile(result);
			Intent intent = getFileIntent(file);
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast toast = Toast.makeText(mContext, "无法自动识别此文件，请手动打开。",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		showProgressDialog();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	private void showProgressDialog() {
		if (mDialog == null) {
			mDialog = new ProgressDialog(mContext);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mDialog.setMessage("正在加载 ，请等待...");
			mDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					mDialog = null;
				}
			});
			mDialog.show();

		}
	}

	private void closeProgressDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	public Intent getFileIntent(File file) {
		Uri uri = Uri.fromFile(file);
		String type = getMIMEType(file);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, type);
		return intent;
	}

	public void writeToSDCard(File file, InputStream input) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			byte[] b = new byte[2048];
			int j = 0;
			while ((j = input.read(b)) != -1) {
				fos.write(b, 0, j);
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase(Locale.getDefault());

		/* 依扩展名的类型决定MimeType */
		if (end.equals("pdf")) {
			type = "application/pdf";//
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else if (end.equals("pptx") || end.equals("ppt")) {
			type = "application/vnd.ms-powerpoint";
		} else if (end.equals("docx") || end.equals("doc")) {
			type = "application/vnd.ms-word";
		} else if (end.equals("xlsx") || end.equals("xls")) {
			type = "application/vnd.ms-excel";
		} else {
			// /*如果无法直接打开，就跳出软件列表给用户选择 */
			type = "*/*";
		}
		return type;
	}

	public static Boolean isURLAvailableFile(String url) {
		/* 取得扩展名 */
		String type = url.substring(url.lastIndexOf("/") + 1);
		if (type.contains(".")) {
			type = type.substring(type.lastIndexOf('.') + 1).toUpperCase(
					Locale.getDefault());
			/* 依扩展名的类型决定MimeType */
			if (type.equals("JPEG") || type.equals("PNG") || type.equals("GIF")
					|| type.equals("TIFF") || type.equals("ogg")
					|| type.equals("BMP") || type.equals("TXT")
					|| type.equals("CSS") || type.equals("PHP")
					|| type.equals("JS") || type.equals("C")
					|| type.equals("CPP") || type.equals("H")
					|| type.equals("HPP") || type.equals("DOC")
					|| type.equals("DOCX") || type.equals("XLS")
					|| type.equals("XLSX") || type.equals("PPT")
					|| type.equals("PPTX") || type.equals("PDF")
					|| type.equals("PAGES") || type.equals("AI")
					|| type.equals("PSD") || type.equals("TIFF")
					|| type.equals("DXF") || type.equals("SVG")
					|| type.equals("EPS") || type.equals("PS")
					|| type.equals("TTF") || type.equals("XPS")
					|| type.equals("ZIP") || type.equals("RAR")
					|| type.equals("APK"))
				return true;
			else
				return false;
		} else
			return false;
	}

}
