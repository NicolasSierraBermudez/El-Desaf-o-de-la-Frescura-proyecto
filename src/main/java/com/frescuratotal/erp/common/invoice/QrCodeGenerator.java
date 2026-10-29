package com.frescuratotal.erp.common.invoice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/** Generates QR code images using the ZXing library. */
public final class QrCodeGenerator {

    private QrCodeGenerator() {
    }

    /**
     * Encodes the given content into a square QR {@link BufferedImage}.
     *
     * @param content text to encode (e.g. the simulated CUFE link)
     * @param size    image side in pixels
     * @return the rendered QR image
     * @throws WriterException if encoding fails
     */
    public static BufferedImage generate(String content, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new QRCodeWriter()
                .encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
