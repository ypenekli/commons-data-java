package com.yp.core.tools;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.yp.core.BaseConstants;

public final class StringTool {

	public static final String KURUS;
	public static final String EMAILREGEX;
	public static final java.util.regex.Pattern EMAILPATERN;

	static {
		KURUS = BaseConstants.getString("Kurus");
		EMAILREGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		EMAILPATERN = java.util.regex.Pattern.compile(EMAILREGEX);
	}

	/**
	 * Icinde {i} seklinde parametrelerin oldugu bir String'i bu parametreleri
	 * Object dizisinden gelen degerler ile replace ederek geri dondurur.<br />
	 * 
	 * @param pMetin
	 *            Parametrik ifadelerin bulundugu sorgu metni
	 * @param pPrmDizi
	 *            Parametre degerlerinin tutuldugu Nesne dizisi
	 * @return Parametreleri degerler haline getirilmis String
	 * 
	 *         String.format("The font %s doesn't have unicode support: %s",
	 *         fontname, e.getMessage()))
	 */
	public static String format(final String pString, final Object... pParams) {
		final int dTpl = pParams.length;
		String output = pString;
		for (int i = 0; i < dTpl; i++) {
			String value = pParams[i] == null ? "" : pParams[i].toString();
			output = output.replaceAll("\\{" + i + "\\}", value);
		}
		return output;
	}

	public static boolean isNull(String pString) {
		return pString == null || "".equals(pString.trim());
	}

	public static String ucaseFirstCharTR(String pText) {
		return ucaseFirstChar(pText, BaseConstants.LOCALE_TR);
	}

	public static String ucaseFirstChar(String pText, Locale pLocale) {
		pText = pText.toLowerCase(pLocale);
		Pattern def = Pattern.compile(BaseConstants.NONLETTER_CHARS_REGEX);
		Matcher matcher = def.matcher(pText);
		int index = 0;
		int baslangic = 0;
		String dEslesen, dYasak;
		StringBuilder sb = new StringBuilder();

		while (matcher.find()) {
			baslangic = matcher.start();
			dEslesen = pText.subSequence(index, baslangic).toString();
			index = matcher.end();
			dYasak = pText.substring(baslangic, index);
			if (dEslesen.length() > 0) {
				sb.append(dEslesen.substring(0, 1).toUpperCase(pLocale));
				if (dEslesen.length() > 1) {
					sb.append(dEslesen.substring(1));
				}
			}
			sb.append(dYasak);
		}
		dEslesen = pText.subSequence(index, pText.length()).toString();
		if (dEslesen.length() > 0) {
			sb.append(dEslesen.substring(0, 1).toUpperCase(pLocale));
			if (dEslesen.length() > 1) {
				sb.append(dEslesen.substring(1));
			}
		}
		return sb.toString();
	}

	// public static String appendPluralSuffix(String pKelime, final boolean
	// pCogul, final boolean pOzelIsim) {}

	public static int findChar(final String pWord, final String pRegex) {
		Pattern dPattern = Pattern.compile(pRegex);
		Matcher dMatcher = dPattern.matcher(pWord);
		if (dMatcher.find()) {
			return dMatcher.start();
		} else {
			return -1;
		}
	}

	public static String trimRight(String pString) {
		return trimRight(pString, ' ');
	}

	/**
	 * Bu fonksiyon bosluk karakteri icin calisan trim fonksiyonunu herhangi bir
	 * karakter icin genisletir, verilen kelimenin sonundan istenilen karakteri
	 * atar.
	 * 
	 * @param pMetin
	 *            karakterin trim edileceï¿½i kelime
	 * @param pHarf
	 *            sondan cikartilacak harf
	 * @return Verilen String'in sagindaki karakterler cikartilir ve kalan String
	 *         geri dï¿½ndï¿½rï¿½lï¿½r
	 */
	public static String trimRight(String pString, char pChar) {
		if (pString != null) {
			char[] charsString = pString.toCharArray();
			int i = charsString.length;

			while (charsString[--i] == pChar)
				;
			// if ((++i) % 3 != 0) {
			// i += (i) % 3 == 1 ? 2 : 1;
			// }
			return pString.substring(0, i + 1);
		}
		return null;
	}

