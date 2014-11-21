package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evaluator {

    private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
    private static final String DOC_NAME = "D[0-9]+\\.html";

    private Evaluation finalEvaluation;
    private List<Evaluation> queriesEvaluations;
    private File qrelsFolder;
    private File resultFolder;

    public Evaluator(File qrelsFolder, File resultFolder){
        this.qrelsFolder = qrelsFolder;
        this.resultFolder = resultFolder;
        this.finalEvaluation = new Evaluation();
        this.queriesEvaluations = new ArrayList<Evaluation>();
    }

    public void evaluate(){

        File[] qrelFiles;
        File[] resultFiles;

        try {
            qrelFiles = listFiles(qrelsFolder);
            resultFiles = listFiles(resultFolder);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Some of the folders to compare do not exist", e);
            return;
        }

        Arrays.sort(qrelFiles);
        Arrays.sort(resultFiles);

        compareResults(qrelFiles, resultFiles);

    }

    private void compareResults(File[] qrelFiles, File[] resultFiles) {

        int qrelsLen = qrelFiles.length;
        int resultLen = resultFiles.length;

        if(qrelsLen != resultLen) {
            LOGGER.log(Level.WARNING, "The number of files in the folders are different!");
        }

        for (int i = 0; i < qrelsLen && i < resultLen; ++i){

            Set<String> relevantSet = getRelevantSet(qrelFiles[i]);
            evaluateQueryResult(relevantSet,resultFiles[i]);
        }
    }

    private void evaluateQueryResult(Set<String> relevantSet, File result) {

        Scanner scanner;

        try {
            scanner = new Scanner(result);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "This should not happen.", e);
            return;
        }

        Evaluation eval = calculateEvaluation(relevantSet, scanner);

        this.queriesEvaluations.add(eval);
    }

    private Evaluation calculateEvaluation(Set<String> relevantSet,
            Scanner scanner) {

        Evaluation eval = new Evaluation();
        int i = 0;

        while (scanner.hasNext(DOC_NAME) && i < 25) {
            String doc = scanner.next(DOC_NAME);
            if(relevantSet.contains(doc)){
                eval.match();
            } else {
                eval.noMatch();
            }
            ++i;
        }
        return eval;
    }

    public Evaluation calculateFinalEvaluation(){

        float precision5 = 0;
        float precision10 = 0;
        float precision25 = 0;

        for(Evaluation eval: queriesEvaluations){
            precision5 += eval.getPrecision5();
            precision10 += eval.getPrecision10();
            precision25 += eval.getPrecision25();
        }

        int total = queriesEvaluations.size();
        finalEvaluation = new Evaluation(precision5,precision10,precision25,total);

        return finalEvaluation;
    }

    public List<Evaluation> getQueriesEvaluations() {
        return queriesEvaluations;
    }

    private static Set<String> getRelevantSet(File qrel){

        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(qrel);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "This should not happen.", e);
            return set;
        }

        scanner.useLocale(Locale.FRANCE);

        while(scanner.hasNext(DOC_NAME)){
            String doc = scanner.next(DOC_NAME);
            float relevance = scanner.nextFloat();

            if(relevance > 0){
                set.add(doc);
            }
        }

        return set;
    }

    private static File[] listFiles(File folder) throws FileNotFoundException{

        File[] fileList = folder.listFiles();

        if (fileList == null) {
            throw new FileNotFoundException("Could not find the folder "
                    + folder.getAbsolutePath());
        }

        return fileList;
    }

}
