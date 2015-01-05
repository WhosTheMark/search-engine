package model.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.indexation.WordNormalizer;
import model.search.calculators.RelevanceCalculator;

public class EnhancedSearcher extends Searcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private QueryEnhancer enhancer;

    //The weight of the words in the original query.
    private static final int DEFAULT_WEIGHT = 5;

    //The weight of the words found in the SPARQL server.
    private static final int ENHANCED_WEIGHT = 1;

    /**
     * Creates a searcher that will enhance the query using a SPARQL server.
     * @param calculator the calculator that will be used.
     * @param resultFolder the folder where the results will be stored.
     */
    public EnhancedSearcher(RelevanceCalculator calculator, String resultFolder) {
        super(calculator, resultFolder);
        enhancer = new QueryEnhancer();
    }

    /**
     * {@inheritDoc} It will also enhance the query with new words found in a
     * SPARQL server and will set different weights to these words.
     */
    @Override
    protected List<RelevantDocument> executeQuery(String query) {

        LOGGER.debug("Excecuting query {} with enhancement.", query);

        List<String> enhancerWords = enhancer.enhanceQuery(query);
        String[] keywords = WordNormalizer.split(query);
        removeRepeated(enhancerWords,keywords);

        List<WeightedKeyword> weigthedKeywords = new ArrayList<WeightedKeyword>();

        setWeights(weigthedKeywords,Arrays.asList(keywords), DEFAULT_WEIGHT);
        setWeights(weigthedKeywords, enhancerWords, ENHANCED_WEIGHT);

        return executeQuery(weigthedKeywords);
    }

    private void removeRepeated(List<String> enhancerWords, String[] keywords){

        for(String keyword : keywords){
            enhancerWords.remove(keyword);
        }
    }

    /**
     * Sets the weights of a list of words.
     * @param weigthedKeywords the list where the words will be added.
     * @param words the list of words.
     * @param weight the weight the words to be added will have.
     * @see WeightedKeyword
     */
    private void setWeights(List<WeightedKeyword> weigthedKeywords,
            List<String> words, int weight) {

        LOGGER.entry(weigthedKeywords, words, weight);

        for(String enhancerWord : words) {
            WeightedKeyword wk = new WeightedKeyword(enhancerWord,weight);
            weigthedKeywords.add(wk);
        }

        LOGGER.exit();
    }

    /**
     * Executes a query using the list of weighted words.
     * @param keywords the list of keywords.
     * @return the list of relevant documents found.
     * @see RelevantDocument
     */
    public List<RelevantDocument> executeQuery(List<WeightedKeyword> keywords){

        LOGGER.entry(keywords);
        LOGGER.info("Calculating relevant documents for the query: {}.", keywords);

        for (WeightedKeyword keyword : keywords) {
            List<RelevantDocument> relevantDocs = getRelevantDocsOfKeyword(keyword.getKeyword());
            calculator.addDocuments(keyword.getWeight(),relevantDocs);
        }

        return sortRelevantDocs();
    }

}
