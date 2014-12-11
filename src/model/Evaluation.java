package model;

import java.util.List;

public class Evaluation {

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
        ++totalDocsFound;
        ++relevantDocsFound;
        updatePrecision();
    }

    public void foundNotRelevantDoc(){
        ++totalDocsFound;
        updatePrecision();
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

        Evaluation evaluation = new Evaluation();
        sumPrecisions(evaluation, list);
        divideByTotal(evaluation, list);

        return evaluation;
    }

    private static void sumPrecisions(Evaluation evaluation,
            List<Evaluation> list) {

        for(Evaluation eval: list){
            evaluation.precision5 += eval.getPrecision5();
            evaluation.precision10 += eval.getPrecision10();
            evaluation.precision25 += eval.getPrecision25();
        }
    }

    private static void divideByTotal(Evaluation evaluation,
            List<Evaluation> list) {

        int total = list.size();

        if(total != 0) {
            evaluation.precision5 = evaluation.precision5 / (float)total;
            evaluation.precision10 = evaluation.precision10 / (float)total;
            evaluation.precision25 = evaluation.precision25 / (float)total;
        }
    }

    public String toString(){
        return "P5: " + precision5 + "\nP10: " + precision10
                + "\nP25: " + precision25;
    }
}
