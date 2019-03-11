package cn.com.broadlink.blappsdkdemo.activity.family.param;

public class BLSUpdateFamilyInfoParams {

    private String name;
    private String description;
    private String countryCode;
    private String provinceCode;
    private String cityCode;
    private int familylimit = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getFamilylimit() {
        return familylimit;
    }

    public void setFamilylimit(int familylimit) {
        this.familylimit = familylimit;
    }

}
