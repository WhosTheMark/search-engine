package model;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class EvaluatorTest {

    @Test
    public void test() {
        File qrels = new File("extra/qrels");
        File results = new File("extra/results");

        Evaluator evaluator = new Evaluator(qrels,results);
        evaluator.evaluate();
        Evaluation finalEval = evaluator.calculateFinalEvaluation();
        List<Evaluation> listEvals = evaluator.getQueriesEvaluations();

        for (Evaluation eval: listEvals){
            System.out.println(eval.toString());
            System.out.println("-------------------");
        }

        System.out.println("Final:");
        System.out.println(finalEval.toString());
    }

}
