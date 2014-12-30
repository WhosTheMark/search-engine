package model.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EvaluationReport {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<Evaluation> queriesEvaluations;
    private Evaluation finalEvaluation;

    public EvaluationReport(){
        queriesEvaluations = new ArrayList<Evaluation>();
    }

    public List<Evaluation> getQueriesEvaluations() {
        return queriesEvaluations;
    }

    public Evaluation getFinalEvaluation() {

        if (finalEvaluation == null) {
            finalEvaluation = calculateAverage();
        }

        return finalEvaluation;

    }

    public void addEvaluation(Evaluation eval) {
        queriesEvaluations.add(eval);
    }

    /**
     * Calculate the average precisions using a list of evaluations.
     * @param list to calculate the  average precision.
     * @return an evaluation with the average.
     */
    private Evaluation calculateAverage(){

        LOGGER.entry(queriesEvaluations);
        Evaluation evaluation = new Evaluation();
        sumPrecisions(evaluation);
        divideByTotal(evaluation);

        return LOGGER.exit(evaluation);
    }

    /**
     * Sums all the precisions.
     * @param evaluation where the precisions are stored.
     * @param list list of evaluations to sum.
     */
    private void sumPrecisions(Evaluation evaluation) {

        LOGGER.entry(evaluation);

        for(Evaluation eval: queriesEvaluations){
            evaluation.setPrecision5(evaluation.getPrecision5() + eval.getPrecision5());
            evaluation.setPrecision10(evaluation.getPrecision10() + eval.getPrecision10());
            evaluation.setPrecision25(evaluation.getPrecision25() + eval.getPrecision25());
        }

        LOGGER.exit();
    }

    /**
     * Divides the precisions by the total of evaluations.
     * @param evaluation where the precisions are stored.
     * @param list list of all evaluations.
     */
    private void divideByTotal(Evaluation evaluation) {

        LOGGER.entry(evaluation);

        int total = queriesEvaluations.size();

        if(total != 0) {
            evaluation.setPrecision5(evaluation.getPrecision5() / (float)total);
            evaluation.setPrecision10(evaluation.getPrecision10() / (float)total);
            evaluation.setPrecision25(evaluation.getPrecision25() / (float)total);
        }

        LOGGER.exit();
    }

    public String toString() {

        StringBuilder builder = new StringBuilder();
        int i = 1;

        for (Evaluation eval: queriesEvaluations){
            builder.append("Query: " + i + "\n");
            builder.append(eval.toString());
            builder.append("\n-------------------\n");
            ++i;
        }

        builder.append("Final:\n");
        builder.append(getFinalEvaluation().toString());
        return builder.toString();
    }
}
