package cz.jkosnar.backup.zip;

import java.io.File;

/**
 * Zipper: Recursive 7zip based archiving tool. Creates non-solid password
 * protected archives.
 */
public class Zipper {

	private static String sevenZipLocation = "c:\\Program Files\\7-Zip\\7z.exe";
	private static File source = new File("");
	private static File destination = new File("");
	private static boolean reuse = false;
	private static int depth = 4;
	private static String archivePass = "_defaultPassword";

	public static void main(String[] args) throws Exception {

		if (args.length != 6) {
			System.out.println("Zipper: Recursive 7zip based archiving tool. Creates non-solid password protected archives. ");
			System.out.println("params: {7zipLocation} {source} {destination} {reuse} {depth} {archivePass} ");
			System.out.println();
			System.out.println("{7zipLocation}              7zip executable (7z.exe)");
			System.out.println("{source} & {destination}:   source and destination folders");
			System.out.println("{reuse}:                    update the desination folder - archives will be synchronized (true/false)");
			System.out.println("{depth}:                    folder tree recursing depth - folders lower than {depth} will be merged into one archive");
			System.out.println("{archivePass}:              password for the resulting archives - archives will be repacked with a new password ");
			return;
		}

		sevenZipLocation = args[0];
		source = new File(args[1]);
		destination = new File(args[2]);
		reuse = new Boolean(args[3]).booleanValue();
		depth = new Integer(args[4]).intValue();
		archivePass = args[5];

		ZipCreator z = new ZipCreator();
		z.createZip(sevenZipLocation, source, destination, reuse, depth, archivePass);
	}

}
