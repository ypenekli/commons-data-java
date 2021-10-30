package com.yp.core.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Adler32;

public class Checksum {

	public static void main(String[] args) throws IOException {
		if (args != null && args.length > 0) {
			long checksum = checksum(Paths.get(args[0]));
			System.out.println("checksum (" + args[0] + ") :" + checksum);
		}

	}

	@SuppressWarnings("unused")
	public static long checksum(Path path) throws IOException {
		Throwable var1 = null;
		Object var2 = null;

		try {
			InputStream input = Files.newInputStream(path);

			try {
				Adler32 checksum = new Adler32();
				byte[] buf = new byte[16384];

				int read;
				while ((read = input.read(buf)) > -1) {
					checksum.update(buf, 0, read);
				}

				long var10000 = checksum.getValue();
				return var10000;
			} finally {
				if (input != null) {
					input.close();
				}

			}
		} catch (Throwable var12) {
			if (var1 == null) {
				var1 = var12;
			} else if (var1 != var12) {
				var1.addSuppressed(var12);
			}

			try {
				throw var1;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return 0l;
	}
}