	/**
	 * Bu fonksiyon bosluk karakteri icin calisan trim fonksiyonunu herhangi bir
	 * karakter icin genisletir, verilen kelimenin basindan istenilen karakteri
	 * atar.
	 * 
	 * @param pMetin
	 *            karakterin trim edileceï¿½i kelime
	 * @param pHarf
	 *            soldan cikartilacak harf
	 * @return Verilen String'in solundaki karakterler cikartilir ve kalan String
	 *         geri dï¿½ndï¿½rï¿½lï¿½r
	 */
	public static String trimLeft(String pString, char pChar) {
		if (pString != null) {
			char[] charsString = pString.toCharArray();
			int i = 0;
			while (charsString[i++] == pChar && i < charsString.length)
				;
			return pString.substring(i - 1, charsString.length);
		}
		return null;
	}

	public static String checkCharsHTML(String pString, boolean pMod) {
		return replaceAmpersandHTML(replaceDoubleQuotesHTML(replaceSingleQuotesHTML(trim(pString), pMod), pMod), pMod);
	}

	/**
	 * '&' karakterini "&amp;" ile degistirir.
	 * 
	 * @param pAck
	 *            : Icerisinde '&' karakteri karakteri degistirilecek string
	 * @return Eger pAck degerinde '&' karakteri varsa bu karakter yerine '&amp;'
	 *         stringWithTurkishCharslmis halini dondurur, yoksa ayni degeri
	 *         dondurur
	 */
	public static String replaceAmpersandHTML(String pString, boolean pMod) {
		if (pMod) {
			if (pString.indexOf('&') == -1) // '&' karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replace("&", "&amp;");
		} else {
			if (pString.indexOf("&amp;") == -1) // '&' karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replace("&amp;", "&");
		}
	}

	/**
	 * ' (tek tirnak) karakterini "%27" ile degistirir.
	 * 
	 * @param pAck
	 *            : Icerisinde ' (tek tirnak) karakteri degistirilecek string
	 * @return Eger pAck degerinde ' (tek tirnak) karakteri varsa bu karakter yerine
	 *         "%27" stringWithTurkishCharslmis halini dondurur, yoksa ayni degeri
	 *         dondurur
	 */
	public static String replaceSingleQuotesHTML(String pString, boolean pMod) {
		if (pMod) {
			if (pString.indexOf('\'') == -1) // ' (tek tirnak) karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replace("'", "%27");
		} else {
			if (pString.indexOf("%27") == -1) // ' (tek tirnak) karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replace("%27", "'");
		}
	}

	/**
	 * ' (cift tirnak) karakterini "%34" ile degistirir.
	 * 
	 * @param pAck
	 *            : Icerisinde " (cift tirnak) karakteri degistirilecek string
	 * @return Eger pAck degerinde " (cift tirnak) karakteri varsa bu karakter
	 *         yerine "%34" stringWithTurkishCharslmis halini dondurur, yoksa ayni
	 *         degeri dondurur
	 */
	public static String replaceDoubleQuotesHTML(String pString, boolean pMod) {
		if (pMod) {
			if (pString.indexOf('\"') == -1) // ' (cift tirnak) karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replaceAll("\"", "%34");
		} else {
			if (pString.indexOf("%34") == -1) // ' (tek tirnak) karakteri var mi
			{
				return pString; // yoksa ayni string i geri dondur
			}
			return pString.replace("%34", "\"");
		}
	}

	/**
	 * Gelen degerin basindaki ve sonundaki bosluk karakterlerini silmede
	 * kullanilmaktadir.
	 * 
	 * @param pDeger
	 *            : Gelen deger
	 * @return Eger gelen degerin null ise bosluk degilse basindaki ve sonundaki
	 *         bosluk karakterlerinin silinmis yeni halini dondurur
	 */
	public static String trim(String pString) {
		if (pString != null) {
			return pString.trim();
		}
		return "";
	}

	public static String trim(String pString, String pTrailingString, int pLength) {
		String trimed = "";
		if (pString != null) {
			trimed = pString.trim();
			if (trimed.length() - pTrailingString.length() > pLength)
				trimed = trimed.substring(0, pLength - pTrailingString.length()) + pTrailingString;
		}
		return trimed;
	}

	private static String padRight(String pString, char pFillingChar, int pLength) {
		char[] result = new char[pLength];
		char[] charsString = pString.toCharArray();
		System.arraycopy(charsString, 0, result, 0, charsString.length);
		Arrays.fill(result, charsString.length, pLength, pFillingChar);
		return String.copyValueOf(result);
	}

