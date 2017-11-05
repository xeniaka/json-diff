package com.ainexka.jsondiff.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DiffResponse {
    private boolean equal;
    private boolean equalSize;
    private List<Diff> diffs = new ArrayList<>();
}