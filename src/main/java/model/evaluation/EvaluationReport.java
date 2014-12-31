package model.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to handle the evaluations made by the evaluator.
 * It stores the evaluations made in a list and can calculates the average of all
 * the evaluations.
 */
public class EvaluationReport {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<Evaluation> queriesEvaluations;

    /**
     * Creates a new Evaluation Report
     */
    EvaluationReport(){
        queriesEvaluations = new ArrayList<Evaluation>();
    }

    /**
     * Gets the list of evaluations already made.
     * @return the list of evaluations already made.
     */
    public List<Evaluation> getQueriesEvaluations() {
        return queriesEvaluations;
    }

    /**
     * Gets the evaluation with the average of the evaluations made.
     * @return an evaluation with the average.
     */
    public Evaluation getFinalEvaluation() {
        return calculateAverage();
    }

    /**
     * Adds an evaluation to the list of evaluations.
     * @param eval the Evaluation to add.
     */
    void addEvaluation(Evaluation eval) {
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
            evaluation.precision5 += eval.precision5;
            evaluation.precision10 += eval.precision10;
            evaluation.precision25 += eval.precision25;
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
            evaluation.precision5 /= (float)total;
            evaluation.precision10 /= (float)total;
            evaluation.precision25 /= (float)total;
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
