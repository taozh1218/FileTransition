package com.taozhang.filetransition.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

public class SortFolderUtil {
	private int mFileSize = 0;
	private List<String> mPathString = new ArrayList<String>();

	private boolean sortFolder(String path) {
		if (path == null || TextUtils.isEmpty(path))
			return false;
		File[] fileList = null;
		File file = new File(path);
		if (file.exists() == false) {
			file.mkdir();
		}
		if (!file.exists()
				|| (file.isDirectory() && (file.listFiles().length == 0))) {
			return true;
		} else {
			fileList = file.listFiles();
			mFileSize = file.listFiles().length;
			mPathString.clear();
			if (mFileSize > 0) {
				for (int i = 0; i < mFileSize; i++) {
					mPathString.add(fileList[i].getAbsolutePath());
				}
				Collections.sort(mPathString);
			}
			return false;
		}
	}

	// Collections.reverse(mPathString);½µÐòÅÅÐò
}
