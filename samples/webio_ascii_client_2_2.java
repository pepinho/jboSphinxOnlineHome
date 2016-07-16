/**
 * UDP Simple Client ist ein kleines Netzwerktool auf  Basis des TCP Protokolls
 * Quelle http://www.wut.de
 */
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.Timer;


public class WebIO_ASCII_Client_2_2 extends JFrame implements ActionListener,KeyListener,Runnable{
	//Allgemein
	private static final long serialVersionUID = -6061394082201596541L;
	final int inputs = 2;
	final int outputs = 2;
	private Container co;
	private JPanel jp[] = new JPanel[2];
	private JTextField tf_statusbar = new JTextField();
	private Timer timer;
	
	//12 Zeilen IO mit allen grafischen Elementen
	private JCheckBox cb_outputs[] = new JCheckBox[outputs];
	private JCheckBox cb_inputs[] = new JCheckBox[inputs];
	private JLabel lb_counter[] = new JLabel[inputs];
	private JButton bt_read[] = new JButton[inputs];
	private JButton bt_clear[] = new JButton[inputs];
	private JTextField tf_counter[] = new JTextField[inputs];
	
	//"All" Zeile
	private JLabel lb_countall = new JLabel("all Counter ->");
	private JButton bt_all[] = new JButton[4];
	
	// Polling Zeile
	private JCheckBox cb_polling[] = new JCheckBox[3];
	private JTextField tf_pollingtime = new JTextField("250");
	private JLabel lb_polling = new JLabel("Intervall");
	
	//Connection Teil
	private JTextField tf_ip = new JTextField();
	private JTextField tf_port = new JTextField("80");
	private JTextField tf_pw = new JTextField();
	private JLabel lb_connection[] = new JLabel[3];
	private JButton bt_connect = new JButton("Connect");
	private JButton bt_disconnect = new JButton("Disconnect");
	
	//Verbindungselemente
	private Socket socket;
	private PrintWriter out;
	private InputStream in;
	
	
	
