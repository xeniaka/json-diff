package com.ainexka.jsondiff.service;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JsonData;
import com.ainexka.jsondiff.exception.InvalidJsonException;
import com.ainexka.jsondiff.model.Diff;
import com.ainexka.jsondiff.model.DiffResponse;
import com.ainexka.jsondiff.repository.JsonRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiffServiceImpl implements DiffService {
    private JsonRepository repository;

    @Autowired
    public DiffServiceImpl(JsonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String identifier, DataPosition position, String json) {
        log.debug(String.format("Saving JSON data: id=%s, position=%s", identifier, position));
        JsonData data = repository.findByIdentifierAndPosition(identifier, position);
        if (null == data) {
            data = new JsonData();
            data.setIdentifier(identifier);
            data.setPosition(position);
        }
        data.setValue(json);

        repository.save(data);
    }

    @Override
    public DiffResponse getDiff(String identifier) {
        log.debug(String.format("Diff for identifier=%s", identifier));
        DiffResponse response = null;

        List<JsonData> data = repository.findByIdentifier(identifier);

        if (data.size() == 2) {
            response = new DiffResponse();
            evaluateContent(response, data);
            evaluateSize(response, data);
            evaluateInsight(response, data);
        }

        return response;
    }

    private void evaluateContent(DiffResponse diffResponse, List<JsonData> data) {
        Map<String, String> map1 = toMap(data.get(0).getValue());
        Map<String, String> map2 = toMap(data.get(1).getValue());

        diffResponse.setEqual(map1.equals(map2));
    }

    private void evaluateSize(DiffResponse diffResponse, List<JsonData> data) {
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

    private void evaluateInsight(DiffResponse diffResponse, List<JsonData> data) {
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
            log.error(String.format("JSON string '%s' could not be parsed.", json));
            throw new InvalidJsonException();
        }
    }

    private List<Diff> insights(String left, String right) {
        int diffOffset = 0;
        int diffLength = 0;
        Map<Integer, Diff> diffs = new HashMap<>();

        for (int index = 0; index < left.length(); index++) {
            char leftChar = left.charAt(index);
            char rightChar = right.charAt(index);
            if (shouldResetLength(diffLength, leftChar, rightChar)) {
                diffs.put(diffOffset, new Diff(diffOffset, diffLength));
                diffLength = 0;
            }
            if (leftChar != rightChar) {
                if (diffLength == 0) {
                    diffOffset = index;
                }
                diffLength++;
            }
            if (diffLength > 0) {
                diffs.put(diffOffset, new Diff(diffOffset, diffLength));
            }
        }

        return diffs.values().stream()
                .collect(Collectors.toList());
    }

    private boolean shouldResetLength(int diffLength, char leftChar, char rightChar) {
        return leftChar == rightChar && diffLength > 0;
    }
}
