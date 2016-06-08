package com.cn.jmantiLost.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "device_db";
	private final static int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql_anti_contact = "CREATE TABLE [anti_contact] (" +
								"[id] INTEGER primary key autoincrement," +
								"[name] TEXT," +
								"[number] TEXT)";
		
		String sql_sos = "CREATE TABLE [sos] (" +
						"[id] INTEGER primary key autoincrement," +
						"[number] TEXT," +
						"[message] TEXT)";

		
		String sql_contact = "CREATE TABLE [contact] (" +
								"[id] INTEGER primary key autoincrement," +
								"[name] TEXT," +
								"[number] TEXT)";
		
		String sql_app = "CREATE TABLE [app] (" +
						 "[id] INTEGER  primary key autoincrement," +
						 "[package_name] TEXT," +
						 "[app_name] TEXT)";
		
		String sql_camera = "CREATE TABLE [camera] (" +
							"[id] INTEGER  primary key autoincrement," +
							"[isFront] INTEGER," +
							"[isFocus] INTEGER);" ;
		
		String sql_key_set = "CREATE TABLE [key_set] (" +
							"[id]  INTEGER  primary key autoincrement," +
							"[bitmap] text," +
							"[detail] text," +
							"[count] INTEGER," +
							"[type] INTEGER," +
							"[action] INTEGER)" ;
		
		//action: 0 : 照相：1：闪光灯：2： 打开应用：3：伪装来电  4：sos
		
		String sql_device_set = "CREATE TABLE [device_set] (" +
				"[id] INTEGER  primary key autoincrement," +
				"[unique_id] VARCHAR," +
				"[imagepath] VARCHAR," +
				"[isdisturb] INTEGER," +
				"[distance] INTEGER," +
				"[deviceName] VARCHAR,"+
				"[deviceAddress] VARCHAR,"+
				"[deviceIsActive] INTEGER,"+
				"[islocation] INTEGER," +
				"[latitude] VARCHAR," +
				"[longitude] VARCHAR," +
				"[anti_time] long," +
				"[connect] INTEGER); ";
		
		String sql_disturb = "CREATE TABLE [device_disturb] (" +
								"[id] INTEGER primary key autoincrement," +
								"[unique_id] VARCHAR," +
								"[isdisturb] INTEGER," +
								"[disturb_start] VARCHAR," +
								"[disturb_end] VARCHAR);";
		String sql_device_ring ="CREATE TABLE [device_ring](" +
								 "[id] INTEGER primary key autoincrement," +
								 "[unique_id] VARCHAR," +
								 "[isshock] INTEGER,"+
								 "[ring_id] INTEGER,"+ //id 其实就是一个标示
								 "[ringvolume] INTEGER," +
								 "[duration_time] VARCHAR);";
		
		String insert_one = "insert into key_set values(null,null,\"AntiLost\",1,0,0)";
		String insert_two = "insert into key_set values(null,null,null,2,0,-1)";
		String insert_three = "insert into key_set values(null,null,null,3,0,-1)";
		String insert_four = "insert into key_set values(null,null,null,4,0,-1)";
		
		String insert_anti = "insert into anti_contact values (null,\"\",\"0322098202\")";
		
		String insert_camera = "insert into camera values (null,1,1)";
		
		db.execSQL(sql_sos);
		db.execSQL(sql_app);
		db.execSQL(sql_camera);
		db.execSQL(sql_anti_contact);
		db.execSQL(sql_contact);
		db.execSQL(sql_key_set);
		db.execSQL(sql_device_set);
		db.execSQL(sql_disturb);
		db.execSQL(sql_device_ring);
		db.execSQL(insert_one);
		db.execSQL(insert_two);
		db.execSQL(insert_three);
		db.execSQL(insert_four);
		db.execSQL(insert_anti);
		db.execSQL(insert_camera);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
