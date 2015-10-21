/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.sistema.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;
import modulo.sistema.negocio.Auditoria;
import modulo.sistema.negocio.SOptionPane;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;

public class DAO {

    private static DAO instance;
    protected EntityManager entityManager;

    public DAO() {
        entityManager = getEntityManager();
    }

    public static DAO getInstance() {
        if (instance == null) {
            instance = new DAO();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("crudHibernatePU");

        if (entityManager == null) {
            entityManager = factory.createEntityManager();
        }

        return entityManager;
    }

    public Object getById(Object object, final int id) {
        return entityManager.find(object.getClass(), id);
    }

    @SuppressWarnings("unchecked")
    public List<Object> findAll(Object object) {
        return entityManager.createQuery("FROM " + object.getClass().getName()).getResultList();
    }
    
    /**
     * Conjunction, para conjuntos AND, ex.: WHERE id = ? AND nome = ?
     * Disjunction, para conjuntos OR, ex.: WHERE id = ? OR nome = ?
     * 
     * @param object
     * @param and
     * @param or
     * @return 
     */
    public List<Object> findByCriteria(Object object, Conjunction and, Disjunction or) {
        Session session = this.getEntityManager().unwrap(Session.class);
        Criteria criteria = session.createCriteria(object.getClass().getName());
        criteria.add(and);
        criteria.add(or);
        
        return criteria.list();
    }

    public void persist(Object object) {
        try {
            int id = (int) object.getClass().getMethod("getId").invoke(object);
            entityManager.getTransaction().begin();
            entityManager.persist(object);
            entityManager.flush();
            entityManager.getTransaction().commit();
            int id2 = (int) object.getClass().getMethod("getId").invoke(object);
            String className = object.getClass().toString();
            className = className.substring(className.lastIndexOf(".") + 1);
            Auditoria.registra( (id==0?"INSERT":"UPDATE") + " on " + className +  " (id = " + id2+")");
        } catch (Exception ex) {
            ex.printStackTrace();
            entityManager.getTransaction().rollback();
            SOptionPane.showMessageDialog(null, ex, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void merge(Object object) {
        try {
            int id = (int) object.getClass().getMethod("getId").invoke(object);
            entityManager.getTransaction().begin();
            entityManager.merge(object);
            entityManager.flush();
            entityManager.getTransaction().commit();
            int id2 = (int) object.getClass().getMethod("getId").invoke(object);
            String className = object.getClass().toString();
            className = className.substring(className.lastIndexOf(".") + 1);
            Auditoria.registra( (id==0?"INSERT":"UPDATE") + " on " + className +  " (id = " + id2+")");
        } catch (Exception ex) {
            ex.printStackTrace();
            entityManager.getTransaction().rollback();
            SOptionPane.showMessageDialog(null, ex, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void remove(Object object) {
        try {
            entityManager.getTransaction().begin();
            object = entityManager.find(object.getClass(), object.getClass().getMethod("getId").invoke(object));
            entityManager.remove(object);
            entityManager.getTransaction().commit();
            int id = (int) object.getClass().getMethod("getId").invoke(object);
            String className = object.getClass().toString();
            className = className.substring(className.lastIndexOf(".") + 1);
            Auditoria.registra("REMOVE" + " on " + className +  " (id = " + id+")");
        } catch (Exception ex) {
            ex.printStackTrace();
            entityManager.getTransaction().rollback();
            SOptionPane.showMessageDialog(null, ex, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeById(Object object, final int id) {
        try {
            Object objectRm = getById(object, id);
            remove(objectRm);
            Auditoria.registra("INSERT/UPDATE on " + object.getClass().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            SOptionPane.showMessageDialog(null, ex, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
