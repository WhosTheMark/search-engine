package model;

public class Evaluation {

    private float precision5 = 0;
    private float precision10 = 0;
    private float precision25 = 0;
    private int docsAnalyzed = 0;   //Number of files found by the search engine
    private int docsMatched = 0;    //Number of files that are actually relevant

    public Evaluation(){
    }

    public Evaluation(float precision5, float precision10, float precision25,
            int total) {
        super();
        this.precision5 = precision5 / (float)total;
        this.precision10 = precision10 / (float)total;
        this.precision25 = precision25 / (float)total;
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

    public void match(){
        ++docsAnalyzed;
        ++docsMatched;
        update();
    }

    public void noMatch(){
        ++docsAnalyzed;
        update();
    }

    private void update() {

        switch(docsAnalyzed){

        case 5:
            precision5 = (float)docsMatched/ (float)docsAnalyzed;
            break;
        case 10:
            precision10 = (float)docsMatched/ (float)docsAnalyzed;
        case 25:
            precision25 = (float)docsMatched/ (float)docsAnalyzed;
            break;
        }
    }

    public String toString(){
        return "P5: " + precision5 + "\nP10: " + precision10
                + "\nP25: " + precision25;
    }
}
