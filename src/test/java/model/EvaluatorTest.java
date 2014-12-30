package model;

import java.io.File;

import model.evaluation.EvaluationReport;
import model.evaluation.Evaluator;

import org.junit.Test;

public class EvaluatorTest {

    @Test
    public void test() {
        File qrels = new File("extra/qrels");
        File results = new File("extra/results");

        EvaluationReport report = Evaluator.evaluate(qrels,results);
        System.out.println(report);
    }

}
