package com.taozhang.filetransition.util.simplifymi;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;

import com.taozhang.filetransition.bean.FileInfo;

public class IntentBuilder {
	public static Intent buildSendFile(FileInfo file) {
		ArrayList<Uri> uris = new ArrayList<Uri>();

		String mimeType = "*/*";
		if (!file.IsDir) {
			File fileIn = new File(file.filePath);
			mimeType = getMimeType(file.fileName);
			Uri u = Uri.fromFile(fileIn);
			uris.add(u);

		}

		if (uris.size() == 0)
			return null;

		boolean multiple = uris.size() > 1;
		Intent intent = new Intent(
				multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
						: android.content.Intent.ACTION_SEND);

		if (multiple) {
			intent.setType("*/*");
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		} else {
			intent.setType(mimeType);
			intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
		}

		return intent;
	}

	private static String getMimeType(String filePath) {
		int dotPosition = filePath.lastIndexOf('.');
		if (dotPosition == -1)
			return "*/*";

		String ext = filePath.substring(dotPosition + 1, filePath.length())
				.toLowerCase();
		String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
		if (ext.equals("mtz")) {
			mimeType = "application/miui-mtz";
		}

		return mimeType != null ? mimeType : "*/*";
	}
}
