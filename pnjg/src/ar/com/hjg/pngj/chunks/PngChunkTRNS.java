package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelper;
import ar.com.hjg.pngj.PngjException;

/*
 */
public class PngChunkTRNS extends PngChunkSingle {
	// http://www.w3.org/TR/PNG/#11tRNS
	// this chunk structure depends on the image type
	// only one of these is meaningful
	private int gray;
	private int red, green, blue;
	private int[] paletteAlpha = new int[] {};

	public PngChunkTRNS(ImageInfo info) {
		super(ChunkHelper.tRNS, info);
	}

	@Override
	public ChunkOrderingConstraint getOrderingConstraint() {
		return ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
	}

	@Override
	public ChunkRaw createRawChunk() {
		ChunkRaw c = null;
		if (imgInfo.greyscale) {
			c = createEmptyChunk(2, true);
			PngHelper.writeInt2tobytes(gray, c.data, 0);
		} else if (imgInfo.indexed) {
			c = createEmptyChunk(paletteAlpha.length, true);
			for (int n = 0; n < c.len; n++) {
				c.data[n] = (byte) paletteAlpha[n];
			}
		} else {
			c = createEmptyChunk(6, true);
			PngHelper.writeInt2tobytes(red, c.data, 0);
			PngHelper.writeInt2tobytes(green, c.data, 0);
			PngHelper.writeInt2tobytes(blue, c.data, 0);
		}
		return c;
	}

	@Override
	public void parseFromRaw(ChunkRaw c) {
		if (imgInfo.greyscale) {
			gray = PngHelper.readInt2fromBytes(c.data, 0);
		} else if (imgInfo.indexed) {
			int nentries = c.data.length;
			paletteAlpha = new int[nentries];
			for (int n = 0; n < nentries; n++) {
				paletteAlpha[n] = (int) (c.data[n] & 0xff);
			}
		} else {
			red = PngHelper.readInt2fromBytes(c.data, 0);
			green = PngHelper.readInt2fromBytes(c.data, 2);
			blue = PngHelper.readInt2fromBytes(c.data, 4);
		}
	}

	@Override
	public void cloneDataFromRead(PngChunk other) {
		PngChunkTRNS otherx = (PngChunkTRNS) other;
		gray = otherx.gray;
		red = otherx.red;
		green = otherx.red;
		blue = otherx.red;
		if (otherx.paletteAlpha != null) {
			paletteAlpha = new int[otherx.paletteAlpha.length];
			System.arraycopy(otherx.paletteAlpha, 0, paletteAlpha, 0, paletteAlpha.length);
		}
	}

	/**
	 * Set rgb values
	 * 
	 */
	public void setRGB(int r, int g, int b) {
		if (imgInfo.greyscale || imgInfo.indexed)
			throw new PngjException("only rgb or rgba images support this");
		red = r;
		green = g;
		blue = b;
	}

	public int[] getRGB() {
		if (imgInfo.greyscale || imgInfo.indexed)
			throw new PngjException("only rgb or rgba images support this");
		return new int[] { red, green, blue };
	}

	public void setGray(int g) {
		if (!imgInfo.greyscale)
			throw new PngjException("only grayscale images support this");
		gray = g;
	}

	public int getGray() {
		if (!imgInfo.greyscale)
			throw new PngjException("only grayscale images support this");
		return gray;
	}

	/**
	 * WARNING: non deep copy
	 */
	public void setPalletteAlpha(int[] palAlpha) {
		if (!imgInfo.indexed)
			throw new PngjException("only indexed images support this");
		paletteAlpha = palAlpha;
	}

	/**
	 * WARNING: non deep copy
	 */
	public int[] getPalletteAlpha() {
		if (!imgInfo.indexed)
			throw new PngjException("only indexed images support this");
		return paletteAlpha;
	}

}