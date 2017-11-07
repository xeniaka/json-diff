package com.ainexka.jsondiff.service;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JsonData;
import com.ainexka.jsondiff.exception.InvalidJsonException;
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
    public void whenEqualDataIsCompared_thenResponseShouldContainNoDifferences() {
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
    public void givenDataIsDifferentInSize_whenCompared_thenResponseShouldShowDifferenceInSize() {
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
    public void givenDataIsDifferentAndOfSameSize_whenTheyAreCompared_thenResponseShouldShowDifferences() {
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
    public void whenLeftDataIsNotPresent_thenResponseShouldBeNull() {
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
    public void whenSavingData_thenPersistenceShouldBeInvoked() {
        //given
        String identifier = "test";
        DataPosition position = DataPosition.LEFT;
        String json = "{\"foo\":\"bar\"}";

        //when
        ArgumentCaptor<JsonData> captor = ArgumentCaptor.forClass(JsonData.class);
        diffService.save(identifier, position, json);

        //then
        verify(mockJsonRepository).save(captor.capture());
        assertEquals(identifier, captor.getValue().getIdentifier());
        assertEquals(position, captor.getValue().getPosition());
        assertEquals(json, captor.getValue().getValue());
    }

    @Test
    public void whenSavingExistentData_thenPersistenceShouldBeInvoked() {
        //given
        String identifier = "test";
        DataPosition position = DataPosition.LEFT;
        String json = "{\"foo\":\"bar\"}";
        JsonData data = createData(identifier, position, json);
        when(mockJsonRepository.findByIdentifierAndPosition(identifier, position)).thenReturn(data);

        //when
        ArgumentCaptor<JsonData> captor = ArgumentCaptor.forClass(JsonData.class);
        diffService.save(identifier, position, json);

        //then
        verify(mockJsonRepository).save(captor.capture());
        assertEquals(identifier, captor.getValue().getIdentifier());
        assertEquals(position, captor.getValue().getPosition());
        assertEquals(json, captor.getValue().getValue());
    }

    @Test(expected = InvalidJsonException.class)
    public void whenJsonIsInvalid_thenThrowException() {
        //given
        String identifier = "test";
        String invalidJson = "{foo:bar}";
        List<JsonData> data = new ArrayList<>();
        data.add(createData(identifier, LEFT, invalidJson));
        data.add(createData(identifier, RIGHT, invalidJson));
        when(mockJsonRepository.findByIdentifier(anyString())).thenReturn(data);

        //when
        diffService.getDiff(identifier);
    }

    private JsonData createData(String objectId, DataPosition position, String value) {
        JsonData data = new JsonData();
        data.setIdentifier(objectId);
        data.setPosition(position);
        data.setValue(value);

        return data;
    }
}