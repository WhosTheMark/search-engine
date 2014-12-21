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
    private static final int MAX_PRECISION = 25;

    // Stores the queries that have already been evaluated.
    private List<Evaluation> queriesEvaluations;

    public Evaluator(){
        this.queriesEvaluations = new ArrayList<Evaluation>();
    }

    public List<Evaluation> getQueriesEvaluations() {
        return queriesEvaluations;
    }

    public Evaluation calculateFinalEvaluation(){

        return Evaluation.calculateAverage(queriesEvaluations);
    }

    /*
     * Evaluate the results using predetermined qrel files.
     */
    public void evaluate(File qrelsFolder, File resultFolder){

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

    /*
     * Compare the results of all queries using predetermined qrel files.
     */
    private void compareResults(File[] qrelFiles, File[] resultFiles) {

        int qrelsLen = qrelFiles.length;
        int resultLen = resultFiles.length;

        if(qrelsLen != resultLen) {
            LOGGER.log(Level.WARNING, "The number of files in the folders are different!");
        }

        for (int i = 0; i < qrelsLen && i < resultLen; ++i){

            Set<String> relevantDocSet = getRelevantDocSet(qrelFiles[i]);
            Evaluation eval = evaluateQueryResult(relevantDocSet,resultFiles[i]);
            this.queriesEvaluations.add(eval);
        }
    }

    /*
     * Evaluate a single query
     */
    private Evaluation evaluateQueryResult(Set<String> relevantDocSet, File result) {

        Scanner scanner;

        try {
            scanner = new Scanner(result);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "This should not happen.", e);
            return null;
        }

        return calculateEvaluation(relevantDocSet, scanner);

    }

    /*
     * Calculates an evaluation using the set of relevant documents
     * and the result file.
     */
    private Evaluation calculateEvaluation(Set<String> relevantDocSet,
            Scanner scanner) {

        Evaluation eval = new Evaluation();
        int i = 0;

        while (scanner.hasNext(DOC_NAME) && i < MAX_PRECISION) {
            String doc = scanner.next(DOC_NAME);
            if(relevantDocSet.contains(doc)){
                eval.foundRelevantDoc();
            } else {
                eval.foundNotRelevantDoc();
            }
            ++i;
        }
        return eval;
    }

    /*
     * Get the relevant docs in the qrel.
     */
    private static Set<String> getRelevantDocSet(File qrel){

        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(qrel);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "This should not happen.", e);
            return set;
        }

        scanner.useLocale(Locale.FRANCE);
        addDocs(scanner, set);

        return set;
    }

    /*
     * Add a relevant doc to the set.
     */
    private static void addDocs(Scanner scanner, Set<String> set) {

        while(scanner.hasNext(DOC_NAME)){
            String doc = scanner.next(DOC_NAME);
            float relevance = scanner.nextFloat();

            if(relevance > 0){
                set.add(doc);
            }
        }
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
