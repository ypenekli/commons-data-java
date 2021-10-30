package com.yp.core.tools;

import javax.imageio.ImageIO;

import com.yp.core.log.MyLogger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageTool {

	public static byte[] getImage(File pFile) {
		byte[] buf = null;
		try (InputStream is = new BufferedInputStream(new FileInputStream(pFile))) {
			buf = new byte[is.available()];
			if (is.read(buf) > 0)
				return buf;
			else buf = null;
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			buf = null;
		}
		return buf;
	}

	public static byte[] getImage(String pFileAddress) {
		return getImage(new File(pFileAddress));
	}

	public static BufferedImage getImageFromFile(File pFile) {
		try {
			return ImageIO.read(pFile);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public static BufferedImage getImageFromFile(String pFileAddress) {
		return getImageFromFile(new File(pFileAddress));
	}

	public static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	public static byte[] getImageFromBuffer(BufferedImage pImageBuf) {		
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ImageIO.write(pImageBuf, "jpg", bos);
			return bos.toByteArray();
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public static void main(String... args) throws IOException {
		File input = new File("/tmp/duke.png");
		BufferedImage image = ImageIO.read(input);

		BufferedImage resized = resize(image, 500, 500);

		File output = new File("/tmp/duke-resized-500x500.png");
		ImageIO.write(resized, "png", output);
	}
}
