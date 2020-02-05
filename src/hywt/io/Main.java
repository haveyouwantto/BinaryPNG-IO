package hywt.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Main {
	public static void toImage(File file, File out) {
		try {
			long size = file.length();
			byte[] extension = getFileExtension(file).getBytes("utf-8");
			byte[] filename = file.getName().getBytes("utf-8");
			long pixels = size / 3 + 80;
			int width = (int) (Math.sqrt(pixels) + 3);
			System.out.println(pixels + " " + width);
			int height = width;
			BinaryPNGOutputStream bpos = new BinaryPNGOutputStream(width, height);

			byte[] sizeb = new byte[6];
			for (int i = 0; i < sizeb.length; i++) {
				sizeb[i] = (byte) ((size >> i * 8) & 0xff);
			}
			bpos.write(sizeb);

			byte[] filenamesizeb = new byte[3];
			for (int i = 0; i < filenamesizeb.length; i++) {
				filenamesizeb[i] = (byte) ((filename.length >> i * 8) & 0xff);
			}
			bpos.write(filenamesizeb);

			byte[] filenameb = new byte[240];
			for (int i = 0; i < filename.length; i++) {
				filenameb[i] = filename[i];
			}
			bpos.write(filenameb);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];

			long progress = 0;
			while (fis.read(buffer) > 0) {
				progress += 1024;
				Window.progressBar.setValue((int) (progress / 1.0d / size * 1000));
				Window.progressBar.setString(readableFileSize((long) progress) + " / " + readableFileSize(size));
				// System.out.print(byte3[i][1] + " ");
				bpos.write(buffer);
			}
			bpos.saveAsImage(out);
			bpos.close();
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf).replace(".", "");
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0B"; //$NON-NLS-1$
		final String[] units = new String[] { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("###.00").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static void fromImage(File selected, File out) {
		try {
			BinaryPNGInputStream bpis = new BinaryPNGInputStream(selected);

			long size = 0;
			int filenamesize = 0;

			byte[] sizeb = new byte[6];
			byte[] filenamesizeb = new byte[3];

			bpis.read(sizeb);

			for (int i = 0; i < sizeb.length; i++) {
				long e = sizeb[i];
				if (e < 0)
					e += 256;
				size |= e << (i << 3);
			}

			bpis.read(filenamesizeb);

			for (int i = 0; i < filenamesizeb.length; i++) {
				long e = filenamesizeb[i];
				if (e < 0)
					e += 256;
				filenamesize |= e << (i << 3);
			}

			byte[] filenameb = new byte[filenamesize];
			bpis.read(filenameb);
			bpis.skip(240 - filenamesize);

			String filename = new String(filenameb, "utf-8");
			System.out.println(filename);

			FileOutputStream fos = new FileOutputStream(new File(filename));

			long pos = 1024;
			byte[] buffer = new byte[1024];
			while (pos < size) {
				bpis.read(buffer);
				fos.write(buffer);
				pos += 1024;
				Window.progressBar.setValue((int) (pos / 1.0d / size * 1000));
				Window.progressBar.setString(readableFileSize((long) pos) + " / " + readableFileSize(size));
			}
			byte[] end = new byte[(int) (size % 1024)];
			bpis.read(end);
			fos.write(end);

			fos.close();
			bpis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}