/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.processo.dao;

import java.sql.Date;
import java.util.List;
import modulo.sistema.dao.DAO;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author augusto
 */
public class AgendamentoDAO extends DAO {
    
    public List<Object> obterHorariosParaAgendamento(Date data) {
        Session session = super.getEntityManager().unwrap(Session.class);
        Query query = session.createSQLQuery("SELECT * FROM obterHorariosParaAgendamento(:data)").setParameter("data", data);
        return query.list();
    }
    
}
