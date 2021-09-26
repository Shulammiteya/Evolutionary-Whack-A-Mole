package javaProjectGroup17;

import java.io.*;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Music extends Thread {
	
	private String filename; 
	
	Music(String wavfile) { 
		filename = wavfile; //constructor
	}
	
	public void run() { //�~��run������
		
		File soundFile = new File(filename); 
		AudioInputStream audioInputStream = null;
		
		try { 
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);//�N����Ū�i���֦�y 
		}
		catch (Exception e1) { 
			e1.printStackTrace(); //�C�L���`�����|��T�A�������~��]
			return; 
		}
		
		AudioFormat format = audioInputStream.getFormat(); //������֮榡
		SourceDataLine auline = null; //���ѱN���W�ƾڼg�J�ƾڦ檺�w�İϤ�����k�C
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format); //�ھګ��w�H���c�y�ƾڦ檺�H����H�A�o�ǫH���]�A��ӭ��W�榡�C
		try { 
			auline = (SourceDataLine) AudioSystem.getLine(info); //�N�ƾڼg�J�w�İϤ�
			auline.open(format); //�}�� ��y���]�t�����W��ƪ��榡
		}
		catch (Exception e) { 
			e.printStackTrace(); //�C�L���`�����|��T�A�������~��]
			return; 
		}
		
		auline.start(); //auline�}�l
		int nBytesRead = 0; 
		byte[] abData = new byte[512]; 
		try { 
			while (nBytesRead != -1) { 
				nBytesRead = audioInputStream.read(abData, 0, abData.length); //�ϥΫ��w��J��y�������W��ƫغc�㦳�ШD���榡�M���ס]�H�d�ҴV�����^�����W��J��y
				if (nBytesRead >= 0) 
					auline.write(abData, 0, nBytesRead); //�q�L�����ƾڽu�N���W�ƾڼg�J�V����
			} 
		}
		catch (IOException e) { 
			e.printStackTrace(); 
			return; 
		}
		finally { 
			auline.drain(); //auline�ƪ�
			auline.close(); //���������W��J��y������P�Ӧ�y���p���Ҧ��t�θ귽
		}	 
	}
}