	public static String padLeft(String pString, char pFillingChar, int pLength) {
		char[] result = new char[pLength];
		char[] charsString = pString.toCharArray();
		int dI = result.length - charsString.length;
		System.arraycopy(charsString, 0, result, dI, charsString.length);
		Arrays.fill(result, 0, dI, pFillingChar);
		return String.copyValueOf(result);
	}

	public static String padRight(String pString, String pFillingChar, int pLength) {
		if (pString != null) {
			if (pFillingChar.length() == 1) {
				return padRight(pString, pFillingChar.toCharArray()[0], pLength);
			}

			int i = pString.length();
			if (i < pLength) {
				char[] charsString = pString.toCharArray();
				char[] charsFilling = pString.toCharArray();
				int j = pFillingChar.length();
				int k = 0;
				while (pLength - j > i) {
					System.arraycopy(charsFilling, 0, charsString, i + j * (k++), j);
					// pMetin += pDeger;
					// pToplamUzunluk -= j;
				}
				System.arraycopy(charsFilling, 0, charsString, i + j * k, pLength - i);
				// pMetin += pDeger.substring(0, pToplamUzunluk - i);
			}
		}
		return pString;
	}

	private static final String regexDecimal = "^-?\\d*\\.\\d+$";
	private static final String regexDecimalTr = "^-?\\d*\\,\\d+$";
	private static final String regexInteger = "^-?\\d+$";
	private static final String regexDouble = regexDecimal + "|" + regexInteger;
	private static final String regexDoubleTr = regexDecimalTr + "|" + regexInteger;

	public static boolean isNumber(String pNumber) {
		if (!isNull(pNumber))
			return pNumber.matches(regexDouble);
		// return pNumber.matches("-?\\d+(\\.\\d+)?");
		return false;
	}

	public static boolean isNumberTr(String pNumber) {
		if (!isNull(pNumber))
			return pNumber.matches(regexDoubleTr);
		return false;
	}

	public static int getRepeatCount(String pString, String pKontrol) {
		if (pString != null && pKontrol != null) {
			String[] dGecDizi = pString.split(pKontrol);
			if (dGecDizi != null && dGecDizi.length > 0)
				return dGecDizi.length;
		}
		return 0;
	}

	public static BigDecimal getBigDecimal(NumberFormat pFormat, String pNumber, BigDecimal pNullValue) {
		if (!isNull(pNumber))
			try {
				return  BigDecimal.valueOf(pFormat.parse(pNumber).doubleValue());
			} catch (ParseException e) {
			}
		return pNullValue;
	}

	public static BigDecimal getBigDecimal(String pNumber, BigDecimal pNullValue) {
		return getBigDecimal(BaseConstants.FORMAT_NUMBER, pNumber, pNullValue);
	}

	public static Double getDouble(NumberFormat pFormat, String pNumber, Double pNullValue) {
		if (!isNull(pNumber))
			try {
				return pFormat.parse(pNumber).doubleValue();
			} catch (ParseException e) {
			}
		return pNullValue;
	}

	public static Double getDouble(String pNumber, Double pNullValue) {
		if (!isNull(pNumber))
			try {
				return Double.parseDouble(pNumber);
			} catch (Exception e) {
			}
		return pNullValue;
	}

	public static Double getDouble(Integer pNumber, Integer pFraction, Double pNullValue) {
		return getDouble(pNumber.toString(), pFraction.toString(), pNullValue);
	}

	public static Double getDouble(String pNumber, String pFraction, Double pNullValue) {
		return getDouble(BaseConstants.FORMAT_NUMBER_TR, pNumber + "," + pFraction, pNullValue);
	}

	public static Integer getInteger(String pNumber, Integer pNullValue) {
		if (!isNull(pNumber))
			try {
				return BaseConstants.FORMAT_NUMBER.parse(pNumber).intValue();
			} catch (ParseException e) {
			}
		return pNullValue;
	}

	public static String getString(DateFormat pFormat, Date pDate) {
		if (pDate != null)
			return pFormat.format(pDate);
		return "";
	}

	public static String getStringShortTr(Date pDate) {
		if (pDate != null)
			return BaseConstants.FORMAT_DATE_SHORT_TR.format(pDate);
		return "";
	}

	public static String getString2xShortTr(Date pDate) {
		if (pDate != null)
			return BaseConstants.FORMAT_DATE_2xSHORT_TR.format(pDate);
		return "";
	}

	public static String getString(Integer pValue) {
		if (pValue != null)
			return pValue.toString();
		return "";
	}

