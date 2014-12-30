package model.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that evaluates the results of the search.
 * @see Evaluation
 */
public class Evaluator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DOC_NAME = "D[0-9]+\\.html";

    // Avoid instantiation.
    private Evaluator(){}

    /*
     * Evaluate the results using predetermined qrel files.
     */
    public static EvaluationReport evaluate(File qrelsFolder, File resultFolder){

        LOGGER.entry(qrelsFolder, resultFolder);

        File[] qrelFiles;
        File[] resultFiles;

        try {
            qrelFiles = listFiles(qrelsFolder);
            resultFiles = listFiles(resultFolder);
        } catch (FileNotFoundException e) {
            LOGGER.error("Some of the folders to compare do not exist", e);
            return new EvaluationReport();
        }

        Arrays.sort(qrelFiles);
        Arrays.sort(resultFiles);

        EvaluationReport report = compareResults(qrelFiles, resultFiles);

        return LOGGER.exit(report);

    }

    /*
     * Compare the results of all queries using predetermined qrel files.
     */
    private static EvaluationReport compareResults(File[] qrelFiles, File[] resultFiles) {

        LOGGER.entry(qrelFiles,resultFiles);

        int qrelsLen = qrelFiles.length;
        int resultLen = resultFiles.length;

        if(qrelsLen != resultLen) {
            LOGGER.warn("The number of files in the folders are different!");
        }

        EvaluationReport report = new EvaluationReport();

        for (int i = 0; i < qrelsLen && i < resultLen; ++i){

            Set<String> relevantDocSet = getRelevantDocSet(qrelFiles[i]);
            Evaluation eval = evaluateQueryResult(relevantDocSet,resultFiles[i]);
            report.addEvaluation(eval);
        }

        return LOGGER.exit(report);
    }

    /*
     * Evaluate a single query
     */
    private static Evaluation evaluateQueryResult(Set<String> relevantDocSet, File result) {

        LOGGER.entry(relevantDocSet,result);

        Scanner scanner;

        try {
            scanner = new Scanner(result);
        } catch (FileNotFoundException e) {
            LOGGER.catching(e);
            return null;
        }

        return calculateEvaluation(relevantDocSet, scanner);
    }

    /*
     * Calculates an evaluation using the set of relevant documents
     * and the result file.
     */
    private static Evaluation calculateEvaluation(Set<String> relevantDocSet,
            Scanner scanner) {

        LOGGER.entry(relevantDocSet,scanner);

        Evaluation eval = new Evaluation(relevantDocSet);
        int i = 0;

        while (scanner.hasNext(DOC_NAME) && i < Evaluation.MAX_PRECISION) {
            String doc = scanner.next(DOC_NAME);
            eval.checkDocument(doc);
            ++i;
        }

        return LOGGER.exit(eval);
    }

    /*
     * Get the relevant docs in the qrel.
     */
    private static Set<String> getRelevantDocSet(File qrel){

        LOGGER.entry(qrel);

        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(qrel);
        } catch (FileNotFoundException e) {
            LOGGER.catching(e);
            return set;
        }

        scanner.useLocale(Locale.FRANCE);
        addDocs(scanner, set);

        return LOGGER.exit(set);
    }

    /*
     * Add a relevant doc to the set.
     */
    private static void addDocs(Scanner scanner, Set<String> set) {

        LOGGER.entry(scanner, set);

        while(scanner.hasNext(DOC_NAME)){
            String doc = scanner.next(DOC_NAME);
            float relevance = scanner.nextFloat();

            if(relevance > 0){
                set.add(doc);
            }
        }

        LOGGER.exit();
    }

    private static File[] listFiles(File folder) throws FileNotFoundException{

        LOGGER.entry(folder);

        File[] fileList = folder.listFiles();

        if (fileList == null) {
            throw new FileNotFoundException("Could not find the folder "
                    + folder.getAbsolutePath());
        }

        return LOGGER.exit(fileList);
    }

}
