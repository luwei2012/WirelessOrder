package com.wirelessorder.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class Geocoding {
	public static String[] getLocationInfo(Location location) {
		String stringBody = null;
		String[] locInfo = new String[2];
		try {

			HttpPost httppost = new HttpPost(
					"http://where.yahooapis.com/geocode?q="
							+ location.getLatitude() + ","
							+ location.getLongitude()
							+ "&gflags=R&locale=zh_CN&appid=x7ogkT32&flags=J");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			stringBody = StringUtils.convertStreamToString(stream);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject = new JSONObject(stringBody);
			JSONArray result = jsonObject.getJSONObject("ResultSet")
					.getJSONArray("Results");
			JSONObject addressComponents = (JSONObject) result.get(0);

			locInfo[1] = addressComponents.getString("county");

			locInfo[0] = addressComponents.getString("state");

		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return locInfo;
	}

}