	public static String getString(Double pValue) {
		if (pValue != null)
			return pValue.toString();
		return "";
	}

	public interface SeperatedDecimalString {
		String getWhole();

		String getBody();

		String getDecimal();
	}

	public static SeperatedDecimalString getStringFromDecimal(final Double pValue) {
		final String[] res = new String[] { "0", "0" };
		if (pValue != null) {
			String[] temp = pValue.toString().split("\\.", 2);
			res[0] = temp[0];
			if (temp.length > 0)
				res[1] = temp[1].replaceAll("^$", "0");
		}

		// double d = 123.456;
		//
		// long a = (long) d;
		// double f = d - a;
		//
		// while (Math.abs((long) f - f) > 0.000001)
		// f *= 10;
		//
		// long b = (long) f;

		return new SeperatedDecimalString() {

			@Override
			public String getWhole() {
				return getString(pValue);
			}

			@Override
			public String getBody() {
				return res[0];
			}

			@Override
			public String getDecimal() {
				return res[1];
			}
		};
	}

	public static String getString(NumberFormat pFormat, Double pValue) {
		if (pValue != null)
			return pFormat.format(pValue);
		return "";
	}

	public static String getString(NumberFormat pFormat, BigDecimal pValue) {
		if (pFormat != null)
			return pFormat.format(pValue);
		return "";
	}

	public static String getString(BigDecimal pValue, BigDecimal pNullValue) {
		if (pValue != null)
			return pValue.toString();
		else if (pNullValue != null)
			return pNullValue.toString();
		return "";
	}

	public static String getString(BigDecimal pValue) {
		if (pValue != null)
			return pValue.toString();
		return "";
	}

	public static String getStringFromCurrency(Double pValue) {
		if (pValue != null)
			return BaseConstants.FORMAT_CURRENCY.format(pValue);
		return "";
	}

	public static String getStringFromCurrencyTr(Double pValue) {
		if (pValue != null)
			return BaseConstants.FORMAT_CURRENCY_TR.format(pValue);
		return "";
	}

	public static String getStringFromNumber(Double pSayi) {
		int birler, onlar, yuzler, binler;

		birler = (int) (pSayi % 10d);
		onlar = (int) ((pSayi / 10) % 10);
		yuzler = (int) ((pSayi / 100) % 10);
		binler = (int) ((pSayi / 1000) % 10);

		String[] birlik = { "Sýfýr", "Bir", "Ýki", "Üç", "Dört", "Beþ", "Altý", "Yedi", "Sekiz", "Dokuz" };
		String[] onluk = { "", "On", "Yirmi", "Otuz", "Kýrk", "Elli", "Altmýþ", "Yetmiþ", "Seksen", "Doksan" };
		String[] yuzluk = { "", "Yüz", "Ýkiyüz", "Üçyüz", "Dörtyüz", "Beþyüz", "Altýyüz", "Yediyüz", "Sekizyüz",
				"Dokuzyüz" };
		String[] binlik = { "", "Bin", "Ýkibin", "Üçbin", "Dörtbin", "Beþbin", "Altýbin", "Yedibin", "Sekizbin",
				"Dokuzbin" };

		return binlik[binler] + yuzluk[yuzler] + onluk[onlar] + birlik[birler];
	}

