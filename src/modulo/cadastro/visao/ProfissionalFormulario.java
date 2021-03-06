/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.cadastro.visao;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import modulo.administrativo.negocio.UserAccount;
import modulo.cadastro.dao.CertificacaoDoProfissionalDAO;
import modulo.cadastro.dao.ProfissionalDAO;
import modulo.cadastro.dao.CidadeDAO;
import modulo.cadastro.dao.EspecializacaoDoProfissionalDAO;
import modulo.cadastro.dao.EstadoDAO;
import modulo.cadastro.dao.PadraoDeAtendimentoDoProfissionalDAO;
import modulo.cadastro.dao.TipoDeAtendimentoDoProfissionalDAO;
import modulo.cadastro.negocio.Certificacao;
import modulo.cadastro.negocio.CertificacaoDoProfissional;
import modulo.cadastro.negocio.Profissional;
import modulo.cadastro.negocio.Cidade;
import modulo.cadastro.negocio.Especializacao;
import modulo.cadastro.negocio.EspecializacaoDoProfissional;
import modulo.cadastro.negocio.Estado;
import modulo.cadastro.negocio.PadraoDeAtendimentoDoProfissional;
import modulo.cadastro.negocio.TipoDeAtendimentoDoProfissional;
import modulo.configuracao.negocio.PadraoDeAtendimento;
import modulo.configuracao.negocio.TipoDeAtendimento;
import modulo.sistema.dao.DAO;
import modulo.sistema.negocio.SOptionPane;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class ProfissionalFormulario extends javax.swing.JDialog {

    public static ProfissionalBusca parent;
    private String message;
    List<Object> especializacoesDoProfissional;
    List<Object> certificacoesDoProfissional;
    List<Object> padroesDeAtendimentoDoProfissional;
    List<Object> tiposDeAtendimentoDoProfissional;
    
    /**
     * Creates new form ProfissionalFormulario
     */
    public ProfissionalFormulario(ProfissionalBusca parent, boolean modal) {
        this.parent = parent;
        this.setModal(modal);
        this.setLocation(600, 530);
        initComponents();

        botaoSalvar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/salvar.png")));
        botaoCancelar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/cancelar.png")));
        inicializaCertificacoes();
        inicializaEspecializacoes();
        inicializaPadroesDeAtendimento();
        inicializaTiposDeAtendimento();

        try {
            cpf.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            dataNascimento.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("##/##/####")));
            dataExpedicao.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("##/##/####")));
            telefoneCelular.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("(##) ####-####")));
            telefoneResidencial.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("(##) ####-####")));
            telefoneTrabalho.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("(##) ####-####")));
        } catch (ParseException ex) {
            Logger.getLogger(ProfissionalFormulario.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ArrayList<Object> estados = new ArrayList<>();
            Estado empty = new Estado();
            empty.setId(0);
            empty.setNome("Selecione um estado...");
            estados.add(empty);
            estados.addAll(EstadoDAO.getInstance().findAll(new Estado()));
            ComboBoxModel model = new DefaultComboBoxModel(estados.toArray());

            estado.setModel(model);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        botaoSalvar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/salvar.png")));
        botaoCancelar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/cancelar.png")));

        sexoM.setEnabled(false);
    }

    public void popularCampos(Profissional profissional) {
        try {
            id.setText((profissional.getId() != 0) ? Integer.toString(profissional.getId()) : "");
            nome.setText(profissional.getNome());
            rg.setText(profissional.getRg());
            cpf.setText(profissional.getCpf());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            if (profissional.getDataNascimento() != null) {
                dataNascimento.setText(format.format(profissional.getDataNascimento()));
            }
            if (profissional.getSexo() == 'M') {
                sexoMActionPerformed(null);
            } else {
                sexoFActionPerformed(null);
            }
            observacao.setText(profissional.getObservacao());

            if ( profissional.getUsuario() != null ) {
                usuario_id.setText("" + profissional.getUsuario().getId());
                login.setText(profissional.getUsuario().getLogin());
                login.setEditable(false);
            }
            senha.setText("");
            cep.setText(profissional.getCep());
            if (profissional.getCidade() != null) {
                estado.setSelectedItem(profissional.getCidade().getEstadoId());
                estadoActionPerformed(null);
                cidade.setSelectedItem(profissional.getCidade());
            }
            bairro.setText(profissional.getBairro());
            endereco.setText(profissional.getEndereco());
            numero.setText((profissional.getNumero() != null) ? Integer.toString(profissional.getNumero()) : "");
            complemento.setText(profissional.getComplemento());
            email.setText(profissional.getEmail());
            telefoneCelular.setText(profissional.getTelefoneCelular());
            telefoneResidencial.setText(profissional.getTelefoneResidencial());
            telefoneTrabalho.setText(profissional.getTelefoneTrabalho());

            populaEspecializacoes(profissional);
            populaCertificacoes(profissional);
            populaPadroesDeAtendimento(profissional);
            populaTiposDeAtendimento(profissional);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
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

        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        toolbar = new javax.swing.JToolBar();
        botaoSalvar = new javax.swing.JButton();
        botaoCancelar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        labelsPainel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nome = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rg = new javax.swing.JTextField();
        sexoM = new javax.swing.JRadioButton();
        sexoF = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        observacao = new javax.swing.JTextArea();
        cpf = new javax.swing.JFormattedTextField();
        dataNascimento = new javax.swing.JFormattedTextField();
        login = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        senha = new javax.swing.JPasswordField();
        jLabel27 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        usuario_id = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        pais = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        estado = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cidade = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cep = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        bairro = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        endereco = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        numero = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        complemento = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        telefoneCelular = new javax.swing.JFormattedTextField();
        telefoneResidencial = new javax.swing.JFormattedTextField();
        telefoneTrabalho = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        botaoAdicionarEspecializacao = new javax.swing.JButton();
        especializacaoDosProfissionais = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaDeEspecializacoes = new javax.swing.JTable();
        botaoRemoverEspecializacao = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        certificacoesDeProfissionais = new javax.swing.JComboBox();
        botaoAdicionarCertificacao = new javax.swing.JButton();
        botaoRemoverCertificacao = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelaDeCertificacoes = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        dataExpedicao = new javax.swing.JFormattedTextField();
        jLabel30 = new javax.swing.JLabel();
        registro = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        orgaoExpedidor = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        padroesDeAtendimentoDosProfissionais = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        botaoAdicionarPadrao = new javax.swing.JButton();
        botaoRemoverPadrao = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelaDePadroesDeAtendimento = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        tipoDeAtendimentoDosProfissionais = new javax.swing.JComboBox();
        botaoAdicionarTipoAtend = new javax.swing.JButton();
        botaoRemoverTipoAtend = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelaDeTipoDeAtendimento = new javax.swing.JTable();

        jLabel34.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel34.setForeground(java.awt.Color.red);
        jLabel34.setText("*");

        jLabel35.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel35.setForeground(java.awt.Color.red);
        jLabel35.setText("*");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        toolbar.setRollover(true);

        botaoSalvar.setText("Salvar");
        botaoSalvar.setFocusable(false);
        botaoSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoSalvarActionPerformed(evt);
            }
        });
        toolbar.add(botaoSalvar);

        botaoCancelar.setText("Cancelar");
        botaoCancelar.setFocusable(false);
        botaoCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botaoCancelar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botaoCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoCancelarActionPerformed(evt);
            }
        });
        toolbar.add(botaoCancelar);

        labelsPainel.setBackground(java.awt.SystemColor.controlLtHighlight);

        jLabel1.setText("Nome:");

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setText("*");

        jLabel3.setText("ID:");

        id.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        id.setPreferredSize(new java.awt.Dimension(0, 27));

        jLabel4.setText("RG:");

        sexoM.setBackground(java.awt.SystemColor.controlLtHighlight);
        sexoM.setSelected(true);
        sexoM.setText("Masculino");
        sexoM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sexoMActionPerformed(evt);
            }
        });

        sexoF.setBackground(java.awt.SystemColor.controlLtHighlight);
        sexoF.setText("Feminino");
        sexoF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sexoFActionPerformed(evt);
            }
        });

        jLabel5.setText("Sexo:");

        jLabel6.setText("CPF:");

        jLabel7.setText("Data de nascimento:");

        jLabel9.setText("Login:");

        jLabel21.setText("Observação:");

        observacao.setColumns(20);
        observacao.setRows(5);
        jScrollPane1.setViewportView(observacao);

        cpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpfActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel26.setForeground(java.awt.Color.red);
        jLabel26.setText("*");

        jLabel27.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel27.setForeground(java.awt.Color.red);
        jLabel27.setText("*");

        jLabel10.setText("Senha:");

        jLabel11.setText("ID Usuário:");

        usuario_id.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        usuario_id.setPreferredSize(new java.awt.Dimension(0, 27));

        javax.swing.GroupLayout labelsPainelLayout = new javax.swing.GroupLayout(labelsPainel);
        labelsPainel.setLayout(labelsPainelLayout);
        labelsPainelLayout.setHorizontalGroup(
            labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelsPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(labelsPainelLayout.createSequentialGroup()
                        .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(labelsPainelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nome, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(labelsPainelLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(labelsPainelLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rg, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(labelsPainelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sexoM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sexoF))
                    .addGroup(labelsPainelLayout.createSequentialGroup()
                        .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(senha)
                            .addComponent(login, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)))
                    .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, labelsPainelLayout.createSequentialGroup()
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(usuario_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(labelsPainelLayout.createSequentialGroup()
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                        .addGroup(labelsPainelLayout.createSequentialGroup()
                            .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dataNascimento)
                                .addComponent(cpf)))))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        labelsPainelLayout.setVerticalGroup(
            labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelsPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(rg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(dataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sexoM)
                    .addComponent(sexoF)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(usuario_id, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelsPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel10))
                .addContainerGap(174, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelsPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelsPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Dados pessoais", jPanel2);

        jPanel3.setBackground(java.awt.SystemColor.controlLtHighlight);

        jLabel12.setText("País:");

        pais.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Brasil" }));

        jLabel13.setText("Estado:");

        estado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Selecione estado..." }));
        estado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estadoActionPerformed(evt);
            }
        });

        jLabel14.setText("Cidade:");

        cidade.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-" }));
        cidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cidadeActionPerformed(evt);
            }
        });

        jLabel15.setText("Cep:");

        jLabel16.setText("Bairro:");

        jLabel17.setText("Endereço:");

        jLabel18.setText("Número:");

        jLabel19.setText("Complemento:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pais, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cidade, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cep, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bairro, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endereco, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numero, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(complemento, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(bairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(endereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(numero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(complemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(285, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Endereço", jPanel1);

        jPanel5.setBackground(java.awt.SystemColor.controlLtHighlight);

        jLabel20.setText("E-mail:");

        jLabel22.setText("Telefone celular:");

        jLabel24.setText("Telefone residencial:");

        jLabel25.setText("Telefone de trabalho:");

        jLabel23.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel23.setForeground(java.awt.Color.red);
        jLabel23.setText("*");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(telefoneTrabalho))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(telefoneCelular)
                            .addComponent(email)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(telefoneResidencial, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(telefoneCelular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(telefoneResidencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(telefoneTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(427, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Contato", jPanel4);

        jLabel8.setText("Especialização");

        botaoAdicionarEspecializacao.setText("Adicionar especialização");
        botaoAdicionarEspecializacao.setEnabled(false);
        botaoAdicionarEspecializacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoAdicionarEspecializacaoActionPerformed(evt);
            }
        });

        especializacaoDosProfissionais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                especializacaoDosProfissionaisActionPerformed(evt);
            }
        });

        tabelaDeEspecializacoes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabelaDeEspecializacoes);

        botaoRemoverEspecializacao.setText("Remover especialização");
        botaoRemoverEspecializacao.setEnabled(false);
        botaoRemoverEspecializacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRemoverEspecializacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(botaoAdicionarEspecializacao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botaoRemoverEspecializacao))
                            .addComponent(especializacaoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 70, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(especializacaoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoAdicionarEspecializacao)
                    .addComponent(botaoRemoverEspecializacao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Especialização", jPanel6);

        jLabel28.setText("Certificações");

        certificacoesDeProfissionais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                certificacoesDeProfissionaisActionPerformed(evt);
            }
        });

        botaoAdicionarCertificacao.setText("Adicionar certificação");
        botaoAdicionarCertificacao.setEnabled(false);
        botaoAdicionarCertificacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoAdicionarCertificacaoActionPerformed(evt);
            }
        });

        botaoRemoverCertificacao.setText("Remover certificação");
        botaoRemoverCertificacao.setEnabled(false);
        botaoRemoverCertificacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRemoverCertificacaoActionPerformed(evt);
            }
        });

        tabelaDeCertificacoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Data exoedição", "Registro", "Orgão Expedidor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tabelaDeCertificacoes);

        jLabel29.setText("Data expedição");

        jLabel30.setText("Registro");

        registro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registroActionPerformed(evt);
            }
        });

        jLabel31.setText("Orgão expedidor");

        jLabel32.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel32.setForeground(java.awt.Color.red);
        jLabel32.setText("*");

        jLabel33.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel33.setForeground(java.awt.Color.red);
        jLabel33.setText("*");

        jLabel36.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel36.setForeground(java.awt.Color.red);
        jLabel36.setText("*");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(botaoAdicionarCertificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botaoRemoverCertificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(orgaoExpedidor, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(registro, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dataExpedicao, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(certificacoesDeProfissionais, javax.swing.GroupLayout.Alignment.LEADING, 0, 336, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel36))))
                        .addGap(0, 75, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(certificacoesDeProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(dataExpedicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(registro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(orgaoExpedidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoAdicionarCertificacao)
                    .addComponent(botaoRemoverCertificacao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Certificação", jPanel7);

        padroesDeAtendimentoDosProfissionais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                padroesDeAtendimentoDosProfissionaisActionPerformed(evt);
            }
        });

        jLabel37.setText("Padrão de atendimento");

        botaoAdicionarPadrao.setText("Adicionar padrão");
        botaoAdicionarPadrao.setEnabled(false);
        botaoAdicionarPadrao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoAdicionarPadraoActionPerformed(evt);
            }
        });

        botaoRemoverPadrao.setText("Remover padrão");
        botaoRemoverPadrao.setEnabled(false);
        botaoRemoverPadrao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRemoverPadraoActionPerformed(evt);
            }
        });

        tabelaDePadroesDeAtendimento.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tabelaDePadroesDeAtendimento);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(botaoAdicionarPadrao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botaoRemoverPadrao))
                            .addComponent(padroesDeAtendimentoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 95, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(padroesDeAtendimentoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoAdicionarPadrao)
                    .addComponent(botaoRemoverPadrao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Padrão de Atendimento", jPanel8);

        jLabel38.setText("Tipo de Atendimento");

        tipoDeAtendimentoDosProfissionais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoDeAtendimentoDosProfissionaisActionPerformed(evt);
            }
        });

        botaoAdicionarTipoAtend.setText("Adicionar tipo");
        botaoAdicionarTipoAtend.setEnabled(false);
        botaoAdicionarTipoAtend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoAdicionarTipoAtendActionPerformed(evt);
            }
        });

        botaoRemoverTipoAtend.setText("Remover tipo");
        botaoRemoverTipoAtend.setEnabled(false);
        botaoRemoverTipoAtend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRemoverTipoAtendActionPerformed(evt);
            }
        });

        tabelaDeTipoDeAtendimento.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(tabelaDeTipoDeAtendimento);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(botaoAdicionarTipoAtend)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botaoRemoverTipoAtend))
                            .addComponent(tipoDeAtendimentoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 95, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(tipoDeAtendimentoDosProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoAdicionarTipoAtend)
                    .addComponent(botaoRemoverTipoAtend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tipo de Atendimento", jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoCancelarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_botaoCancelarActionPerformed

    private boolean verificaCampos() {
        boolean ok = true;
        message = "Os seguintes erros ocorreram:\n\n";
        if (nome.getText().isEmpty()) {
            ok = false;
            message += "* Campo Nome deve ser preenchido\n";
        }
        if (login.getText().isEmpty()) {
            ok = false;
            message += "* Campo Login deve ser preenchido\n";
        }
        if (id.getText().isEmpty()) {
            if (senha.getText().isEmpty()) {
                ok = false;
                message += "* Campo Senha deve ser preenchido\n";
            } else {
                if (senha.getText().length() < 8) {
                    ok = false;
                    message += "* Campo Senha deve ter no mínimo 8 caracteres\n";
                }
            }
        }
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        java.util.Date date = new Date((long) 0);
        try {
            System.out.println("" + dataNascimento.getText());
            if (!dataNascimento.getText().trim().startsWith("/")) {
                date = format.parse(dataNascimento.getText());
                java.util.Date hoje = new java.util.Date();

                if (date.after(hoje)) {
                    ok = false;
                    message += "* Data inválida\n";
                }
            }
        } catch (ParseException ex) {
            ok = false;
            message += "* Data inválida\n";
        }
        try {
            if (!numero.getText().isEmpty()) {
                Integer.parseInt(numero.getText());
            }
        } catch (NumberFormatException e) {
            ok = false;
            message += "* Numero do endereço inválido\n";
        }
        
        String celular = telefoneCelular.getText().replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
        if (celular.isEmpty()) {
            ok = false;
            message += "* Campo Telefone Celular deve ser preenchido\n";
        }
        
        return ok;
    }

    private void botaoSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoSalvarActionPerformed
        try {
            if (verificaCampos()) {
                Profissional profissional = new Profissional();

                if (id.getText().length() > 0) {
                    profissional.setId(Integer.parseInt(id.getText()));
                }

                profissional.setNome(nome.getText());
                profissional.setRg(rg.getText());
                profissional.setCpf(cpf.getText());

                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    java.sql.Date data = new java.sql.Date(format.parse(dataNascimento.getText()).getTime());
                    profissional.setDataNascimento(data);
                } catch (ParseException ex) {
                }

                profissional.setSexo(sexoM.isSelected() ? 'M' : 'F');
                profissional.setObservacao(observacao.getText());
                try {
                    profissional.setCidade((Cidade) cidade.getSelectedItem());
                } catch (ClassCastException e) {
                }
                profissional.setCep(cep.getText());
                profissional.setBairro(bairro.getText());
                profissional.setEndereco(endereco.getText());
                profissional.setComplemento(complemento.getText());
                try {
                    profissional.setNumero(Integer.parseInt(numero.getText()));
                } catch (NumberFormatException e) {
                    profissional.setNumero(0);
                }
                profissional.setTelefoneCelular(telefoneCelular.getText());
                
                profissional.setTelefoneResidencial(telefoneResidencial.getText());
                String residencial = telefoneResidencial.getText().replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
                if ( residencial.isEmpty() ) {
                    profissional.setTelefoneResidencial(residencial);
                }
                
                profissional.setTelefoneTrabalho(telefoneTrabalho.getText());
                String trabalho = telefoneTrabalho.getText().replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
                if ( trabalho.isEmpty() ) {
                    profissional.setTelefoneTrabalho(trabalho);
                }
                
                profissional.setEmail(email.getText());

                UserAccount usuario = new UserAccount();

                if (usuario_id.getText().length() > 0) {
                    usuario.setId(Integer.parseInt(usuario_id.getText()));
                }

                usuario.setActive(true);
                usuario.setLogin(login.getText());
                usuario.setPassword(senha.getText());
                usuario.setName(nome.getText());
                profissional.setUsuario(usuario);
                profissional.setTipo("profissional");

                if ( id.getText().length() > 0 ) {
                    ProfissionalDAO.getInstance().merge(profissional);
                } else {
                    ProfissionalDAO.getInstance().persist(profissional);
                }
                
                // Salva as dependencias do profissional.
                salvaEspecializacoes(profissional);
                salvaCertificacoes(profissional);
                salvaPadroesDeAtendimento(profissional);
                salvaTiposDeAtendimento(profissional);

                parent.atualizarGrid(profissional.getId(), new ArrayList());
                JOptionPane.showMessageDialog(this, "Registro efetuado com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
                
                this.setVisible(false);
            } else {
                throw new Exception(message);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoSalvarActionPerformed

    private void cpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpfActionPerformed

    private void sexoFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sexoFActionPerformed
        sexoM.setSelected(false);
        sexoM.setEnabled(true);
        sexoF.setSelected(true);
        sexoF.setEnabled(false);
    }//GEN-LAST:event_sexoFActionPerformed

    private void sexoMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sexoMActionPerformed
        sexoF.setSelected(false);
        sexoF.setEnabled(true);
        sexoM.setSelected(true);
        sexoM.setEnabled(false);
    }//GEN-LAST:event_sexoMActionPerformed

    private void cidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cidadeActionPerformed

    }//GEN-LAST:event_cidadeActionPerformed

    private void estadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estadoActionPerformed
        try {
            int busca = ((Estado) estado.getSelectedItem()).getId();

            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.eq("estado_id.id", busca));

            List<Object> grupos = CidadeDAO.getInstance().findByCriteria(new Cidade(), Restrictions.conjunction(), or);
            ComboBoxModel model = new DefaultComboBoxModel(grupos.toArray());
            cidade.setModel(model);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_estadoActionPerformed

    private void especializacaoDosProfissionaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_especializacaoDosProfissionaisActionPerformed
        if (especializacaoDosProfissionais.getSelectedItem().toString().isEmpty()) {
            botaoAdicionarEspecializacao.setEnabled(false);
        } else {
            botaoAdicionarEspecializacao.setEnabled(true);
        }
    }//GEN-LAST:event_especializacaoDosProfissionaisActionPerformed

    private void botaoAdicionarEspecializacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAdicionarEspecializacaoActionPerformed
        try {
            Especializacao especializacao = (Especializacao) especializacaoDosProfissionais.getSelectedItem();
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeEspecializacoes.getModel();
            modelo.addRow(new Object[]{especializacao.getId(), especializacao.getNome()});
            especializacaoDosProfissionais.removeItem(especializacao);
            especializacaoDosProfissionais.setSelectedItem("");
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoAdicionarEspecializacaoActionPerformed

    private void botaoRemoverEspecializacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRemoverEspecializacaoActionPerformed
        try {
            int selected = tabelaDeEspecializacoes.getSelectedRow();
            Object registro = tabelaDeEspecializacoes.getValueAt(selected, 0);
            int especializacao_id = Integer.parseInt(registro.toString());

            Object grupo = DAO.getInstance().getById(new Especializacao(), especializacao_id);
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeEspecializacoes.getModel();
            modelo.removeRow(selected);
            especializacaoDosProfissionais.addItem(grupo);
            botaoRemoverEspecializacao.setEnabled(false);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoRemoverEspecializacaoActionPerformed

    private void registroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registroActionPerformed

    private void botaoRemoverCertificacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRemoverCertificacaoActionPerformed
        try {
            int selected = tabelaDeCertificacoes.getSelectedRow();
            Object registro = tabelaDeCertificacoes.getValueAt(selected, 0);
            int certificacao_id = Integer.parseInt(registro.toString());

            Object grupo = DAO.getInstance().getById(new Certificacao(), certificacao_id);
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeCertificacoes.getModel();
            modelo.removeRow(selected);
            certificacoesDeProfissionais.addItem(grupo);
            botaoRemoverCertificacao.setEnabled(false);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoRemoverCertificacaoActionPerformed

    private void botaoAdicionarCertificacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAdicionarCertificacaoActionPerformed
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            java.util.Date date = new Date((long) 0);
            try {
                if (!dataExpedicao.getText().trim().startsWith("/")) {
                    date = format.parse(dataExpedicao.getText());
                    java.util.Date hoje = new java.util.Date();

                    if (date.after(hoje)) {
                        throw new Exception("O campo 'Data' deve ser no passado!");
                    }
                }
                else{
                    throw new Exception("O campo 'Data' é obrigatório!");
                }
            } catch (ParseException ex) {
                throw new Exception("Campo 'Data' inválido!");
            }
            if(registro.getText().isEmpty()){
                throw new Exception("O campo 'Registro' é obrigatório!");
            }
            if(orgaoExpedidor.getText().isEmpty()){
                throw new Exception("O campo 'Orgão Expedidor' é obrigatório");
            }
            Certificacao certificacao = (Certificacao) certificacoesDeProfissionais.getSelectedItem();
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeCertificacoes.getModel();
            modelo.addRow(new Object[]{certificacao.getId(), certificacao.getNome(), dataExpedicao.getText(), registro.getText(), orgaoExpedidor.getText()});
            certificacoesDeProfissionais.removeItem(certificacao);
            certificacoesDeProfissionais.setSelectedItem("");
            dataExpedicao.setText("");
            registro.setText("");
            orgaoExpedidor.setText("");
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoAdicionarCertificacaoActionPerformed

    private void certificacoesDeProfissionaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_certificacoesDeProfissionaisActionPerformed
        if (certificacoesDeProfissionais.getSelectedItem().toString().isEmpty()) {
            botaoAdicionarCertificacao.setEnabled(false);
        } else {
            botaoAdicionarCertificacao.setEnabled(true);
        }
    }//GEN-LAST:event_certificacoesDeProfissionaisActionPerformed

    private void padroesDeAtendimentoDosProfissionaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_padroesDeAtendimentoDosProfissionaisActionPerformed
        if ( padroesDeAtendimentoDosProfissionais.getSelectedItem().toString().isEmpty() ) {
            botaoAdicionarPadrao.setEnabled(false);
        } else {
            botaoAdicionarPadrao.setEnabled(true);
        }
    }//GEN-LAST:event_padroesDeAtendimentoDosProfissionaisActionPerformed

    private void botaoAdicionarPadraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAdicionarPadraoActionPerformed
        try {
            PadraoDeAtendimento padrao = (PadraoDeAtendimento) padroesDeAtendimentoDosProfissionais.getSelectedItem();
            DefaultTableModel modelo = (DefaultTableModel) tabelaDePadroesDeAtendimento.getModel();
            modelo.addRow(new Object[]{padrao.getId(), padrao.getNome()});
            padroesDeAtendimentoDosProfissionais.removeItem(padrao);
            padroesDeAtendimentoDosProfissionais.setSelectedItem("");
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoAdicionarPadraoActionPerformed

    private void botaoRemoverPadraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRemoverPadraoActionPerformed
        try {
            int selected = tabelaDePadroesDeAtendimento.getSelectedRow();
            Object registro = tabelaDePadroesDeAtendimento.getValueAt(selected, 0);
            int padrao_id = Integer.parseInt(registro.toString());

            Object grupo = DAO.getInstance().getById(new PadraoDeAtendimento(), padrao_id);
            DefaultTableModel modelo = (DefaultTableModel) tabelaDePadroesDeAtendimento.getModel();
            modelo.removeRow(selected);
            padroesDeAtendimentoDosProfissionais.addItem(grupo);
            botaoRemoverPadrao.setEnabled(false);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoRemoverPadraoActionPerformed

    private void tipoDeAtendimentoDosProfissionaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoDeAtendimentoDosProfissionaisActionPerformed
        if ( tipoDeAtendimentoDosProfissionais.getSelectedItem().toString().isEmpty() ) {
            botaoAdicionarTipoAtend.setEnabled(false);
        } else {
            botaoAdicionarTipoAtend.setEnabled(true);
        }
    }//GEN-LAST:event_tipoDeAtendimentoDosProfissionaisActionPerformed

    private void botaoAdicionarTipoAtendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAdicionarTipoAtendActionPerformed
        try {
            TipoDeAtendimento tipo = (TipoDeAtendimento) tipoDeAtendimentoDosProfissionais.getSelectedItem();
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeTipoDeAtendimento.getModel();
            modelo.addRow(new Object[]{tipo.getId(), tipo.getNome()});
            tipoDeAtendimentoDosProfissionais.removeItem(tipo);
            tipoDeAtendimentoDosProfissionais.setSelectedItem("");
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoAdicionarTipoAtendActionPerformed

    private void botaoRemoverTipoAtendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRemoverTipoAtendActionPerformed
        try {
            int selected = tabelaDeTipoDeAtendimento.getSelectedRow();
            Object registro = tabelaDeTipoDeAtendimento.getValueAt(selected, 0);
            int tipo_id = Integer.parseInt(registro.toString());

            Object grupo = DAO.getInstance().getById(new TipoDeAtendimento(), tipo_id);
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeTipoDeAtendimento.getModel();
            modelo.removeRow(selected);
            tipoDeAtendimentoDosProfissionais.addItem(grupo);
            botaoRemoverTipoAtend.setEnabled(false);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botaoRemoverTipoAtendActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProfissionalFormulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfissionalFormulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfissionalFormulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfissionalFormulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProfissionalFormulario dialog = new ProfissionalFormulario(parent, true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void inicializaEspecializacoes() {
        tabelaDeEspecializacoes.setSelectionBackground(new java.awt.Color(22, 160, 133));
        tabelaDeEspecializacoes.setSelectionForeground(new java.awt.Color(255, 255, 255));
        botaoAdicionarEspecializacao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/add.png")));
        botaoRemoverEspecializacao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/remover.png")));

        tabelaDeEspecializacoes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (tabelaDeEspecializacoes.getSelectedRow() > tabelaDeEspecializacoes.getRowCount()) {
                    botaoRemoverEspecializacao.setEnabled(false);
                } else {
                    botaoRemoverEspecializacao.setEnabled(true);
                }
            }
        });
    }

    public void populaEspecializacoes(Profissional profissional) {
        if(profissional != null && profissional.getId() != 0){
            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.eq("profissional", profissional));
            especializacoesDoProfissional = EspecializacaoDoProfissionalDAO.getInstance().findByCriteria(new EspecializacaoDoProfissional(), and, Restrictions.disjunction());
        }else{
            especializacoesDoProfissional = new ArrayList<>();
        }

        especializacaoDosProfissionais.addItem("");
        List<Object> especializacoes = EspecializacaoDoProfissionalDAO.getInstance().findAll(new Especializacao());

        for (int i = 0; i < especializacoes.size(); i++) {
            Especializacao especializacao = (Especializacao) especializacoes.get(i);
            boolean possuiEspecializacao = false;

            for (int g = 0; g < especializacoesDoProfissional.size(); g++) {
                EspecializacaoDoProfissional especializacaoDoProfissional1 = (EspecializacaoDoProfissional) especializacoesDoProfissional.get(g);

                if (especializacaoDoProfissional1.getEspecializacao().getId() == especializacao.getId()) {
                    possuiEspecializacao = true;
                    break;
                }
            }

            if (!possuiEspecializacao) {
                especializacaoDosProfissionais.addItem(especializacao);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDeEspecializacoes.getModel();
        for (int g = 0; g < especializacoesDoProfissional.size(); g++) {
            EspecializacaoDoProfissional especializacaoDoProfissional = (EspecializacaoDoProfissional) especializacoesDoProfissional.get(g);
            modelo.addRow(new Object[]{especializacaoDoProfissional.getEspecializacao().getId(), especializacaoDoProfissional.getEspecializacao().getNome()});
        }
    }

    private void salvaEspecializacoes(Profissional profissional) {
        if ( especializacoesDoProfissional != null ) {
            for (int g = 0; g < especializacoesDoProfissional.size(); g++) {
                EspecializacaoDoProfissional especializacaoDoProfissional = (EspecializacaoDoProfissional) especializacoesDoProfissional.get(g);
                EspecializacaoDoProfissionalDAO.getInstance().remove(especializacaoDoProfissional);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDeEspecializacoes.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Especializacao especializacao = new Especializacao();
            especializacao.setId((int) modelo.getValueAt(i, 0));

            EspecializacaoDoProfissional especializacaoDoProfissional = new EspecializacaoDoProfissional();
            especializacaoDoProfissional.setProfissional(profissional);
            especializacaoDoProfissional.setEspecializacao(especializacao);

            EspecializacaoDoProfissionalDAO.getInstance().merge(especializacaoDoProfissional);
        }
    }

    private void inicializaCertificacoes() {
        tabelaDeCertificacoes.setSelectionBackground(new java.awt.Color(22, 160, 133));
        tabelaDeCertificacoes.setSelectionForeground(new java.awt.Color(255, 255, 255));
        botaoAdicionarCertificacao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/add.png")));
        botaoRemoverCertificacao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/remover.png")));

        tabelaDeCertificacoes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (tabelaDeCertificacoes.getSelectedRow() > tabelaDeCertificacoes.getRowCount()) {
                    botaoRemoverCertificacao.setEnabled(false);
                } else {
                    botaoRemoverCertificacao.setEnabled(true);
                }
            }
        });
    }

    public void populaCertificacoes(Profissional profissional) {
        if(profissional != null && profissional.getId() != 0){
            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.eq("profissional", profissional));
            certificacoesDoProfissional = CertificacaoDoProfissionalDAO.getInstance().findByCriteria(new CertificacaoDoProfissional(), and, Restrictions.disjunction());
        }
        else{
            certificacoesDoProfissional = new ArrayList<>();
        }

        certificacoesDeProfissionais.addItem("");
        List<Object> certificacoes = CertificacaoDoProfissionalDAO.getInstance().findAll(new Certificacao());

        for (int i = 0; i < certificacoes.size(); i++) {
            Certificacao certificacao = (Certificacao) certificacoes.get(i);
            boolean possuiCertificacao = false;

            for (int g = 0; g < certificacoesDoProfissional.size(); g++) {
                CertificacaoDoProfissional certificacaoDoProfissional = (CertificacaoDoProfissional) certificacoesDoProfissional.get(g);

                if (certificacaoDoProfissional.getCertificacao().getId() == certificacao.getId()) {
                    possuiCertificacao = true;
                    break;
                }
            }

            if (!possuiCertificacao) {
                certificacoesDeProfissionais.addItem(certificacao);
            }
        }

        try{
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            DefaultTableModel modelo = (DefaultTableModel) tabelaDeCertificacoes.getModel();
            for (int g = 0; g < certificacoesDoProfissional.size(); g++) {
                CertificacaoDoProfissional certificacaoDoProfissional = (CertificacaoDoProfissional) certificacoesDoProfissional.get(g);
                modelo.addRow(new Object[]{
                    certificacaoDoProfissional.getCertificacao().getId(), 
                    certificacaoDoProfissional.getCertificacao().getNome(),
                    format.format(certificacaoDoProfissional.getDataExpedicao()),
                    certificacaoDoProfissional.getRegistro(),
                    certificacaoDoProfissional.getOrgaoExpedidor()
                });
            }
        }
        catch(Exception err){
            SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salvaCertificacoes(Profissional profissional) {
        if ( certificacoesDoProfissional != null ) {
            for (int g = 0; g < certificacoesDoProfissional.size(); g++) {
                CertificacaoDoProfissional certificacaoDoProfissional = (CertificacaoDoProfissional) certificacoesDoProfissional.get(g);


                CertificacaoDoProfissionalDAO.getInstance().remove(certificacaoDoProfissional);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDeCertificacoes.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            try{
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                
                Certificacao certificacao = new Certificacao();
                certificacao.setId((int) modelo.getValueAt(i, 0));

                CertificacaoDoProfissional certificacaoDoProfissional = new CertificacaoDoProfissional();
                certificacaoDoProfissional.setProfissional(profissional);
                certificacaoDoProfissional.setCertificacao(certificacao);
                certificacaoDoProfissional.setDataExpedicao(new Date(format.parse((String)modelo.getValueAt(i, 2)).getTime()));
                certificacaoDoProfissional.setRegistro((String) modelo.getValueAt(i, 3));
                certificacaoDoProfissional.setOrgaoExpedidor((String) modelo.getValueAt(i, 4));

                CertificacaoDoProfissionalDAO.getInstance().merge(certificacaoDoProfissional);
            }
            catch(ParseException err){
                SOptionPane.showMessageDialog(this, err, "Erro!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void inicializaPadroesDeAtendimento(){
        tabelaDePadroesDeAtendimento.setSelectionBackground(new java.awt.Color(22, 160, 133));
        tabelaDePadroesDeAtendimento.setSelectionForeground(new java.awt.Color(255, 255, 255));
        botaoAdicionarPadrao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/add.png")));
        botaoRemoverPadrao.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/remover.png")));

        tabelaDePadroesDeAtendimento.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (tabelaDeEspecializacoes.getSelectedRow() > tabelaDeEspecializacoes.getRowCount()) {
                    botaoRemoverPadrao.setEnabled(false);
                } else {
                    botaoRemoverPadrao.setEnabled(true);
                }
            }
        });
    }
    
    public void populaPadroesDeAtendimento(Profissional profissional){
        if(profissional != null && profissional.getId() != 0){
            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.eq("profissional", profissional));
            padroesDeAtendimentoDoProfissional = PadraoDeAtendimentoDoProfissionalDAO.getInstance().findByCriteria(new PadraoDeAtendimentoDoProfissional(), and, Restrictions.disjunction());
        }
        else{
            padroesDeAtendimentoDoProfissional = new ArrayList<>();
        }

        padroesDeAtendimentoDosProfissionais.addItem("");
        List<Object> padroesDeAtendimento = PadraoDeAtendimentoDoProfissionalDAO.getInstance().findAll(new PadraoDeAtendimento());

        for (int i = 0; i < padroesDeAtendimento.size(); i++) {
            PadraoDeAtendimento padraoDeAtendimento = (PadraoDeAtendimento) padroesDeAtendimento.get(i);
            boolean possuiPadraoDeAtendimento = false;

            for (int g = 0; g < padroesDeAtendimentoDoProfissional.size(); g++) {
                PadraoDeAtendimentoDoProfissional padraoDeAtendimentoDoProfissional = (PadraoDeAtendimentoDoProfissional) padroesDeAtendimentoDoProfissional.get(g);

                if (padraoDeAtendimentoDoProfissional.getPadraoDeAtendimento().getId() == padraoDeAtendimento.getId()) {
                    possuiPadraoDeAtendimento = true;
                    break;
                }
            }

            if (!possuiPadraoDeAtendimento) {
                padroesDeAtendimentoDosProfissionais.addItem(padraoDeAtendimento);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDePadroesDeAtendimento.getModel();
        for (int g = 0; g < padroesDeAtendimentoDoProfissional.size(); g++) {
            PadraoDeAtendimentoDoProfissional padraoDeAtendimentoDoProfissional = (PadraoDeAtendimentoDoProfissional) padroesDeAtendimentoDoProfissional.get(g);
            modelo.addRow(new Object[]{
                padraoDeAtendimentoDoProfissional.getPadraoDeAtendimento().getId(), 
                padraoDeAtendimentoDoProfissional.getPadraoDeAtendimento().toString()
            });
        }
    }
    
    private void salvaPadroesDeAtendimento(Profissional profissional){
        if ( padroesDeAtendimentoDoProfissional != null ) {
            for (int g = 0; g < padroesDeAtendimentoDoProfissional.size(); g++) {
                PadraoDeAtendimentoDoProfissional padraoDeAtendimentoDoProfissional = (PadraoDeAtendimentoDoProfissional) padroesDeAtendimentoDoProfissional.get(g);
                PadraoDeAtendimentoDoProfissionalDAO.getInstance().remove(padraoDeAtendimentoDoProfissional);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDePadroesDeAtendimento.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            PadraoDeAtendimento padraoDeAtendimento = new PadraoDeAtendimento();
            padraoDeAtendimento.setId((int) modelo.getValueAt(i, 0));

            PadraoDeAtendimentoDoProfissional padraoDeAtendimentoDoProfissional = new PadraoDeAtendimentoDoProfissional();
            padraoDeAtendimentoDoProfissional.setProfissional(profissional);
            padraoDeAtendimentoDoProfissional.setPadraoDeAtendimento(padraoDeAtendimento);

            PadraoDeAtendimentoDoProfissionalDAO.getInstance().merge(padraoDeAtendimentoDoProfissional);
        }
    }
    
    private void inicializaTiposDeAtendimento(){
        tabelaDeTipoDeAtendimento.setSelectionBackground(new java.awt.Color(22, 160, 133));
        tabelaDeTipoDeAtendimento.setSelectionForeground(new java.awt.Color(255, 255, 255));
        botaoAdicionarTipoAtend.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/add.png")));
        botaoRemoverTipoAtend.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/remover.png")));

        tabelaDeTipoDeAtendimento.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (tabelaDeTipoDeAtendimento.getSelectedRow() > tabelaDeTipoDeAtendimento.getRowCount()) {
                    botaoRemoverTipoAtend.setEnabled(false);
                } else {
                    botaoRemoverTipoAtend.setEnabled(true);
                }
            }
        });
    }
    
    public void populaTiposDeAtendimento(Profissional profissional){
        if(profissional != null && profissional.getId() != 0){
            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.eq("profissional", profissional));
            tiposDeAtendimentoDoProfissional = TipoDeAtendimentoDoProfissionalDAO.getInstance().findByCriteria(new TipoDeAtendimentoDoProfissional(), and, Restrictions.disjunction());
        }
        else{
            tiposDeAtendimentoDoProfissional = new ArrayList<>();
        }

        tipoDeAtendimentoDosProfissionais.addItem("");
        List<Object> tiposDeAtendimento = TipoDeAtendimentoDoProfissionalDAO.getInstance().findAll(new TipoDeAtendimento());
        
        for (int i = 0; i < tiposDeAtendimento.size(); i++) {
            TipoDeAtendimento tipoDeAtendimento = (TipoDeAtendimento) tiposDeAtendimento.get(i);
            boolean possuiTipoDeAtendimento = false;

            for (int g = 0; g < tiposDeAtendimentoDoProfissional.size(); g++) {
                TipoDeAtendimentoDoProfissional tipoDeAtendimentoDoProfissional = (TipoDeAtendimentoDoProfissional) tiposDeAtendimentoDoProfissional.get(g);

                if (tipoDeAtendimentoDoProfissional.getTipoDeAtendimento().getId() == tipoDeAtendimento.getId()) {
                    possuiTipoDeAtendimento = true;
                    break;
                }
            }
            
            if (!possuiTipoDeAtendimento) {
                tipoDeAtendimentoDosProfissionais.addItem(tipoDeAtendimento);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDeTipoDeAtendimento.getModel();
        for (int g = 0; g < tiposDeAtendimentoDoProfissional.size(); g++) {
            TipoDeAtendimentoDoProfissional tipoDeAtendimentoDoProfissional = (TipoDeAtendimentoDoProfissional) tiposDeAtendimentoDoProfissional.get(g);
            modelo.addRow(new Object[]{
                tipoDeAtendimentoDoProfissional.getTipoDeAtendimento().getId(), 
                tipoDeAtendimentoDoProfissional.getTipoDeAtendimento().toString()
            });
        }
    }
    
    private void salvaTiposDeAtendimento(Profissional profissional){
        if ( tiposDeAtendimentoDoProfissional != null ) {        
            for (int g = 0; g < tiposDeAtendimentoDoProfissional.size(); g++) {
                TipoDeAtendimentoDoProfissional tipoDeAtendimentoDoProfissional = (TipoDeAtendimentoDoProfissional) tiposDeAtendimentoDoProfissional.get(g);
                TipoDeAtendimentoDoProfissionalDAO.getInstance().remove(tipoDeAtendimentoDoProfissional);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaDeTipoDeAtendimento.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            TipoDeAtendimento tipoDeAtendimento = new TipoDeAtendimento();
            tipoDeAtendimento.setId((int) modelo.getValueAt(i, 0));
            tipoDeAtendimento.setNome((String) modelo.getValueAt(i, 1));

            TipoDeAtendimentoDoProfissional tipoDeAtendimentoDoProfissional = new TipoDeAtendimentoDoProfissional();
            tipoDeAtendimentoDoProfissional.setProfissional(profissional);
            tipoDeAtendimentoDoProfissional.setTipoDeAtendimento(tipoDeAtendimento);

            TipoDeAtendimentoDoProfissionalDAO.getInstance().merge(tipoDeAtendimentoDoProfissional);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bairro;
    private javax.swing.JButton botaoAdicionarCertificacao;
    private javax.swing.JButton botaoAdicionarEspecializacao;
    private javax.swing.JButton botaoAdicionarPadrao;
    private javax.swing.JButton botaoAdicionarTipoAtend;
    private javax.swing.JButton botaoCancelar;
    private javax.swing.JButton botaoRemoverCertificacao;
    private javax.swing.JButton botaoRemoverEspecializacao;
    private javax.swing.JButton botaoRemoverPadrao;
    private javax.swing.JButton botaoRemoverTipoAtend;
    private javax.swing.JButton botaoSalvar;
    private javax.swing.JTextField cep;
    private javax.swing.JComboBox certificacoesDeProfissionais;
    private javax.swing.JComboBox cidade;
    private javax.swing.JTextField complemento;
    private javax.swing.JFormattedTextField cpf;
    private javax.swing.JFormattedTextField dataExpedicao;
    private javax.swing.JFormattedTextField dataNascimento;
    private javax.swing.JTextField email;
    private javax.swing.JTextField endereco;
    private javax.swing.JComboBox especializacaoDosProfissionais;
    private javax.swing.JComboBox estado;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel labelsPainel;
    private javax.swing.JTextField login;
    private javax.swing.JTextField nome;
    private javax.swing.JTextField numero;
    private javax.swing.JTextArea observacao;
    private javax.swing.JTextField orgaoExpedidor;
    private javax.swing.JComboBox padroesDeAtendimentoDosProfissionais;
    private javax.swing.JComboBox pais;
    private javax.swing.JTextField registro;
    private javax.swing.JTextField rg;
    private javax.swing.JPasswordField senha;
    private javax.swing.JRadioButton sexoF;
    private javax.swing.JRadioButton sexoM;
    private javax.swing.JTable tabelaDeCertificacoes;
    private javax.swing.JTable tabelaDeEspecializacoes;
    private javax.swing.JTable tabelaDePadroesDeAtendimento;
    private javax.swing.JTable tabelaDeTipoDeAtendimento;
    private javax.swing.JFormattedTextField telefoneCelular;
    private javax.swing.JFormattedTextField telefoneResidencial;
    private javax.swing.JFormattedTextField telefoneTrabalho;
    private javax.swing.JComboBox tipoDeAtendimentoDosProfissionais;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JLabel usuario_id;
    // End of variables declaration//GEN-END:variables

}
