package zProjeFinal;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Urunler {
	private int urunNo; 
	private BigInteger urunBarkod;
	private String urunAd;
	private double urunAlisFiyat, urunSatisFiyat;
	private int urunKalanAdet;
	private String urunTedarikci;
	private ImageIcon urunFoto;
	private Timestamp urunIlkEklemeTarih, urunSonAlisTarih, urunSonSatisTarih;

	Urunler (BigInteger barkod) {
			ResultSet dbVeri = null;
			String urun_sorgu = "SELECT * FROM urunler where urun_barkod = '" + barkod + "'";
			dbVeri = DbBaglanti.sorgula(urun_sorgu);
			Blob urunFotoBlob = null;
			BufferedImage urunFotoBuff = null;
			InputStream inFoto = null;
			
			try {
				while(dbVeri.next()) { //bu while yazmasa before start of result set hatas� al�n�r
					urunNo = dbVeri.getInt("urun_no");
					urunBarkod = BigInteger.valueOf(dbVeri.getLong("urun_barkod")); //barkodlar en az 13 haneli oldu�u i�in biginteger ile al�nd�
					urunAd = dbVeri.getString("urun_ad");
					urunAlisFiyat = dbVeri.getDouble("urun_alis_fiyat");
					urunSatisFiyat = dbVeri.getDouble("urun_satis_fiyat");
					urunKalanAdet = dbVeri.getInt("urun_kalan_adet");
					urunTedarikci = dbVeri.getString("urun_tedarikci");
					urunIlkEklemeTarih = dbVeri.getTimestamp("urun_ilk_eklenme_tarih");
					urunSonAlisTarih = dbVeri.getTimestamp("urun_son_alis_tarih");
					urunSonSatisTarih = dbVeri.getTimestamp("urun_son_satis_tarih");
					urunFotoBlob = dbVeri.getBlob("urun_foto"); //(internet) blob olan resmi blob de�i�kene aktarmas
				}
				inFoto = urunFotoBlob.getBinaryStream(); //(internet) blob veriyi inputstreame aktarma
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		
			
			try {
				urunFotoBuff = ImageIO.read(inFoto); //(internet) inputstream edilen blob u bufferedimage yapma
			} catch (IOException e) {
				e.printStackTrace();
			}
//			ImageIcon sImageIcon = new ImageIcon(urunFotoBuff);//(internet) bufferedimage i imageicon yapma
//			Image sImage = sImageIcon.getImage();//imageicon u image e kaydetti
//			Image yeniBoyutImage = sImage.getScaledInstance(200, 150, Image.SCALE_SMOOTH); //image yeniden boyutland�rma ve yeni kaydetme
//			urunFoto = new ImageIcon(yeniBoyutImage);// yeniden boyutland�r�lm�� image i image icona kaydetti
			urunFoto = new ImageIcon(urunFotoBuff);
	}
	
	
	static int ekle(String barkod, String urunAd, String alis, String satis, String tedarikci, FileInputStream foto) {
//		boolean girdiKontrol = false;
		int basarili = 0;
		if(barkod.replaceAll("\\W", "").equals("") || urunAd.replaceAll("\\W", "").equals("") ||
				alis.replaceAll("\\W", "").equals("") || satis.replaceAll("\\W", "").equals("") ||
				tedarikci.replaceAll("\\W", "").equals("")) {
//			girdiKontrol = false;
			JOptionPane.showMessageDialog(null, "L�tfen b�t�n bo�luklar� uygun �ekilde giriniz.", "Depom Sepette", JOptionPane.INFORMATION_MESSAGE);
		} else {
//			girdiKontrol = true;
			String max_no_sorgu = "SELECT Max(urun_no) from urunler";
			ResultSet dbVeri = DbBaglanti.sorgula(max_no_sorgu);
			int yeni_no = 0;
			try {
				while(dbVeri.next()) {
					yeni_no = (dbVeri.getInt("Max(urun_no)")+1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			final int ilkStok = 0;
			final String alimSatimYokTarih = "0001-01-01 00:00:00";
			DateTimeFormatter sqlTipSaat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
			LocalDateTime eklenenSaat = LocalDateTime.now();
			
			String ekle_sql = "INSERT INTO `zprojefinal`.`urunler` (`urun_no`, `urun_barkod`, `urun_ad`, "+
																	"`urun_alis_fiyat`, `urun_satis_fiyat`, `urun_kalan_adet`, "+
																	"`urun_ilk_eklenme_tarih`, `urun_son_alis_tarih`, `urun_son_satis_tarih`,"+
																	" `urun_tedarikci`, `urun_foto`)"+
			"VALUES ('"+yeni_no+"', '"+barkod+"', '"+urunAd+"', '"+alis+"', '"+satis+"', '"+ilkStok+
					 "', '"+sqlTipSaat.format(eklenenSaat)+"', '"+alimSatimYokTarih+"', '"+alimSatimYokTarih+"', '"+tedarikci+"', ?)";
			basarili = DbBaglanti.preparedEkle(ekle_sql, 1, foto);
			if(basarili==1) {
				JOptionPane.showMessageDialog(null, "Yeni �r�n ba�ar�yla eklendi.", "Depom Sepette", JOptionPane.INFORMATION_MESSAGE);
				return basarili;
			} else JOptionPane.showMessageDialog(null, "Yeni �r�n eklenemedi.", "Depom Sepette", JOptionPane.WARNING_MESSAGE);
		}
		
		return basarili;
	}
	
	static int degistir(String barkod, String yeniBarkod, String ad, String alis, String satis, String tedarikci) {
		//UPDATE `zprojefinal`.`urunler` SET `urun_barkod` = '15', `urun_ad` = 'DGSTR', `urun_alis_fiyat` = '5', `urun_satis_fiyat` = '6', `urun_tedarikci` = 'HEHE' WHERE (`urun_no` = '11');
		int basarili = 0;
		if(barkod.replaceAll("\\W", "").equals("") || ad.replaceAll("\\W", "").equals("") ||
				alis.replaceAll("\\W", "").equals("") || satis.replaceAll("\\W", "").equals("") ||
				tedarikci.replaceAll("\\W", "").equals("") || yeniBarkod.replaceAll("\\W", "").equals("")) {
			JOptionPane.showMessageDialog(null, "L�tfen b�t�n bo�luklar� uygun �ekilde giriniz.", "Depom Sepette", JOptionPane.INFORMATION_MESSAGE);
		} else {
			String guncelle_sql = "UPDATE `zprojefinal`.`urunler` SET `urun_barkod` = '"+yeniBarkod+
																	  "', `urun_ad` = '"+ad+
																	  "', `urun_alis_fiyat` = '"+alis+
																	  "', `urun_satis_fiyat` = '"+satis+
																	  "', `urun_tedarikci` = '"+tedarikci+
																	  "' WHERE (`urun_barkod` = '"+barkod+"')";
			basarili = DbBaglanti.degistir(guncelle_sql);
			if(basarili==1) {
				JOptionPane.showMessageDialog(null, "��lem Ba�ar�l�", "Depom Sepette", JOptionPane.INFORMATION_MESSAGE);
				return basarili;
			} else {
				JOptionPane.showMessageDialog(null, "�r�n g�ncelleme ba�ar�s�z.", "Depom Sepette", JOptionPane.WARNING_MESSAGE);
				//HATA SAPTAMA
//				System.out.println("Eski barkod: "+barkod+"\nYeni Barkod: "+yeniBarkod+"\rAd: "+ad+"\rAlis-Satis: "+alis+"-"+satis+"\rTedarikci: "+tedarikci);
			}
		}
		return basarili;
	}
	
	
	static boolean barkodKontrol(BigInteger barkod) {
		boolean eslesme = false;
		String urun_sorgu = "SELECT * FROM urunler";
		ResultSet dbVeriKontrol = DbBaglanti.sorgula(urun_sorgu);
		try {
			while(dbVeriKontrol.next()) {
				if(barkod.equals(BigInteger.valueOf(dbVeriKontrol.getLong("urun_barkod"))))  {
					return eslesme=true;
				} else eslesme=false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eslesme;
	}
	
	static ResultSet arama(String urunAd) {
		ResultSet dbVeriArama = null;
		String arama_sql = "SELECT * FROM zprojefinal.urunler where urun_ad like '%"+urunAd+"%'"; //isim i�inde olan charlara g�re �eker
		dbVeriArama = DbBaglanti.sorgula(arama_sql);
		return dbVeriArama;
	}
	static ResultSet arama(BigInteger barkod) {
		ResultSet dbVeriArama = null;
		String arama_sql = "SELECT * FROM zprojefinal.urunler where urun_barkod like '"+barkod+"%'"; //barkodunda 4 olan t�m barkodlar gelir // de�i�ti barkod ba�tan ba�lar
		dbVeriArama = DbBaglanti.sorgula(arama_sql);
		return dbVeriArama;
	}

	public int getUrunNo() {
		return urunNo;
	}

	public BigInteger getUrunBarkod() {
		return urunBarkod;
	}

	public String getUrunAd() {
		return urunAd;
	}

	public double getUrunAlisFiyat() {
		return urunAlisFiyat;
	}

	public double getUrunSatisFiyat() {
		return urunSatisFiyat;
	}

	public int getUrunKalanAdet() {
		return urunKalanAdet;
	}

	public String getUrunTedarikci() {
		return urunTedarikci;
	}

	public ImageIcon getUrunFoto() {
		return urunFoto;
	}
	public int setUrunFoto(FileInputStream foto) {
		String update_sql = "UPDATE `zprojefinal`.`urunler` SET `urun_foto` = ? WHERE (`urun_barkod` = '"+this.urunBarkod+"');";
		int basarili = DbBaglanti.preparedEkle(update_sql, 1, foto);
		return basarili;
	}
	
	public Timestamp getUrunIlkEklemeTarih() {
		return urunIlkEklemeTarih;
	}

	public Timestamp getUrunSonAlisTarih() {
		return urunSonAlisTarih;
	}

	public Timestamp getUrunSonSatisTarih() {
		return urunSonSatisTarih;
	}
	
	public void setUrunKalanAdet(int urunKalanAdet) {
		this.urunKalanAdet = urunKalanAdet;
		String urun_update = "UPDATE `zprojefinal`.`urunler` SET `urun_kalan_adet` = '"+urunKalanAdet+"' WHERE (`urun_no` = '"+this.urunNo+"')";
		DbBaglanti.degistir(urun_update);
	}
	
	static void setUrunKalanAdet(String barkod, int eklenecekAdet) {
		BigInteger barkod_int= new BigInteger(barkod);
		new Urunler(barkod_int).getUrunKalanAdet(); // bu sat�ra gerek olmayabilir ama �imdi bozmayal�m �al���yor
		String urun_update = "UPDATE `zprojefinal`.`urunler` SET `urun_kalan_adet` = '"+(new Urunler(barkod_int).getUrunKalanAdet()+eklenecekAdet)+"' WHERE (`urun_barkod` = '"+barkod+"')";
//		System.out.println(urun_update);
		DbBaglanti.degistir(urun_update);
	}
}
