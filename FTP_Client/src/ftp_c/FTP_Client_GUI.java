package ftp_c;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.net.*;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;

class FTP_Client_GUI extends JFrame {

	private JPanel contentPane;
	private JTextField txf_Name;
	private JTextField txf_Password;
	JTextArea txa_View = new JTextArea();
	JComboBox combox_cmd = new JComboBox();
	//String strName = "S10459023",strPwd = "0000";
	String strName="",strPwd="";

	/**
	 * Launch the application.
	 */
	Socket ctrlSocket;
	public PrintWriter ctrlOutput;
	public BufferedReader ctrlInput;
	
	final int CTRLPORT = 21;
	
	public void openConnection(String host)throws IOException,UnknownHostException
	{
		ctrlSocket = new Socket(host,CTRLPORT);
		ctrlOutput = new PrintWriter(ctrlSocket.getOutputStream());
		ctrlInput = new BufferedReader(new InputStreamReader(ctrlSocket.getInputStream()));
	}
	
	public void closeConnection()throws IOException
	{
		ctrlSocket.close();
	}
	
	public void showMenu()
	{
		/*
		System.out.println(">Command?");
		System.out.println("2 ls");
		System.out.println("3 cd");
		System.out.println("4 get");
		System.out.println("5 put");
		System.out.println("6 ascii");
		System.out.println("7 binary");
		System.out.println("8 ");
		System.out.println("9 quit");
		*/
		//txa_View.setText("Please choose one command in combo box.");
		txa_View.append("Please choose one command in combo box.");
	}
	
