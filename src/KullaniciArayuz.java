
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class KullaniciArayuz extends javax.swing.JFrame {

    // kulAdi nesnesini socket'e gidip gelen verilerde kullanılmak için oluşturduk.
    // clientID'yi aktif kişiler listesinde her bir üyenin kendine özel adını tanımlamak, bununla özel mesaj veya genel mesajı 
    // kontrol ederek gönderilen mesajın komutunu ayrıştırmak, özelleştirmek için kullanacağız.
    
    String kulAdi, clientID = "";
    DataInputStream dins;
    DataOutputStream douts;
    DefaultListModel deflm;
    // Frame component'imiz olan List'i varsayılan biçimde tutması için DefaultListModel kullandık.
    
    // Temel verileri okumak ve yazmak için DataInputStream ve DataOutputStream kullanıyoruz. 
    // Daha sonra bunları yeni nesne oluşturarak kullanacağız ve Socket'ten alacağımız veya yazdıracağımız verileri bunlarla yapacağız.
    // *** Giriş akışı ve çıkış akışını (veriler için) kontrollü tutmak amacıyla kullanıyoruz. 
    
    /**
     * KullaniciArayuz formunu oluşturur.
     */
    public KullaniciArayuz() {
        initComponents();
    }
    
    // Farklı parametreli constructor oluşturduk, sohbete giriş yapması için bu constructor kullanılacak.
    // Kullanıcı adı girildikten sonra "read" metodunu kontrol etmesi için başlatmamız lazım. "start()" kullanmamızın sebebi Thread'i çalıştırmak, çalışmasını sağlamak.
    public KullaniciArayuz(String id, Socket socket) {
        kulAdi = id;
        try {
            initComponents();
            deflm = new DefaultListModel();
            userListler = new javax.swing.JList<>(deflm);
            kullaniciAdiLabel.setText(id);
            dins = new DataInputStream(socket.getInputStream());
            douts = new DataOutputStream(socket.getOutputStream());
            new Read().start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Sunucuya gelen kullanıcıyı okur ve kontrol ederek düzeltir. Eğer özel karakterli isim girilirse siler ve kullanıcı listesini barındırınan List componentini temizler
    // Ardından StringTokenizer ile okunan veriyi (kullanıcı adını) parçalandırır.
    // Eğer kontrol edilecek String varsa bunu "n" String'ine eşitler. "n" String'i işlemler sonunda bir kelime olarak ortaya çıkar.
    // "n" String'i arayüzdeki kullanıcı adına eşit değilse bizim design'da oluşturduğumuz DefaultListModel'imize atanmış userListler adlı List component'imize bu kullanıcı adını ekler. 
    class Read extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    String x = dins.readUTF();
                    if (x.contains(":;.,/=")) {
                        x = x.substring(6);
                        deflm.clear();
                        
                        StringTokenizer strtok = new StringTokenizer(x, ",");
                        while (strtok.hasMoreTokens()) {
                            String n = strtok.nextToken();
                            if (!kulAdi.equals(n)) {
                                deflm.addElement(n);
                            }
                        }
                    } else {
                        mesajKutusu.append("" + x + "\n");
                        
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mesajKutusu = new javax.swing.JTextArea();
        kullaniciAdiLabel = new javax.swing.JLabel();
        butunUserSec = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        mesajGonder = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        userListler = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(193, 160, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel1.setText("Hoş geldin");

        mesajKutusu.setColumns(20);
        mesajKutusu.setRows(5);
        jScrollPane1.setViewportView(mesajKutusu);

        kullaniciAdiLabel.setText("-----");

        butunUserSec.setText("Herkesi Seç");
        butunUserSec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butunUserSecActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel3.setText("Aktif Kullanıcılar");

        mesajGonder.setText("Mesaj Yolla");
        mesajGonder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mesajGonderActionPerformed(evt);
            }
        });

        userListler.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userListlerValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(userListler);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(kullaniciAdiLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(185, 185, 185))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(butunUserSec, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(mesajGonder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(kullaniciAdiLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mesajGonder, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(butunUserSec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(messageTextField))
                .addGap(26, 26, 26))
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
    
    // Özel mesaj veya herkese mesaj komut işlevlerini burada ayarladık.
    // Mesajın özel mesaj olup olmadığını ayırt etmeden önce (seçili clientID varsa veya yoksa) yazılan mesajı korumak adına m_m adında bir String değişkeni oluşturduk 
    // ve yazılan mesajı ona atadık, eğer clientID boş olmazsa mesajın eski halini, ekranda yazdıracak şekilde yeni haline çeviriyor.
    // Özel mesaj yollarken hangi clientID'ye ne gönderdiğimizin ilk halini değiştirip sunucuya yollamamız ardından bunu kullanmamız lazım.
    // Daha sonra yeni halini (m) sunucuya komut gönderiyor ardından mesajın ilk halini (m_m) ilgili kişilerin ekranına yazdırıyor.
    // Eğer clientID boşsa yani kişi seçilmemişse genel sohbete kısaca herkese mesaj atmış oluyorsunuz.
    
    // Server kısmındaki verilerle beraber olmak üzere ana mesaj kontrolümüz ve
    // Mesajlaşma (Kullanıcı) arayüzünde mesaj gönder butonumuza kullanılma eventinde tanımladığımız
    // özel mesaj/genel mesaj kontrolümüz ile çift kontrollü şekilde
    // yani kullanıcı arayüzü kontrolü sonrasında Server kontrolüyle mesaj yollattık.
    
    private void mesajGonderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mesajGonderActionPerformed
        try {
            String m = messageTextField.getText(), m_m = m;
            String ClientID = clientID;
            if (!clientID.isEmpty()) {
                m = "#5455665" + ClientID + ":" + m_m;
                douts.writeUTF(m);
                messageTextField.setText("");
                mesajKutusu.append("*-* " + ClientID + "adlı kişiye gönderdiniz: " + m_m + "`\n");
            } else {
                douts.writeUTF(m);
                messageTextField.setText("");
                mesajKutusu.append("*-* Siz genel sohbete gönderdiniz: " + m_m + "\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Böyle bir kullanıcı yok.");
        }
    }//GEN-LAST:event_mesajGonderActionPerformed

    // Uygulamayı kapatırken bir sorun olursa bunu konsolda (server frame'inde değil) logluyor.
    // Sunucuya 'leavechat' diye komut gidiyor. Sonra RAM'den form üzerinde kayıtlı herşeyi kaldırıyor.
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        String i = "leavechat";
        try {
            douts.writeUTF(i);
            this.dispose();
        } catch (IOException e) {
            Logger.getLogger(KullaniciArayuz.class.getName()).log(Level.SEVERE, null, e);

        }
    }//GEN-LAST:event_formWindowClosing
    
    // Tanımladığımız clientID nesnesini List componentimizden seçili elemanı atadık
    // Her farklı kişi seçildiğinde kullanıcının List üzerindeki tanımlayıcısını getirecek, haliyle istediğimiz kullanıcıya mesaj atabileceğiz.
    
    private void userListlerValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userListlerValueChanged
        clientID = (String) userListler.getSelectedValue();
    }//GEN-LAST:event_userListlerValueChanged

    // Bütün kullanıcıları seçmek için bir buton koyduk, clientID'yi boş yapacak haliyle kişi o butonu seçtiğinde 
    // sonra tanımlayacağımız metodlarda bütün kullanıcılara (yani genel sohbete) mesaj atabilecek.
    private void butunUserSecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butunUserSecActionPerformed
        clientID="";
    }//GEN-LAST:event_butunUserSecActionPerformed

    //public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KullaniciArayuz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KullaniciArayuz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KullaniciArayuz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KullaniciArayuz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(new Runnable() {
            //public void run() {
                //new KullaniciArayuz().setVisible(true);
            //}
        //});
    //}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butunUserSec;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel kullaniciAdiLabel;
    private javax.swing.JButton mesajGonder;
    private javax.swing.JTextArea mesajKutusu;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JList<String> userListler;
    // End of variables declaration//GEN-END:variables
}
