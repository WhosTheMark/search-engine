package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.calculators.RelevanceCalculator;

public class EnhancedSearcher extends Searcher {

    private static final Logger LOGGER = Logger.getLogger(EnhancedSearcher.class.getName());
    private QueryEnhancer enhancer;
    private static final int DEFAULT_WEIGHT = 5;
    private static final int ENHANCED_WEIGHT = 1;

    public EnhancedSearcher(RelevanceCalculator calculator, String resultFolder) {
        super(calculator, resultFolder);
        enhancer = new QueryEnhancer();
    }

    @Override
    public List<RelevantDocument> executeQuery(String query) {

        LOGGER.log(Level.FINE, "Excecuting query " + query + "with enhancement.");

        List<String> enhancerWords = enhancer.enhanceQuery(query);

        List<WeightedKeyword> weigthedKeywords = new ArrayList<WeightedKeyword>();
        setWeights(weigthedKeywords, enhancerWords, ENHANCED_WEIGHT);

        String[] keywords = query.split(Indexation.SEPARATOR_REGEXP);
        setWeights(weigthedKeywords,Arrays.asList(keywords), DEFAULT_WEIGHT);

        return executeQuery(weigthedKeywords);
    }

    private void setWeights(List<WeightedKeyword> weigthedKeywords,
            List<String> enhancerWords, int weight) {

        LOGGER.log(Level.FINEST, "Setting weights.");

        for(String enhancerWord : enhancerWords) {
            WeightedKeyword wk = new WeightedKeyword(enhancerWord,weight);
            weigthedKeywords.add(wk);
        }
    }

    public List<RelevantDocument> executeQuery(List<WeightedKeyword> keywords){

        LOGGER.log(Level.INFO, "Calculating relevant documents for the query: "
                + keywords + ".");

        for (WeightedKeyword keyword : keywords) {
            List<RelevantDocument> relevantDocs = getRelevantDocsOfKeyword(keyword.getKeyword());
            calculator.addDocuments(keyword.getWeight(),relevantDocs);
        }

        return sortRelevantDocs();
    }

}