package com.taozhang.filetransition.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class SortFileUtil {
	// for test
	public static void main(String[] args) {
		File dirFile = new File("c:\\");

		File[] sortedFiles = sortFilesByName(dirFile);

	}

	// list sorted files
	public static File[] sortFilesByName(File dirFile) {
		assert dirFile.isDirectory();

		File[] files = dirFile.listFiles();

		FileWrapperByName[] fileWrappers = new FileWrapperByName[files.length];
		for (int i = 0; i < files.length; i++) {
			fileWrappers[i] = new FileWrapperByName(files[i]);
		}

		Arrays.sort(fileWrappers);

		File[] sortedFiles = new File[files.length];
		for (int i = 0; i < files.length; i++) {
			sortedFiles[i] = fileWrappers[i].getFile();
		}

		return sortedFiles;
	}

	public static void sortFilesByTime(List<File> list) {

		// FileComparableByTime[] fileWrappers = new
		// FileComparableByTime[files.length];
		FileComparableByTime fileWrapper = new FileComparableByTime();
		if (!list.isEmpty()) {
			Log.e("sortListBySize()", "");
			synchronized (list) {
				Collections.sort(list, fileWrapper);
			}
		}
	}

	public static void sortFilesBySize(List<File> list) {
		FileComparableBySize fileWrapper = new FileComparableBySize();
		if (!list.isEmpty()) {
			Log.e("sortListBySize()", "");
			synchronized (list) {
				Collections.sort(list, fileWrapper);
			}
		}
	}

}

class FileWrapperByName implements Comparable {
	/** File */
	private File file;

	public FileWrapperByName(File file) {
		this.file = file;
	}

	public int compareTo(Object obj) {
		assert obj instanceof FileWrapperByName;

		FileWrapperByName castObj = (FileWrapperByName) obj;

		if (this.file.getName().compareTo(castObj.getFile().getName()) > 0) {
			return 1;
		} else if (this.file.getName().compareTo(castObj.getFile().getName()) < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public File getFile() {
		return this.file;
	}
}

class FileComparableByTime implements Comparator<File> {

	@Override
	public int compare(File f1, File f2) {
		long diff = f1.lastModified() - f2.lastModified();
		if (diff > 0) {
			return 1;
		} else if (diff == 0) {
			return 0;
		} else {
			return -1;
		}
	}
}

class FileComparableBySize implements Comparator<File> {

	@Override
	public int compare(File f1, File f2) {

		long temp = new File(f2.getPath()).length()
				- new File(f1.getPath()).length();

		if (temp > 0) {
			return 1;
		} else if (temp == 0) {
			return 0;
		} else {
			return -1;
		}

	}

}