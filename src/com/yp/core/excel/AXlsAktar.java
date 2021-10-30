package com.yp.core.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.log.MyLogger;
import com.yp.core.tools.StringTool;

public abstract class AXlsAktar implements IXlsAktar {

	public static final String UZANTI_XLS = ".xls";

	private static final String BASLIK = (new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
			.append(BaseConstants.EOL).append("<?mso-application progid=\"Excel.Sheet\"?>").append(BaseConstants.EOL)
			.append("<ss:Workbook").append(" xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"")
			.append(BaseConstants.EOL).append(" xmlns:c=\"urn:schemas-microsoft-com:office:component:spreadsheet\"")
			.append(BaseConstants.EOL).append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\"")
			.append(BaseConstants.EOL).append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"")
			.append(BaseConstants.EOL).append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"")
			.append(BaseConstants.EOL).append(" xmlns:x2=\"http://schemas.microsoft.com/office/excel/2003/xml\"")
			.append(BaseConstants.EOL).append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"")
			.append(BaseConstants.EOL).append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
			.append(BaseConstants.EOL)
			// .append("<DocumentProperties
			// xmlns=\"urn:schemas-microsoft-com:office:office\"></DocumentProperties>")
			.append("<OfficeDocumentSettings xmlns=\"urn:schemas-microsoft-com:office:office\"></OfficeDocumentSettings>")
			.append(BaseConstants.EOL)
			.append("<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\"></ExcelWorkbook>")
			.append(BaseConstants.EOL)).toString();

	private static final String STILLER_BASI = "<ss:Styles>\n";

	private static final String STILLER_SONU = "</ss:Styles>\n";

	private static final String SAYFA_BASI = "<ss:Worksheet ss:Name=\"%s\">\n";

	private static final String SAYFA_SONU = "</ss:Worksheet>\n";

	private static final String TABLO_BASI = "<ss:Table>\n";

	private static final String TABLO_SONU = "</ss:Table>\n";

	private static final String SATIR_BASI = "<ss:Row>\n";

	private static final String SATIR_SONU = "</ss:Row>\n";

	private static final String HUCRE_BASI = "<ss:Cell ";

	private static final String HUCRE_SONU = "</ss:Cell>\n";

	private static final String VERI_BASI = "<ss:Data ss:Type=\"";

	private static final String VERI_SONU = "</ss:Data>";

	private static final String KAPANIS = "</ss:Workbook>";

	private HashMap<String, IStil> dStilListe;

	private String dDosyaTamAdi;

	private static final int MAXVERISAYISI = 65000;

	protected String dosyaAdresi;

	public AXlsAktar(String pDosyaAdresi) {
		dosyaAdresi = pDosyaAdresi;
		dStilListe = new HashMap<>();
		dStilListe.put(DEFAULT, new Stil());
	}

	public void setDosyaAdi(String pDosyaAdi) {
		pDosyaAdi = BaseConstants.normalizeStringWithoutSpace(pDosyaAdi).replaceAll("[\\W]", "");
		dDosyaTamAdi = dosyaAdresi + BaseConstants.SLASH_OS + pDosyaAdi + UZANTI_XLS;
	}

	public int yaz(List<? extends IDataEntity> pDataList) {
		int dSnc = 0;
		File parent = new File(dDosyaTamAdi).getParentFile();
		boolean dDevam = parent.exists();
		if (!dDevam)
			dDevam = parent.mkdirs();

		if (dDevam) {
			try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dDosyaTamAdi),
					StandardCharsets.UTF_16)) {
				osw.write(BASLIK);
				osw.write(alStiller());
				dSnc = yazSayfa(osw, pDataList, SAYFA + 1);
				osw.write(KAPANIS);
				osw.flush();
			} catch (IOException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return dSnc;
	}

	@SuppressWarnings("unused")
	private void yazBaslik(OutputStreamWriter pYaz, Hucre pK) throws IOException {
		if (pK != null) {
			pYaz.write(HUCRE_BASI);
			if (pK.getBilesikHucreSayisi() > 0) {
				pYaz.write(SS_MERGE_ACROSS);
				pYaz.write(String.valueOf(pK.getBilesikHucreSayisi()));
				pYaz.write(SLASH);
			}
			if (pK.getIndeks() > 0) {
				pYaz.write(SS_INDEX);
				pYaz.write(String.valueOf(pK.getIndeks()));
				pYaz.write(SLASH);
			}
			pYaz.write(((Stil) dStilListe.get(pK.getStilId())).getIId());
			pYaz.write(GT);
			pYaz.write(VERI_BASI);
			pYaz.write(STRING);
			pYaz.write(pK.getDeger().toString());
			pYaz.write(VERI_SONU);
			pYaz.write(HUCRE_SONU);
		}
	}

	protected int yazSayfa(OutputStreamWriter pYaz, ResultSet pRs, String pSayfaAdi) throws IOException, SQLException {
		pYaz.write(String.format(SAYFA_BASI, pSayfaAdi));
		// pYaz.write(pSayfaAdi);
		// pYaz.write(SLASH_GT);
		int dSnc = yazTablo(pYaz, pRs);
		pYaz.write(SAYFA_SONU);
		return dSnc;

	}

	private int yazSayfa(OutputStreamWriter pYaz, List<? extends IDataEntity> pRsList, String pSayfaAdi)
			throws IOException {
		pYaz.write(String.format(SAYFA_BASI, pSayfaAdi));
		// pYaz.write(pSayfaAdi);
		// pYaz.write(SLASH_GT);
		int dSnc = yazTablo(pYaz, pRsList);
		pYaz.write(SAYFA_SONU);
		return dSnc;

	}

	protected boolean writePage = true;

	private int yazTablo(OutputStreamWriter pYaz, ResultSet pRs) throws IOException, SQLException {
		int dSnc = 0;
		pYaz.write(TABLO_BASI);
		yazSatirDizi(pYaz, alUstBilgi());

		do {
			dSnc += 1;
			yazSatir(pYaz, yukle(pRs));
		} while (writePage && dSnc < MAXVERISAYISI && pRs.next());

		yazSatirDizi(pYaz, alAltBilgi());
		pYaz.write(TABLO_SONU);
		return dSnc;

	}

	private int yazTablo(OutputStreamWriter pYaz, List<? extends IDataEntity> pListe) throws IOException {
		int dSnc = 0;
		pYaz.write(TABLO_BASI);
		yazSatirDizi(pYaz, alUstBilgi());
		for (int i = 0; i < pListe.size(); i++) {
			IDataEntity pVeri = pListe.get(i);
			yazSatir(pYaz, yukle(pVeri));
			dSnc += 1;
		}
		yazSatirDizi(pYaz, alAltBilgi());
		pYaz.write(TABLO_SONU);
		return dSnc;

	}

	private void yazSatirDizi(OutputStreamWriter pYaz, Hucre[][] pDizi) throws IOException {
		if (pDizi != null && pDizi.length > 0) {
			for (int i = 0; i < pDizi.length; i++) {
				yazSatir(pYaz, pDizi[i]);
			}
		}
	}

	private void yazSatir(OutputStreamWriter pYaz, Hucre[] pDizi) throws IOException {
		if (pDizi != null && pDizi.length > 0) {
			pYaz.write(SATIR_BASI);
			yazHucre(pYaz, pDizi);
			pYaz.write(SATIR_SONU);
		}
	}

	private void yazHucre(OutputStreamWriter pYaz, Hucre[] pK) throws IOException {
		if (pK != null && pK.length > 0) {
			for (int i = 0; i < pK.length; i++)
				if (pK[i] != null) {
					pYaz.write(HUCRE_BASI);
					if (pK[i].getBilesikHucreSayisi() > 0) {
						pYaz.write(SS_MERGE);
						pYaz.write(String.valueOf(pK[i].getBilesikHucreSayisi()));
						pYaz.write(SLASH);
					}
					if (pK[i].getIndeks() > 0) {
						pYaz.write(SS_INDEX);
						pYaz.write(String.valueOf(pK[i].getIndeks()));
						pYaz.write(SLASH);
					}
					pYaz.write(((Stil) dStilListe.get(pK[i].getStilId())).getIId());
					pYaz.write(GT);
					yazVeri(pYaz, pK[i]);
					pYaz.write(HUCRE_SONU);
				}
		}
	}

	private void yazVeri(OutputStreamWriter pYaz, Hucre pH) throws IOException {
		pYaz.write(VERI_BASI);
		pYaz.write(pH.getVeriTipi());
		pYaz.write(SLASH_GT);
		pYaz.write(pH.getDeger().toString());
		pYaz.write(VERI_SONU);
	}

	private String alStiller() {
		StringBuilder dSnc = new StringBuilder(STILLER_BASI);
		Set<String> dGec = dStilListe.keySet();
		String dAnh;
		for (Iterator<String> iter = dGec.iterator(); iter.hasNext();) {
			dAnh = iter.next();
			dSnc.append((dStilListe.get(dAnh)));
		}
		dSnc.append(STILLER_SONU);
		return dSnc.toString();
	}

	@Override
	public abstract Hucre[][] alUstBilgi();

	@Override
	public abstract Hucre[] alFormat();

	@Override
	public abstract Hucre[][] alAltBilgi();

	@Override
	public Hucre[] yukle(ResultSet pRs) throws SQLException {
		Hucre[] dSnc = alFormat();
		for (int j = 0; j < dSnc.length; j++) {
			dSnc[j].setDeger(pRs.getString(j + 1));
		}
		return dSnc;
	}

	public Hucre[] yukle(IDataEntity pVeri) {
		Hucre[] dSnc = alFormat();
		for (int j = 0; j < dSnc.length; j++) {
			dSnc[j].setDeger(pVeri.get(dSnc[j].getAlnAdi()));
		}
		return dSnc;
	}

	@Override
	public String getDosyaTamAdi() {
		return dDosyaTamAdi;
	}

	@Override
	public void setDosyaTamAdi(String pDosyaTamAdi) {
		dDosyaTamAdi = pDosyaTamAdi;
	}

	@Override
	public void ekleStil(IStil pStil) {
		dStilListe.put(pStil.getId(), pStil);
	}

	@Override
	public Stil alStil(String pStilId) {
		return (Stil) dStilListe.get(pStilId);
	}

	public class Hucre implements IHucre {

		private int dIndeks;

		private String dAlnAdi;

		private String dVeriTipi;

		private String dStilId;

		private int dBilesikHucreSayisi;

		private Object dDeger = YOK;

		public static final String DATA_TYPE_STRING = "String";

		public Hucre() {
			this(YOK, DATA_TYPE_STRING, DEFAULT_STIL, 0, 0);
		}

		public Hucre(Object pDeger) {
			this(pDeger, DATA_TYPE_STRING, DEFAULT_STIL, 0, 0);
		}

		public Hucre(Object pDeger, IStil pStil) {
			this(pDeger, DATA_TYPE_STRING, pStil, 0, 0);
		}

		public Hucre(Object pDeger, String pVeriTipi, IStil pStil) {
			this(pDeger, pVeriTipi, pStil, 0, 0);
		}

		public Hucre(Object pDeger, String pVeriTipi, IStil pStil, int pBilesikHucreSayisi, int pIndeks) {
			super();
			dIndeks = pIndeks;
			dVeriTipi = pVeriTipi;
			dStilId = pStil.getId();
			ekleStil(pStil);
			dBilesikHucreSayisi = pBilesikHucreSayisi;
			if (pDeger != null)
				dDeger = pDeger;
		}

		/**
		 * @return Returns the rowNum.
		 */
		@Override
		public int getIndeks() {
			return dIndeks;
		}

		/**
		 * @param pIndeks The rowNum to set.
		 */
		@Override
		public void setIndeks(int pIndeks) {
			dIndeks = pIndeks;
		}

		/**
		 * @return Returns the stilId.
		 */
		@Override
		public String getStilId() {
			return dStilId;
		}

		/**
		 * @param pStilId The stilId to set.
		 */
		@Override
		public void setStilId(String pStilId) {
			dStilId = pStilId;
		}

		/**
		 * @return Returns the veriTipi.
		 */
		@Override
		public String getVeriTipi() {
			return dVeriTipi;
		}

		/**
		 * @param pVeriTipi The veriTipi to set.
		 */
		@Override
		public void setVeriTipi(String pVeriTipi) {
			dVeriTipi = pVeriTipi;
		}

		/**
		 * @return Returns the bilesikHucreSayisi.
		 */
		@Override
		public int getBilesikHucreSayisi() {
			return dBilesikHucreSayisi;
		}

		/**
		 * @param pBilesikHucreSayisi The bilesikHucreSayisi to set.
		 */
		@Override
		public void setBilesikHucreSayisi(int pBilesikHucreSayisi) {
			dBilesikHucreSayisi = pBilesikHucreSayisi;
		}

		/**
		 * @return Returns the deger.
		 */
		@Override
		public Object getDeger() {
			return dDeger;
		}

		/**
		 * @param pDeger The deger to set.
		 */
		@Override
		public void setDeger(Object pDeger) {
			dDeger = YOK;
			if (pDeger != null)
				dDeger = pDeger;

		}

		private static final String YOK = "";

		@Override
		public String getAlnAdi() {
			return dAlnAdi;
		}

		@Override
		public void setAlnAdi(String pAlnAdi) {
			dAlnAdi = pAlnAdi;
		}
	}

	public final Font ARIEL = new Font("Arial", "162", "Swiss");

	public final Font COURIER_NEW = new Font("Courier New", "162", "Modern");

	public final Font TIMES_NEW_ROMAN = new Font("Times New Roman", "162", "Roman");

	public final Font LUCIDA_CONSOLE = new Font("Lucida Console", "162", "Modern");

	public class Font implements IFont {

		public Font(String fontName, String charSet, String family) {
			this.fontName = fontName;
			this.charSet = charSet;
			this.family = family;
		}

		@Override
		public String getCharSet() {
			return charSet;
		}

		@Override
		public void setCharSet(String pCharSet) {
			charSet = pCharSet;
		}

		@Override
		public String getFamily() {
			return family;
		}

		@Override
		public void setFamily(String pFamily) {
			family = pFamily;
		}

		@Override
		public String getFontName() {
			return fontName;
		}

		@Override
		public void setFontName(String pFontName) {
			fontName = pFontName;
		}

		private String fontName;

		private String charSet;

		private String family;
	}

	public final Stil DEFAULT_STIL = new Stil();

	public final Stil B1Sag = new Stil("u01", ARIEL, "12", true, false, "Red", null, Stil.HIZALAMA_SAG, false);

	public final Stil B1Orta = new Stil("u02", ARIEL, "12", true, false, "Red", null, Stil.HIZALAMA_ORTA, false);

	public final Stil B1Sol = new Stil("u03", ARIEL, "12", true, false, "Red", null, null, false);

	public final Stil B2Sag = new Stil("u11", ARIEL, "10", true, true, null, null, Stil.HIZALAMA_SAG, false);

	public final Stil B2Orta = new Stil("u12", ARIEL, "10", true, true, null, null, Stil.HIZALAMA_ORTA, false);

	public final Stil B2Sol = new Stil("u13", ARIEL, "10", true, true, null, null, null, false);

	public final Stil B3Sag = new Stil("u21", ARIEL, "8", true, true, null, null, Stil.HIZALAMA_SAG, false);

	public final Stil B3Orta = new Stil("u22", ARIEL, "8", true, true, null, null, Stil.HIZALAMA_ORTA, false);

	public final Stil B3Sol = new Stil("u23", ARIEL, "8", true, true, null, null, null, false);

	public final Stil VS1Sag = new Stil("u31", ARIEL, "8", false, false, null, "#,##0.000", Stil.HIZALAMA_SAG, false);

	public final Stil VS1Sol = new Stil("u31", ARIEL, "8", false, false, null, "#,##0.000", Stil.HIZALAMA_SOL, false);

	public final Stil VS1Orta = new Stil("u44", ARIEL, "8", false, false, null, "#,##0.000", Stil.HIZALAMA_ORTA, false);

	public final Stil VS2Sag = new Stil("u31", ARIEL, "8", false, false, null, "#,##0", Stil.HIZALAMA_SAG, false);

	public final Stil VS2Orta = new Stil("u44", ARIEL, "8", false, false, null, "#,##0", Stil.HIZALAMA_ORTA, false);

	public final Stil VM1Sag = new Stil("u41", ARIEL, "8", false, false, null, null, Stil.HIZALAMA_SAG, true);

	public final Stil VM1Orta = new Stil("u42", ARIEL, "8", false, false, null, null, Stil.HIZALAMA_ORTA, true);

	public final Stil VM1Sol = new Stil("u43", ARIEL, "8", false, false, null, null, null, true);

	public final Stil VM1SolKirmizi = new Stil("u431", ARIEL, "8", false, false, "Red", null, null, true);

	public final Stil VSTtr = new Stil("TTR31", ARIEL, "8", false, false, null, "#,##0.00TL", Stil.HIZALAMA_SAG, false);

	public class Stil implements IStil {

		public static final String HIZALAMA_SAG = "Right";

		public static final String HIZALAMA_SOL = "Left";

		public static final String HIZALAMA_ORTA = "Center";

		public static final String NO_WRAP = "nowrap";

		private String dIId;

		private String dId;

		private String name;

		private Font dFont = ARIEL;

		private boolean def = true;

		private boolean dMetniSigdir;

		private String alignment;

		private String format;

		private String color;

		private boolean italic;

		private boolean bold;

		private String size;

		public Stil() {
			dIId = STYLE_DEFAULT;
		}

		public Stil(String pId, Font pFont, String size, boolean bold, boolean italic, String color, String format,
				String alignment, boolean pMetniSigdir) {
			def = false;
			dId = pId;
			name = "_" + pId;
			dIId = STYLE_ID + dId + SLSH;
			this.size = size;
			this.bold = bold;
			this.italic = italic;
			this.color = color;
			this.format = format;
			this.alignment = alignment;
			if (pFont != null) {
				dFont = pFont;
			}
			dMetniSigdir = pMetniSigdir;
		}

		@Override
		public String getIId() {
			return dIId;
		}

		@Override
		public String getId() {
			return dId;
		}

		@Override
		public Font getFont() {
			return dFont;
		}

		@Override
		public void setFont(IFont pFont) {
			dFont = (Font) pFont;
		}

		@Override
		public String toString() {
			StringBuilder dSnc = new StringBuilder(STYLEID);
			if (def)
				dSnc.append("Default\"");
			else
				dSnc.append(dId).append("\"");
			dSnc.append(" ss:Name=\"").append(name).append("\"> \n");
			if ((!StringTool.isNull(alignment)) || dMetniSigdir) {
				dSnc.append(" <Alignment ");
				if (alignment != null && !alignment.equals(""))
					dSnc.append("ss:Horizontal=\"").append(alignment).append(SLASH);
				if (dMetniSigdir)
					dSnc.append("ss:WrapText=\"1\" ");
				dSnc.append("/> \n");
			}
			dSnc.append("<Font ss:FontName=\"").append(dFont.getFontName()).append(SLASH);
			dSnc.append("x:CharSet=\"").append(dFont.getCharSet()).append(SLASH);

			if (!StringTool.isNull(dFont.getFamily()))
				dSnc.append("x:Family=\"").append(dFont.getFamily()).append(SLASH);

			if (!StringTool.isNull(size))
				dSnc.append("ss:Size=\"").append(size).append(SLASH);
			if (bold)
				dSnc.append("ss:Bold=\"1\" ");
			if (italic)
				dSnc.append("ss:Italic=\"1\" ");
			if (!StringTool.isNull(color))
				dSnc.append("ss:Color=\"").append(color).append(SLSH);
			dSnc.append("/> \n");
			if (!StringTool.isNull(format))
				dSnc.append(" <NumberFormat ss:Format=\"").append(format).append("\"/> \n ");
			dSnc.append("</Style>").append(BaseConstants.EOL);
			return dSnc.toString();
		}

		@Override
		public boolean getBold() {
			return bold;
		}

		@Override
		public void setBold(boolean bold) {
			this.bold = bold;
		}

		@Override
		public String getColor() {
			return color;
		}

		@Override
		public void setColor(String color) {
			this.color = color;
		}

		@Override
		public String getFormat() {
			return format;
		}

		@Override
		public void setFormat(String format) {
			this.format = format;
		}

		@Override
		public boolean getItalic() {
			return italic;
		}

		@Override
		public void setItalic(boolean italic) {
			this.italic = italic;
		}

		@Override
		public String getSize() {
			return size;
		}

		@Override
		public void setSize(String size) {
			this.size = size;
		}

		@Override
		public String getAlignment() {
			return alignment;
		}

		@Override
		public void setAlignment(String alignment) {
			this.alignment = alignment;
		}

		@Override
		public boolean isMetniSigdir() {
			return dMetniSigdir;
		}

		@Override
		public void setMetniSigdir(boolean pMetniSigdir) {
			dMetniSigdir = pMetniSigdir;
		}

		private static final String STYLE_DEFAULT = "ss:StyleID=\"Default\"";

		private static final String STYLE_ID = "ss:StyleID=\"";

		private static final String SLSH = "\"";

		private static final String STYLEID = "<Style ss:ID=\"";
	}

	private static final String DEFAULT = "Default";

	private static final String SAYFA = "Sayfa ";

	private static final String SS_MERGE_ACROSS = "ss:MergeAcross=\" ";

	private static final String SLASH = "\" ";

	private static final String SS_INDEX = "ss:Index=\"";

	private static final String GT = " >";

	private static final String STRING = "String\">";

	private static final String SLASH_GT = "\">";

	private static final String SS_MERGE = "ss:MergeAcross=\"";
}
