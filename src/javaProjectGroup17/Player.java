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

	public static int state; //�C���i�檺���A�A(-1:�����A0:���ӡA1:�i�椤�A2:�Ȱ��A3:���ݪ��A����������O)
	public static final int position[][] = new int[17][2]; //�}����m
	public static int hole_number_pressed; //�ƹ���쪺�}�A0~16�A(����:hole_number_pressed=-1)
	public static boolean win, lose; //Ĺ�ο�
	
	public int hole_number_mouse[] = new int[17]; //�C�Ӭ}���ѹ����A�A(�S���ѹ�=0�A�X�{=1�A�X�{��=2)
	public int hole_number_mouse_hide[] = new int[17]; //�C�Ӭ}���ѹ��������A�A(�ѹ��n��U�������}=1�A��l��0)
	public boolean didNotCatchMouse; //�S���ѹ�
	public JFrame frame = new JFrame();//�]�@��frame
	
	private int score, time;
	private Timer timer;
	private JLabel score_label = new JLabel(); //���score��label
	private JLabel time_label = new JLabel(); //���time��label
	private JLabel[][] mouse_label = new JLabel[17][10]; //��ܦѹ��ϼ˪�����
	private String title; //���D����ءA���a��or��a��
	
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
		
		for(int i=0; i<17; i++) { //��ѹ��Ϥ��w�Ʀn
			for(int j=1; j<=10; j++) {
				mouse_label[i][10-j] = new JLabel(new ImageIcon("src/image/"+(j+5)+".png"));//�s�Wlabel1��Ϥ�
				mouse_label[i][10-j].setLocation(position[i][0]-44, position[i][1]-147);
				mouse_label[i][10-j].setSize(100,100);
				mouse_label[i][10-j].setVisible(false);
				frame.add(mouse_label[i][10-j]);
			}
		}
		
		set_two_label(); //���label��l
		restart(); //��l��
		timer = new Timer(); //�إ߭p�ɾ�
		//�]�w�p�ɾ�
		//�Ĥ@�ӰѼƬ�"�����檺�u�@",�|�I�s������run() method
		//�ĤG�ӰѼƬ��{���Ұʫ�,"����"���w���@��ƫ�"�Ĥ@��"����Ӥu�@
		//�ĤT�ӰѼƬ��C���j�h�ֲ@�����Ӥu�@
		timer.schedule(new MyTimerTask(this), 500, 1000);
		frame.add(score_label); //����label�[�bframe�W
		frame.add(time_label); //���label�[�bframe�W
		show_frame(); //�إ�frame
	}
	
	public void restart() { //���s�}�l�ɻݭn��l�ƪ��ƭȳ��|�b�o�̪�l�n
		Music music_background = new Music("background.wav");
		music_background.start();
		didNotCatchMouse = false;
		win = false;
		lose = false;
		hole_number_pressed = -1;
		set_score(0);
		set_time(30);
	}
	public void add_to_score(int addend) { //score�[�Y�ӭ�
		score += addend;
		score_label.setText("����:" + score);
	}
	public void add_to_time(int addend) { //time�[�Y�ӭ�
		time += addend;
		time_label.setText("�ɶ�:" + time);
	}
	public void mouse_get_out() { //��Thread���ѹ��X��
		Thread playerThread = new Thread(new threadForMouseGetOut());
		playerThread.start();
	}
	public int get_score() { //�^��score
		return score;
	}
	
	private void set_score(int score_tmp) { //score�]�w��
		score = score_tmp;
		score_label.setText("����:" + score);
	}
	private void set_time(int tmp_time) { //time�]�w��
		time = tmp_time;
		time_label.setText("�ɶ�:" + time);
	}
	private void set_two_label() { //���label����l
		score_label.setLocation(30, 35); //�]�wscore����label����m 
		score_label.setSize(300, 35); //�j�p
		score_label.setFont(new java.awt.Font("Luxi Mono", 1, 25));//�r��,����or����,�r�j�p
		time_label.setLocation(800, 35);
		time_label.setSize(300, 35);
		time_label.setFont(new java.awt.Font("Luxi Mono", 1, 25));
	}
	private void show_frame() { //�إ�frame
		frame.setTitle(title); //�wtitle
		frame.setSize(920,580); //�]�w�j�p
		
		ImageIcon icon1 = new ImageIcon("src/image/background.jpg"); //���J�Ϥ�
		JLabel background = new JLabel(icon1); //�s�Wlabel��Ϥ�
		background.setBounds(0, 0, icon1.getIconWidth(), icon1.getIconHeight()); //�]�wlabel����m�B�j�p�Alabel�j�p���Ϥ����j�p
		
		JMenu diner = new JMenu("Menu"); //�]�wmenu�W
		JMenuItem item1 = new JMenuItem("���s�}�l"); //�s�W�T��menu�������O���W
		JMenuItem item2 = new JMenuItem("�}�l/�Ȱ�");
		JMenuItem item3 = new JMenuItem("�h�X");
		item1.addActionListener(new MyBtnListener()); //�C�@��item�ؤ@�ӷs��MyButtonListener(Player�̪�private class)
		item2.addActionListener(new MyBtnListener());
		item3.addActionListener(new MyBtnListener());
		diner.add(item1); //�N�T��item�[��diner��
		diner.add(item2);
		diner.add(item3);
		JMenuBar bar = new JMenuBar(); //�n�D�s��MenuBar
		bar.add(diner); //�[��diner��
		
		frame.addMouseListener(new MyMouseEvent()); //�N�ƹ������[��frame��
		frame.add(background); //�I���[�bframe�W
		frame.setLayout(null); //�]�wframe��layout
		frame.setJMenuBar(bar);
		frame.setVisible(true); //��frame�i��
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	private class threadForMouseGetOut implements Runnable { //��10��mouse_label���X�{�P���������ѹ��X�}���L�{
		@Override
		public void run() {
			int mouse_hole = 0;
        	for(int i=0; i<17; i++) { //��ѹ��n�X�Ӫ��}�����bmouse_hole
        		if(hole_number_mouse[i] == 1) { //hole_number_mouse[i]��1�N��ѹ��n�X��
        			hole_number_mouse[i] = 2; //hole_number_mouse[i]��2�N��ѹ����b�X��
        			mouse_hole = i;
        			break;
        		}
        	}
            for (int i = 0; i < 17; i++) { //��10��mouse_label���X�{�P���������ѹ��X�}���L�{
            	if(Player.state == 1 || Player.state == 0) { //state==1�A�ѹ��X�ӡAstate==0�A�ѹ�����
            		if(hole_number_mouse_hide[mouse_hole] == 1) { //���ѹ��Ahole_number_mouse_hide��1��ܦѹ��n����
                		i = 16; //�ѹ�����
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
                	else if(i == 15) { //�ѹ��^��}�̤F�A�Y�O��i���i==15�A�h��ܨS���ѹ��AdidNotCatchMouse=true
                		mouse_label[mouse_hole][5].setVisible(false);
        				mouse_label[mouse_hole][3].setVisible(true);
        				didNotCatchMouse = true;
                	}
                	else if(i == 16) { //�ѹ�����
                		hole_number_mouse[mouse_hole] = 0;
                		for(int j=0; j<10; j++)
                			mouse_label[mouse_hole][j].setVisible(false);
                	}
                	else {
                		mouse_label[mouse_hole][i-1].setVisible(false);
        				mouse_label[mouse_hole][i].setVisible(true);
                	}
            	}
            	else { //state����0�]����1�A�h�ѹ����ʡAi�bfor�j��|�[1�A�ҥH�A��1
            		i--;
            	}
            	
                try {
                    Thread.sleep(100); //����
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
		}
	}
	
	private class MyBtnListener implements ActionListener{ //private class�APlayer�M�Ϊ�ButtonListener
		
		public void actionPerformed(ActionEvent e) {// ActionListener�]�t���禡 ���Ӧۤv�w�q
			
			String command=e.getActionCommand();
			
			if(command.equals("���s�}�l")) { //���s�}�l�Astate=0
				System.out.println("you pressed ���s�}�l");
				Player.state = 0;
			}
			else if(command.equals("�}�l/�Ȱ�")) {
				System.out.println("you pressed �}�l/�Ȱ�");
				if(Player.state == 1) {//�Y�~��h�Ȱ�
					Player.state = 2;
				}
				else {//�Y�Ȱ��h�~��
					Player.state = 1;
				}
			}
			else if(command.equals("�h�X")) {//�h�X�Astate=-1
				System.out.println("you pressed �h�X");
				Player.state = -1;
			}
			
		}
		
	}
	
	private static class MyTimerTask extends TimerTask{ //private class�A�p�ɥ�
		
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
