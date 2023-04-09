package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

import java.util.Date;

@HCSVBean(codePosition = 0, codeValue = "0000")
public class HeaderLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvDate("yyyy-MM-dd")
    @CsvBindByPosition(position = 1)
    private Date date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HeaderLine{" +
                "code='" + code + '\'' +
                ", date=" + date +
                '}';
    }
}
