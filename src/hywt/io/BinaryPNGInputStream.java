package hywt.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class BinaryPNGInputStream extends InputStream {

	private BufferedImage image;
	private long pointer;

	public BinaryPNGInputStream(File png) throws IOException {
		this.image = ImageIO.read(png);
		this.pointer = -1;
	}

	private int get() {
		int width = image.getWidth();
		int x = (int) (pointer / 3 % width);
		int y = (int) (pointer / 3 / width);
		int c = (int) (pointer % 3);
		int original = image.getRGB(x, y);
		return (original >> (2 - c << 3)) & 0xff;
	}

	@Override
	public int read() throws IOException {
		this.pointer += 1;
		return this.get();
	}

	@Override
	public long skip(long n) {
		long last = this.pointer;
		this.pointer += n;
		return this.pointer - last;
	}

}
