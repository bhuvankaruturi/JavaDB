package com.javadb.types;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DataType {
    NULL ((byte)0x00, (short)0, "null"),
    TINYINT((byte)0x01, (short)1, "tinyint"),
    SMALLINT ((byte)0x02, (short)2, "smallint"),
    INT ((byte)0x03, (short)4, "int"),
    BIGINT((byte)0x04, (short)8, "bigint"),
    LONG((byte)0x04, (short)8, "long"),
    FLOAT((byte)0x05, (short)4, "float"),
    DOUBLE((byte)0x06, (short)8, "double"),
    YEAR((byte)0x08, (short)2, "year"),
    DATETIME((byte)0x0A, (short)8, "datetime"),
    DATE((byte)0x0B, (short)8, "date"),
    TEXT((byte)0x0C, (short)0, "text");

    private final byte serialCode;
    private final short size;
    private final String name;

    DataType(byte serialCode, short size, String name) {
        this.size = size;
        this.name = name;
        this.serialCode = serialCode;
    }

    public byte getSerialCode() {
        return serialCode;
    }

    public String getName() {
        return name;
    }

    public short getSize(String data) {
        if (serialCode == 0x0C) {
            return (short) (0x0C + data.length());
        }
        return size;
    }

    byte[] stringToByteArr(String data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.getSize(data));
        if (this.getSerialCode() > 0x0C) {
            byteBuffer.put(data.getBytes());
        }
        else if (this.getSerialCode() == 0x0C) {
            byteBuffer.put("null".getBytes());
        }
        else {
            try {
                switch (this.getSerialCode()) {
                    case 0x00:
                        break;
                    case 0x01:
                        byteBuffer.put(Byte.parseByte(data));
                        break;
                    case 0x02:
                        byteBuffer.putShort(Short.parseShort(data));
                        break;
                    case 0x03:
                        byteBuffer.putInt(Integer.parseInt(data));
                        break;
                    case 0x04:
                        byteBuffer.putLong(Long.parseLong(data));
                        break;
                    case 0x05:
                        byteBuffer.putFloat(Float.parseFloat(data));
                        break;
                    case 0x06:
                        byteBuffer.putDouble(Double.parseDouble(data));
                        break;
                    case 0x08:
                        int year = Integer.parseInt(data);
                        year -= 2000;
                        byteBuffer.putShort((short)year);
                        break;
                    case 0x0A:
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
                        Date date = fmt.parse(data);
                        byteBuffer.putLong(date.getTime());
                        break;
                    case 0x0B:
                        fmt = new SimpleDateFormat("yyyy-MM-dd");
                        date = fmt.parse(data);
                        byteBuffer.putLong(date.getTime());
                        break;
                    default:
                        System.out.println("Invalid datatype " + this.getSerialCode());
                        throw new IllegalArgumentException("Invalid datatype");
                }
            }
            catch (ParseException e) {
                System.out.println("invalid data");
                e.printStackTrace();
            }
        }
        byteBuffer.position(0);
        return byteBuffer.array();
    }

    String byteArrToString(byte[] arr) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(arr);
        switch(getSerialCode()) {
            case 0x00:
                return "null";
            case 0x01:
                return "" + byteBuffer.get();
            case 0x02:
                return "" + byteBuffer.getShort();
            case 0x03:
                return "" + byteBuffer.getInt();
            case 0x04:
                return "" + byteBuffer.getLong();
            case 0x05:
                return "" + byteBuffer.getFloat();
            case 0x06:
                return "" + byteBuffer.getDouble();
            case 0x08:
                short year = byteBuffer.getShort();
                return "" + (2000 + year);
            case 0x0A:
                Date date = new Date(byteBuffer.getLong());
                return new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(date);
            case 0x0B:
                date = new Date(byteBuffer.getLong());
                return new SimpleDateFormat("yyyy-MM-dd").format(date);
            case 0x0C:
                return new String(arr);
            default:
                System.out.println("Invalid datatype " + getSerialCode());
                throw new IllegalArgumentException("Invalid datatype");
        }
    }
}
