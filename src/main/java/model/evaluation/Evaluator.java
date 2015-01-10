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

    /**
     * Evaluates the result of the search.
     * @param qrelsFolder folder with the qrel files.
     * @param resultFolder folder with the results of the search.
     * @return a report with an EvaluationReport object with the evaluations of
     * all queries and the average.
     * @see EvaluationReport
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

    /**
     * Compares the files in the qrel folder with the results.
     * @param qrelFiles qrel files with the relevant and non-relevant documents.
     * @param resultFiles files with the results of the search.
     * @return a report with an EvaluationReport object with the evaluations of
     * all queries and the average.
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

            Set<String> relevantDocSet = buildRelevantDocSet(qrelFiles[i]);
            Evaluation eval = evaluateQueryResult(relevantDocSet,resultFiles[i]);
            report.addEvaluation(eval);
        }

        return LOGGER.exit(report);
    }

    /**
     * Evaluates the result of a single query.
     * @param relevantDocSet the set of relevant documents for this query.
     * @param result the file with the documents found by the engine.
     * @return the Evaluation of the result of the query.
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

    /**
     * Reads the lines of the result file to calculate the evaluation.
     * @param relevantDocSet the set of relevant documents for this query.
     * @param scanner to read the lines of the file.
     * @return the Evaluation of the result of the query.
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

    /**
     * Builds the set containing the relevant documents of a qrel file.
     * @param qrel the file used to build the set.
     * @return the set with the name of the relevant documents.
     */
    private static Set<String> buildRelevantDocSet(File qrel){

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

    /**
     * Adds documents to the set.
     * @param scanner the text scanner of the result file.
     * @param set set with the name of the relevant documents.
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

    /**
     * Lists the files in a folder.
     * @param folder used to list the files.
     * @return An array with the files found.
     * @throws FileNotFoundException if the folder is not found.
     */
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
