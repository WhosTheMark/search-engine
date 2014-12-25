package model;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Evaluation {

    private static final Logger LOGGER = LogManager.getLogger();
    private float precision5 = 0;
    private float precision10 = 0;
    private float precision25 = 0;
    private int totalDocsFound = 0;
    private int relevantDocsFound = 0;

    public Evaluation(){
    }

    public float getPrecision5() {
        return precision5;
    }

    public float getPrecision10() {
        return precision10;
    }

    public float getPrecision25() {
        return precision25;
    }

    public void setPrecision5(float precision5) {
        this.precision5 = precision5;
    }

    public void setPrecision10(float precision10) {
        this.precision10 = precision10;
    }

    public void setPrecision25(float precision25) {
        this.precision25 = precision25;
    }

    /*
     * Document found is a relevant doc
     */
    public void foundRelevantDoc(){

        LOGGER.entry();
        ++totalDocsFound;
        ++relevantDocsFound;
        updatePrecision();
        LOGGER.exit();
    }

    public void foundNotRelevantDoc(){

        LOGGER.entry();
        ++totalDocsFound;
        updatePrecision();
        LOGGER.exit();
    }

    private void updatePrecision() {

        switch(totalDocsFound){

        case 5:
            precision5 =  (float)relevantDocsFound / (float)totalDocsFound;
            break;
        case 10:
            precision10 = (float)relevantDocsFound / (float)totalDocsFound;
            break;
        case 25:
            precision25 = (float)relevantDocsFound / (float)totalDocsFound;
            break;
        }
    }

    public static Evaluation calculateAverage(List<Evaluation> list){

        LOGGER.entry(list);
        Evaluation evaluation = new Evaluation();
        sumPrecisions(evaluation, list);
        divideByTotal(evaluation, list);

        return LOGGER.exit(evaluation);
    }

    private static void sumPrecisions(Evaluation evaluation,
            List<Evaluation> list) {

        LOGGER.entry(evaluation,list);

        for(Evaluation eval: list){
            evaluation.precision5 += eval.getPrecision5();
            evaluation.precision10 += eval.getPrecision10();
            evaluation.precision25 += eval.getPrecision25();
        }

        LOGGER.exit();
    }

    private static void divideByTotal(Evaluation evaluation,
            List<Evaluation> list) {

        LOGGER.entry(evaluation,list);

        int total = list.size();

        if(total != 0) {
            evaluation.precision5 = evaluation.precision5 / (float)total;
            evaluation.precision10 = evaluation.precision10 / (float)total;
            evaluation.precision25 = evaluation.precision25 / (float)total;
        }

        LOGGER.exit();
    }

    public String toString(){
        return "P5: " + precision5 + "\nP10: " + precision10
                + "\nP25: " + precision25;
    }
}
