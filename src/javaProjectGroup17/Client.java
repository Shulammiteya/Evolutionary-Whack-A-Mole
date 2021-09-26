package javaProjectGroup17;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Client {

	private static boolean wait_for_instr; //重新開始的初始化控制
	private static boolean singleVersion; //單機版
	private static boolean doubleVersion; //雙人版
	private static boolean putMouse; //放地鼠
	private static boolean catchMouse; //抓地鼠
	private static boolean pause; //暫停
	private static String message; //連線輸入的訊息
	private static int myScore, hisScore; //我的最終成績、對手最終成績
	
	private static void initialize() { //初始化
		pause = false;
		putMouse = false;
		catchMouse = false;
		singleVersion = false;
		doubleVersion = false;
		wait_for_instr = false;
		myScore = hisScore = -1;
	}
	
	public static void main(String [] args) {
		
		JFrame playerSettingFrame1 = new JFrame("Client"); //版本選擇的frame，單機版or雙人版
		playerSettingFrame1.setSize(550, 450);
		playerSettingFrame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton singleBtn = new JButton("單機版");
		JButton doubleBtn = new JButton("雙人版");
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
		
		if(singleVersion == true) { //單機版
			playerSettingFrame1.dispose();
			SingleVersion singleVersion = new SingleVersion();
			singleVersion.play();
		}
		else if(doubleVersion == true) { //雙人版
			playerSettingFrame1.dispose();
			message = "";
			
			try{
				Socket cSock = new Socket("127.0.0.1", 8000);
				DataOutputStream output = new DataOutputStream(cSock.getOutputStream());
				
				Thread myThread = new Thread(new MyThread(cSock)); //Connection made, waiting for input by Thread.
				myThread.start();
				
				while(message.equals("") && putMouse==false && catchMouse==false) { //確認腳色
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
				
				//遊戲開始
				if(putMouse == true) { //放老鼠
					ClientBtnListener.playerSettingFrame2.dispose();
					initialize();
					Player player = new Player("放地鼠"); //產生玩家
					
					while(true) { //
						if(myScore!=-1 && hisScore!=-1) { //遊戲結束，產生輸贏的frame
							if(myScore > hisScore) {
								frameForWinnerAndLoser("放地鼠Client:你贏了", player.frame, output);
							}
							else if(myScore < hisScore) {
								frameForWinnerAndLoser("放地鼠Client:你輸了", player.frame, output);
							}
							else if(myScore == hisScore) {
								frameForWinnerAndLoser("放地鼠Client:平手", player.frame, output);
							}
							myScore = hisScore = -1;
						}
						else {
							System.out.println(Player.state);
							if(Player.state == -1) { //state==-1，退出遊戲
				            	System.exit(0);
							}
							else if(Player.state == 0) { //state==0，重新開始
								if(!wait_for_instr) { //一切初始
									for(int i=0; i<17; i++) { //老鼠全部消失
										if(player.hole_number_mouse[i] != 0) {
											player.hole_number_mouse[i] = 0;
											player.hole_number_mouse_hide[i] = 1;
										}
									}
									player.restart();
									wait_for_instr = true;
									message = "";
									output.writeBytes("restart\n"); //告訴對方重新開始
								}
							}
							else if(Player.state == 1) { //state==1，遊戲開始
								if(Player.win || Player.lose) { //遊戲時間到
									Player.state = 3; //state==3，等待輸贏判斷
									myScore = player.get_score();
									output.writeBytes("score" + myScore + "\n"); //告訴對方我的成績
								}
								else {
									if(wait_for_instr || pause) { //遊戲剛開始時的基本設定
										wait_for_instr = false;
										pause = false;
										output.writeBytes("start\n"); //告訴對方遊戲開始
									}
									if(message.equals("didNotCatchMouse")) { //對方沒抓到老鼠，分數+1
										message = "";
										player.add_to_score(1); //分數+1
									}
									if(message.length()>4 && message.substring(0, 5).equals("catch")) { //對方抓到老鼠
										int disappearHoleNum = Integer.parseInt(message.substring(5));
										message = "";
										player.hole_number_mouse_hide[disappearHoleNum] = 1; //老鼠消失
									}
									if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] == 0) { //放老鼠
										player.hole_number_mouse[Player.hole_number_pressed] = 1;
										player.mouse_get_out(); //老鼠出現
										output.writeBytes(Player.hole_number_pressed + "\n"); //告訴對方在哪個洞放了老鼠
										Player.hole_number_pressed = -1;
					            	}
								}
							}
							else if(Player.state == 2) { //state==2，暫停
								if(pause == false) {
									wait_for_instr = false;
									pause = true;
									output.writeBytes("pause\n"); //告訴對方暫停
								}
							}
							else if(Player.state == 3) { } //state==3，等待輸贏判斷
						}
					}
				}
				else if(catchMouse == true) { //抓老鼠
					ClientBtnListener.playerSettingFrame2.dispose();
					initialize();
					Player player = new Player("打地鼠"); //產生玩家
					
					while(true) { //
						if(myScore!=-1 && hisScore!=-1) { //遊戲結束，產生輸贏的frame
							if(myScore > hisScore) {
								frameForWinnerAndLoser("打地鼠Client:你贏了", player.frame, output);
							}
							else if(myScore < hisScore) {
								frameForWinnerAndLoser("打地鼠Client:你輸了", player.frame, output);
							}
							else if(myScore == hisScore) {
								frameForWinnerAndLoser("打地鼠Client:平手", player.frame, output);
							}
							myScore = hisScore = -1;
						}
						else {
							System.out.println(Player.state);
							if(Player.state == -1) { //state==-1，退出遊戲
				            	System.exit(0);
							}
							else if(Player.state == 0) { //state==0，重新開始
								if(!wait_for_instr) { //一切初始
									for(int i=0; i<17; i++) { //老鼠全部消失
										if(player.hole_number_mouse[i] != 0) {
											player.hole_number_mouse[i] = 0;
											player.hole_number_mouse_hide[i] = 1;
										}
									}
									player.restart();
									wait_for_instr = true;
									message = "";
									output.writeBytes("restart\n"); //告訴對方重新開始
								}
							}
							else if(Player.state == 1) { //state==1，遊戲開始
								if(Player.win || Player.lose) { //遊戲時間到
									Player.state = 3; //state==3，等待輸贏判斷
									myScore = player.get_score();
									output.writeBytes("score" + myScore + "\n"); //告訴對方我的成績
								}
								else {
									if(wait_for_instr || pause) { //遊戲剛開始時的基本設定
										wait_for_instr = false;
										pause = false;
										output.writeBytes("start\n"); //告訴對方遊戲開始
									}
									if(!message.equals("")) { //得到對手輸出的老鼠出沒地
										int inputHoleNum = Integer.parseInt(message);
										if(player.hole_number_mouse[inputHoleNum] == 0 ) { //老鼠出現
											player.hole_number_mouse[inputHoleNum] = 1;
											player.mouse_get_out();
										}
										message = "";
									}
									if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] != 0) { //抓到老鼠，分數+1
										player.add_to_score(1); //分數+1
										player.hole_number_mouse_hide[Player.hole_number_pressed] = 1; //老鼠消失
										output.writeBytes("catch" + Player.hole_number_pressed + "\n"); //告訴對方哪個洞老鼠要消失
										Player.hole_number_pressed = -1;
					            	}
									if(player.didNotCatchMouse) { //告訴對方我沒抓到老鼠
										output.writeBytes("didNotCatchMouse\n");
										player.didNotCatchMouse = false;
									}
								}
							}
							else if(Player.state == 2) { //告訴對方我沒抓到老鼠
								if(pause == false) {
									wait_for_instr = false;
									pause = true;
									output.writeBytes("pause\n"); //告訴對方暫停
								}
							}
							else if(Player.state == 3) { } //state==3，等待輸贏判斷
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

	private static void frameForWinnerAndLoser(String text, JFrame playerFrame, DataOutputStream output) { //有遊戲結果後，選擇要再來一次or退出遊戲的frame
		
		playerFrame.setEnabled(false);
		
		JFrame playerSettingFrame3 = new JFrame(text); //Title:你贏了or你輸了
		playerSettingFrame3.setSize(350, 150);
		playerSettingFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton restartBtn = new JButton("再來一次");
		JButton exitBtn = new JButton("退出遊戲");
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
		
	private static class MyThread implements Runnable { //private class，用Thread不停的讀取對方輸入的資料

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
			
			if(command.equals("單機版")) {//singleVersion = true
				Client.singleVersion = true;
			}
			else if(command.equals("雙人版")) {//doubleVersion = true
				Client.doubleVersion = true;

				playerSettingFrame2 = new JFrame("Client"); //選擇要抓老鼠還是放老鼠
				playerSettingFrame2.setSize(350, 300);
				playerSettingFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				putBtn = new JButton("放地鼠");
				catchBtn = new JButton("抓地鼠");
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
			else if(command.equals("放地鼠")) {//putMouse = true
				Client.putMouse = true;
				catchBtn.setEnabled(false);
			}
			else if(command.equals("抓地鼠")) {//catchMouse = true
				Client.catchMouse = true;
				putBtn.setEnabled(false);
			}
			else if(command.equals("再來一次")) {
				Player.state = 0;
				deleteFrame.dispose();
				playerFrame.setEnabled(true);
				try {
					output.writeBytes("restart\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(command.equals("退出遊戲")) {
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
