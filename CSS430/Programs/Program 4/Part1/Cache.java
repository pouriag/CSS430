public class Cache {

    cacheBlock[] pageTable;
    private int clock = 0;

    private class cacheBlock {
        public int frameNumber;
        public boolean refBit;
        public boolean dirtyBit;
        public byte[] buffer;

        public cacheBlock(int blockSize) {

            frameNumber = -1;
            refBit = false;
            dirtyBit = false;
            buffer = new byte[blockSize];

        }
    }

    public Cache(int blockSize, int cacheBlock) {

        this.pageTable = new cacheBlock[cacheBlock];
        for (int i = 0; i < cacheBlock; i++) {
            this.pageTable[i] = new cacheBlock(blockSize);
        }
    }

    public synchronized boolean read(int blockId, byte buffer[]) {

        SysLib.cout("2");
        if (blockId < 0) return false;

        //Look for a hit
        for (int i = 0; i < this.pageTable.length; i++) {
            if (this.pageTable[i].frameNumber == blockId) {
                buffer = this.pageTable[i].buffer.clone();
                this.pageTable[i].refBit = true;
            }
        }

        while (true) {

            //is there any empty page
            if (this.pageTable[clock].frameNumber == -1) {
                SysLib.rawread(blockId, buffer);
                this.pageTable[clock].frameNumber = blockId;
                this.pageTable[clock].refBit = true;
                this.pageTable[clock].buffer = buffer.clone();
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
                return true;
            }

            //look for a victim to remove
            if (!this.pageTable[clock].refBit) {
                if (this.pageTable[clock].dirtyBit) {
                    SysLib.rawwrite(this.pageTable[clock].frameNumber, this.pageTable[clock].buffer);
                    this.pageTable[clock].dirtyBit = false;
                }

                SysLib.rawread(blockId, this.pageTable[clock].buffer);
                this.pageTable[clock].frameNumber = blockId;
                this.pageTable[clock].refBit = true;
                buffer = this.pageTable[clock].buffer.clone();
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
                return true;
            } else {
                this.pageTable[clock].refBit = false;
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
            }
        }
    }

    public synchronized boolean write(int blockId, byte buffer[]) {

        SysLib.cout("3");
        if (blockId < 0) return false;

        //Look for a hit
        for (int i = 0; i < this.pageTable.length; i++) {
            if (this.pageTable[i].frameNumber == blockId) {
                this.pageTable[i].buffer = buffer.clone();
                this.pageTable[i].refBit = true;
                this.pageTable[i].dirtyBit = true;
                return true;
            }
        }

        while (true) {

            //is there any empty page
            if (this.pageTable[clock].frameNumber == -1) {

                this.pageTable[clock].buffer = buffer.clone();
                this.pageTable[clock].frameNumber = blockId;
                this.pageTable[clock].refBit = true;
                this.pageTable[clock].dirtyBit = true;
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
                return true;
            }

            //look for a victim to remove
            if (!this.pageTable[clock].refBit) {
                if (this.pageTable[clock].dirtyBit) {
                    SysLib.rawwrite(this.pageTable[clock].frameNumber, this.pageTable[clock].buffer);
                    this.pageTable[clock].dirtyBit = false;
                }

                SysLib.rawread(blockId, this.pageTable[clock].buffer);
                this.pageTable[clock].refBit = true;
                this.pageTable[clock].dirtyBit = true;
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
                return true;
            } else {
                this.pageTable[clock].refBit = false;
                clock = (clock == this.pageTable.length - 1) ? 0 : ++clock;
            }
        }
    }

    public synchronized void sync() {
        SysLib.cout("4");
        for (int i = 0; i < this.pageTable.length; i++) {
            if (this.pageTable[i].frameNumber != -1) {
                SysLib.rawwrite(this.pageTable[i].frameNumber, this.pageTable[i].buffer);
                this.pageTable[i].dirtyBit = false;
            }
        }
    }

    public synchronized void flush() {

        for (int i = 0; i < this.pageTable.length; i++) {
            if (this.pageTable[i].frameNumber != -1 && this.pageTable[i].dirtyBit){
                SysLib.rawwrite(this.pageTable[i].frameNumber, this.pageTable[i].buffer);
                this.pageTable[i].dirtyBit = false;
                this.pageTable[i].refBit = false;
                this.pageTable[i].frameNumber = -1;
            }

        }
    }
}
