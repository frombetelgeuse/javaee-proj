/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author danya
 */
@RunWith(Parameterized.class)
public abstract class DaoTest<Context> {
    
    protected Class daoClass;
    protected Identified notPersistedDto;
    
    public abstract Dao dao();
    public abstract Context context();
    
    public DaoTest(Class clazz, Identified notPersistedDto) {
        this.daoClass = clazz;
        this.notPersistedDto = notPersistedDto;
    }

    @Test
    public void testCreate() throws Exception {
        Identified dto = dao().create();
        
        assertNotNull(dto);
        assertNotNull(dto.getId());
    }

    @Test
    public void testPersist() throws Exception {
        assertNull("Id before persist is not null.", notPersistedDto.getId());
        notPersistedDto = dao().persist(notPersistedDto);
        assertNotNull("After persist id is null", notPersistedDto.getId());
    }

    @Test
    public void testGetEntityById() throws Exception {
        Integer id = dao().create().getId();
        Identified dto = dao().getEntityById(id);
        assertNotNull(dto);
    }

//    @Test
//    public void testUpdate() throws Exception {
//    }

    @Test
    public void testDelete() throws Exception {
        Identified dto = dao().create();
        assertNotNull(dto);
        
        List<Identified> list = dao().getAll();
        Assert.assertNotNull(list);

        int oldSize = list.size();
        Assert.assertTrue(oldSize > 0);

        dao().delete(dto);

        list = dao().getAll();
        assertNotNull(list);

        int newSize = list.size();
        assertEquals(1, oldSize - newSize);
    }

    @Test
    public void testGetAll() throws Exception {
        dao().create();
        List<Identified> list = dao().getAll();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }    
}
