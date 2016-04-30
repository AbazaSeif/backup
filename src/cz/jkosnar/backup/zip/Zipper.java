package cz.jkosnar.backup.zip;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Zipper: Recursive 7zip based archiving tool. Creates non-solid password protected archives.
 */
public class Zipper {

	private static String sevenZipLocation;
	private static File source;
	private static File destination;
	private static int depth;
	private static String archivePass;
	private static boolean synchronize;
	private static String workingDir;
	private static int compression;

	public static void main(String[] args) throws Exception {

		if (args.length != 8) {
			System.out.println("Zipper: Recursive 7zip based archiving tool. Creates non-solid password protected archives. ");
			System.out.println("Parameters: {7zipLocation} {source} {destination} {depth} {archivePass} {synchronize} {workingDir} {compression}");
			System.out.println();
			System.out.println("{7zipLocation}              7zip executable (7z.exe)");
			System.out.println("{source} & {destination}:   source and destination folders");
			System.out.println("{depth}:                    folder tree recursing depth - folders lower than {depth} will be merged into one archive");
			System.out.println("{archivePass}:              password for the resulting archives");
			System.out.println("{synchronize}:              true - update existing destination, false - delete destination first");
			System.out.println("{workingDir}:               7zip temporary directory location");
			System.out.println("{compression}:              compression: 0-9 - 0 no compression");
			System.out.println();
			System.out.println("All parameters must by specified in the given order.");
			return;
		}

		sevenZipLocation = args[0];
		source = new File(args[1]);
		destination = new File(args[2]);
		depth = new Integer(args[3]).intValue();
		archivePass = args[4];
		synchronize = new Boolean(args[5]).booleanValue();
		workingDir = args[6];
		compression = new Integer(args[7]).intValue();

		// check parameters
		if (!source.exists()) {
			System.out.println("Source file does not exist...");
			return;
		}

		if (!destination.exists()) {
			FileUtils.forceMkdir(destination);
		}

		File workingDirFile = new File(workingDir);
		if (!"".equals(workingDir) && !workingDirFile.exists()) {
			FileUtils.forceMkdir(workingDirFile);
		}

		ZipCreator z = new ZipCreator();
		z.createZip(sevenZipLocation, source, destination, depth, archivePass, synchronize, workingDir, compression);
	}

}
