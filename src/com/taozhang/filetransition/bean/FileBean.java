package com.taozhang.filetransition.bean;

/**
 * Description: girdView adapter 中的基本的文件类型
 * Created by taoZh on 2016/5/5.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taoZh
 * @updateDate 2016/5/5
 */
public class FileBean  {

    private String name;
    private int id;
    private String fileNum;

    public FileBean() {
        super();
    }

	public FileBean(String name, int id, String fileNum) {
		super();
		this.name = name;
		this.id = id;
		this.fileNum = fileNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileNum() {
		return fileNum;
	}

	public void setFileNum(String fileNum) {
		this.fileNum = fileNum;
	}

	@Override
	public String toString() {
		return "FileBean [name=" + name + ", id=" + id + ", fileNum=" + fileNum
				+ "]";
	}

}
