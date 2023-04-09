package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChild;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "0002")
public class AddressLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvParentKeyPart(UserLine.class)
    @CsvBindByPosition(position = 1)
    private String userCode;

    @CsvKey
    @CsvBindByPosition(position = 2)
    private String country;

    @CsvBindByPosition(position = 3)
    private String postalCode;

    @CsvChild
    private RegionLine regionLine;

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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public RegionLine getRegionLine() {
        return regionLine;
    }

    public void setRegionLine(RegionLine regionLine) {
        this.regionLine = regionLine;
    }

    @Override
    public String toString() {
        return "AddressLine{" +
                "code='" + code + '\'' +
                ", userCode='" + userCode + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", regionLine=" + regionLine +
                '}';
    }
}
