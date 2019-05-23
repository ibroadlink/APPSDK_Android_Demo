package cn.com.broadlink.blappsdkdemo.db.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import cn.com.broadlink.blappsdkdemo.db.dao.DNAKitDirInfoDao;

/**
 * 产品品类信息表
 * Created by YeJin on 2016/2/24.
 */
@DatabaseTable(tableName = "productCategoryInfo", daoClass = DNAKitDirInfoDao.class)
public class DNAKitDirInfo implements Serializable {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String categoryid;

    @DatabaseField
    private String name;

    @DatabaseField
    private int rank;

    @DatabaseField
    private String link;

    @DatabaseField
    private String protocols;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }
}
