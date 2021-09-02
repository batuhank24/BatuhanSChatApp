
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class Server extends javax.swing.JFrame {

    /**
     * Creates new form Server
     */
    // Sunucuyu yaratırken kullanacağımız Socket'i ve kullanıcıları bağlantılarıyla tutacağımız listeyi (HashMap) tanımladık.
    // HashMap'i anahtar-kilit ilişkisine uygun gördüğümüz için kullandık çünkü uygulamaya bağlanmadan önce her kullanıcı adlarıyla ayırt edilerek, standart socket bağlantısını kullanmalı.
    // Kısaca HashMap kullanmak daha işimize yarıyor.
    ServerSocket servsock;
    HashMap clientListe = new HashMap();

    // Server constructor'ın içerisinde port ile yeni bir server socket oluşturup, kullanıcıyı sunucuya bu porttan kabul etmesini burada tanımladık.
    public Server() {
        try {
            initComponents();
            servsock = new ServerSocket(3080);
            this.sunucuDurumLabel.setText("Sunucu çalışıyor - AÇIK");
            new ClientAccept().start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ClientAccept sınıfının "run" metodu sonsuz bir döngü içerisinde kullanıcıları kabul ediyor, sonsuz döngü kullanmamızın sebebi her kullanıcı bağlandığında
    // sorun olmadan kullanıcıyı kabul etmeli.
    // Yani her kullanıcı için açık olmalı, içerisindeki "run" metodunun da haliyle sürekli çalışması gerek.
    // DataInputStream ve DataOutputStream ile verileri Socket'e aktarmak veya Socket'ten veri almak için kullanmak zorundayız.
    // Aşağıda mesajOkuma class'ının constructor'ını kullandık çünkü Multithreading olarak açılan socket'i ve giren kullanıcının adını bir arada tutmamız sonra bu multithread durumunu başlatmamız lazımdı.
    class ClientAccept extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = servsock.accept();
                    String dins = new DataInputStream(socket.getInputStream()).readUTF();
                    if (clientListe.containsKey(dins)) {
                        DataOutputStream douts = new DataOutputStream(socket.getOutputStream());
                        douts.writeUTF("Kullanıcı adı zaten alınmış!");
                    } else {
                        clientListe.put(dins, socket);
                        serverKayıtlar.append(dins + " konuşmaya katıldı.\n");
                        DataOutputStream douts = new DataOutputStream(socket.getOutputStream());
                        douts.writeUTF("");
                        new mesajOkuma(socket, dins).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // mesajOkuma class'ının içerisinde "run" metodunda veri ve mesaj kontrollerini bu class çalıştırılırken yapmayı denedik.
    // Yukarıda (ClientAccept class'ı) çağırdığımız mesajOkuma constructor'ına yukarıda verilecek değerleri eşleştirdik.
    // Aslında hepsi bir zincir gibi birbirine bağımlı.
    // Kullanıcıların ve socketin tanım olduğu HashMap eğer boş değilse gelen veriyi 3 durumda kontrol edecek;
    // * Kullanıcının ayrıldığı durum
    // * Özel mesaj gönderdiği durum
    // * Herkese mesaj yazdığı durum
    // Uygulamada mesaj yollanılan ana metotun içinde, Private Mesaj yollandığı takdirde özel mesaj yollayan kişinin kullanıcı adının başına sayısal id gibi sayı koyup bunu diğerlerinden ayrıştırdık.
    // Belli koşullara göre kullanıcı adını altta kontrol ettirdik ve öyle işlem yaptırtdık.
    // Sistemden kaynaklı bir error alındığında kullanıcıyı sunucudan çıkarttık ve listeyi güncelledik.
    // Sistemin verileri ayırt edip doğru işlemleri gerçekleştirmesi adına böyle 'if - elseif - else' kontrollü bir yöntemi seçtik.
    // StringTokenizer'ın kullanım amacı DataInputStream'den okunan veriyi parçalandırmak, ardından kendinde türetilmiş metotlarının yardımıyla içlerinde kontrol etmek ve ona göre girilmiş komutları uygulamak.
    // Sunucudan veri (kullanıcı adı) okuduğumuz tek işlemde StringTokenizer metoduyla ayrıştırıp teyit ettirdik. --- Özel mesaj olarak gönderilmesi için.
    class mesajOkuma extends Thread {

        Socket socket;
        String kullaniciAdi;

        private mesajOkuma(Socket socket, String id) {
            this.socket = socket;
            this.kullaniciAdi = id;
        }

        @Override
        public void run() {
            while (!clientListe.isEmpty()) {
                try {
                    String dins = new DataInputStream(socket.getInputStream()).readUTF();
                    if (dins.equals("leavechat")) {
                        clientListe.remove(kullaniciAdi);
                        serverKayıtlar.append(kullaniciAdi + " ayrıldı.\n");
                        new ClientListeDuzenle().start();

                        Set listeset = clientListe.keySet();
                        Iterator iterator = listeset.iterator();

                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            if (!key.equalsIgnoreCase(kullaniciAdi)) {
                                try {
                                    new DataOutputStream(((Socket) clientListe.get(key)).getOutputStream()).writeUTF("*-_ " + kullaniciAdi + " sohbetten ayrıldı. _-*" + "\n");
                                } catch (Exception e) {
                                    clientListe.remove(key);
                                    serverKayıtlar.append(kullaniciAdi + " ayrıldı.\n");
                                    new ClientListeDuzenle().start();
                                }
                            }
                        }
                    } else if (dins.contains("#5455665")) {
                        dins = dins.substring(20);
                        StringTokenizer strtok = new StringTokenizer(dins, ":");
                        String id = strtok.nextToken();
                        dins = strtok.nextToken();
                        try {
                            new DataOutputStream(((Socket) clientListe.get(id)).getOutputStream()).writeUTF("*-* " + kullaniciAdi + " den/dan" + id + " -a mesaj: " + dins);
                        } catch (Exception e) {
                            clientListe.remove(id);
                            serverKayıtlar.append(id + ": ayrıldı.\n");
                            new ClientListeDuzenle().start();
                        }

                    } else {
                        Set k = clientListe.keySet();
                        Iterator iterator = k.iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            if (!key.equalsIgnoreCase(kullaniciAdi)) {
                                try {
                                    new DataOutputStream(((Socket) clientListe.get(key)).getOutputStream()).writeUTF("*-* " + kullaniciAdi + " herkese yazdı: " + dins);
                                } catch (Exception e) {
                                    clientListe.remove(key);
                                    serverKayıtlar.append(key + ": ayrıldı.\n");
                                    new ClientListeDuzenle().start();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Kullanıcı listesinde bir değişiklik olduğunda güncellenmesi için ayrı bir class yazdık.
    // clientListe'yi sonradan Set olarak aldık, sebebi Set tipinde listelerde birbirini tekrarlayan verilerin bulunmaması ve üzerinde değişiklik yapılabilmesi.
    // Birbirini tekrarlayan verilerin (örnek; kullanıcı adı) aynı uygulamada bulunması projemizin amacı için yanlış olur.
    // ve de Client üzerinde data loselara (veri kaybı) ve görsel hatalara sebep olacak.
    // HashMap'i yeni bir Set'e tanımladık. Set'e aktarmamızın sebebini yukarıda yazdık.
    // Iterator kullanmamızın sebebi Set içerisindeki verilerin sıralı biçimini bozmadan üzerlerinde gezmek, teker teker alınacak şekilde bir String değişkende toplamak ve gerektiği yerde kullanmaktı.
    // Set üzerinde hem her bir veriden geçmemiz lazım hem kontrol etmemiz lazım, bu durumda kolaylık adına Iterator kullandık.
    class ClientListeDuzenle extends Thread {

        @Override
        public void run() {
            try {
                String ids = "";
                Set k = clientListe.keySet();
                Iterator iterator = k.iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    ids += key + ",";
                }
                if (ids.length() != 0) {
                    ids = ids.substring(0, ids.length() - 1);
                }

                iterator = k.iterator();

                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    try {
                        new DataOutputStream(((Socket) clientListe.get(key)).getOutputStream()).writeUTF(":;.,/=" + ids);
                    } catch (Exception e) {
                        clientListe.remove(key);
                        serverKayıtlar.append(key + ": ayrıldı.\n");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sunucuDurumLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serverKayıtlar = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server Client");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Sunucu:");

        sunucuDurumLabel.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        sunucuDurumLabel.setForeground(new java.awt.Color(51, 204, 0));
        sunucuDurumLabel.setText("-----");

        serverKayıtlar.setEditable(false);
        serverKayıtlar.setColumns(20);
        serverKayıtlar.setRows(5);
        jScrollPane1.setViewportView(serverKayıtlar);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(sunucuDurumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sunucuDurumLabel))
                .addGap(25, 25, 25))
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

    // Frame'imizin çalışması için gereken main metodu.
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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea serverKayıtlar;
    private javax.swing.JLabel sunucuDurumLabel;
    // End of variables declaration//GEN-END:variables
}
