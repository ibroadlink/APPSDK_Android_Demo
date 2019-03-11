package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by YeJin on 2016/2/25.
 */
public class DNAKitProductInfo implements Serializable {
    private String pid;

    private String name;

    private String shortcuticon;

    private int rank;

    private String brandname;

    private String displaybrand;

    private String displaymodel;

    private String model;
    //别名
    private String alias;

    //拼音
    private String modelPinyin;

    //映射pid列表
    private ArrayList<String> pids;

    //映射pid
    private String mappid;

    public String getMappid() {
        return mappid;
    }

    public void setMappid(String mappid) {
        this.mappid = mappid;
    }

    public ArrayList<String> getPids() {
        return pids;
    }

    public void setPids(ArrayList<String> pids) {
        this.pids = pids;
    }

    public String getBrandname() {
        if(brandname == null){
            return "";
        }else{
            return brandname;
        }
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getModel() {
        if(model == null){
            return "";
        }else{
            return model;
        }
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortcuticon() {
        return shortcuticon;
    }

    public void setShortcuticon(String shortcuticon) {
        this.shortcuticon = shortcuticon;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getModelPinyin() {
        return modelPinyin;
    }

    public void setModelPinyin(String modelPinyin) {
        this.modelPinyin = modelPinyin;
    }

    public String getDisplaybrand() {
        return displaybrand;
    }

    public void setDisplaybrand(String displaybrand) {
        this.displaybrand = displaybrand;
    }

    public String getDisplaymodel() {
        return displaymodel;
    }

    public void setDisplaymodel(String displaymodel) {
        this.displaymodel = displaymodel;
    }
}
