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
	
	public void run() { //繼承run的部分
		
		File soundFile = new File(filename); 
		AudioInputStream audioInputStream = null;
		
		try { 
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);//將音樂讀進音樂串流 
		}
		catch (Exception e1) { 
			e1.printStackTrace(); //列印異常的堆疊資訊，指明錯誤原因
			return; 
		}
		
		AudioFormat format = audioInputStream.getFormat(); //獲取音樂格式
		SourceDataLine auline = null; //提供將音頻數據寫入數據行的緩衝區中的方法。
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format); //根據指定信息構造數據行的信息對象，這些信息包括單個音頻格式。
		try { 
			auline = (SourceDataLine) AudioSystem.getLine(info); //將數據寫入緩衝區內
			auline.open(format); //開啟 串流中包含的音頻資料的格式
		}
		catch (Exception e) { 
			e.printStackTrace(); //列印異常的堆疊資訊，指明錯誤原因
			return; 
		}
		
		auline.start(); //auline開始
		int nBytesRead = 0; 
		byte[] abData = new byte[512]; 
		try { 
			while (nBytesRead != -1) { 
				nBytesRead = audioInputStream.read(abData, 0, abData.length); //使用指定輸入串流中的音頻資料建構具有請求的格式和長度（以範例幀為單位）的音頻輸入串流
				if (nBytesRead >= 0) 
					auline.write(abData, 0, nBytesRead); //通過此源數據線將音頻數據寫入混音器
			} 
		}
		catch (IOException e) { 
			e.printStackTrace(); 
			return; 
		}
		finally { 
			auline.drain(); //auline排空
			auline.close(); //關閉此音頻輸入串流並釋放與該串流關聯的所有系統資源
		}	 
	}
}
