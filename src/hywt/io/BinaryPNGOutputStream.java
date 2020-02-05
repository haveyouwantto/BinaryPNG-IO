package hywt.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class BinaryPNGOutputStream extends OutputStream {

	private BufferedImage image;
	private long pointer;
	private long capacity;

	public BinaryPNGOutputStream(int width, int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.pointer = 0;
		this.capacity = width * height * 3;
	}

	@Override
	public void write(int b) throws IOException {
		this.paint(b);
		this.pointer += 1;
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)
				|| (len > capacity - pointer)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		for (int i = off; i < len; i++) {
			if (b[i] < 0)
				write(b[i] + 0x100);
			else
				write(b[i]);
		}
	}

	@Override
	public void close() throws IOException {
		image.flush();
		super.close();
	}

	public long skip(long off) {
		long prev = pointer;
		pointer += off;
		return pointer - prev;
	}

	public void saveAsImage(File file) throws IOException {
		ImageIO.write(image, "png", file);
	}

	private void paint(int color) {
		int width = image.getWidth();
		int x = (int) (pointer / 3 % width);
		int y = (int) (pointer / 3 / width);
		int c = (int) (pointer % 3);
		int original = image.getRGB(x, y);
		image.setRGB(x, y, original | color << (2 - c << 3));
	}

}
