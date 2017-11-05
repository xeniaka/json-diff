package com.ainexka.jsondiff.dao;

import com.ainexka.jsondiff.config.PersistenceConfig;
import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JSONData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = PersistenceConfig.class)
public class JSONDataDaoImplTest {

    @PersistenceContext
    private EntityManager entityManager;
    private JSONDataDaoImpl jsonDataDao;

    @Before
    public void createFixtures() {
        jsonDataDao = new JSONDataDaoImpl(entityManager);
    }

    @Test
    public void whenJSONDataIsInsertedThenDataShouldBePersisted() {
        // given
        String objectIdentifier = "test";
        DataPosition position = DataPosition.LEFT;
        String value = "{value}";

        //when
        JSONData result = jsonDataDao.saveOrUpdate(objectIdentifier, position, value);

        //then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(objectIdentifier, result.getObjectIdentifier());
        assertEquals(position, result.getPosition());
        assertEquals(value, result.getValue());
    }

    @Test(expected = PersistenceException.class)
    public void givenMissingPositionWhenJSONDataIsInsertedThenDataShouldNotBePersisted() {
        // given
        String objectIdentifier = "test";
        String value = "{value}";

        //when
        jsonDataDao.saveOrUpdate(objectIdentifier, null, value);
    }

    @Test
    public void whenFindingByObjectIdentifierThenExpectedResultsShouldBeReturned() {
        //given
        jsonDataDao.saveOrUpdate("test", DataPosition.LEFT, "{value1}");
        jsonDataDao.saveOrUpdate("test", DataPosition.RIGHT, "{value2}");
        jsonDataDao.saveOrUpdate("anotherTest", DataPosition.RIGHT, "{value2}");

        //when
        List<JSONData> result = jsonDataDao.findByIdentifier("test");

        //then
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getId());
        assertNotNull(result.get(1).getId());
    }

    @Test
    public void whenFindingByObjectIdentifierAndPositionThenExpectedResultsShouldBeReturned() {
        //given
        jsonDataDao.saveOrUpdate("test", DataPosition.LEFT, "{value1}");
        jsonDataDao.saveOrUpdate("test", DataPosition.RIGHT, "{value2}");
        jsonDataDao.saveOrUpdate("anotherTest", DataPosition.RIGHT, "{value2}");

        //when
        JSONData result = jsonDataDao.findByIdentifierAndPosition("test", DataPosition.RIGHT);

        //then
        assertNotNull(result);
        assertEquals("test", result.getObjectIdentifier());
        assertEquals(DataPosition.RIGHT, result.getPosition());
    }
}
