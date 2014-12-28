package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.calculators.RelevanceCalculator;

public class EnhancedSearcher extends Searcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private QueryEnhancer enhancer;
    private static final int DEFAULT_WEIGHT = 5;
    private static final int ENHANCED_WEIGHT = 1;

    public EnhancedSearcher(RelevanceCalculator calculator, String resultFolder) {
        super(calculator, resultFolder);
        enhancer = new QueryEnhancer();
    }

    @Override
    protected List<RelevantDocument> executeQuery(String query) {

        LOGGER.debug("Excecuting query {} with enhancement.", query);

        List<String> enhancerWords = enhancer.enhanceQuery(query);

        List<WeightedKeyword> weigthedKeywords = new ArrayList<WeightedKeyword>();
        setWeights(weigthedKeywords, enhancerWords, ENHANCED_WEIGHT);

        String[] keywords = query.split(Indexation.SEPARATOR_REGEXP);
        setWeights(weigthedKeywords,Arrays.asList(keywords), DEFAULT_WEIGHT);

        return executeQuery(weigthedKeywords);
    }

    private void setWeights(List<WeightedKeyword> weigthedKeywords,
            List<String> enhancerWords, int weight) {

        LOGGER.entry(weigthedKeywords, enhancerWords, weight);

        for(String enhancerWord : enhancerWords) {
            WeightedKeyword wk = new WeightedKeyword(enhancerWord,weight);
            weigthedKeywords.add(wk);
        }

        LOGGER.exit();
    }

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
