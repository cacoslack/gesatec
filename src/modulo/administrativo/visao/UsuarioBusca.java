/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.visao;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.dao.UsuarioDAO;
import modulo.administrativo.negocio.Usuario;
import modulo.sistema.visao.Busca;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class UsuarioBusca extends Busca {

    public static UsuarioFormulario form;
    
    /**
     * Creates new form UsuarioBusca
     */
    public UsuarioBusca() {
        super.initComponents();
    }
    
    @Override
    public DefaultTableModel construirGrid() {
        
        DefaultTableModel defaultTableModel = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
                
            // Colunas
            new String [] {
                "ID", 
                "Pessoa", 
                "Login", 
                "Ativo"
            }
        ) {
            // Tipos
            Class[] types = new Class [] {
                java.lang.Integer.class, 
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class
            };
            
            // Podem ser editados
            boolean[] canEdit = new boolean [] {
                false, 
                false,
                false,
                false
            };
        };
        
        return defaultTableModel;
    }

    @Override
    public void atualizarGrid(int selecionar, List<Object> registros) {
        try {           
            
            if ( registros.isEmpty() )
            {
                registros = UsuarioDAO.getInstance().findAll(new Usuario());
            }
            
            DefaultTableModel modelo = (DefaultTableModel) getTabela().getModel();
            modelo.setNumRows(0);
            
            for ( int i = 0; i < registros.size(); i ++ ) {                
                Usuario usuario = (Usuario) registros.get(i);
                modelo.addRow(new Object[]{
                    usuario.getId(), 
                    "",//usuario.getNome(),
                    usuario.getLogin(),
                    usuario.isAtivo() ? "SIM" : "NÃO"
                });
                
                // Verifica item a selecionar
                if ( usuario.getId() == selecionar )
                {
                    getTabela().addRowSelectionInterval(i, i);
                }
            }
            
            if ( selecionar == -1 )
            {
                getBotaoEditar().setEnabled(false);
                getBotaoExcluir().setEnabled(false);
            }
            
        } catch (Exception err) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar grid: " + err.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoNovoActionPerformed(ActionEvent evt) {
        form = new UsuarioFormulario(this, true);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    @Override
    public void botaoEditarActionPerformed(ActionEvent evt) {
        int selected = getTabela().getSelectedRow();
        Object registro = getTabela().getValueAt(selected, 0);
        int usuario_id = Integer.parseInt(registro.toString());
        
        Object usuario = UsuarioDAO.getInstance().getById(new Usuario(), usuario_id);
        
        form = new UsuarioFormulario(this, true);
        form.popularCampos((Usuario) usuario);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    @Override
    public void botaoExcluirActionPerformed(ActionEvent evt) {
        int selected = getTabela().getSelectedRow();
        Object registro = getTabela().getValueAt(selected, 0);
        int grupodeusuarios_id = Integer.parseInt(registro.toString());
        
        int escolha = JOptionPane.showConfirmDialog(null, "Você têm certeza que deseja excluir este registro?", "Atenção!", JOptionPane.YES_NO_OPTION);
            
        if ( escolha == JOptionPane.YES_OPTION ) 
        {
            Usuario usuario = new Usuario();
            usuario.setId(grupodeusuarios_id);
            UsuarioDAO.getInstance().remove(usuario);
            
            this.atualizarGrid(-1, new ArrayList());
            JOptionPane.showMessageDialog(this, "Registro excluído com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void botaoBuscarActionPerformed(ActionEvent evt) {
        String busca = getCampoBusca().getText();
        
        Disjunction or = Restrictions.disjunction();
        or.add(Restrictions.ilike("login", busca, MatchMode.ANYWHERE));
        //or.add(Restrictions.ilike("ativo", busca, MatchMode.ANYWHERE));
        
        try {
            or.add(Restrictions.eq("id", Integer.parseInt(busca)));
        } catch (Exception err) {
        }
        
        List<Object> grupos = UsuarioDAO.getInstance().findByCriteria(new Usuario(), Restrictions.conjunction(), or);
        this.atualizarGrid(-1, grupos);
    }
}