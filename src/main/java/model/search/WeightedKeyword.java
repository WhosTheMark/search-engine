package model.search;

public final class WeightedKeyword {

    private final String keyword;
    private final int weight;

    public WeightedKeyword(String keyword, int weight) {
        super();
        this.keyword = keyword;
        this.weight = weight;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return keyword;
    }

}
