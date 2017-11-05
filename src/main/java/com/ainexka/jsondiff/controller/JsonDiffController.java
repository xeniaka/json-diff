package com.ainexka.jsondiff.controller;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.model.DiffResponse;
import com.ainexka.jsondiff.service.DiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/json-diff")
public class JsonDiffController {

    private final DiffService diffService;

    @Autowired
    public JsonDiffController(DiffService diffService) {
        this.diffService = diffService;
    }

    @PostMapping(value = "/{id}/left", consumes = "application/base64")
    public void left(@RequestBody String body, @PathVariable String id) {
        diffService.save(id, DataPosition.LEFT, body);
    }

    @PostMapping(value = "/{id}/right", consumes = "application/base64")
    public void right(@RequestBody String body, @PathVariable String id) {
        diffService.save(id, DataPosition.RIGHT, body);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DiffResponse diff(@PathVariable String id) {
        return diffService.getDiff(id);
    }
}
