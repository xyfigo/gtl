package gtl.stil.impl;

import gtl.stil.Entry;
import gtl.stil.Identifier;
import gtl.stil.IndexSuits;
import gtl.stil.Node;
import gtl.stil.shape.Region;
import gtl.stil.shape.Shape;
import gtl.stil.shape.impl.RegionImpl;

import java.io.*;
import java.util.function.Supplier;

/**
 * Created by ZhenwenHe on 2017/2/12.
 */
public abstract class NodeImpl implements Node {

    Identifier identifier;
    Shape      shape;
    // The level of the node in the tree.
    // Leaves are always at level 0.
    int level;
    // The number of children pointed by this node.
    int children;
    // child entry array
    Entry [] entries;
    // Specifies the node capacity.
    int capacity;
    // total data length
    int totalDataLength;


    public NodeImpl(Identifier identifier, int level, int capacity ) {
        this.identifier=(Identifier) identifier.clone();
        this.shape=newShape();
        this.level=level;
        this.children=0;
        this.totalDataLength=0;
        this.capacity = capacity;
        this.entries=new Entry[this.capacity+1];

    }

    public NodeImpl() {
        this.identifier=new IdentifierImpl(-1L);
        this.shape=newShape();
        this.capacity = 64;
        this.level=0;
        this.children=0;
        this.totalDataLength=0;
        this.entries=new Entry[this.capacity+1];
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }

    @Override
    public void setIdentifier(long id) {
        this.identifier.reset(id);
    }

    @Override
    public void setIdentifier(Identifier id) {
        this.identifier.reset(id.longValue());
    }

    @Override
    public void setShape(Shape s) {
        this.shape=(Shape) s.clone();
    }

    @Override
    public byte[] getData() {
        return null;
    }

    @Override
    public void setData(byte[] data) {
        // do nothing
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public long getDataLength() {
        return 0;
    }

    @Override
    public int getChildrenCount() {
        return this.children;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public boolean isInternalNode() {
        return (this.level != 0);
    }

    @Override
    public boolean isExternalNode() {
        return (this.level == 0);
    }

    @Override
    public Entry getChildEntry(int index) {
        if(index>=0 && index<this.children)
            return this.entries[index];
        else
            return null;
    }

    @Override
    public long getByteArraySize() {
        long sum=4*4;
        sum+=this.identifier.getByteArraySize();
        sum+=this.shape.getByteArraySize();
        // child entry array
        for(int iChild=0;iChild<this.children;++iChild){
            sum+=this.entries[iChild].getByteArraySize();
        }
        return sum;
    }

    @Override
    public boolean read(InputStream in) throws IOException {
        DataInputStream dis=new DataInputStream(in);
        this.identifier.read(in);
        this.shape.read(in);
        // Leaves are always at level 0.
        this.level=dis.readInt();
        // The number of children pointed by this node.
        this.children=dis.readInt();
        // Specifies the node capacity.
        this.capacity=dis.readInt();
        // total data length
        this.totalDataLength=dis.readInt();
        // child entry array
        for(int iChild=0;iChild<this.children;++iChild){
            EntryImpl e = new EntryImpl();
            Shape s = newShape();
            assert s!=null;
            e.setShape(s);
            e.read(in);
            this.entries[iChild]=e;
        }
        dis.close();
        return true;
    }

    @Override
    public boolean write(OutputStream out) throws IOException {
        DataOutputStream dos =new DataOutputStream(out);
        this.identifier.write(out);
        this.shape.write(out);
        // Leaves are always at level 0.
        dos.writeInt(this.level);
        // The number of children pointed by this node.
        dos.writeInt(this.children);
        // Specifies the node capacity.
        dos.writeInt(this.capacity);
        // total data length
        dos.writeInt(this.totalDataLength);
        dos.flush();
        // child entry array
        for(int iChild=0;iChild<this.children;++iChild){
            this.entries[iChild].write(out);
        }
        dos.close();
        return true;
    }

    @Override
    public void copyFrom(Object i) {
        if(i==null) return ;
        if(i instanceof NodeImpl){
            NodeImpl ni = ((NodeImpl)i);
            this.identifier.reset(ni.identifier.longValue());
            this.shape=(Shape)ni.shape.clone();
            this.level=ni.level;
            this.children =ni.children;
            this.capacity=ni.capacity;
            this.totalDataLength=ni.totalDataLength;
            for (int iChild =0;iChild<this.children;++iChild){
                this.entries[iChild]= (Entry) ni.entries[iChild].clone();
            }
        }
    }

    @Override
    public void insertEntry(Entry e){
        assert(this.children < this.capacity);
        this.entries[this.children] = e;
        this.totalDataLength += e.getDataLength();
        ++this.children;
    }
    @Override
    public Entry removeEntry(int index) {
        assert(index >= 0 && index < this.children);
        Entry e = this.entries[index];
        this.totalDataLength -= e.getDataLength();
        if (this.children > 1 && index != this.children - 1){
            this.entries[index]=this.entries[this.children-1];
            this.entries[this.children-1]=null;
        }
        --this.children;
        return e;
    }

    @Override
    public abstract Object clone();
    @Override
    public abstract Shape recalculateShape();
    @Override
    public abstract Shape newShape();
}