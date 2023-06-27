package ftn.socialnetwork.model.entity;

public enum ReactionType {
    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    HEART("HEART");

    private final String value;

    ReactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}