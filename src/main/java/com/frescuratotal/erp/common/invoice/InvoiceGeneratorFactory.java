package com.frescuratotal.erp.common.invoice;

/**
 * Factory that returns an {@link InvoiceDocumentGenerator} for a given format.
 *
 * <p><b>Design pattern: Factory + OCP.</b> The caller requests a format by enum
 * value and gets the matching strategy. Supporting a new format means adding a
 * constant and a case here plus a new generator class — existing code is left
 * untouched (Open/Closed Principle).</p>
 */
public final class InvoiceGeneratorFactory {

    /** Supported invoice output formats. */
    public enum Format {
        PNG,
        PDF
    }

    private InvoiceGeneratorFactory() {
    }

    /**
     * @param format desired output format
     * @return a generator able to produce that format
     */
    public static InvoiceDocumentGenerator create(Format format) {
        switch (format) {
            case PNG:
                return new PngInvoiceGenerator();
            case PDF:
                return new PdfInvoiceGenerator();
            default:
                throw new IllegalArgumentException("Formato de factura no soportado: " + format);
        }
    }
}
