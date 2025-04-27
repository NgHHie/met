package ptit.dblab.app.enumerate;

public enum SequenceType {

    USER("user"),
    QUESTION("question");
    private String value;
    SequenceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
