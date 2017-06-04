package com.ironz.binaryprefs.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;

public final class BinaryPrefsObjectOutputImpl implements ObjectOutput {

    //bytes for initial array size, buffer array are resizable to (buffer.length + GROW_ARRAY_CAPACITY) after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 128;

    private int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];
    private boolean closed = false;

    public <T extends Externalizable> byte[] serialize(T t) throws Exception {

        checkNull(t);
        checkClosed();

        byte[] flag = {Bits.FLAG_EXTERNALIZABLE};
        String className = t.getClass().getName();
        byte[] nameLength = Bits.intToBytesWithFlag(className.length());
        byte[] name = Bits.stringToBytesWithFlag(className);

        write(flag, 0, flag.length);
        write(nameLength, 0, nameLength.length);
        write(name, 0, name.length);

        t.writeExternal(this);

        byte[] bytes = new byte[offset];
        System.arraycopy(buffer, 0, bytes, 0, offset);
        return bytes;
    }

    @Override
    public void writeObject(Object value) throws IOException {
        throw new UnsupportedOperationException("This serialization type does not supported!");
    }

    @Override
    public void write(byte[] value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.byteArrayToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.booleanToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeByte(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.byteToBytesWithFlag((byte) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeShort(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.shortToBytesWithFlag((short) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeChar(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.charToBytesWithFlag((char) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeInt(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.intToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeLong(long value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.longToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.floatToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.doubleToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(String value) throws IOException {
        checkClosed();
        checkNull(value);
        byte[] trim = value.getBytes();
        for (byte b : trim) {
            byte[] bytes = Bits.byteToBytesWithFlag(b);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeChars(String value) throws IOException {
        checkClosed();
        checkNull(value);
        char[] trim = value.toCharArray();
        for (char c : trim) {
            byte[] bytes = Bits.charToBytesWithFlag(c);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeUTF(String value) throws IOException {
        checkClosed();
        checkNull(value);
        byte[] bytes = Bits.stringToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(int value) throws IOException {
        checkClosed();
        byte[] bytes = {(byte) value};
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] value, int off, int len) throws IOException {
        checkClosed();
        checkBounds(value, off, len);
        tryGrowArray(len);
        System.arraycopy(value, off, buffer, offset, len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Cannot write to already closed object output");
        }
    }

    private void checkBounds(byte[] value, int off, int len) {
        boolean incorrectOffset = off > value.length;
        boolean incorrectLength = len > value.length;
        boolean incorrectBounds = (value.length - off) < len;
        if (incorrectOffset || incorrectLength || incorrectBounds) {
            throw new ArrayIndexOutOfBoundsException("Can't write out of bounds array");
        }
    }

    private void growArray(int len) {
        byte[] bytes = new byte[buffer.length + GROW_ARRAY_CAPACITY];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
        offset += len;
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }

    private void tryGrowArray(int len) {
        if (offset + len <= buffer.length) {
            growArray(len);
        }
    }
}