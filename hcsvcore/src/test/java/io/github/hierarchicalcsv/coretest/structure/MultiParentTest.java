package io.github.hierarchicalcsv.coretest.structure;

import com.opencsv.CSVParserBuilder;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.HCSVReader;
import io.github.hierarchicalcsv.core.HCSVReaderBuilder;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.CsvLineProcessListener;
import io.github.hierarchicalcsv.coretest.structure.model.correct.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MultiParentTest {

    @Test
    public void givenCorrectFile_whenReaderIsDefinedCorrectly_correctResultReturned() throws IOException, CsvException, URISyntaxException {
        LeadingZerosListener lineProcessListener = new LeadingZerosListener();
        try(FileReader fileReader = new FileReader(new File(this.getClass().getResource("/hierarchical-correct-example.csv").toURI()));
            // Given
            HCSVReader hcsvReader = new HCSVReaderBuilder(fileReader)
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withBeanType(new CsvBeanType<>(HeaderLine.class))
                .withBeanClass(UserLine.class)
                .withBeanClasses(Collections.singletonList(FooterLine.class))
                .withBeanType(new CsvBeanType<>(RegionLine.class))
                .withBeanTypes(Collections.singletonList(new CsvBeanType<>(AddressLine.class)))
                .withBeanType(new CsvBeanType<>(ProductLine.class, line ->
                        line != null && line.length >2 && (line[2].startsWith("A") || line[2].startsWith("B"))))
                .withIgnoreUnknownBeanType(true)
                .withSkipLines(2)
                .withListener(lineProcessListener)
                .build()) {
            // when
            var list = hcsvReader.readFile();
            // then
            Assertions.assertEquals(4, list.size());
            // assert header
            Object elementZero = list.get(0);
            Assertions.assertInstanceOf(HeaderLine.class, elementZero);
            HeaderLine headerLine = (HeaderLine) elementZero;
            Assertions.assertArrayEquals(new String[]{"0000", "Wed Mar 01 00:00:00 CET 2023"},
                    new String[] {headerLine.getCode(), headerLine.getDate().toString()});
            // assert users
            Object elementOne = list.get(1);
            Assertions.assertInstanceOf(UserLine.class, elementOne);
            UserLine firstUserLine = (UserLine) elementOne;
            Assertions.assertEquals("UserLine{" +
                            "code='0001', userCode='001', userName='user1', " +
                            "addressLines=[AddressLine{code='0002', userCode='001', " +
                            "country='Spain', postalCode='99', regionLine=RegionLine{code='0003', " +
                            "userCode='001', country='Spain', region='Barcelona'}}, " +
                            "AddressLine{code='0002', userCode='001', country='France', " +
                            "postalCode='93', regionLine=RegionLine{code='0003', " +
                            "userCode='001', country='France', region='Paris'}}], " +
                            "positionsProductLines=[]}",
                    firstUserLine.toString());
            Object elementTwo = list.get(2);
            Assertions.assertInstanceOf(UserLine.class, elementTwo);
            UserLine secondUserLine = (UserLine) elementTwo;
            Assertions.assertEquals("UserLine{code='0001', userCode='002', " +
                            "userName='user2', addressLines=[AddressLine{code='0002', " +
                            "userCode='002', country='France', postalCode='93', " +
                            "regionLine=RegionLine{code='0003', userCode='002', country='France', region='Paris'}}, " +
                            "AddressLine{code='0002', userCode='002', country='France', postalCode='76', " +
                            "regionLine=RegionLine{code='0003', userCode='002', country='France', region='Normandie'}}], " +
                            "positionsProductLines=[ProductLine{code='0004', userCode='002', barcode='B938197310', name='Iphone 14'}, " +
                            "ProductLine{code='0004', userCode='002', barcode='A917497177', name='Washing Machine Hisence'}]}",
                    secondUserLine.toString());
            // assert footer
            Object elementThree = list.get(3);
            Assertions.assertInstanceOf(FooterLine.class, elementThree);
            FooterLine footerLine = (FooterLine) elementThree;
            Assertions.assertArrayEquals(new String[]{"0999", "2"},
                    new String[] {footerLine.getCode(), "" + footerLine.getCount()});
            Assertions.assertEquals(17, lineProcessListener.afterLineProcessCallCount);
            Assertions.assertEquals(2, lineProcessListener.filteredBeansCallCount);
        }
    }

    static class LeadingZerosListener implements CsvLineProcessListener {

        protected int afterLineProcessCallCount = 0;
        protected int filteredBeansCallCount = 0;

        @Override
        public void beforeLineProcess(long lineNumber, String[] line) {
            if(line != null && line.length > 0) {
                String code = line[0];
                line[0] = code.length() == 4? code: "0".repeat(4 - code.length()) + code;
            }
        }

        @Override
        public void afterLineProcess(long lineNumber, Object csvBean, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue, boolean filteredBean) {
            afterLineProcessCallCount++;
            if(filteredBean) {
                filteredBeansCallCount++;
            }
        }
    }

}
