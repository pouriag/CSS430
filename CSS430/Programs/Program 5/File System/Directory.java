import java.util.Arrays;

/**
 * The "/" root directory maintains each file in a diff directory entry that
 * contains its file name (max 30 chars; 60 bytes in Java) and corresponding
 * inode number.
 *
 * It basically maps a files name to its inode number
 * So on fnames[][], fnames[0][] = inode 0, fnames[1][] mapes to inode 1
 *
 * The directory:
 * 		1. receives the max number of inodes to be created
 * 			(i.e: thus the max number of files to be created)
 * 		2. keeps track of which inode numbers are in use
 *
 * Since the directory itsef is considered as a file, its contents are maintained
 * by an inode, specifically inode 0. This can be located in the FIRST 32 bytes of
 * the disk block 1.
 *
 * Created by lisakim and pouriaG on 12/1/17.
 */

public class Directory {
	private final static int maxChars = 30;

	// Directory entries
	private int fsizes[];        // each element stores a different file size.
	private char fnames[][];    // each element stores a different file name.

	/**
	 * Constructor that gives a blank directory
	 * 1. Reads the file from the disk that can be found through inode 0
	 * 	  at 32 bytes of the disk block 1
	 * 2. Intializes the Directory instance with the file contents
	 * @param maxInumber = max files
	 */
	public Directory( int maxInumber ) {
		fsizes = new int[maxInumber];
		for ( int i = 0; i < maxInumber; i++ )
			fsizes[i] = 0;                 // all file size initialized to 0

		fnames = new char[maxInumber][maxChars];
		String root = "/";                // entry(inode) 0 is "/"
		fsizes[0] = root.length( );        // fsize[0] is the size of "/".
		root.getChars( 0, fsizes[0], fnames[0], 0 ); // fnames[0] includes "/"
	}

	/**
	 * Intializes the Directory instance with the given data[]
	 * by reading the bytes from data[], and storing it in Directory object
	 * by unpacking it into fsize[], fnames[][]
	 *
	 * @param data info about the entire directory file contents in bytes from disk
	 */
	public void bytes2directory( byte data[] ) {
		int offset = 0;

		// reading by offset 4 because int is 4 bytes
		// reading in the file sizes
		for(int i = 0; i < fsizes.length; i++, offset +=4) {
			fsizes[i] = SysLib.bytes2int(data, offset);
		}

		// reading in the filenames
		// each offset is 60, b/c there are 30 2 byte short (a char is a byte)
		for(int i = 0; i < fnames.length; i++, offset += maxChars * 2) {
			String fname = new String(data, offset, maxChars *2);
			fname.getChars(0, fsizes[i], fnames[i], 0);
		}
	}

	/**
	 * Converts and return this Directory information fsize[] and fname[][]
	 * into a plain byte array. This byte array will be written back to disk.
	 *
	 * note: only meaningfull directory information should be converted into bytes.
	 *
	 * @return a giant byte array containing this directory information
	 */
	public byte[] directory2bytes() {
		byte buffer[] = new byte[(fsizes.length * 4) + (fnames.length * maxChars * 2)];
		int offset = 0;

		for(int i =0; i < fsizes.length; i++, offset += 4) {
			SysLib.int2bytes(fsizes[i], buffer, 0);
		}

		for(int i =0; i < fnames.length; i++, offset += maxChars *2) {
			// convert char array to a String
			String fname = new String(fnames[i]);

			// convert string to byte array
			byte charBuff[] = fname.getBytes();

			// copy to array
			System.arraycopy(charBuff, 0, buffer, offset, charBuff.length);
		}
		return buffer;
	}

	/**
	 * Allocates a new inode number for filename, if available
	 * Filename is the one of a file to be created
	 *
	 * @param filename to create
	 * @return inode number corressponding to fname index, else -1 if no free inodes available
	 */
	public short ialloc( String filename ) {
		for(short i =0; i < fsizes.length; i++) {
			if(fsizes[i] == 0) {
				fsizes[i] = filename.length();
				filename.getChars(0,fsizes[i], fnames[i], 0);
				return i;
			}
		}

		return -1;
	}

	/**
	 * Deallocates this inumber (inode number), and deletes
	 * the coressponding file in fsize[] and fname[][]
	 *
	 * @param iNumber the inode to delete
	 * @return true if successfully deleted, else false
	 */
	public boolean ifree( short iNumber ) {
		if(iNumber >= fsizes.length || iNumber < 0 || fsizes[iNumber] == 0) {
			return false;
		}

		// reset file sizes and file names
		fsizes[iNumber] = 0;
		Arrays.fill(fnames[iNumber], '\0');
		return true;
	}


	/**
	 * Given this filename, search for an inode corresponding to it
	 *
	 * @param filename to look for
	 * @return inode corresponding to this filename, else -1 if no filename found
	 */
	public short namei( String filename ) {
		for(short i =0; i < fsizes.length; i++) {
			char name[] = new char[fsizes[i]];
			System.arraycopy(fnames[i], 0, name, 0, fsizes[i]);
			String strFileName = new String(name);
			if(filename.equals(strFileName)) {
				return i;
			}
		}
		return -1;
	}
}
