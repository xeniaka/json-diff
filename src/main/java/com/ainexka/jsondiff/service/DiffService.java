package com.ainexka.jsondiff.service;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.model.DiffResponse;

public interface DiffService {

    DiffResponse getDiff(String objectIdentifier);
    void save(String identifier, DataPosition position, String json);
}
