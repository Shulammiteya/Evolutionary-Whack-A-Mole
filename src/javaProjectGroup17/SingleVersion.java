package javaProjectGroup17;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SingleVersion {

	private boolean wait_for_instr; //���s�}�l����l�Ʊ���
	
	public void play() { //������}�l
		wait_for_instr = false;
		Player player = new Player("���a��"); //���ͪ��a
		Player2 p2 = new Player2(); //���͵������a
		p2.call_mouse = -1; //call_mouse:�ѹ��n�X�Ӫ��}
		p2.call_mouse_get_out(); //�ѹ��}�l����
		
		while(true) {
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
					p2.call_mouse = -1;
					p2.call_mouse_speed = 3000;
					wait_for_instr = true;
				}
			}
			else if(Player.state == 1) { //state==1�A�C���}�l
				if(Player.win) { //���aĹ
					Player.state = 2;
					frameForWinnerAndLoser("�AĹ�F", player.frame);
				}
				else if(Player.lose) { //���a��
					Player.state = 2;
					frameForWinnerAndLoser("�A��F", player.frame);
				}
				else {
					if(wait_for_instr == true) { //�C����}�l�ɪ����ܼƳ]�w
						wait_for_instr = false;
						p2.call_mouse_speed = 3000;
					}
					if(p2.call_mouse != -1 && player.hole_number_mouse[p2.call_mouse] == 0 ) { //�o��������a��X���ѹ��X�S�a
						player.hole_number_mouse[p2.call_mouse] = 1;
						p2.call_mouse = -1;
						player.mouse_get_out(); //�ѹ��X��
					}	
					if(player.didNotCatchMouse) { //�S���ѹ��A����-1
						player.didNotCatchMouse = false;
						player.add_to_score(-1); //����-1
					}
					if(Player.hole_number_pressed != -1 && player.hole_number_mouse[Player.hole_number_pressed] != 0) { //���ѹ��A����+1
						player.add_to_score(1); //����+1
						player.hole_number_mouse_hide[Player.hole_number_pressed] = 1; //�ѹ�����
						Player.hole_number_pressed = -1;
	            	}
				}
			}
			else { //state==2�A�Ȱ�
				wait_for_instr = false;
			}
		}
	}
	private void frameForWinnerAndLoser(String text, JFrame playerFrame) { //���C�����G��A��ܭn�A�Ӥ@��or�h�X�C����frame
		
		playerFrame.setEnabled(false);
		
		JFrame playerSettingFrame3 = new JFrame(text); //Title:�AĹ�For�A��F
		playerSettingFrame3.setSize(350, 150);
		playerSettingFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton restartBtn = new JButton("�A�Ӥ@��");
		JButton exitBtn = new JButton("�h�X�C��");
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
			if(command.equals("�A�Ӥ@��")) {
				Player.state = 0;
				deleteFrame.dispose();
				playerFrame.setEnabled(true);
			}
			else if(command.equals("�h�X�C��")) {
				System.exit(0);
			}
		}
		
	}
	private class Player2 { //private class�A�������a

		public int call_mouse = -1; //�ѹ��n�X�{���}
		public int call_mouse_speed = 3000; //�ѹ��X�{���t��
		
		public void call_mouse_get_out() { //�C�j�X��A�N��ѹ��n�X�{���}�s�bcall_mouse
			new Thread(new Runnable() {
	            @Override
	            public void run() {
	                for (int i = 0; ; i++) { //i==0�ɡA�ѹ��X�{���t��(call_mouse_speed)�|����C
	                	call_mouse = (int) Math.floor(Math.abs(Math.random())*17);
	                	
	                	if(call_mouse_speed == 3000)
	                		i = 0;
	                	if(i < 24 && i%4 == 0) //�ѹ��X�{���t��(call_mouse_speed)�A�|��6�����[�t
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
