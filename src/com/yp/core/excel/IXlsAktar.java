package com.yp.core.excel;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IXlsAktar {

	public interface IFont {
		public String getCharSet();

		public void setCharSet(String pCharSet);

		public String getFamily();

		public void setFamily(String pFamily);

		public String getFontName();

		public void setFontName(String pFontName);

	}

	public interface IHucre {
		public static  final  String VERITIPI_METIN = "String";

		public static  final  String VERITIPI_SAYISAL = "Number";

		public static  final  String VERITIPI_TARIH = "DateTime";

		public int getIndeks();

		public void setIndeks(int pIndeks);

		public String getStilId();

		public void setStilId(String pStilId);

		public String getVeriTipi();

		public void setVeriTipi(String pVeriTipi);

		public int getBilesikHucreSayisi();

		public void setBilesikHucreSayisi(int pBilesikHucreSayisi);

		public Object getDeger();

		public void setDeger(Object pDeger);

		public String getAlnAdi();

		public void setAlnAdi(String pAlnAdi);

	}

	public interface IStil {
		public String getIId();

		public String getId();

		public IFont getFont();

		public void setFont(IFont pFont);

		public boolean getBold();

		public void setBold(boolean bold);

		public String getColor();

		public void setColor(String color);

		public String getFormat();

		public void setFormat(String format);

		public boolean getItalic();

		public void setItalic(boolean italic);

		public String getSize();

		public void setSize(String size);

		public String getAlignment();

		public void setAlignment(String alignment);

		public boolean isMetniSigdir();

		public void setMetniSigdir(boolean pMetniSigdir);

	};

	public abstract IHucre[][] alUstBilgi();

	public abstract IHucre[] alFormat();

	public abstract IHucre[][] alAltBilgi();

	public abstract IHucre[] yukle(ResultSet pRs) throws SQLException;

	public abstract String getDosyaTamAdi();

	public abstract void setDosyaTamAdi(String pDosyaTamAdi);

	public abstract void ekleStil(IStil pStil);

	public abstract IStil alStil(String pStilId);
}