package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class XorKeyEncryptionImplTest {

    private final XorKeyEncryptionImpl encryption = new XorKeyEncryptionImpl("LZN8KKF7KH816D0U".getBytes());
    private final XorKeyEncryptionImpl badEncryption = new XorKeyEncryptionImpl("1111111111111110".getBytes());

    @Test
    public void decrypt() {
        String origin = "origin";
        String decrypt = encryption.decrypt(origin);
        assertNotEquals(origin, decrypt);

        String encrypt = encryption.encrypt(decrypt);
        assertEquals(origin, encrypt);
    }

    @Test
    public void encrypt() {
        String origin = "origin";
        String encrypted = encryption.encrypt(origin);
        assertNotEquals(origin, encrypted);

        String decrypt = encryption.decrypt(encrypted);
        assertEquals(origin, decrypt);
    }

    @Test
    public void anotherEncryption() {
        String origin = "origin";
        String encrypted = encryption.encrypt(origin);

        String decryptByAnother = badEncryption.decrypt(encrypted);

        assertNotEquals(origin, decryptByAnother);
    }

    @Test
    public void oddSize() {
        String withOddSize = "11111111111111111";
        new XorKeyEncryptionImpl(withOddSize.getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void evenAndMirrored() {
        String evenWithMirror = "0101010101010101";
        new XorKeyEncryptionImpl(evenWithMirror.getBytes());
    }

    @Test
    public void evenNotMirrored() {
        String evenWithMirror = "1111111111111110";
        new XorKeyEncryptionImpl(evenWithMirror.getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void shortXorLength() {
        String withSizeLessThenSixteen = "";
        new XorKeyEncryptionImpl(withSizeLessThenSixteen.getBytes());
    }

    @Test
    public void longXorLength() {
        String withSizeGreaterThenFifthTeen = "11111111111111111";
        new XorKeyEncryptionImpl(withSizeGreaterThenFifthTeen.getBytes());
    }
}