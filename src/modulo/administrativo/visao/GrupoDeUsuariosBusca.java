/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.visao;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.dao.GrupoDeUsuariosDAO;
import modulo.administrativo.negocio.GrupoDeUsuarios;

/**
 *
 * @author augusto
 */
public class GrupoDeUsuariosBusca extends javax.swing.JInternalFrame {

    public static GrupoDeUsuariosFormulario form;
    
    /**
     * Creates new form ModeloBusca
     */
    public GrupoDeUsuariosBusca() {
        initComponents();
        
        setTitle("Grupo de usuários");
        this.setBorder(null);
        tabela.setSelectionBackground(new java.awt.Color(22, 160, 133));

        botaoNovo.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/novo.png")));
        botaoEditar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/editar.png")));
        botaoExcluir.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/excluir.png")));
        botaoAtualizar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/atualizar.png")));
        botaoBuscar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/buscar.png")));
        
        this.atualizarGrid(-1);
        
        tabela.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if(tabela.getSelectedRow() > tabela.getRowCount()){
                    botaoEditar.setEnabled(false);
                    botaoExcluir.setEnabled(false);
                }
                else{
                    botaoExcluir.setEnabled(true);
                    botaoEditar.setEnabled(true);
                }
            }
        });
    }
    
    /**
     * Recebe o id do registro que deverá ser selecionado automáticamente.
     * Se receber -1, não selecionará registro algum.
     * 
     * @param selecionar 
     */
    public final void atualizarGrid(int selecionar) {
        try {
            
            List<Object> grupoDeUsuarios = GrupoDeUsuariosDAO.getInstance().findAll(new GrupoDeUsuarios());
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.setNumRows(0);
            
            for ( int i = 0; i < grupoDeUsuarios.size(); i ++ ) {
                GrupoDeUsuarios gruposDeUsuarios = (GrupoDeUsuarios) grupoDeUsuarios.get(i);
                modelo.addRow(new Object[]{gruposDeUsuarios.getId(), gruposDeUsuarios.getNome()});
                
                // Verifica item a selecionar
                if ( gruposDeUsuarios.getId() == selecionar )
                {
                    tabela.addRowSelectionInterval(i, i);
                }
            }
            
            if ( selecionar == -1 )
            {
                botaoEditar.setEnabled(false);
                botaoExcluir.setEnabled(false);
            }
            
        } catch (Exception err) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar grid: " + err.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        botaoNovo = new javax.swing.JButton();
        botaoEditar = new javax.swing.JButton();
        botaoExcluir = new javax.swing.JButton();
        botaoAtualizar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        campoBusca = new javax.swing.JTextField();
        botaoBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setClosable(true);
        setTitle("Nome da tela");

        toolbar.setRollover(true);

        botaoNovo.setText("Novo");
        botaoNovo.setFocusable(false);
        botaoNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoNovoActionPerformed(evt);
            }
        });
        toolbar.add(botaoNovo);

        botaoEditar.setText("Editar");
        botaoEditar.setFocusable(false);
        botaoEditar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoEditar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoEditarActionPerformed(evt);
            }
        });
        toolbar.add(botaoEditar);

        botaoExcluir.setText("Excluir");
        botaoExcluir.setFocusable(false);
        botaoExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoExcluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoExcluirActionPerformed(evt);
            }
        });
        toolbar.add(botaoExcluir);

        botaoAtualizar.setText("Atualizar");
        botaoAtualizar.setFocusable(false);
        botaoAtualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoAtualizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoAtualizarActionPerformed(evt);
            }
        });
        toolbar.add(botaoAtualizar);

        jPanel1.setBackground(java.awt.SystemColor.controlLtHighlight);

        tabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabela);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(campoBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botaoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botaoBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(campoBusca, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoNovoActionPerformed
        form = new GrupoDeUsuariosFormulario(this, true);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }//GEN-LAST:event_botaoNovoActionPerformed

    private void botaoAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAtualizarActionPerformed
        this.atualizarGrid(-1);
    }//GEN-LAST:event_botaoAtualizarActionPerformed

    private void botaoEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoEditarActionPerformed
        int selected = tabela.getSelectedRow();
        Object registro = tabela.getValueAt(selected, 0);
        int grupodeusuarios_id = Integer.parseInt(registro.toString());
        
        Object grupoDeUsuarios = GrupoDeUsuariosDAO.getInstance().getById(new GrupoDeUsuarios(), grupodeusuarios_id);
        
        form = new GrupoDeUsuariosFormulario(this, true);
        form.popularCampos((GrupoDeUsuarios) grupoDeUsuarios);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }//GEN-LAST:event_botaoEditarActionPerformed

    private void botaoExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoExcluirActionPerformed
        int selected = tabela.getSelectedRow();
        Object registro = tabela.getValueAt(selected, 0);
        int grupodeusuarios_id = Integer.parseInt(registro.toString());
        
        int escolha = JOptionPane.showConfirmDialog(null, "Você têm certeza que deseja excluir este registro?", "Atenção!", JOptionPane.YES_NO_OPTION);
            
        if ( escolha == JOptionPane.YES_OPTION ) 
        {
            GrupoDeUsuariosDAO.getInstance().removeById(new GrupoDeUsuarios(), grupodeusuarios_id);
            this.atualizarGrid(-1);
            JOptionPane.showMessageDialog(this, "Registro excluído com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_botaoExcluirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoAtualizar;
    private javax.swing.JButton botaoBuscar;
    private javax.swing.JButton botaoEditar;
    private javax.swing.JButton botaoExcluir;
    private javax.swing.JButton botaoNovo;
    private javax.swing.JTextField campoBusca;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabela;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
