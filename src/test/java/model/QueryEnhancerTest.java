package model;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.search.QueryEnhancer;

import org.junit.Test;

public class QueryEnhancerTest {

    final String QUERY_1 = "lieu";
    final String[] RES_1 = {"localisation"};
    final String QUERY_2 = "lieu, courant cinématographique";
    final String[] RES_2 = {"localisation" , "mouvement cinématographique", "école cinématographique"};

    @Test
    public void enhanceQueryTest() {

        QueryEnhancer enhancer = new QueryEnhancer();
        List<String> syn = enhancer.enhanceQuery(QUERY_1);
        Collections.sort(syn);
        String[] result = new String[syn.size()];
        result = syn.toArray(result);
        assertArrayEquals(RES_1,result);
    }
 
    @Test
    public void enhanceQueryTest2() {
        QueryEnhancer enhancer = new QueryEnhancer();
        List<String> syn = enhancer.enhanceQuery(QUERY_2);
        Collections.sort(syn);
        String[] result = new String[syn.size()];
        result = syn.toArray(result);
        System.out.println(Arrays.toString(result));
        assertArrayEquals(RES_2,result);
    }

}
