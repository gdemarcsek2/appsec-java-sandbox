package com.gdemarcsek.appsec.visibility.demo.util;

import com.gdemarcsek.appsec.visibility.demo.core.EntityBase;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.MDC;
import java.util.Optional;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;

public class AuditInterceptor extends EmptyInterceptor {
    public final static int MAX_ENTITY_IDS = 100;

    enum WriteOperation {
        UPDATE, CREATE, DELETE
    }

    private void updateMDC(Object entity, WriteOperation op) {
        Integer oldWriteCount = Integer.parseInt(Optional.ofNullable(MDC.get("entityWriteCount")).orElse("0"));
        MDC.put("entityWriteCount", String.valueOf(oldWriteCount + 1));

        if (oldWriteCount >= MAX_ENTITY_IDS - 1) {
            return;
        }

        String idKeyName = "entityIds";
        switch (op) {
        case UPDATE:
            idKeyName = "changedEntityIds";
            break;
        case CREATE:
            idKeyName = "createdEntityIds";
            break;
        case DELETE:
            idKeyName = "deletedEntityIds";
            break;
        }

        ArrayList<String> ids = new ArrayList<String>();
        String currentList = MDC.get(idKeyName);
        if (currentList != null) {
            ids.addAll(Arrays.asList(currentList.split(",")));
        }
        ids.add(((EntityBase) entity).getId().toString());

        MDC.put(idKeyName, String.join(",", ids));
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
        this.updateMDC(entity, WriteOperation.UPDATE);
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        this.updateMDC(entity, WriteOperation.CREATE);
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        this.updateMDC(entity, WriteOperation.DELETE);
    }
}