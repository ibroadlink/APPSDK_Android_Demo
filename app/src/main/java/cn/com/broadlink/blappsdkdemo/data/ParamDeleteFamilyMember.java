package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除家庭成员
 */
public class ParamDeleteFamilyMember {
    private List<String> familymember = new ArrayList<>();

    public List<String> getFamilymember() {
        return familymember;
    }

    public void setFamilymember(List<String> familymember) {
        this.familymember = familymember;
    }
}