	public String getCommand()
	{
		String buf = "";
		//BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
		while(buf.length()!=1)
		{
			try
			{
				//buf = lineread.readLine();
				buf = combox_cmd.getSelectedIndex()+"";
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		return buf;
	}
	
	public void doLogin(String n,String p)
	{
		String loginName = "";
		String password = "";
		//BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			
			
			//loginName = lineread.readLine();
			
			//txa_View.append("請輸入使用者名稱: ");
			
			ctrlOutput.println("USER "+n);
			ctrlOutput.flush();
			
			//System.out.println("請輸入密碼: ");
			//password = lineread.readLine();
			
			ctrlOutput.println("PASS "+p);
			ctrlOutput.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doQuit()
	{
		try
		{
			ctrlOutput.println("QUIT");
			ctrlOutput.flush();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doCd()
	{
		String dirName = "";
		BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.println("請輸入目錄名稱: ");
			dirName = lineread.readLine();
			ctrlOutput.println("CWD" + dirName);
			ctrlOutput.flush();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doLs()
	{
		try
		{
			int n ;
			byte[] buff = new byte[1024];
			Socket dataSocket = dataConnection("LIST");
			BufferedInputStream dataInput = new BufferedInputStream(dataSocket.getInputStream());
			while((n = dataInput.read(buff))>0)
			{
				System.out.write(buff,0,n);
			}
			dataSocket.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doAscii()
	{
		try
		{
			ctrlOutput.println("TYPE A");
			ctrlOutput.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doBinary()
	{
		try
		{
			ctrlOutput.println("TYPE I");
			ctrlOutput.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doGet()
	{
		String fileName = "";
		BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			int n;
			byte[] buff = new byte[1024];
			System.out.println("請輸入檔案: ");
			fileName = lineread.readLine();
			FileOutputStream outfile = new FileOutputStream(fileName);
			Socket dataSocket = dataConnection("RETR " + fileName );
			BufferedInputStream dataInput = new BufferedInputStream(dataSocket.getInputStream());
			while((n = dataInput.read(buff))>0)
			{
				outfile.write(buff,0,n);
			}
			dataSocket.close();
			outfile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doPut()
	{
		String fileName = "";
		BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			int n;
			byte[] buff = new byte[1024];
			FileInputStream sendfile = null;
			System.out.println("請輸入檔案名稱: ");
			fileName = lineread.readLine();
			try
			{
				sendfile = new FileInputStream(fileName);
			}
			catch(Exception e)
			{
				System.out.println("檔案不存在");
				return;
			}
			
			Socket dataSocket = dataConnection("STOR" + fileName);
			OutputStream outstr = dataSocket.getOutputStream();
			while((n = sendfile.read(buff))>0)
			{
				outstr.write(buff,0,n);
			}
			dataSocket.close();
			sendfile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Socket dataConnection(String ctrlcmd)
	{
		String cmd = "PORT";
		int i;
		Socket dataSocket = null;
		try
		{
			byte[] address = InetAddress.getLocalHost().getAddress();
			ServerSocket serverDataSocket = new ServerSocket(0,1);
			for(i = 0;i<4;++i)
			{
				cmd = cmd + (address[i] & 0xff)+".";
			}
			cmd = cmd + (((serverDataSocket.getLocalPort())/256) & 0xff) + "." + (serverDataSocket.getLocalPort() & 0xff);
			ctrlOutput.println(cmd);
			ctrlOutput.flush();
			ctrlOutput.println(ctrlcmd);
			ctrlOutput.flush();
			
			dataSocket = serverDataSocket.accept();
			serverDataSocket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return dataSocket;
	}
	public boolean execCommand(String command)
	{
		boolean cont = true;
		
		switch(Integer.parseInt(command))
		{
		case -1:
			//doLogin();
			break;
		case 1:
			doLs();
			break;
		case 2:
			doCd();
			break;
		case 3:
			doGet();
			break;
		case 4:
			doPut();
			break;
		case 5:
			doAscii();
			break;
		case 6:
			doBinary();
			break;
		case 7:
			doQuit();
			cont = false;
			break;
		default:
			txa_View.append("請輸入一個序號: ");
				
		}
		return(cont);
	}
	public void main_proc()throws IOException
	{
		boolean cont = true;
		try
		{
			while(cont)
			{
				
				showMenu();
				
				cont = execCommand(getCommand());
			}
		}
		catch(Exception e)
		{
			System.err.print(e);
			System.exit(1);
		}
	}
	
	public void getMsgs()
	{
		try
		{
			CtrlListen listener = new CtrlListen(ctrlInput);
			Thread listenerthread = new Thread(listener);
			listenerthread.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FTP_Client_GUI frame = new FTP_Client_GUI();
					frame.setVisible(true);
					/*
					FTP_Client_GUI f = null;
					
					if(args.length<1)
					{
						System.out.println("usage: java Ftp <host name>");
						return;
					}
					f = new FTP_Client_GUI();
					f.openConnection(args[0]);
					
					f.getMsgs();
					/*
					f.main_proc();
					
					f.closeConnection();
					System.exit(0);
					*/
					
					
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public FTP_Client_GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 751, 538);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		combox_cmd.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		
		//JComboBox combox_cmd = new JComboBox();
		combox_cmd.setModel(new DefaultComboBoxModel(new String[] {"1.  LOGIN", "2.  LIST", "3.  CWD", "4.  RETR", "5.  STOR", "6.  TYPE A", "7.  TYPE I", "8.  QUIT "}));
		
		JLabel lbl_cmd = new JLabel("\u8ACB\u9078\u64C7\u6307\u4EE4 : ");
		lbl_cmd.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		
		JButton btn_cmd = new JButton("Login");
		btn_cmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					strName = txf_Name.getText();
					strPwd = txf_Password.getText();
					FTP_Client_GUI f = null;
					String args = "localhost";
					if(args.isEmpty())
					{
						System.out.println("usage: java Ftp <host name>");
						return;
					}
					f = new FTP_Client_GUI();
					f.openConnection(args);
					
					f.getMsgs();
					
					
					f.doLogin(strName, strPwd);
					//System.out.println(strName);
					
					
					f.main_proc();
					
					f.closeConnection();
					System.exit(0);
				}
				catch(Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				
			}
		});
		btn_cmd.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		
		JLabel lbl_Name = new JLabel("\u4F7F\u7528\u8005\u540D\u7A31 :");
		lbl_Name.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		
		JLabel lbl_Password = new JLabel("\u4F7F\u7528\u8005\u5BC6\u78BC :");
		lbl_Password.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		
		txf_Name = new JTextField();
		txf_Name.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		txf_Name.setColumns(10);
		
		txf_Password = new JTextField();
		txf_Password.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		txf_Password.setColumns(10);
		
		//JTextArea txa_View = new JTextArea();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lbl_Name)
						.addComponent(lbl_Password))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(txf_Password, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
						.addComponent(txf_Name, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lbl_cmd, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(combox_cmd, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE))
						.addComponent(btn_cmd, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)))
				.addComponent(txa_View, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(32)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(combox_cmd, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
						.addComponent(lbl_cmd)
						.addComponent(lbl_Name)
						.addComponent(txf_Name, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btn_cmd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lbl_Password)
							.addComponent(txf_Password, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txa_View, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
class CtrlListen implements Runnable
{
	BufferedReader ctrlInput = null;
	public CtrlListen(BufferedReader in)
	{
		ctrlInput = in;
	}
	public void run()
	{
		while(true)
		{
			try
			{
				System.out.println(ctrlInput.readLine());
			}
			catch(Exception e)
			{
				System.exit(1);
			}
		}
	}
}


