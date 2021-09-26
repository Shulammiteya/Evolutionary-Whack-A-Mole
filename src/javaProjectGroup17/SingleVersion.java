package javaProjectGroup17;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SingleVersion {

	private boolean wait_for_instr; //重新開始的初始化控制
	
	public void play() { //單機版開始
		wait_for_instr = false;
		Player player = new Player("打地鼠"); //產生玩家
		Player2 p2 = new Player2(); //產生虛擬玩家
		p2.call_mouse = -1; //call_mouse:老鼠要出來的洞
		p2.call_mouse_get_out(); //老鼠開始產生
		
		while(true) {
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
					p2.call_mouse = -1;
					p2.call_mouse_speed = 3000;
					wait_for_instr = true;
				}
			}
			else if(Player.state == 1) { //state==1，遊戲開始
				if(Player.win) { //玩家贏
					Player.state = 2;
					frameForWinnerAndLoser("你贏了", player.frame);
				}
				else if(Player.lose) { //玩家輸
					Player.state = 2;
					frameForWinnerAndLoser("你輸了", player.frame);
				}
				else {
					if(wait_for_instr == true) { //遊戲剛開始時的基本變數設定
						wait_for_instr = false;
						p2.call_mouse_speed = 3000;
					}
					if(p2.call_mouse != -1 && player.hole_number_mouse[p2.call_mouse] == 0 ) { //得到虛擬玩家輸出的老鼠出沒地
						player.hole_number_mouse[p2.call_mouse] = 1;
						p2.call_mouse = -1;
						player.mouse_get_out(); //老鼠出來
					}	
					if(player.didNotCatchMouse) { //沒抓到老鼠，分數-1
						player.didNotCatchMouse = false;
						player.add_to_score(-1); //分數-1
					}
					if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] != 0) { //抓到老鼠，分數+1
						player.add_to_score(1); //分數+1
						player.hole_number_mouse_hide[Player.hole_number_pressed] = 1; //老鼠消失
						Player.hole_number_pressed = -1;
	            	}
				}
			}
			else { //state==2，暫停
				wait_for_instr = false;
			}
		}
	}
	private void frameForWinnerAndLoser(String text, JFrame playerFrame) { //有遊戲結果後，選擇要再來一次or退出遊戲的frame
		
		playerFrame.setEnabled(false);
		
		JFrame playerSettingFrame3 = new JFrame(text); //Title:你贏了or你輸了
		playerSettingFrame3.setSize(350, 150);
		playerSettingFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton restartBtn = new JButton("再來一次");
		JButton exitBtn = new JButton("退出遊戲");
		restartBtn.addActionListener(new SingleVersionBtnListener(playerSettingFrame3, playerFrame));
		exitBtn.addActionListener(new SingleVersionBtnListener(playerSettingFrame3, playerFrame));
		restartBtn.setLocation(12, 30);
		exitBtn.setLocation(172, 30);
		restartBtn.setSize(150,50);
		exitBtn.setSize(150,50);

		playerSettingFrame3.setLayout(null);
		playerSettingFrame3.add(restartBtn);
		playerSettingFrame3.add(exitBtn);
		playerSettingFrame3.setVisible(true);
	}
	
	private class SingleVersionBtnListener implements ActionListener{ //ButtonListener
		
		public JFrame deleteFrame, playerFrame;
		
		SingleVersionBtnListener(JFrame delete_Frame, JFrame player_frame) {
			deleteFrame = delete_Frame;
			playerFrame = player_frame;
		}
		
		public void actionPerformed(ActionEvent e) {
			String command=e.getActionCommand();
			if(command.equals("再來一次")) {
				Player.state = 0;
				deleteFrame.dispose();
				playerFrame.setEnabled(true);
			}
			else if(command.equals("退出遊戲")) {
				System.exit(0);
			}
		}
		
	}
	private class Player2 { //private class，虛擬玩家

		public int call_mouse = -1; //老鼠要出現的洞
		public int call_mouse_speed = 3000; //老鼠出現的速度
		
		public void call_mouse_get_out() { //每隔幾秒，就把老鼠要出現的洞存在call_mouse
			new Thread(new Runnable() {
	            @Override
	            public void run() {
	                for (int i = 0; ; i++) { //i==0時，老鼠出現的速度(call_mouse_speed)會比較慢
	                	call_mouse = (int) Math.floor(Math.abs(Math.random())*17);
	                	
	                	if(call_mouse_speed == 3000)
	                		i = 0;
	                	if(i < 24 && i%4 == 0) //老鼠出現的速度(call_mouse_speed)，會有6次的加速
	                		call_mouse_speed = call_mouse_speed /3 *2;
	                	
	                    try {
	                        Thread.sleep(call_mouse_speed);
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    }
	                }
	            }
	        }).start();
		}
		
	}
	
}
