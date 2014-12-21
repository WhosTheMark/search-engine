package model.sparql;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

public class SparqlAccessorTest {

    final String LABEL_1 = "lieu";
    final String[] RES_1 = {"localisation"};

    @Test
    public void test() {

        SparqlAccessor sparqlDAO = new SparqlAccessor();
        List<String> list = sparqlDAO.getOtherLabels(LABEL_1);
        Collections.sort(list);
        String[] result = new String[list.size()];
        result = list.toArray(result);
        assertArrayEquals(RES_1,result);
    }

}
