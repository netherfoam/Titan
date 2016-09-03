package org.maxgamer.rs.structure.sql;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author netherfoam
 */
public class HibernateJPAListener extends EmptyInterceptor {
    private static final Class[] HOOK_TYPES = {
            PostLoad.class,
            PrePersist.class,
            PreRemove.class,
            PreUpdate.class,
    };

    private HashMap<Class<?>, List<Method>> hooks = new HashMap<Class<?>, List<Method>>();

    private List<Method> getHooks(Class<?> clazz) {
        List<Method> hooks = this.hooks.get(clazz);
        if (hooks == null) {
            hooks = new ArrayList<Method>(1);
            Class<?> type = clazz;

            while (type != Object.class) {
                for (Method m : type.getDeclaredMethods()) {
                    //PostLoad hook = m.getAnnotation(PostLoad.class);
                    for (Class<?> annotationType : HOOK_TYPES) {
                        Object hook = m.getAnnotation((Class<? extends Annotation>) annotationType);
                        if (hook == null) continue;
                        hooks.add(m);
                        break;
                    }
                }
                type = type.getSuperclass();
            }

            this.hooks.put(clazz, hooks);
        }

        return hooks;
    }

    private void hook(Object entity, Class<? extends Annotation> type) {
        List<Method> hooks = getHooks(entity.getClass());

        for (Method m : hooks) {
            if (m.getAnnotation(type) == null) continue;
            try {
                m.invoke(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        hook(entity, PostLoad.class);

        return super.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        // Technically not always right: Save may be done on something already persisted
        hook(entity, PrePersist.class);
        hook(entity, PreUpdate.class);

        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        hook(entity, PreRemove.class);
    }

   /* @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {

    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {

    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {

    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {

    }

    @Override
    public void preFlush(Iterator entities) throws CallbackException {

    }

    @Override
    public void postFlush(Iterator entities) throws CallbackException {

    }

    @Override
    public Boolean isTransient(Object entity) {
        return null;
    }

    @Override
    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return new int[0];
    }

    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
        return null;
    }

    @Override
    public String getEntityName(Object object) throws CallbackException {
        return null;
    }

    @Override
    public Object getEntity(String entityName, Serializable id) throws CallbackException {
        return null;
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {

    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {

    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {

    }

    @Override
    public String onPrepareStatement(String sql) {
        return null;
    }*/
}
