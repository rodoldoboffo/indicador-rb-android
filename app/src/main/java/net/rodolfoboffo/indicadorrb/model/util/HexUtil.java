package net.rodolfoboffo.indicadorrb.model.util;

import java.nio.ByteBuffer;

public class HexUtil {

    public static Integer byteArrayToInt(byte[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(array);
        buffer.flip();
        return buffer.getInt();
    }

    public static Boolean byteArrayToBoolean(byte[] array) {
        return array[0] > 0;
    }
}
