package javaProjectGroup17;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseEvent implements MouseListener{
	
	public void mouseClicked(MouseEvent a) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		if(Player.state == 1) {
			for(int i=0; i<17; i++) //判斷有沒有選到洞
				if( Math.abs( e.getX()-Player.position[i][0] ) < 36 && ((e.getY()-Player.position[i][1])<10 && (e.getY()-Player.position[i][1])>-40) ) {
					Player.hole_number_pressed = i;
					break;
				}
		}
	}
	
}
