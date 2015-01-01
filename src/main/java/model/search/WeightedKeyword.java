package model.search;

/**
 * Class to represent a keyword with its associated weight.
 */
class WeightedKeyword {

    private final String keyword;
    private final int weight;

    /**
     * Creates an object with a keyword and a weight.
     * @param keyword
     * @param weight
     */
    WeightedKeyword(String keyword, int weight) {
        super();
        this.keyword = keyword;
        this.weight = weight;
    }

    /**
     * Gets the associated keyword.
     * @return the keyword.
     */
    String getKeyword() {
        return keyword;
    }

    /**
     * Gets the associated weight.
     * @return the weight.
     */
    int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return keyword;
    }

}
