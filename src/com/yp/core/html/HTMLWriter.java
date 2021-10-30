package com.yp.core.html;

import com.yp.core.BaseConstants;
import com.yp.core.user.IUser;

public abstract class HTMLWriter {

	protected static final String TEXT_ALIGN_RIGHT = "text-align:right";
	protected static final String TEXT_ALIGN_LEFT = "text-align:left";
	protected static final String TEXT_ALIGN_CENTER = "text-align:center";
	protected static final String BORDER = " border=\"%s\"";
	protected static final String WITDTH = " width:\"%s\"";
	protected static final String COLSPAN = " colspan=\"%s\"";
	protected static final String STYLE = " style=\"%s\"";

	protected StringBuilder writer;

	protected IUser user;

	public HTMLWriter(IUser pUser) {
		user =  pUser;
	}

	protected void openWriter() {
		openWriter(BaseConstants.CHAR_UTF_8);
	}

	protected void openWriter(String pEncoding) {
		// formatter = new Formatter();
		// formatter.out().append("<html>\n<head>").
		writer = new StringBuilder("<html>\n<head>").append("<meta charset=\"").append(pEncoding).append("\">\n")
				.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=").append(pEncoding)
				.append("\">\n").append("</head>\n<body>\n");

	}

	protected void openTable(int border, String... style) {
		writer.append("<table ");
		if (border > 0)
			writer.append(String.format(BORDER, border));
		if (style != null && style.length > 0)
			writer.append(String.format(STYLE, style[0]));

		writer.append(">\n");
	}

	protected void closeTable() {
		writer.append("</table>\n");
	}

	protected void openTableRow(String... style) {
		if (style != null && style.length > 0)
			writer.append("<tr style=\"").append(style[0]).append("\" >\n");
		else writer.append("<tr>\n");
	}

	protected void closeTableRow() {
		writer.append("</tr>\n");
	}

	protected void addTableHCell(Object value, int colspan, String... style) {
		writer.append("<th ");
		if (colspan > 1)
			writer.append(String.format(COLSPAN, colspan));
		if (style != null && style.length > 0)
			writer.append(String.format(STYLE, style[0]));
		writer.append(">");
		writer.append(value);
		writer.append("</th>");
	}

	protected void addTableCell(Object value, int colspan, String... style) {
		writer.append("<td ");
		if (colspan > 1)
			writer.append(String.format(COLSPAN, colspan));
		if (style != null && style.length > 0)
			writer.append(String.format(STYLE, style[0]));
		writer.append(">");
		writer.append(value);
		writer.append("</td>");
	}

	protected void openTableHCell(int colspan, String... style) {
		writer.append("<th ");
		if (colspan > 1)
			writer.append(String.format(COLSPAN, colspan));
		if (style != null && style.length > 0)
			writer.append(String.format(STYLE, style[0]));
		writer.append(">");
	}

	protected void closeTableHCell() {
		writer.append("</th>");
	}

	protected void openTableCell(int colspan, String... style) {
		writer.append("<td ");
		if (colspan > 1)
			writer.append(String.format(COLSPAN, colspan));
		if (style != null && style.length > 0)
			writer.append(String.format(STYLE, style[0]));
		writer.append(">");
	}

	protected void closeTableCell() {
		writer.append("</td>");
	}

	protected void closeWriter() {
		writer.append("</body>\n").append("</html>\n");
	}

	protected void writeBold(String value, String fontStyle) {
		writer.append("<b>");
		if (fontStyle != null)
			writer.append("<font style=\"").append(fontStyle).append("\" >");
		writer.append(value);
		if (fontStyle != null)
			writer.append("</font>");
		writer.append("</b>");
	}

	protected void writeEOL(int rowCount) {
		writer.append(BaseConstants.EOL_HTML);
		if (rowCount > 1)
			for (int i = 0; i < rowCount; i++) {
				writer.append(BaseConstants.EOL_HTML);
			}
	}

	public abstract String render();

	public static String stripHeader(String pContent) {
		if (pContent != null && pContent.length() > 10) {
			if (pContent.startsWith("<html")) {
				pContent = pContent.substring(pContent.indexOf(">") + 1);
				pContent = pContent.substring(0, pContent.lastIndexOf("</html>"));
			}

			if (pContent.startsWith("<head")) {
				pContent = pContent.substring(pContent.indexOf("</head>") + 7);
			}

			if (pContent.startsWith("<body")) {
				pContent = pContent.substring(pContent.indexOf(">") + 1);
				pContent = pContent.substring(0, pContent.lastIndexOf("</body>"));
			}
		}
		return pContent;
	}

	protected void addSender() {
		if (user != null) {
			writer.append(BaseConstants.EOL_HTML).append(BaseConstants.EOL_HTML);
			writer.append(user.getFullName());
			writer.append(BaseConstants.EOL_HTML);
			writer.append(user.getEmail());
			writer.append(BaseConstants.EOL_HTML);
			writer.append(user.getMobilePhoneNu());
			writer.append(BaseConstants.EOL_HTML);
		}
	}
}
