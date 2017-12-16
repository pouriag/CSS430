/**
 * In memory object representation of an inode
 * Used to simplify how we interact with the system
 *
 * Each file descriptor should correspond to exactly ONE of FileTableEntry
 * B/c each file descriptor can open its file in its individual mode
 * And can seek a file into its individual point
 *
 * Contains:
 * 		reference to inode
 *      reference count for the number of things that have this open
 *      etc.
 *
 * Created by lisakim and pouriaG on 12/1/17.
 */
public class FileTableEntry {			// Each table entry should have
	public int seekPtr;                 //    a file seek pointer
	public final Inode inode;           //    a reference to its inode
	public final short iNumber;         //    this inode number
	public int count;                   //    # threads sharing this entry
	public final String mode;           //    "r", "w", "w+", or "a"

	public FileTableEntry ( Inode i, short inumber, String m ) {
		seekPtr = 0;             // the seek pointer is set to the file top
		inode = i;
		iNumber = inumber;
		count = 1;               // at least on thread is using this entry
		mode = m;                // once access mode is set, it never changes

		if ( mode.compareTo( "a" ) == 0 ) // if mode is append,
			seekPtr = inode.length;        // seekPtr points to the end of file
	}
}
