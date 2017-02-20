package gtl.stil;

import java.io.*;

/**
 * Created by hadoop on 17-2-19.
 */
public class Property extends Variant  implements  Serializable  {
    private String name=null;

    public Property( ) {
        super();
        this.name="Unknown";
    }

    public Property(String name,Variant v) {
        super(v);
        this.name = name;
    }

    public Property(String name,int type, Object value) {
        super(type,value);
        this.name = name;
    }

    public Property(String name,Object value) {
        super(value);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.name.equals(((Property)o).name);
    }

    @Override
    public String toString() {
        return this.name+":"+super.toString();
    }

    @Override
    public void copyFrom(Object i) {
        super.copyFrom(i);
        if(i instanceof Property){
            this.name=((Property)i).name;
        }
    }

    @Override
    public boolean read(InputStream in) throws IOException {
        DataInputStream dis=new DataInputStream(in);
        this.name =Variant.readString(dis);
        boolean b=  super.read(in);
        dis.close();
        return b;
    }

    @Override
    public boolean write(OutputStream out) throws IOException {
        DataOutputStream dos =new DataOutputStream(out);
        Variant.writeString(dos,this.name);
        boolean b= super.write(out);
        dos.close();
        return b;
    }

    @Override
    public long getByteArraySize() {
        return super.getByteArraySize()+4+this.name.length()*2;
    }



}