package com.ainexka.jsondiff.service;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JsonData;
import com.ainexka.jsondiff.model.DiffResponse;
import com.ainexka.jsondiff.repository.JsonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.ainexka.jsondiff.entity.DataPosition.LEFT;
import static com.ainexka.jsondiff.entity.DataPosition.RIGHT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiffServiceImplTest {
    @Mock private JsonRepository mockJsonRepository;
    private DiffServiceImpl diffService;

    @Before
    public void createFixtures() {
        diffService = new DiffServiceImpl(mockJsonRepository);
    }

    @Test
    public void whenEqualDataIsComparedThenResponseShouldContainNoDifferences() {
        //given
        String id = "test";
        List<JsonData> data = new ArrayList<>();
        data.add(createData(id, LEFT, "{\"foo\":\"bar\"}"));
        data.add(createData(id, RIGHT, "{\"foo\":\"bar\"}"));
        when(mockJsonRepository.findByIdentifier(anyString())).thenReturn(data);

        //when
        DiffResponse response = diffService.getDiff(id);

        //then
        assertNotNull(response);
        assertTrue(response.isEqual());
        assertTrue(response.isEqualSize());
        assertEquals(0, response.getDiffs().size());
    }

    @Test
    public void givenDataIsDifferentInSizeWhenComparedThenResponseShouldShowDifferenceInSize() {
        //given
        String id = "test";
        List<JsonData> data = new ArrayList<>();
        data.add(createData(id, LEFT, "{\"foo\":\"bar\"}"));
        data.add(createData(id, RIGHT, "{\"foo\":\"barbar\"}"));
        when(mockJsonRepository.findByIdentifier(anyString())).thenReturn(data);

        //when
        DiffResponse response = diffService.getDiff(id);

        //then
        assertNotNull(response);
        assertFalse(response.isEqual());
        assertFalse(response.isEqualSize());
        assertEquals(0, response.getDiffs().size());
    }

    @Test
    public void givenDataIsDifferentAndOfSameSizeWhenTheyAreComparedThenResponseShouldShowDifferences() {
        //given
        String id = "test";
        List<JsonData> data = new ArrayList<>();
        data.add(createData(id, LEFT, "{\"foo\":\"bar\"}"));
        data.add(createData(id, RIGHT, "{\"faa\":\"ber\"}"));
        when(mockJsonRepository.findByIdentifier(anyString())).thenReturn(data);

        //when
        DiffResponse response = diffService.getDiff(id);

        //then
        assertNotNull(response);
        assertFalse(response.isEqual());
        assertTrue(response.isEqualSize());
        assertEquals(2, response.getDiffs().size());
        assertEquals(3, response.getDiffs().get(0).getOffset());
        assertEquals(2, response.getDiffs().get(0).getLength());
        assertEquals(9, response.getDiffs().get(1).getOffset());
        assertEquals(1, response.getDiffs().get(1).getLength());
    }

    @Test
    public void whenLeftDataIsNotPresentThenResponseShouldBeNull() {
        //given
        String id = "test";
        List<JsonData> data = new ArrayList<>();
        data.add(createData(id, RIGHT, "{\"foo\":\"bar\"}"));
        when(mockJsonRepository.findByIdentifier(anyString())).thenReturn(data);

        //when
        DiffResponse response = diffService.getDiff(id);

        //then
        assertNull(response);
    }

    @Test
    public void whenSavingDataThenPersistenceShouldBeInvoked() {
        //given
        String identifier = "test";
        DataPosition position = DataPosition.LEFT;
        String json = "{\"foo\":\"bar\"}";
        JsonData data = new JsonData();
        data.setIdentifier(identifier);
        data.setPosition(position);
        data.setValue(json);

        //when
        ArgumentCaptor<JsonData> captor = ArgumentCaptor.forClass(JsonData.class);
        diffService.save(identifier, position, json);

        //then
        verify(mockJsonRepository).save(captor.capture());
        assertEquals(identifier, captor.getValue().getIdentifier());
        assertEquals(position, captor.getValue().getPosition());
        assertEquals(json, captor.getValue().getValue());
        verifyZeroInteractions(mockJsonRepository);
    }

    private JsonData createData(String objectId, DataPosition position, String value) {
        JsonData data = new JsonData();
        data.setIdentifier(objectId);
        data.setPosition(position);
        data.setValue(value);

        return data;
    }
}