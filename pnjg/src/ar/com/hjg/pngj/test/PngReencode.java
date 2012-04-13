package ar.com.hjg.pngj.test;

import java.io.File;

import ar.com.hjg.pngj.FileHelper;
import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

/**
 * reencodes a png image with a given filter and compression level
 */
public class PngReencode {
	public static void reencode(String orig, String dest, FilterType filterType, int cLevel) {
		if (orig.equals(dest))
			throw new RuntimeException("files are the same!");
		PngReader pngr = FileHelper.createPngReader(new File(orig));
		PngWriter pngw = new PngWriter(FileHelper.openFileForWriting(new File(dest), true), pngr.imgInfo);
		System.out.println(pngr.toString());
		pngw.setFilterType(filterType);
		pngw.setCompLevel(cLevel);
		pngw.copyChunksFirst(pngr, ChunkCopyBehaviour.COPY_ALL);
		System.out.printf("Creating Image %s  filter=%s compLevel=%d \n", pngw.getFilename(), filterType.toString(),
				cLevel);
		for (int row = 0; row < pngr.imgInfo.rows; row++) {
			ImageLine l1 = pngr.readRow(row);
			pngw.writeRow(l1,row);
		}
		pngr.end();
		pngw.copyChunksLast(pngr, ChunkCopyBehaviour.COPY_ALL);
		pngw.end();
		System.out.println("Done");
	}

	public static void fromCmdLineArgs(String[] args) {
		if (args.length != 4) {
			System.err.println("Arguments: [pngsrc] [pngdest] [filter] [compressionlevel]");
			System.err.println(" Where filter = 0..4  , compressionLevel = 0 .. 9");
			System.exit(1);
		}
		long t0 = System.currentTimeMillis();
		reencode(args[0], args[1], FilterType.getByVal(Integer.parseInt(args[2])), Integer.parseInt(args[3]));
		long t1 = System.currentTimeMillis();
		System.out.println("Listo: " + (t1 - t0) + " msecs");
	}

	public static void main(String[] args) throws Exception {
		// fromCmdLineArgs(args);
		long t0 = System.currentTimeMillis();

		/*
		 * reencode("/temp/people.png", "/temp/peoplex.png", PngFilterType.FILTER_ALTERNATE, 9);
		 * reencode("/temp/peoplex.png", "/temp/peoplex2.png", PngFilterType.FILTER_ALTERNATE, 9);
		 */
		reencode("/temp/ibadPortrait.png", "/temp/ibadPortrait2.png", FilterType.FILTER_AVERAGE, 9);
		long t1 = System.currentTimeMillis();
		System.out.println("Listo: " + (t1 - t0) + " msecs");
	}
}