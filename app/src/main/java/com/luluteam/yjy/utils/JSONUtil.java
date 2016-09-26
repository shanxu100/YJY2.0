package com.luluteam.yjy.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.luluteam.yjy.model.Courseware;
import com.luluteam.yjy.model.GuanCoursewareList;
import com.luluteam.yjy.model.OperationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guan on 2016/6/9.
 */

//测试时临时使用的类，今后可以用Gson来替代

public class JSONUtil {

	private static Gson gson=new Gson();
	public static String TAG = "JSONUtil";

	public static String forUploadFileResult(JSONObject jsonObject) {
		try {
			String result = jsonObject.getString("result");
			String info = jsonObject.getString("info");
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Courseware forCourseware(JSONObject jsonObject)
	{
		try {
			Courseware tmpC = new Courseware();
			JSONObject tmpJO = jsonObject.getJSONObject("document");
			tmpC.setDocId(tmpJO.getString("docId"));
			tmpC.setOriginalName(tmpJO.getString("originalName"));
			tmpC.setDocSize(tmpJO.getString("docSize"));
			tmpC.setDownPath(tmpJO.getString("downPath"));

			return tmpC;

		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static List<Courseware> forCoursewarelist(JSONObject jsonObject) {
		try {

			int total = Integer.valueOf(jsonObject.getString("total"));
			List<Courseware> coursewares = new ArrayList<Courseware>();
			if (total == 0) {
				return coursewares;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("documents");

			for (int i = 0; i < total; i++) {
				Courseware tmpC = new Courseware();
				JSONObject tmpJO = jsonArray.getJSONObject(i);

				tmpC.setDocId(tmpJO.getString("docId"));
				tmpC.setOriginalName(tmpJO.getString("originalName"));
				tmpC.setDownPath(tmpJO.getString("downPath"));
				tmpC.setDocSize(tmpJO.getString("docSize"));
				tmpC.setType(tmpJO.getString("type"));
				coursewares.add(tmpC) ;
			}

			return coursewares;


		} catch (Exception e) {
			Log.e(TAG, "JSONUtil forCoursewarelist errors");
			e.printStackTrace();
			return null;
		}

	}

	public static GuanCoursewareList forCoursewareList(String jsonstring)
	{
		GuanCoursewareList list=gson.fromJson(jsonstring,GuanCoursewareList.class);
		Log.e(TAG,"GuanCoursewareList:"+list.getResult());
		return list;
	}

	public static OperationResult forOperationResult(String jsonstring)
	{
		OperationResult or=gson.fromJson(jsonstring,OperationResult.class);
		Log.e(TAG,"OperationResult:"+or.getResult()+"\t"+or.getInfo());
		return or;
	}

}
