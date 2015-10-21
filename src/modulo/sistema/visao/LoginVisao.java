/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.sistema.visao;

import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modulo.administrativo.dao.GrupoDoUsuarioDAO;
import modulo.administrativo.dao.UsuarioDAO;
import modulo.administrativo.negocio.GrupoDoUsuario;
import modulo.administrativo.negocio.UserAccount;
import modulo.sistema.negocio.Auditoria;
import modulo.sistema.negocio.SOptionPane;
import modulo.sistema.negocio.UsuarioLogado;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class LoginVisao extends javax.swing.JFrame {

    /**
     * Creates new form LoginVisao
     */
    public LoginVisao() {
        initComponents();
        this.setIconImage(new ImageIcon(this.getClass().getResource("/publico/imagens/logo.png")).getImage());
        logoImage.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/logo_gesatec.png")));
        botaoLogar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/logar.png")));
        botaoSair.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/sair.png")));
    }

    public boolean validarCampos() {
        try {
            String message = "";

            if (login.getText().isEmpty()) {
                message += "O campo 'Login' deve ser preenchido!\n";
            }

            if (senha.getText().isEmpty()) {
                message += "O campo 'Senha' deve ser preenchido!\n";
            }

            if (!message.isEmpty()) {
                throw new Exception(message);
            }
            
            return true;
            
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
            return false;
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

        jPanel1 = new javax.swing.JPanel();
        logoImage = new javax.swing.JLabel();
        loginLabel = new javax.swing.JLabel();
        login = new javax.swing.JTextField();
        loginLabel1 = new javax.swing.JLabel();
        senha = new javax.swing.JPasswordField();
        botaoLogar = new javax.swing.JButton();
        botaoSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(java.awt.SystemColor.controlLtHighlight);

        logoImage.setBackground(java.awt.SystemColor.controlLtHighlight);
        logoImage.setBorder(null);

        loginLabel.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        loginLabel.setForeground(new java.awt.Color(51, 51, 51));
        loginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginLabel.setText("Login");
        loginLabel.setAlignmentY(0.0F);

        login.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });
        login.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                loginKeyPressed(evt);
            }
        });

        loginLabel1.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        loginLabel1.setForeground(new java.awt.Color(51, 51, 51));
        loginLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginLabel1.setText("Senha");
        loginLabel1.setAlignmentY(0.0F);

        senha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        senha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                senhaKeyPressed(evt);
            }
        });

        botaoLogar.setBackground(new java.awt.Color(0, 139, 139));
        botaoLogar.setText("Logar");
        botaoLogar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoLogarActionPerformed(evt);
            }
        });

        botaoSair.setText("Sair   ");
        botaoSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(login, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(senha)
                    .addComponent(loginLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(logoImage, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(botaoLogar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botaoSair, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(logoImage, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(senha, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoLogar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botaoSair, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loginActionPerformed

    private void botaoLogarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoLogarActionPerformed
        try {
            if (this.validarCampos()) {
                Conjunction and = Restrictions.conjunction();
                and.add(Restrictions.eq("login", login.getText()));
                and.add(Restrictions.eq("password", UserAccount.MD5(senha.getText())));
                and.add(Restrictions.eq("active", true));
                List<Object> findUsuario = UsuarioDAO.getInstance().findByCriteria(new UserAccount(), and, Restrictions.disjunction());

                if ( findUsuario.size() > 0 )
                {
                    // Seta o usuário autenticado para o objeto usuariologado.
                    UserAccount userAccount = (UserAccount) findUsuario.get(0);
                    UsuarioLogado.getInstance().setaUsuarioLogado(userAccount);
                    Auditoria.registra("LOGIN AUTORIZADO");
                    // Obter todos os grupos do usuário, e seta para o objeto usuariologado.
                    Conjunction find = Restrictions.conjunction();
                    find.add(Restrictions.eq("usuario", userAccount));
                    List<Object> gruposDoUsuario = GrupoDoUsuarioDAO.getInstance().findByCriteria(new GrupoDoUsuario(), find, Restrictions.disjunction());
                    UsuarioLogado.getInstance().setGruposDoUsuarioLogado(gruposDoUsuario);                

                    SistemaVisao sistema = new SistemaVisao();
                    sistema.setExtendedState(sistema.MAXIMIZED_BOTH);
                    sistema.setVisible(true);
                    this.setVisible(false);
                }
                else
                {
                    Auditoria.registra("LOGIN NÃO AUTORIZADO ("+ login.getText() +")");
                    throw new Exception("Usuário e(ou) senha inválidos.");
                }
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoLogarActionPerformed

    private void botaoSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoSairActionPerformed
        System.exit(0);
    }//GEN-LAST:event_botaoSairActionPerformed

    private void senhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_senhaKeyPressed
        if ( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
            this.botaoLogarActionPerformed(null);
        }
    }//GEN-LAST:event_senhaKeyPressed

    private void loginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_loginKeyPressed
        if ( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
            this.botaoLogarActionPerformed(null);
        }
    }//GEN-LAST:event_loginKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginVisao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginVisao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginVisao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginVisao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginVisao().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoLogar;
    private javax.swing.JButton botaoSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField login;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JLabel loginLabel1;
    private javax.swing.JLabel logoImage;
    private javax.swing.JPasswordField senha;
    // End of variables declaration//GEN-END:variables
}
