package com.cn.jmantiLost.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapUtility {
	
	public static JSONObject getLocationInfo(double lat,double lng) {

		//String aa = "http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=false" ;
		
		HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat+","+lng+"&sensor=false");
		//HttpGet httpGet = new HttpGet(aa);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	// After executing this, another method converts that JSONObject into a
	// GeoPoint.

	public static String getGeoAddress(JSONObject jsonObject) {
		
		String address = null ;
		try {
			address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return address ;
	}

}
