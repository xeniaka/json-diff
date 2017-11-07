package com.ainexka.jsondiff.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Diff {
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

        Diff diff = (Diff) o;

        if (offset != diff.offset) {
            return false;
        }
        return length == diff.length;
    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + length;
        return result;
    }
}