	public WebIO_ASCII_Client_2_2(String name){
		//Fenster Allg.
		super(name);
		this.setLocation(300,300);
		this.setSize(413,240);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		co = getContentPane();
		co.setLayout(null);
		jp[0] = new JPanel(null);
		jp[1] = new JPanel(null);
		jp[0].setBorder(new TitledBorder("Input/Output Control"));
		jp[1].setBorder(new TitledBorder("Connection Control"));
		jp[0].setBounds(2, 5, 395, 125);
		jp[1].setBounds(2, 130, 395, 50);
		tf_statusbar.setBounds(2,185,395,20);
		tf_statusbar.setEditable(false);
		tf_statusbar.setText("No Connection");
		co.add(tf_statusbar);
		co.add(jp[0]);
		co.add(jp[1]);
		
		//12xIO Zeilen
		for(int i=0;i<outputs;i++){
			cb_outputs[i] =  new JCheckBox("Output "+i);
			cb_outputs[i].setBounds(3, 20+(i*20), 80, 15);
			cb_outputs[i].addActionListener(this);
			jp[0].add(cb_outputs[i]);
		}
		for(int i=0;i<inputs;i++){
			cb_inputs[i] =  new JCheckBox("Input "+i);
			lb_counter[i] = new JLabel("Counter "+i);
			tf_counter[i] = new JTextField();
			bt_read[i]= new JButton("read");
			bt_clear[i]= new JButton("clear");

			
			cb_inputs[i].setBounds(80, 20+(i*20), 70, 15);
			lb_counter[i].setBounds(150, 20+(i*20), 65, 15);
			tf_counter[i].setBounds(215, 19+(i*20), 50, 18);
			bt_read[i].setBounds(265, 20+(i*20), 60, 15);
			bt_clear[i].setBounds(325, 20+(i*20), 65, 15);
			
			tf_counter[i].setText("0");
			tf_counter[i].setHorizontalAlignment(JTextField.CENTER);
			bt_read[i].addActionListener(this);
			bt_clear[i].addActionListener(this);
			
			cb_inputs[i].setEnabled(false);
			
			
			jp[0].add(cb_inputs[i]);
			jp[0].add(lb_counter[i]);
			jp[0].add(tf_counter[i]);
			jp[0].add(bt_read[i]);
			jp[0].add(bt_clear[i]);
		}
		
		//"All" Zeile
		String[] s = {"read all","read all","read","clear"};
		for(int i=0;i<=3;i++){
			bt_all[i]= new JButton(s[i]);
			bt_all[i].addActionListener(this);
			jp[0].add(bt_all[i]);
		}
		jp[0].add(lb_countall);
		bt_all[0].setBounds(2, 70, 76, 15);
		bt_all[1].setBounds(76, 70, 76, 15);
		lb_countall.setBounds(165, 70, 76, 15);
		bt_all[2].setBounds(265, 70, 60, 15);
		bt_all[3].setBounds(325, 70, 65, 15);
		
		// Polling Zeile
		for(int i=0;i<3;i++){
			cb_polling[i] = new JCheckBox("polling");
			jp[0].add(cb_polling[i]);
		}
		jp[0].add(tf_pollingtime);
		jp[0].add(lb_polling);
		tf_pollingtime.addKeyListener(this);
		tf_pollingtime.setBounds(215, 100, 50, 18);
		lb_polling.setBounds(165, 100, 50, 18);
		cb_polling[0].setBounds(3, 100, 80, 15);
		cb_polling[1].setBounds(80, 100, 80, 15);
		cb_polling[2].setBounds(265, 100, 80, 15);
		
		
		//Connection Teil
		jp[1].add(tf_ip);
		jp[1].add(tf_port);
		jp[1].add(tf_pw);
		jp[1].add(bt_connect);
		jp[1].add(bt_disconnect);
		
		tf_ip.setBounds(15,30,100,15);
		tf_port.setBounds(125,30,50,15);
		tf_pw.setBounds(185,30,100,15);
		bt_connect.setBounds(288,30,100,15);
		bt_disconnect.setBounds(288,15,100,15);
		
		bt_connect.addActionListener(this);
		bt_disconnect.addActionListener(this);
		
		String[] s2 = {"IP","Port","Password"};
		for(int i=0;i<=2;i++){
			lb_connection[i]= new JLabel(s2[i]);
			jp[1].add(lb_connection[i]);	
		}
		lb_connection[0].setBounds(15,15,100,15);
		lb_connection[1].setBounds(125,15,50,15);
		lb_connection[2].setBounds(185,15,100,15);
		
		//Fenster sichtbar machen wenn alles geladen ist
		this.setVisible(true);
		//Disconnect Methode deaktiviert nicht verf�gbare Elmente
		this.disconnect();
	}
	public void actionPerformed(ActionEvent ev){
		String pw = tf_pw.getText();
		if(ev.getActionCommand()=="Connect"){
			this.connect();	
		}else if(ev.getActionCommand()=="Disconnect"){
			this.disconnect();
		}else if(ev.getSource() == timer){
			if(cb_polling[0].isSelected()){
				out.print("GET /output?PW="+pw+"&");
				out.flush();
			}
			if(cb_polling[1].isSelected()){
				out.print("GET /input?PW="+pw+"&");
				out.flush();
			}
			if(cb_polling[2].isSelected()){
				out.print("GET /counter?PW="+pw+"&");
				out.flush();
			}
		}else{
			//12 read und 12 clear Button Events in einer Schleife auswerten
			String message = "GET /";
			for(int i=0;i<inputs;i++){
				
				if(ev.getSource() == bt_read[i]){
					message += "counter"+i+"?PW="+pw+"&";
				}else if(ev.getSource() == bt_clear[i]){
					message += "counterclear"+i+"?PW="+pw+"&";
				}else if(i<outputs && ev.getSource() == cb_outputs[i]){
					if(!cb_outputs[i].isSelected()){
						message += "outputaccess"+i+"?PW="+pw+"&State=OFF&";
					}else{
						message += "outputaccess"+i+"?PW="+pw+"&State=ON&";
					}
				}
			}
			//jetzt noch alle "all"Button und pollings pr�fen 
			if(ev.getSource() == bt_all[0]){
				message +="output?PW="+pw+"&";
			}else if(ev.getSource() == bt_all[1]){
				message +="input?PW="+pw+"&";
			}if(ev.getSource() == bt_all[2]){
				message +="counter?PW="+pw+"&";
			}if(ev.getSource() == bt_all[3]){
				message +="counterclear?PW="+pw+"&";
			}
			out.print(message);
			out.flush();
		}
	}
	public void keyPressed(KeyEvent ev) {
	}

	public void keyReleased(KeyEvent ev) {
	  try {
	    timer.setDelay(Integer.parseInt(tf_pollingtime.getText()));
	  } catch (NumberFormatException e) {
	  }
	}

