package org.jasic.intellijKeyGen;/*    */ import org.jasic.intellijKeyGen.KeyGen;

import java.awt.Dimension;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.awt.event.KeyListener;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFormattedTextField;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.KeyStroke;
/*    */ 
/*    */ public class IDEAKeyGen
/*    */   extends JDialog
/*    */ {
/*  6 */   private JPanel contentPane = new JPanel();
/*  7 */   private JTextField userName = new JTextField();
/*  8 */   private JFormattedTextField version = new JFormattedTextField();
/*  9 */   private JTextField licenseKey = new JTextField();
/* 10 */   private KeyGen keyGen = new KeyGen();
/*    */   
/*    */   public IDEAKeyGen()
/*    */   {
/* 13 */     this.userName.setPreferredSize(new Dimension(150, 30));
/* 14 */     this.version.setPreferredSize(new Dimension(50, 30));
/* 15 */     this.licenseKey.setPreferredSize(new Dimension(340, 30));
/* 16 */     this.version.setText("14");
/*    */     
/* 18 */     this.contentPane.add(new JLabel("User Name"));
/* 19 */     this.contentPane.add(this.userName);
/* 20 */     this.contentPane.add(new JLabel("Version"));
/* 21 */     this.contentPane.add(this.version);
/* 22 */     this.contentPane.add(new JLabel("License Key"));
/* 23 */     this.contentPane.add(this.licenseKey);
/* 24 */     this.contentPane.add(new JLabel("Power By fireflyc"));
/*    */     
/* 26 */     setContentPane(this.contentPane);
/* 27 */     setModal(true);
/* 28 */     KeyListener keyListener = new KeyListener()
/*    */     {
/*    */       public void keyTyped(KeyEvent e) {}
/*    */       
/*    */       public void keyPressed(KeyEvent e) {}
/*    */       
/*    */       public void keyReleased(KeyEvent e)
/*    */       {
/* 36 */         byte bVersion = 14;
/*    */         try
/*    */         {
/* 38 */           bVersion = Byte.parseByte(IDEAKeyGen.this.version.getText());
/*    */         }
/*    */         catch (Exception ex) {}
/* 42 */         IDEAKeyGen.this.version.setText(String.valueOf(bVersion));
/* 43 */         String key = IDEAKeyGen.this.keyGen.key(IDEAKeyGen.this.userName.getText(), bVersion);
/* 44 */         IDEAKeyGen.this.licenseKey.setText(key);
/*    */       }
/* 46 */     };
/* 47 */     this.userName.addKeyListener(keyListener);
/* 48 */     this.version.addKeyListener(keyListener);
/*    */     
/* 50 */     setDefaultCloseOperation(0);
/* 51 */     addWindowListener(new WindowAdapter()
/*    */     {
/*    */       public void windowClosing(WindowEvent e)
/*    */       {
/* 53 */         IDEAKeyGen.this.onClose();
/*    */       }
/* 55 */     });
/* 56 */     this.contentPane.registerKeyboardAction(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e)
/*    */       {
/* 58 */         IDEAKeyGen.this.onClose();
/*    */       }
/* 58 */     }, KeyStroke.getKeyStroke(27, 0), 1);
/*    */   }
/*    */   
/*    */   private void onClose()
/*    */   {
/* 65 */     dispose();
/*    */   }
/*    */   
/*    */   public static void main(String[] args)
/*    */   {
/* 69 */     IDEAKeyGen dialog = new IDEAKeyGen();
/* 70 */     dialog.pack();
/* 71 */     dialog.setVisible(true);
/* 72 */     System.exit(0);
/*    */   }
/*    */ }


/* Location:           C:\Users\Caiying\Downloads\ideakeygen-1.0.0.jar
 * Qualified Name:     IDEAKeyGen
 * JD-Core Version:    0.7.0.1
 */