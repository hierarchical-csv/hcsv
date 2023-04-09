package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

import java.util.ArrayList;
import java.util.List;

@HCSVBean(codePosition = 0, codeValue = "0001")
public class UserLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvKey
    @CsvBindByPosition(position = 1)
    private String userCode;

    @CsvBindByPosition(position = 2)
    private String userName;

    @CsvChildList
    private List<AddressLine> addressLines;

    @CsvChildList
    private List<ProductLine> positionsProductLines;

    public UserLine() {
        addressLines = new ArrayList<>();
        positionsProductLines = new ArrayList<>();
    }

    public List<AddressLine> getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(List<AddressLine> addressLines) {
        this.addressLines = addressLines;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ProductLine> getPositionsProductLines() {
        return positionsProductLines;
    }

    public void setPositionsProductLines(List<ProductLine> positionsProductLines) {
        this.positionsProductLines = positionsProductLines;
    }

    @Override
    public String toString() {
        return "UserLine{" +
                "code='" + code + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userName='" + userName + '\'' +
                ", addressLines=" + addressLines +
                ", positionsProductLines=" + positionsProductLines +
                '}';
    }
}
