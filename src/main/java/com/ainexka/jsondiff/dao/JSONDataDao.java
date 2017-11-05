package com.ainexka.jsondiff.dao;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JSONData;

import java.util.List;

public interface JSONDataDao {

    JSONData saveOrUpdate(String objectIdentifier, DataPosition position, String jsonData);
    List<JSONData> findByIdentifier(String id);
    JSONData findByIdentifierAndPosition(String id, DataPosition position);
}
