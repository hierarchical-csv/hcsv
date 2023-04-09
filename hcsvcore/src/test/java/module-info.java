module io.github.hierarchicalcsv.coretest {
    requires com.opencsv;
    requires org.apache.commons.lang3;
    requires io.github.hierarchicalcsv.core;
    requires org.junit.jupiter.api;
    exports io.github.hierarchicalcsv.coretest.structure;
    exports io.github.hierarchicalcsv.coretest.structure.model.correct;
}