	public static String getStringFromNumber(String sayi, int kurusbasamak, String parabirimi, String parakurus,
			String diyez, String[] bb1, String[] bb2, String[] bb3) {
		// kurusbasamak virgülden sonra gösterilecek basamak sayýsý
		// bb1, bb2, bb3 ise sayýlarýn deðiþik dillerde stringWithTurkishCharslmasý için
		// list
		// parabirimi = TL gibi , parakurus = Kuruþ gibi
		// diyez baþa ve sona kapatma iþareti atar # gibi
		String[] b1 = { "", "bir", "iki", "üç", "dört", "beþ", "altý", "yedi", "sekiz", "dokuz" };
		String[] b2 = { "", "on", "yirmi", "otuz", "kýrk", "elli", "altmýþ", "yetmiþ", "seksen", "doksan" };
		String[] b3 = { "", "yüz", "bin", "milyon", "milyar", "trilyon", "katrilyon" };

		if (bb1 != null) { // farklý dil kullanýmý yada farklý stringWithTurkishCharsm biçimi için
			b1 = bb1;
		}
		if (bb2 != null) { // farklý dil kullanýmý
			b2 = bb2;
		}
		if (bb3 != null) { // farklý dil kullanýmý
			b3 = bb3;
		}

		String say1, say2 = ""; // say1 virgül öncesi, say2 kuruþ bölümü
		String sonuc = "";

		sayi = sayi.replace(",", "."); // virgül noktaya çevrilir

		if (sayi.indexOf(".") > 0) { // nokta varsa (kuruþ)

			say1 = sayi.substring(0, sayi.indexOf(".")); // virgül öncesi
			say2 = sayi.substring(sayi.indexOf("."), sayi.length()); // virgül
																		// sonrasý,
																		// kuruþ

		} else {
			say1 = sayi; // kuruþ yok
		}

		char[] rk = say1.toCharArray(); // rakamlara ayýrma

		String son;
		int w = 1; // iþlenen basamak
		int sonaekle = 0; // binler on binler yüzbinler vs. için sona bin
							// (milyon,trilyon...) eklenecek mi?
		int kac = rk.length; // kaç rakam var?
		int sonint; // iþlenen basamaðýn rakamsal deðeri
		int uclubasamak = 0; // hangi basamakta (birler onlar yüzler gibi)
		int artan = 0; // binler milyonlar milyarlar gibi artýþlarý yapar
		String gecici;

		if (kac > 0) { // virgül öncesinde rakam var mý?

			for (int i = 0; i < kac; i++) {

				son = String.valueOf(rk[kac - 1 - i]); // son karakterden
														// baþlayarak çözümleme
														// yapýlýr.
				sonint = Integer.parseInt(son); // iþlenen rakam

				if (w == 1) { // birinci basamak bulunuyor

					sonuc = b1[sonint] + sonuc;

				} else if (w == 2) { // ikinci basamak

					sonuc = b2[sonint] + sonuc;

				} else if (w == 3) { // 3. basamak

					if (sonint == 1) {
						sonuc = b3[1] + sonuc;
					} else if (sonint > 1) {
						sonuc = b1[sonint] + b3[1] + sonuc;
					}
					uclubasamak++;
				}

				if (w > 3) { // 3. basamaktan sonraki iþlemler

					if (uclubasamak == 1) {

						if (sonint > 0) {
							sonuc = b1[sonint] + b3[2 + artan] + sonuc;
							if (artan == 0) { // birbin yazmasýný engelle
								if (kac - 1 == i) { // 11000 stringWithTurkishCharslýþýný düzeltme
									sonuc = sonuc.replace(b1[1] + b3[2], b3[2]);
								}
							}
							sonaekle = 1; // sona bin eklendi
						} else {
							sonaekle = 0;
						}
						uclubasamak++;

					} else if (uclubasamak == 2) {

						if (sonint > 0) {
							if (sonaekle > 0) {
								sonuc = b2[sonint] + sonuc;
								sonaekle++;
							} else {
								sonuc = b2[sonint] + b3[2 + artan] + sonuc;
								sonaekle++;
							}
						}
						uclubasamak++;

					} else if (uclubasamak == 3) {

						if (sonint > 0) {
							if (sonint == 1) {
								gecici = b3[1];
							} else {
								gecici = b1[sonint] + b3[1];
							}
							if (sonaekle == 0) {
								gecici = gecici + b3[2 + artan];
							}
							sonuc = gecici + sonuc;
						}
						uclubasamak = 1;
						artan++;
					}

				}

				w++; // iþlenen basamak

			}
		} // if(kac>0)

		if ("".equals(sonuc)) { // virgül öncesi sayý yoksa para birimi yazma
			parabirimi = "";
		}

		say2 = say2.replace(".", "");
		String kurus = "";

		if (!"".equals(say2)) { // kuruþ hanesi varsa

			if (kurusbasamak > 3) { // 3 basamakla sýnýrlý
				kurusbasamak = 3;
			}
			if (say2.length() > kurusbasamak) { // belirlenen basamak kadar
												// rakam stringWithTurkishCharslýr
				say2 = say2.substring(0, kurusbasamak);
			} else say2 = StringTool.padRight(say2, '0', kurusbasamak);

			char[] kurusrk = say2.toCharArray(); // rakamlara ayýrma
			kac = kurusrk.length; // kaç rakam var?
			w = 1;

			for (int i = 0; i < kac; i++) { // kuruþ hesabý

				son = String.valueOf(kurusrk[kac - 1 - i]); // son karakterden
															// baþlayarak
															// çözümleme
															// yapýlýr.
				sonint = Integer.parseInt(son); // iþlenen rakam

				if (w == 1) { // birinci basamak

					if (kurusbasamak > 0) {
						kurus = b1[sonint] + kurus;
					}

				} else if (w == 2) { // ikinci basamak
					if (kurusbasamak > 1) {
						kurus = b2[sonint] + kurus;
					}

				} else if (w == 3) { // 3. basamak
					if (kurusbasamak > 2) {
						if (sonint == 1) { // 'biryüz' ü engeller
							kurus = b3[1] + kurus;
						} else if (sonint > 1) {
							kurus = b1[sonint] + b3[1] + kurus;
						}
					}
				}
				w++;
			}
			if ("".equals(kurus)) { // virgül öncesi sayý yoksa para birimi
									// yazma
				parakurus = "";
			} else {
				kurus = kurus + " ";
			}
			kurus = kurus + parakurus; // kuruþ hanesine 'kuruþ' kelimesi ekler
		}

		sonuc = diyez + sonuc + " " + parabirimi + " " + kurus + diyez;

		return sonuc;

	}