	public void keyTyped(KeyEvent ev) {
	}
	public void connect(){
		if(!tf_ip.getText().isEmpty() && !tf_port.getText().isEmpty()){
			try{
				int port = Integer.parseInt( tf_port.getText());
				socket = new Socket(tf_ip.getText(),port);
				OutputStreamWriter ow;
				ow= new OutputStreamWriter(socket.getOutputStream());
				out = new PrintWriter(ow);
				in = socket.getInputStream();
				timer = new Timer(Integer.parseInt(tf_pollingtime.getText()), this);
				timer.start();
				//	Ausgabethread erzeugen
				new Thread(this).start();
				
				bt_connect.setEnabled(false);
				bt_disconnect.setEnabled(true);
				tf_ip.setEditable(false);
				tf_port.setEditable(false);
				tf_pw.setEditable(false);
				for(int i=0;i<inputs;i++){
					bt_read[i].setEnabled(true);
					bt_clear[i].setEnabled(true);
					lb_counter[i].setEnabled(true);
					if(i<outputs)cb_outputs[i].setEnabled(true);
					if(i<=3) bt_all[i].setEnabled(true);
					if(i<=2) cb_polling[i].setEnabled(true);
				}
				tf_pollingtime.setEnabled(true);
				lb_polling.setEnabled(true);
				lb_countall.setEnabled(true);
				tf_statusbar.setText("Connected to"+tf_ip.getText());
			}catch(IOException e){
				tf_statusbar.setText("error: "+e.getMessage());
			}
		}
	}
	public void disconnect(){
		try{
			if(timer != null)  timer.stop();
			if(socket != null){
				socket.close();
			}
			bt_connect.setEnabled(true);
			bt_disconnect.setEnabled(false);
			tf_ip.setEditable(true);
			tf_port.setEditable(true);
			tf_pw.setEditable(true);
			for(int i=0;i<4;i++){
				if(i<=1) bt_read[i].setEnabled(false);
				if(i<=1) bt_clear[i].setEnabled(false);
				if(i<=1) lb_counter[i].setEnabled(false);
				
				if(i<=3) bt_all[i].setEnabled(false);
				if(i<=2) cb_polling[i].setEnabled(false);
				if(i<outputs) cb_outputs[i].setEnabled(false);
			}
			
			tf_pollingtime.setEnabled(false);
			lb_polling.setEnabled(false);
			lb_countall.setEnabled(false);
			tf_statusbar.setText("No Connection");
		}catch(IOException e){
			tf_statusbar.setText("error: "+e.getMessage());
		}
	}
	public void receive(String[] tokend_line){
		String command = tokend_line[0];
		String argument = tokend_line[1];
		if (command.startsWith("counter") && tokend_line.length==2) {
			 int nr = Integer.parseInt(command.replaceFirst("counter", ""));
			 tf_counter[nr].setText(argument); 
		}else if(command.startsWith("counter") && tokend_line.length>2) {
			for(int i=0;i<inputs;i++){
				tf_counter[i].setText(tokend_line[i+1]);
			}
		}else if(command.startsWith("input")) {
			 for(int i=0;i<inputs;i++){ 
				 if((Long.parseLong(argument,16) & (long)Math.pow(2,i))>0){
					 cb_inputs[i].setSelected(true);
				 }else{
					 cb_inputs[i].setSelected(false);
				 }
			 }
		}else if(command.startsWith("output") && !argument.equals("ON") && !argument.equals("OFF")) {
			 for(int i=0;i<outputs;i++){ 
				 if((Long.parseLong(argument,16) & (long)Math.pow(2,i))>0){
					 cb_outputs[i].setSelected(true);
				 }else{
					 cb_outputs[i].setSelected(false);
				 }
			 }
			  
		}
	}
	public void run()
	{
		char buffer[] = new char[1024];
		int i=0;
		try {
		   int sign;
		   while ((sign = in.read()) != -1) { 
			   	if(sign==0){
			   		String be = new String(buffer,0, i);
			   		StringTokenizer st = new StringTokenizer(be,";");
					String[] tokend_line = new String[st.countTokens()];
					int count = st.countTokens();
					for(int x=0;x<count;x++){
						tokend_line[x] = st.nextToken();
					}
			   		this.receive(tokend_line);
			   		i=0;
			   	}else{
			   		buffer[i]=(char)sign;
			   		i++;
			   	}
		   }  
		}catch (IOException e) {
		}
		tf_statusbar.setText("");
		this.disconnect();
		   
	}
	public static void main(String[] args) {
		new WebIO_ASCII_Client_2_2("Web-IO ASCII Client");
	}
}