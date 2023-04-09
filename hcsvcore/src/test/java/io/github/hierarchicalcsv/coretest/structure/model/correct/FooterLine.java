package io.github.hierarchicalcsv.coretest.structure.model.correct;

import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "0999")
public class FooterLine {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvBindByPosition(position = 1)
    private int count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "FooterLine{" +
                "code='" + code + '\'' +
                ", count=" + count +
                '}';
    }
}
