package model.sparql;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

public class SparqlDAOTest {

    static final String LABEL_1 = "lieu";
    static final String[] RES_1 = {"localisation"};
    static final String[] RES_2 = {"continent", "lieu administratif", "pays", "ville", "zone géographique"};

    @Test
    public void test() {

        SparqlDAO sparqlDAO = new SparqlDAO();
        List<String> list = sparqlDAO.getOtherLabels(LABEL_1);
        Collections.sort(list);
        String[] result = new String[list.size()];
        result = list.toArray(result);
        assertArrayEquals(RES_1,result);
    }

    @Test
    public void test2() {

        SparqlDAO sparqlDAO = new SparqlDAO();
        List<String> list = sparqlDAO.getSubClassLabels(LABEL_1);
        Collections.sort(list);
        String[] result = new String[list.size()];
        result = list.toArray(result);
        assertArrayEquals(RES_2,result);
    }

}
