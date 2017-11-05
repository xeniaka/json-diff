package com.ainexka.jsondiff.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Insight {
    private int offset;
    private int length;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Insight insight = (Insight) o;

        return offset == insight.offset;
    }

    @Override
    public int hashCode() {
        return offset;
    }
}
