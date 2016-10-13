package com.taozhang.filetransition.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;

/**
 * 扫描系统中所有的文件 sdcard
 * 
 * @author Administrator
 * 
 */
public class ScanSystemFile {

	/**
	 * 纯文本
	 */
	public final static String TEXT_PLAIN = "text/plain";// （纯文本）
	/**
	 * （HTML文档）
	 */
	public final static String TEXT_HTML = "text/html";
	/**
	 * （XHTML文档）
	 */
	public final static String XHTML = "application/xhtml+xml";

	/**
	 * （GIF图像）
	 */
	public final static String GIF = "image/gif";
	/**
	 * 【PHP中为：image/pjpeg】 （JPEG图像）
	 */
	public final static String JPEG = "mage/jpeg";
	/**
	 * （PNG图像）【PHP中为：image/x-png】
	 */
	public final static String PNG = "image/png";
	/**
	 * （MPEG动画）
	 */
	public final static String MPEG = "video/mpeg";
	/**
	 * （任意的二进制数据）
	 */
	public final static String OCTET = "application/octet-stream";
	/**
	 * （PDF文档）
	 */
	public final static String PDF = "application/pdf";
	/**
	 * （Microsoft Word文件）
	 */
	public final static String WORD = "application/msword";
	/**
	 * （RFC 822形式）
	 */
	public final static String RFC = "message/rfc822";
	/**
	 * （HTML邮件的HTML形式和纯文本形式，相同内容使用不同形式表示）
	 */
	public final static String ALT = "multipart/alternative";
	/**
	 * （使用HTTP的POST方法提交的表单）
	 */
	public final static String FORM = "application/x-www-form-urlencoded";
	/**
	 * （同上，但主要用于表单提交时伴随文件上传的场合）
	 */
	public final static String FORM_DATA = "multipart/form-data";

	public static Context context = App.context;
	
	
	
	public final  Map<String, List<FileInfo>> folders = new HashMap<String, List<FileInfo>>();
	public Map<String, List<FileInfo>> getFolders(){
		return folders;
	}

	public static ArrayList<FileInfo> scanMusicFile() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA }, "_size>?",
				new String[] { 1024 * 1024 + "" }, null);

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
			String type = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
			String data = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			long id = cursor.getLong(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// get thumbnail
//			Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
//					MyApplication.cr, id,
			
//			Log.e("Name,Type,data,thumbnail", name + " ," + type + ", " + data + "," +  thumbnail);
			FileInfo file = new FileInfo();//name, type, data
			file.dbId = id;
			file.fileName = name;
			file.fileType = type;
			file.filePath = data;
			fileList.add(file);

		}
		cursor.close();
		return fileList;
	}
//	/**
//	 * @Description 获取专辑封面
//	 * @param filePath 文件路径，like XXX/XXX/XX.mp3
//	 * @return 专辑封面bitmap
//	 */
//	public static Bitmap createAlbumArt(final String filePath) {
//	    Bitmap bitmap = null;
//	    //能够获取多媒体文件元数据的类
//	    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//	    try {
//	        retriever.setDataSource(filePath); //设置数据源
//	        byte[] embedPic = retriever.getEmbeddedPicture(); //得到字节型数据
//	        bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //转换为图片
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    } finally {
//	        try {
//	            retriever.release();
//	        } catch (Exception e2) {
//	            e2.printStackTrace();
//	        }
//	    }
//	    return bitmap;
//	}
	
	/**
	 * 视频
	 * 
	 * @return
	 */
	public static ArrayList<FileInfo> scanVideoFile() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media._ID,
						MediaStore.Video.Media.DISPLAY_NAME,
						MediaStore.Video.Media.TITLE,
						MediaStore.Video.Media.DURATION,
						MediaStore.Video.Media.ARTIST,
						MediaStore.Video.Media.ALBUM,
						MediaStore.Video.Media.MIME_TYPE,
						MediaStore.Video.Media.SIZE,
						MediaStore.Video.Media.DATA }, "_size>?",
				new String[] { 1024 * 1024 + "" }, null);

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (cursor.moveToNext()) {

			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
			String type = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
			long id = cursor.getLong(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
			
			String data = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

			// get the video thumbnail by id,only for video,the follew methods both are success
//			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data,Images.Thumbnails.MINI_KIND);
			Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
					App.cr, id,Images.Thumbnails.MICRO_KIND,options);
			FileInfo file = new FileInfo();//name, type, data
			file.fileName = name;
			file.fileType = type;
			file.filePath = data;
			// save id and thumbnail
			file.dbId = id;
			file.thumbnail = thumbnail;

			fileList.add(file);

		}
		cursor.close();
		return fileList;
	}

	/**
	 * 返回指定类型的文件
	 * 
	 * @param types
	 *            指定的类型
	 */
	public static ArrayList<FileInfo> scanAllFile(String[] selectionArg) {
		String[] columns = new String[] { MediaStore.Files.FileColumns.TITLE,
				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.MIME_TYPE };

		Uri uri = MediaStore.Files.getContentUri("external");
		String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

		Cursor c;
		c = App.context.getContentResolver().query(uri, columns,
				selection, selectionArg, null);
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (c.moveToNext()) {
			String title = c.getString(c
					.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
			String data = c.getString(c
					.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
			String type = c
					.getString(c
							.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
			FileInfo file = new FileInfo();//title, type, data
			file.fileName = title;
			file.fileType = type;
			file.filePath = data;
			fileList.add(file);
		}
		c.close();
		return fileList;
	}
	
	public boolean isRunning = false;
	  //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.TITLE
    };
    final List<FileInfo> paths = new ArrayList<FileInfo>();
    public boolean isInited() {
        return paths.size() > 0;
    }
    
    
	public synchronized void initImage() {
        if (isRunning)
            return;
        isRunning=true;
        if (isInited())
            return;
        //获取大图的游标
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if(StringUtils.isEmpty(uri))
                    continue;
                if (StringUtils.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();

                FileInfo localFile = new FileInfo();
                localFile.fileName = cursor.getString(3);
                localFile.filePath = file.getAbsolutePath();
                localFile.oriURI = uri;
                localFile.thURI = thumbUri;
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.orientation = 360-degree;

                paths.add(localFile);
                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<FileInfo> files = new ArrayList<FileInfo>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("所有图片", paths);
        cursor.close();
        isRunning=false;
    }
	  //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA,
    };
	private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }
	
	 


}
