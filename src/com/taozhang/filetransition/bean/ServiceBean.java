package com.taozhang.filetransition.bean;

import java.net.InetSocketAddress;

/**
 * Description:
 * Created by taozhang on 2016/5/9.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/9
 */
public class ServiceBean {
    public String name;
    public InetSocketAddress address;

    public ServiceBean(String name, InetSocketAddress address) {
        super();
        this.name = name;
        this.address = address;
    }

    public ServiceBean() {
        super();
    }
}
