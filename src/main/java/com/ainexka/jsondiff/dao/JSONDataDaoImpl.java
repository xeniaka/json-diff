package com.ainexka.jsondiff.dao;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JSONData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class JSONDataDaoImpl implements JSONDataDao {
    private static final Logger LOG = Logger.getLogger(JSONDataDaoImpl.class);
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public JSONDataDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public JSONData saveOrUpdate(String objectIdentifier, DataPosition position, String json) {
        JSONData data = findByIdentifierAndPosition(objectIdentifier, position);
        if (null == data) {
            data = new JSONData();
        }

        data.setObjectIdentifier(objectIdentifier);
        data.setPosition(position);
        data.setValue(json);
        return entityManager.merge(data);
    }

    @Override
    public List<JSONData> findByIdentifier(String id) {
        Query query = entityManager.createQuery("from JSONData where objectIdentifier = :oid");
        query.setParameter("oid", id);
        return query.getResultList();
    }

    @Override
    public JSONData findByIdentifierAndPosition(String id, DataPosition position) {
        JSONData data = null;
        try {
            Query query = entityManager.createQuery("from JSONData where objectIdentifier = :oid and position = :position");
            query.setParameter("oid", id);
            query.setParameter("position", position);
            data = (JSONData) query.getSingleResult();
        } catch (NoResultException e) {
            LOG.warn(String.format("No record found for identifier '%s', position '%s'", id, position));
        }
        return data;
    }
}