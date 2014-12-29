package model;

import java.io.File;
import java.util.List;

import model.evaluation.Evaluation;
import model.evaluation.Evaluator;

import org.junit.Test;

public class EvaluatorTest {

    @Test
    public void test() {
        File qrels = new File("extra/qrels");
        File results = new File("extra/results");

        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(qrels,results);
        Evaluation finalEval = evaluator.calculateFinalEvaluation();
        List<Evaluation> listEvals = evaluator.getQueriesEvaluations();

        int i = 1;

        for (Evaluation eval: listEvals){
            System.out.println("Query: " + i);
            System.out.println(eval);
            System.out.println("-------------------");
            ++i;
        }

        System.out.println("Final:");
        System.out.println(finalEval.toString());
    }

}
