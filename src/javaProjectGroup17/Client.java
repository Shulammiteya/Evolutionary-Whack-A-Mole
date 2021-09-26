package javaProjectGroup17;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Client {

	private static boolean wait_for_instr; //���s�}�l����l�Ʊ���
	private static boolean singleVersion; //�����
	private static boolean doubleVersion; //���H��
	private static boolean putMouse; //��a��
	private static boolean catchMouse; //��a��
	private static boolean pause; //�Ȱ�
	private static String message; //�s�u��J���T��
	private static int myScore, hisScore; //�ڪ��̲צ��Z�B���̲צ��Z
	
	private static void initialize() { //��l��
		pause = false;
		putMouse = false;
		catchMouse = false;
		singleVersion = false;
		doubleVersion = false;
		wait_for_instr = false;
		myScore = hisScore = -1;
	}
	
	public static void main(String [] args) {
		
		JFrame playerSettingFrame1 = new JFrame("Client"); //������ܪ�frame�A�����or���H��
		playerSettingFrame1.setSize(550, 450);
		playerSettingFrame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton singleBtn = new JButton("�����");
		JButton doubleBtn = new JButton("���H��");
		singleBtn.addActionListener(new ClientBtnListener());
		doubleBtn.addActionListener(new ClientBtnListener());
		singleBtn.setLocation(150, 100);
		doubleBtn.setLocation(150, 200);
		singleBtn.setSize(200,50);
		doubleBtn.setSize(200,50);

		playerSettingFrame1.setLayout(null);
		playerSettingFrame1.add(singleBtn);
		playerSettingFrame1.add(doubleBtn);
		playerSettingFrame1.setVisible(true);
		
		while(singleVersion==false && doubleVersion==false) {
			System.out.println(singleVersion);
		}
		
		if(singleVersion == true) { //�����
			playerSettingFrame1.dispose();
			SingleVersion singleVersion = new SingleVersion();
			singleVersion.play();
		}
		else if(doubleVersion == true) { //���H��
			playerSettingFrame1.dispose();
			message = "";
			
			try{
				Socket cSock = new Socket("127.0.0.1", 8000);
				DataOutputStream output = new DataOutputStream(cSock.getOutputStream());
				
				Thread myThread = new Thread(new MyThread(cSock)); //Connection made, waiting for input by Thread.
				myThread.start();
				
				while(message.equals("") && putMouse==false && catchMouse==false) { //�T�{�}��
					System.out.println(putMouse);
				}
				if(message.equals("putMouse")) {
					ClientBtnListener.putBtn.setEnabled(false);
				}
				else if(message.equals("catchMouse")) {
					ClientBtnListener.catchBtn.setEnabled(false);
				}
				if(putMouse == true) {
					output.writeBytes("putMouse\n");
				}
				else if(catchMouse == true) {
					output.writeBytes("catchMouse\n");
				}
				while(!( (message.equals("putMouse")&&catchMouse==true) || (message.equals("catchMouse")&&putMouse==true) )) {
					System.out.println(putMouse);
				}
				if(putMouse == true) {
					output.writeBytes("putMouse\n");
				}
				else if(catchMouse == true) {
					output.writeBytes("catchMouse\n");
				}
				
				//�C���}�l
				if(putMouse == true) { //��ѹ�
					ClientBtnListener.playerSettingFrame2.dispose();
					initialize();
					Player player = new Player("��a��"); //���ͪ��a
					
					while(true) { //
						if(myScore!=-1 && hisScore!=-1) { //�C�������A���Ϳ�Ĺ��frame
							if(myScore > hisScore) {
								frameForWinnerAndLoser("��a��Client:�AĹ�F", player.frame, output);
							}
							else if(myScore < hisScore) {
								frameForWinnerAndLoser("��a��Client:�A��F", player.frame, output);
							}
							else if(myScore == hisScore) {
								frameForWinnerAndLoser("��a��Client:����", player.frame, output);
							}
							myScore = hisScore = -1;
						}
						else {
							System.out.println(Player.state);
							if(Player.state == -1) { //state==-1�A�h�X�C��
				            	System.exit(0);
							}
							else if(Player.state == 0) { //state==0�A���s�}�l
								if(!wait_for_instr) { //�@����l
									for(int i=0; i<17; i++) { //�ѹ���������
										if(player.hole_number_mouse[i] != 0) {
											player.hole_number_mouse[i] = 0;
											player.hole_number_mouse_hide[i] = 1;
										}
									}
									player.restart();
									wait_for_instr = true;
									message = "";
									output.writeBytes("restart\n"); //�i�D��譫�s�}�l
								}
							}
							else if(Player.state == 1) { //state==1�A�C���}�l
								if(Player.win || Player.lose) { //�C���ɶ���
									Player.state = 3; //state==3�A���ݿ�Ĺ�P�_
									myScore = player.get_score();
									output.writeBytes("score" + myScore + "\n"); //�i�D���ڪ����Z
								}
								else {
									if(wait_for_instr || pause) { //�C����}�l�ɪ��򥻳]�w
										wait_for_instr = false;
										pause = false;
										output.writeBytes("start\n"); //�i�D���C���}�l
									}
									if(message.equals("didNotCatchMouse")) { //���S���ѹ��A����+1
										message = "";
										player.add_to_score(1); //����+1
									}
									if(message.length()>4 && message.substring(0, 5).equals("catch")) { //�����ѹ�
										int disappearHoleNum = Integer.parseInt(message.substring(5));
										message = "";
										player.hole_number_mouse_hide[disappearHoleNum] = 1; //�ѹ�����
									}
									if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] == 0) { //��ѹ�
										player.hole_number_mouse[Player.hole_number_pressed] = 1;
										player.mouse_get_out(); //�ѹ��X�{
										output.writeBytes(Player.hole_number_pressed + "\n"); //�i�D���b���Ӭ}��F�ѹ�
										Player.hole_number_pressed = -1;
					            	}
								}
							}
							else if(Player.state == 2) { //state==2�A�Ȱ�
								if(pause == false) {
									wait_for_instr = false;
									pause = true;
									output.writeBytes("pause\n"); //�i�D���Ȱ�
								}
							}
							else if(Player.state == 3) { } //state==3�A���ݿ�Ĺ�P�_
						}
					}
				}
				else if(catchMouse == true) { //��ѹ�
					ClientBtnListener.playerSettingFrame2.dispose();
					initialize();
					Player player = new Player("���a��"); //���ͪ��a
					
					while(true) { //
						if(myScore!=-1 && hisScore!=-1) { //�C�������A���Ϳ�Ĺ��frame
							if(myScore > hisScore) {
								frameForWinnerAndLoser("���a��Client:�AĹ�F", player.frame, output);
							}
							else if(myScore < hisScore) {
								frameForWinnerAndLoser("���a��Client:�A��F", player.frame, output);
							}
							else if(myScore == hisScore) {
								frameForWinnerAndLoser("���a��Client:����", player.frame, output);
							}
							myScore = hisScore = -1;
						}
						else {
							System.out.println(Player.state);
							if(Player.state == -1) { //state==-1�A�h�X�C��
				            	System.exit(0);
							}
							else if(Player.state == 0) { //state==0�A���s�}�l
								if(!wait_for_instr) { //�@����l
									for(int i=0; i<17; i++) { //�ѹ���������
										if(player.hole_number_mouse[i] != 0) {
											player.hole_number_mouse[i] = 0;
											player.hole_number_mouse_hide[i] = 1;
										}
									}
									player.restart();
									wait_for_instr = true;
									message = "";
									output.writeBytes("restart\n"); //�i�D��譫�s�}�l
								}
							}
							else if(Player.state == 1) { //state==1�A�C���}�l
								if(Player.win || Player.lose) { //�C���ɶ���
									Player.state = 3; //state==3�A���ݿ�Ĺ�P�_
									myScore = player.get_score();
									output.writeBytes("score" + myScore + "\n"); //�i�D���ڪ����Z
								}
								else {
									if(wait_for_instr || pause) { //�C����}�l�ɪ��򥻳]�w
										wait_for_instr = false;
										pause = false;
										output.writeBytes("start\n"); //�i�D���C���}�l
									}
									if(!message.equals("")) { //�o�����X���ѹ��X�S�a
										int inputHoleNum = Integer.parseInt(message);
										if(player.hole_number_mouse[inputHoleNum] == 0 ) { //�ѹ��X�{
											player.hole_number_mouse[inputHoleNum] = 1;
											player.mouse_get_out();
										}
										message = "";
									}
									if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] != 0) { //���ѹ��A����+1
										player.add_to_score(1); //����+1
										player.hole_number_mouse_hide[Player.hole_number_pressed] = 1; //�ѹ�����
										output.writeBytes("catch" + Player.hole_number_pressed + "\n"); //�i�D�����Ӭ}�ѹ��n����
										Player.hole_number_pressed = -1;
					            	}
									if(player.didNotCatchMouse) { //�i�D���ڨS���ѹ�
										output.writeBytes("didNotCatchMouse\n");
										player.didNotCatchMouse = false;
									}
								}
							}
							else if(Player.state == 2) { //�i�D���ڨS���ѹ�
								if(pause == false) {
									wait_for_instr = false;
									pause = true;
									output.writeBytes("pause\n"); //�i�D���Ȱ�
								}
							}
							else if(Player.state == 3) { } //state==3�A���ݿ�Ĺ�P�_
						}
					}
				}
				
				output.close();
				cSock.close();
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
		}
		
	}

	private static void frameForWinnerAndLoser(String text, JFrame playerFrame, DataOutputStream output) { //���C�����G��A��ܭn�A�Ӥ@��or�h�X�C����frame
		
		playerFrame.setEnabled(false);
		
		JFrame playerSettingFrame3 = new JFrame(text); //Title:�AĹ�For�A��F
		playerSettingFrame3.setSize(350, 150);
		playerSettingFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton restartBtn = new JButton("�A�Ӥ@��");
		JButton exitBtn = new JButton("�h�X�C��");
		restartBtn.addActionListener(new ClientBtnListener(playerSettingFrame3, playerFrame, output));
		exitBtn.addActionListener(new ClientBtnListener(playerSettingFrame3, playerFrame, output));
		restartBtn.setLocation(12, 30);
		exitBtn.setLocation(172, 30);
		restartBtn.setSize(150,50);
		exitBtn.setSize(150,50);

		playerSettingFrame3.setLayout(null);
		playerSettingFrame3.add(restartBtn);
		playerSettingFrame3.add(exitBtn);
		playerSettingFrame3.setVisible(true);
	}
		
	private static class MyThread implements Runnable { //private class�A��Thread������Ū������J�����

		private Socket socket;
		private String inputStr;
		
		MyThread(Socket s) {
			socket = s;
		}
		
		@Override
		public void run() {
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while(true) {
					inputStr = input.readLine();
					if(inputStr.equals("restart")) {
						Player.state = 0;
						inputStr = "";
					}
					if(inputStr.equals("start")) {
						Player.state = 1;
						inputStr = "";
					}
					if(inputStr.equals("pause")) {
						Player.state = 2;
						inputStr = "";
					}
					if(inputStr.equals("exit")) {
						Player.state = -1;
						inputStr = "";
						break;
					}
					if(inputStr.length()>4 && inputStr.substring(0, 5).equals("score")) { //
						Client.hisScore = Integer.parseInt(inputStr.substring(5));
						inputStr = "";
					}
					Client.message = inputStr;
				}
				input.close();
				socket.close();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static  class ClientBtnListener implements ActionListener{ //ButtonListener
		
		public static JFrame playerSettingFrame2 = new JFrame();
		public static JButton putBtn, catchBtn;
		private DataOutputStream output; 
		private JFrame deleteFrame, playerFrame;
		
		ClientBtnListener(JFrame f, JFrame player_frame, DataOutputStream o) {
			deleteFrame = f;
			playerFrame = player_frame;
			output = o;
		}
		ClientBtnListener() { }
		
		public void actionPerformed(ActionEvent e) {
			
			String command=e.getActionCommand();
			
			if(command.equals("�����")) {//singleVersion = true
				Client.singleVersion = true;
			}
			else if(command.equals("���H��")) {//doubleVersion = true
				Client.doubleVersion = true;

				playerSettingFrame2 = new JFrame("Client"); //��ܭn��ѹ��٬O��ѹ�
				playerSettingFrame2.setSize(350, 300);
				playerSettingFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				putBtn = new JButton("��a��");
				catchBtn = new JButton("��a��");
				putBtn.addActionListener(new ClientBtnListener());
				catchBtn.addActionListener(new ClientBtnListener());
				putBtn.setLocation(70, 50);
				catchBtn.setLocation(70, 150);
				putBtn.setSize(200,50);
				catchBtn.setSize(200,50);

				playerSettingFrame2.setLayout(null);
				playerSettingFrame2.add(putBtn);
				playerSettingFrame2.add(catchBtn);
				playerSettingFrame2.setVisible(true);
			}
			else if(command.equals("��a��")) {//putMouse = true
				Client.putMouse = true;
				catchBtn.setEnabled(false);
			}
			else if(command.equals("��a��")) {//catchMouse = true
				Client.catchMouse = true;
				putBtn.setEnabled(false);
			}
			else if(command.equals("�A�Ӥ@��")) {
				Player.state = 0;
				deleteFrame.dispose();
				playerFrame.setEnabled(true);
				try {
					output.writeBytes("restart\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(command.equals("�h�X�C��")) {
				try {
					output.writeBytes("exit\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
			
		}
		
	}
	
}
