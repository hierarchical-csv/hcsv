package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "0003")
public class RegionLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvParentKeyPart(UserLine.class)
    @CsvBindByPosition(position = 1)
    private String userCode;

    @CsvParentKeyPart(value = AddressLine.class, order = 1)
    @CsvBindByPosition(position = 2)
    private String country;

    @CsvBindByPosition(position = 3)
    private String region;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "RegionLine{" +
                "code='" + code + '\'' +
                ", userCode='" + userCode + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
