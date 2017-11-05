package com.ainexka.jsondiff.service;

import com.ainexka.jsondiff.dao.JSONDataDao;
import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JSONData;
import com.ainexka.jsondiff.exception.InvalidJsonException;
import com.ainexka.jsondiff.model.DiffResponse;
import com.ainexka.jsondiff.model.Insight;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiffServiceImpl implements DiffService {
    private final static Logger LOG = Logger.getLogger(DiffServiceImpl.class);

    private JSONDataDao jsonDataDao;

    @Autowired
    public DiffServiceImpl(JSONDataDao jsonDataDao) {
        this.jsonDataDao = jsonDataDao;
    }

    public void save(String identifier, DataPosition position, String json) {
        jsonDataDao.saveOrUpdate(identifier, position, json);
    }

    public DiffResponse getDiff(String objectIdentifier) {
        DiffResponse response = null;

        List<JSONData> data = jsonDataDao.findByIdentifier(objectIdentifier);

        if (data.size() == 2) {
            response = new DiffResponse();
            evaluateContent(response, data);
            evaluateSize(response, data);
            evaluateInsight(response, data);
        }

        return response;
    }

    private void evaluateContent(DiffResponse diffResponse, List<JSONData> data) {
        Map<String, String> map1 = toMap(data.get(0).getValue());
        Map<String, String> map2 = toMap(data.get(1).getValue());

        diffResponse.setEqual(map1.equals(map2));
    }

    private void evaluateSize(DiffResponse diffResponse, List<JSONData> data) {
        boolean equalSize = true;
        Map<String, String> map1 = toMap(data.get(0).getValue());
        Map<String, String> map2 = toMap(data.get(1).getValue());

        for (String value1 : map1.values()) {
            for (String value2 : map2.values()) {
                if (value1.length() != value2.length()) {
                    equalSize = false;
                    break;
                }
            }
        }
        diffResponse.setEqualSize(equalSize);
    }

    private void evaluateInsight(DiffResponse diffResponse, List<JSONData> data) {
        if (!diffResponse.isEqual() && diffResponse.isEqualSize()) {
            diffResponse.setDiffs(insights(data.get(0).getValue(), data.get(1).getValue()));
        }
    }

    private Map<String, String> toMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            LOG.error(String.format("JSON string '%s' could not be parsed.", json));
            throw new InvalidJsonException();
        }
    }

    private static List<Insight> insights(String left, String right) {
        int offset = 0;
        int length = 0;
        Map<Integer, Insight> diffs = new HashMap<>();

        for (int index = 0; index < left.length(); index++) {
            char leftChar = left.charAt(index);
            char rightChar = right.charAt(index);
            if (leftChar == rightChar && length > 0) {
                diffs.put(offset, new Insight(offset, length));
                length = 0;
            }
            if (leftChar != rightChar) {
                if (length == 0) {
                    offset = index;
                }
                length++;
            }
            if (length > 0) {
                diffs.put(offset, new Insight(offset, length));
            }
        }

        return diffs.values().stream()
                .collect(Collectors.toList());
    }
}