package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class DataProvider {
	
	private static final String JSON_PATH;
	
	static {
		JSON_PATH = System.getProperty("jsonPath", "json");
	}
	
	/**
	 * 读取json文件转type数据
	 * @param jsonName  json文件名称
	 * @param type  json转换目标数据类型
	 * @return  type定义的数据类型
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static Object fromJsonByReader(String jsonName, Type type) 
			throws FileNotFoundException, UnsupportedEncodingException {
		InputStream in = null;
		JsonReader reader = null;
		try {
			in = new FileInputStream(JSON_PATH + File.separator + jsonName);
			reader = new JsonReader(new InputStreamReader(in, "utf-8"));
			Gson gson = new Gson();
			return gson.fromJson(reader, type);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if( in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Object fromJson(String jsonName, Type type) throws IOException  {
		String mapjson = FileUtils.readFileToString(new File(JSON_PATH + File.separator + jsonName), "utf-8");
		Gson gson = new Gson();
		return gson.fromJson(mapjson, type);
	}
}
