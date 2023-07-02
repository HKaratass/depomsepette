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
		String[] responses= {"Evet","Ýptal"};
		
		int answer =JOptionPane.showOptionDialog(
				null, //component
				"Çýkmak istediðinize emin misiniz?", //message 
				"Depom Sepette", //title
				JOptionPane.OK_CANCEL_OPTION, //option type 
				JOptionPane.WARNING_MESSAGE, //message type
				icon, // icon
				responses, // option da yazacaklar 0-1-2 olarak deðiþtirir
				0);
		return answer; //evet çýkýþ yap 0
		 				// iptal 1 - çarpý ile kapatma -1
	}
	
	
	
}
