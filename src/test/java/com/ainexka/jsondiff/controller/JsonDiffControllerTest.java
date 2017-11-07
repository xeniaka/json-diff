package com.ainexka.jsondiff.controller;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.model.DiffResponse;
import com.ainexka.jsondiff.model.Diff;
import com.ainexka.jsondiff.service.DiffServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class JsonDiffControllerTest {

    private MockMvc mockMvc;
    @Mock private DiffServiceImpl diffService;
    @InjectMocks private JsonDiffController controller;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    public void givenValidJson_whenLeftEndpointIsCalled_thenResponseSuccessful() throws Exception {
        //given
        String identifier = "test";
        DataPosition position = DataPosition.LEFT;
        String json = "{\"foo\":\"bar\"}";

        String url = String.format("/json-diff/%s/left", identifier);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType("application/base64")
                .content(json))
                .andExpect(status().isOk());

        //then
        verify(diffService).save(identifier, position, json);
        verifyNoMoreInteractions(diffService);
    }

    @Test
    public void givenInvalidJson_whenLeftEndpointIsCalled_thenResponseSuccessful() throws Exception {
        //given
        String identifier = "test";
        String json = "{\"foo\":\"bar\"}";

        String url = String.format("/json-diff/%s/left", identifier);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_ATOM_XML)
                .content(json))
                .andExpect(status().isUnsupportedMediaType());

        //then
        verifyZeroInteractions(diffService);
    }

    @Test
    public void givenValidJson_whenRightEndpointIsCalled_thenResponseSuccessful() throws Exception {
        //given
        String identifier = "test";
        DataPosition position = DataPosition.RIGHT;
        String json = "{\"foo\":\"bar\"}";

        String url = String.format("/json-diff/%s/right", identifier);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType("application/base64")
                .content(json))
                .andExpect(status().isOk());

        //then
        verify(diffService).save(identifier, position, json);
        verifyNoMoreInteractions(diffService);
    }

    @Test
    public void givenInvalidJson_whenRightEndpointIsCalled_thenResponseSuccessful() throws Exception {
        //given
        String identifier = "test";
        String json = "{\"foo\":\"bar\"}";

        String url = String.format("/json-diff/%s/right", identifier);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_ATOM_XML)
                .content(json))
                .andExpect(status().isUnsupportedMediaType());

        //then
        verifyZeroInteractions(diffService);
    }

    @Test
    public void whenDiffIsCalled_thenSetResponseAsExpected() throws Exception {
        //given
        String identifier = "test";
        DiffResponse expectedResponse = new DiffResponse();
        String url = String.format("/json-diff/%s", identifier);

        //when
        when(diffService.getDiff(identifier)).thenReturn(expectedResponse);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.equal", is(false)))
                .andExpect(jsonPath("$.equalSize", is(false)))
                .andExpect(jsonPath("$.diffs", hasSize(0)));

        //then
        verify(diffService).getDiff(identifier);
        verifyZeroInteractions(diffService);
    }

    @Test
    public void givenDifferentObjectWithSameSize_whenDiffIsCalled_thenSetResponseSetAsExpected() throws Exception {
        //given
        String identifier = "test";
        DiffResponse expectedResponse = new DiffResponse();
        expectedResponse.setEqualSize(true);
        expectedResponse.setEqual(false);
        expectedResponse.setDiffs(Arrays.asList(new Diff(11, 22)));
        String url = String.format("/json-diff/%s", identifier);

        //when
        when(diffService.getDiff(identifier)).thenReturn(expectedResponse);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.equal", is(false)))
                .andExpect(jsonPath("$.equalSize", is(true)))
                .andExpect(jsonPath("$.diffs", hasSize(1)))
                .andExpect(jsonPath("$.diffs[0].offset", is(11)))
                .andExpect(jsonPath("$.diffs[0].length", is(22)));

        //then
        verify(diffService).getDiff(identifier);
        verifyZeroInteractions(diffService);
    }
}
