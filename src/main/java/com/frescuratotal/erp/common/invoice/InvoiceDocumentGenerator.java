package com.frescuratotal.erp.common.invoice;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy contract for rendering an invoice to a file in a specific format.
 *
 * <p><b>OCP:</b> adding a new output format (e.g. HTML) only requires a new
 * implementation of this interface and a new branch in the factory, without
 * modifying existing generators or the calling code.</p>
 */
public interface InvoiceDocumentGenerator {

    /**
     * Renders the invoice described by {@code ctx} into {@code outputDir}.
     *
     * @param ctx       invoice data
     * @param outputDir target directory (created if missing)
     * @return the path to the written file
     * @throws IOException if writing fails
     */
    Path generate(InvoiceContext ctx, Path outputDir) throws IOException;

    /** @return the file extension produced (e.g. "png", "pdf"). */
    String getExtension();
}
