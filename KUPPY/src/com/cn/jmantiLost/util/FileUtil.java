package com.cn.jmantiLost.util;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.cn.jmantiLost.bean.RecordInfo;

public class FileUtil {

	/*public static ArrayList<String> getFiles(String dirname) throws Exception {
		
		ArrayList<String> filesList = new ArrayList<String>();
		
		File dir = new File(dirname);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			Arrays.sort(files, new CompratorByLastModified());
			for (int i = 0; i < files.length; i++) {
				filesList.add(files[i].getPath());
			}
		}
		return filesList;
	}*/

	public static ArrayList<String> getVideoFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();

		ArrayList<String> filesList = new ArrayList<String>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				final File ff = files[i];
				if (ff.isDirectory()) {
					getVideoFiles(ff.getPath());
				} else {
					if (getExtensionName(ff.getPath()).equals("jpg")) {
						filesList.add(ff.getPath());
					}
				}
			}
		}
		return filesList;
	}
	
	public static class CompratorByLastModified implements Comparator<RecordInfo> {  
		  
        public int compare(RecordInfo f1, RecordInfo f2) {  
            long diff = new File(f1.getFilePath()).lastModified() - new File(f2.getFilePath()).lastModified();  
            if (diff > 0) {  
                return 1;  
            } else if (diff == 0) {  
                return 0;  
            } else {  
                return -1;  
            }  
        }  
   }  
		
	public static ArrayList<RecordInfo> getRecordFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();

		ArrayList<RecordInfo> filesList = new ArrayList<RecordInfo>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				final File ff = files[i];
				if (ff.isDirectory()) {
					getRecordFiles(ff.getPath());
				} else {
					if (getExtensionName(ff.getPath()).equals("amr")) {
						filesList.add(new RecordInfo(false,false,ff.getPath()));
					}
				}
			}
		}
		
		Collections.sort(filesList, new CompratorByLastModified());  
		
		return filesList;
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public static void deleteFile(File file) {
		if (file != null && file.exists()) {
			file.delete();
		}
	}
	
	public static File renameFile(File file,String newName){
		
		File retFile = null ;
		String parent = file.getParent();
		retFile = new File(parent +"/"+ newName+".amr");
        file.renameTo(retFile);
        
        return retFile ;
	}
}