	public static boolean isValidEmailAddress(String email) {
		java.util.regex.Matcher m = EMAILPATERN.matcher(email);
		return m.matches();
	}

	public static boolean isValidPhoneNumber(final String pPhoneNumber) {
		return !isNull(pPhoneNumber) && pPhoneNumber.length() > 9;
	}

	public static String maskString(String pSourceString, int pOfset, char pMask) {
		int length = pSourceString.length();
		StringBuilder sb = new StringBuilder(length);
		if (pOfset > length)
			pOfset = length - 1;
		sb.append(pSourceString.substring(0, pOfset));
		for (int dI = pOfset - 1; dI < length; dI++) {
			sb.append(pMask);
		}
		return sb.toString();
	}

	public static boolean equalsIgnoreCaseAndLocale(String firstString, String secondString) {
		if (firstString == null)
			return false;
		if (secondString == null)
			return false;

		firstString = replaceTurkishCharsWithLatin(firstString.replace(BaseConstants.SPACE, ""))
				.toUpperCase(Locale.ENGLISH);
		secondString = replaceTurkishCharsWithLatin(secondString.replace(BaseConstants.SPACE, ""))
				.toUpperCase(Locale.ENGLISH);
		return firstString.equals(secondString);
	}

	public static String replaceTurkishCharsWithLatin(String stringWithTurkishChars) {
		stringWithTurkishChars = stringWithTurkishChars.replace("ü", "u");
		stringWithTurkishChars = stringWithTurkishChars.replace("ý", "i");
		stringWithTurkishChars = stringWithTurkishChars.replace("ö", "o");
		stringWithTurkishChars = stringWithTurkishChars.replace("ü", "u");
		stringWithTurkishChars = stringWithTurkishChars.replace("þ", "s");
		stringWithTurkishChars = stringWithTurkishChars.replace("ð", "g");
		stringWithTurkishChars = stringWithTurkishChars.replace("ç", "c");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ü", "U");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ý", "I");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ö", "O");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ü", "U");
		stringWithTurkishChars = stringWithTurkishChars.replace("Þ", "S");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ð", "G");
		stringWithTurkishChars = stringWithTurkishChars.replace("Ç", "C");
		return stringWithTurkishChars;
	}

	public static void replaceInFile(String fileToReplace, String fileReplacements) throws IOException {
		try (Scanner sc = new Scanner(new File(fileToReplace))) {
			// instantiating the StringBuffer class
			StringBuilder buffer = new StringBuilder();
			// Reading lines of the file and appending them to StringBuffer
			while (sc.hasNextLine()) {
				buffer.append(sc.nextLine() + System.lineSeparator());
			}
			String fileContents = buffer.toString();

			try (Scanner sc2 = new Scanner(new File(fileReplacements))) {
				while (sc2.hasNextLine()) {
					String[] replaces = sc2.nextLine().split("=");
					if (replaces != null && replaces.length > 1)
						fileContents = fileContents.replaceAll(replaces[0], replaces[1]);
				}
			}

			// instantiating the FileWriter class
			try (FileWriter writer = new FileWriter(fileToReplace)) {
				writer.append(fileContents);
				writer.flush();
			}

		}

	}

	public static void main(String[] args) {
		if (args != null && args.length > 2) {
			try {
				FileUtils.copyFile(new File(args[0]), new File(args[1]));
				replaceInFile(args[1], args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

