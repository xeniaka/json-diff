package com.ainexka.jsondiff.repository;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JsonData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JsonRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JsonRepository repository;

    @Test
    public void whenFindByIdentifier_thenReturnJsonData() {
        // given
        persistData("test", DataPosition.RIGHT, "value");
        persistData("test", DataPosition.LEFT, "value");
        persistData("non-test", DataPosition.LEFT, "value");

        // when
        List<JsonData> result = repository.findByIdentifier("test");

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getIdentifier());
        assertEquals("test", result.get(1).getIdentifier());
        assertEquals(DataPosition.RIGHT, result.get(0).getPosition());
        assertEquals(DataPosition.LEFT, result.get(1).getPosition());
        assertEquals("value", result.get(0).getValue());
        assertEquals("value", result.get(1).getValue());
    }

    @Test
    public void givenNoMatch_whenFindByIdentifier_thenReturnEmptyList() {
        // given
        persistData("test", DataPosition.RIGHT, "value");
        persistData("test", DataPosition.LEFT, "value");

        // when
        List<JsonData> result = repository.findByIdentifier("non-existent");

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    public void persistData(String identifier, DataPosition position, String value) {
        JsonData data = new JsonData();
        data.setIdentifier(identifier);
        data.setPosition(position);
        data.setValue(value);

        entityManager.persist(data);
        entityManager.flush();
    }
}
