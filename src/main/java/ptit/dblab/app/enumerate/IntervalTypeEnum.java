package ptit.dblab.app.enumerate;

import lombok.Getter;

@Getter
public enum IntervalTypeEnum {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    private final String value;

    IntervalTypeEnum(String value) {
        this.value = value;
    }
}
