package javaProjectGroup17;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Player {	

	public static int state; //遊戲進行的狀態，(-1:結束，0:重來，1:進行中，2:暫停，3:等待狀態不做任何指令)
	public static final int position[][] = new int[17][2]; //洞的位置
	public static int hole_number_pressed; //滑鼠選到的洞，0~16，(未選:hole_number_pressed=-1)
	public static boolean win, lose; //贏或輸
	
	public int hole_number_mouse[] = new int[17]; //每個洞的老鼠狀態，(沒有老鼠=0，出現=1，出現中=2)
	public int hole_number_mouse_hide[] = new int[17]; //每個洞的老鼠消失狀態，(老鼠要當下消失的洞=1，其餘為0)
	public boolean didNotCatchMouse; //沒抓到老鼠
	public JFrame frame = new JFrame();//設一個frame
	
	private int score, time;
	private Timer timer;
	private JLabel score_label = new JLabel(); //顯示score的label
	private JLabel time_label = new JLabel(); //顯示time的label
	private JLabel[][] mouse_label = new JLabel[17][10]; //顯示老鼠圖樣的標籤
	private String title; //標題有兩種，打地鼠or放地鼠
	
	Player(String titleStr) { //constructor
		state = 0;
		title = titleStr;
		position[0][0] = 300;
		position[0][1] = 195;
		position[1][0] = 475;
		position[1][1] = 195;
		position[2][0] = 655;
		position[2][1] = 195;
		position[3][0] = 188;
		position[3][1] = 265;
		position[4][0] = 388;
		position[4][1] = 265;
		position[5][0] = 588;
		position[5][1] = 265;
		position[6][0] = 788;
		position[6][1] = 265;
		position[7][0] = 245;
		position[7][1] = 340;
		position[8][0] = 480;
		position[8][1] = 340;
		position[9][0] = 718;
		position[9][1] = 340;
		position[10][0] = 100;
		position[10][1] = 419;
		position[11][0] = 360;
		position[11][1] = 419;
		position[12][0] = 620;
		position[12][1] = 419;
		position[13][0] = 860;
		position[13][1] = 419;
		position[14][0] = 150;
		position[14][1] = 508;
		position[15][0] = 475;
		position[15][1] = 508;
		position[16][0] = 795;
		position[16][1] = 508;
		for(int i=0; i<17; i++) {
			hole_number_mouse[i] = hole_number_mouse_hide[i] = 0;
		}
		
		for(int i=0; i<17; i++) { //把老鼠圖片預備好
			for(int j=1; j<=10; j++) {
				mouse_label[i][10-j] = new JLabel(new ImageIcon("src/image/"+(j+5)+".png"));//新增label1放圖片
				mouse_label[i][10-j].setLocation(position[i][0]-44, position[i][1]-147);
				mouse_label[i][10-j].setSize(100,100);
				mouse_label[i][10-j].setVisible(false);
				frame.add(mouse_label[i][10-j]);
			}
		}
		
		set_two_label(); //兩個label初始
		restart(); //初始化
		timer = new Timer(); //建立計時器
		//設定計時器
		//第一個參數為"欲執行的工作",會呼叫對應的run() method
		//第二個參數為程式啟動後,"延遲"指定的毫秒數後"第一次"執行該工作
		//第三個參數為每間隔多少毫秒執行該工作
		timer.schedule(new MyTimerTask(this), 500, 1000);
		frame.add(score_label); //分數label加在frame上
		frame.add(time_label); //秒數label加在frame上
		show_frame(); //建立frame
	}
	
	public void restart() { //重新開始時需要初始化的數值都會在這裡初始好
		Music music_background = new Music("background.wav");
		music_background.start();
		didNotCatchMouse = false;
		win = false;
		lose = false;
		hole_number_pressed = -1;
		set_score(0);
		set_time(30);
	}
	public void add_to_score(int addend) { //score加某個值
		score += addend;
		score_label.setText("分數:" + score);
	}
	public void add_to_time(int addend) { //time加某個值
		time += addend;
		time_label.setText("時間:" + time);
	}
	public void mouse_get_out() { //用Thread讓老鼠出來
		Thread playerThread = new Thread(new threadForMouseGetOut());
		playerThread.start();
	}
	public int get_score() { //回傳score
		return score;
	}
	
	private void set_score(int score_tmp) { //score設定值
		score = score_tmp;
		score_label.setText("分數:" + score);
	}
	private void set_time(int tmp_time) { //time設定值
		time = tmp_time;
		time_label.setText("時間:" + time);
	}
	private void set_two_label() { //兩個label的初始
		score_label.setLocation(30, 35); //設定score所屬label的位置 
		score_label.setSize(300, 35); //大小
		score_label.setFont(new java.awt.Font("Luxi Mono", 1, 25));//字形,粗體or細體,字大小
		time_label.setLocation(800, 35);
		time_label.setSize(300, 35);
		time_label.setFont(new java.awt.Font("Luxi Mono", 1, 25));
	}
	private void show_frame() { //建立frame
		frame.setTitle(title); //定title
		frame.setSize(920,580); //設定大小
		
		ImageIcon icon1 = new ImageIcon("src/image/background.jpg"); //插入圖片
		JLabel background = new JLabel(icon1); //新增label放圖片
		background.setBounds(0, 0, icon1.getIconWidth(), icon1.getIconHeight()); //設定label的位置、大小，label大小為圖片的大小
		
		JMenu diner = new JMenu("Menu"); //設定menu名
		JMenuItem item1 = new JMenuItem("重新開始"); //新增三個menu中的類別的名
		JMenuItem item2 = new JMenuItem("開始/暫停");
		JMenuItem item3 = new JMenuItem("退出");
		item1.addActionListener(new MyBtnListener()); //每一個item建一個新的MyButtonListener(Player裡的private class)
		item2.addActionListener(new MyBtnListener());
		item3.addActionListener(new MyBtnListener());
		diner.add(item1); //將三個item加到diner中
		diner.add(item2);
		diner.add(item3);
		JMenuBar bar = new JMenuBar(); //要求新的MenuBar
		bar.add(diner); //加到diner中
		
		frame.addMouseListener(new MyMouseEvent()); //將滑鼠偵測加到frame中
		frame.add(background); //背景加在frame上
		frame.setLayout(null); //設定frame的layout
		frame.setJMenuBar(bar);
		frame.setVisible(true); //使frame可視
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	private class threadForMouseGetOut implements Runnable { //用10個mouse_label的出現與消失模擬老鼠出洞的過程
		@Override
		public void run() {
			int mouse_hole = 0;
        	for(int i=0; i<17; i++) { //把老鼠要出來的洞紀錄在mouse_hole
        		if(hole_number_mouse[i] == 1) { //hole_number_mouse[i]為1代表老鼠要出來
        			hole_number_mouse[i] = 2; //hole_number_mouse[i]為2代表老鼠正在出來
        			mouse_hole = i;
        			break;
        		}
        	}
            for (int i = 0; i < 17; i++) { //用10個mouse_label的出現與消失模擬老鼠出洞的過程
            	if(Player.state == 1 || Player.state == 0) { //state==1，老鼠出來，state==0，老鼠消失
            		if(hole_number_mouse_hide[mouse_hole] == 1) { //抓到老鼠，hole_number_mouse_hide為1表示老鼠要消失
                		i = 16; //老鼠消失
                		hole_number_mouse_hide[mouse_hole] = 0;
				Music music_get = new Music("get.wav");
				music_get.start();
                	}
                	if(i == 0) {
                		mouse_label[mouse_hole][0].setVisible(true);
                	}
                	else if(i >= 10 && i <= 12) {
        				mouse_label[mouse_hole][9].setVisible(true);
                	}
                	else if(i == 13) {
                		mouse_label[mouse_hole][9].setVisible(false);
        				mouse_label[mouse_hole][7].setVisible(true);
                	}
                	else if(i == 14) {
                		mouse_label[mouse_hole][7].setVisible(false);
        				mouse_label[mouse_hole][5].setVisible(true);
                	}
                	else if(i == 15) { //老鼠回到洞裡了，若是能進行到i==15，則表示沒抓到老鼠，didNotCatchMouse=true
                		mouse_label[mouse_hole][5].setVisible(false);
        				mouse_label[mouse_hole][3].setVisible(true);
        				didNotCatchMouse = true;
                	}
                	else if(i == 16) { //老鼠消失
                		hole_number_mouse[mouse_hole] = 0;
                		for(int j=0; j<10; j++)
                			mouse_label[mouse_hole][j].setVisible(false);
                	}
                	else {
                		mouse_label[mouse_hole][i-1].setVisible(false);
        				mouse_label[mouse_hole][i].setVisible(true);
                	}
            	}
            	else { //state不為0也不為1，則老鼠不動，i在for迴圈會加1，所以再減1
            		i--;
            	}
            	
                try {
                    Thread.sleep(100); //等待
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
		}
	}
	
	private class MyBtnListener implements ActionListener{ //private class，Player專用的ButtonListener
		
		public void actionPerformed(ActionEvent e) {// ActionListener包含的函式 拿來自己定義
			
			String command=e.getActionCommand();
			
			if(command.equals("重新開始")) { //重新開始，state=0
				System.out.println("you pressed 重新開始");
				Player.state = 0;
			}
			else if(command.equals("開始/暫停")) {
				System.out.println("you pressed 開始/暫停");
				if(Player.state == 1) {//若繼續則暫停
					Player.state = 2;
				}
				else {//若暫停則繼續
					Player.state = 1;
				}
			}
			else if(command.equals("退出")) {//退出，state=-1
				System.out.println("you pressed 退出");
				Player.state = -1;
			}
			
		}
		
	}
	
	private static class MyTimerTask extends TimerTask{ //private class，計時用
		
		private Player p;
		
		MyTimerTask(Player tmp_p) {
			p = tmp_p;
		}
		
		public void run(){
			if(Player.state == 1) {
				if(p.time <= 0) {
					if(p.score > 0) {
						win = true;
					}
					else {
						lose = true;
					}
				}
				else {
					p.add_to_time(-1);
				}
			}
		}		
	}
	
}
