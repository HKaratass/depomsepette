package zProjeFinal;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class CikisUyariFrame {
	
	CikisUyariFrame() {
		if(cikisUyari()==0) {
			System.exit(0);
		}
	}
	
	public int cikisUyari() {
		ImageIcon icon = new ImageIcon("Images/exitico.png");
		String[] responses= {"Evet","�ptal"};
		
		int answer =JOptionPane.showOptionDialog(
				null, //component
				"��kmak istedi�inize emin misiniz?", //message 
				"Depom Sepette", //title
				JOptionPane.OK_CANCEL_OPTION, //option type 
				JOptionPane.WARNING_MESSAGE, //message type
				icon, // icon
				responses, // option da yazacaklar 0-1-2 olarak de�i�tirir
				0);
		return answer; //evet ��k�� yap 0
		 				// iptal 1 - �arp� ile kapatma -1
	}
	
	
	
}
