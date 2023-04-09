package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "0004")
public class ProductLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvParentKeyPart(UserLine.class)
    @CsvBindByPosition(position = 1)
    private String userCode;

    @CsvKey
    @CsvBindByPosition(position = 2)
    private String barcode;

    @CsvBindByPosition(position = 3)
    private String name;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductLine{" +
                "code='" + code + '\'' +
                ", userCode='" + userCode + '\'' +
                ", barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
