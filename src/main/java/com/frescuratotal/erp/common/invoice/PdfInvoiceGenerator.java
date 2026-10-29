package com.frescuratotal.erp.common.invoice;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Renders a visual electronic invoice as a PDF using Apache PDFBox.
 *
 * <p>The ZXing-generated QR is embedded as an image inside the PDF.</p>
 */
public class PdfInvoiceGenerator implements InvoiceDocumentGenerator {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getExtension() {
        return "pdf";
    }

    @Override
    public Path generate(InvoiceContext ctx, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Path out = outputDir.resolve(ctx.getInvoiceNumber() + ".pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float width = page.getMediaBox().getWidth();
            float top = page.getMediaBox().getHeight() - 50;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // Header
                text(cs, PDType1Font.HELVETICA_BOLD, 20, 50, top, safe(ctx.getCompanyName()));
                text(cs, PDType1Font.HELVETICA, 9, 50, top - 16,
                        "NIT: " + safe(ctx.getCompanyNit()) + "  |  "
                                + safe(ctx.getCompanyAddress()) + ", " + safe(ctx.getCompanyCity()));
                text(cs, PDType1Font.HELVETICA, 9, 50, top - 28,
                        "Tel: " + safe(ctx.getCompanyPhone()) + "  |  " + safe(ctx.getCompanyEmail()));

                // Invoice identity
                text(cs, PDType1Font.HELVETICA_BOLD, 12, 360, top,
                        "FACTURA ELECTRONICA DE VENTA");
                text(cs, PDType1Font.HELVETICA, 10, 360, top - 18,
                        "No. " + safe(ctx.getInvoiceNumber()));
                text(cs, PDType1Font.HELVETICA, 10, 360, top - 32,
                        "Venta: " + safe(ctx.getSaleNumber()));
                text(cs, PDType1Font.HELVETICA, 10, 360, top - 46,
                        "Fecha: " + (ctx.getIssueDate() != null ? ctx.getIssueDate().format(DTF) : "-"));

                // Customer
                float cy = top - 70;
                text(cs, PDType1Font.HELVETICA_BOLD, 11, 50, cy, "CLIENTE");
                text(cs, PDType1Font.HELVETICA, 10, 50, cy - 14, "Nombre: " + safe(ctx.getCustomerName()));
                text(cs, PDType1Font.HELVETICA, 10, 50, cy - 26, "Documento: " + safe(ctx.getCustomerDocument()));
                text(cs, PDType1Font.HELVETICA, 10, 50, cy - 38, "Direccion: " + safe(ctx.getCustomerAddress()));
                text(cs, PDType1Font.HELVETICA, 10, 50, cy - 50, "Telefono: " + safe(ctx.getCustomerPhone()));

                // Table header
                float ty = cy - 80;
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 50, ty, "Producto");
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 260, ty, "Lote");
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 360, ty, "Cant");
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 410, ty, "V.Unit");
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 470, ty, "Desc");
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 520, ty, "Total");
                line(cs, 50, ty - 4, width - 50, ty - 4);

                // Rows
                float ry = ty - 18;
                for (InvoiceContext.Line l : ctx.getLines()) {
                    text(cs, PDType1Font.HELVETICA, 9, 50, ry, trim(safe(l.getProductName()), 38));
                    text(cs, PDType1Font.HELVETICA, 9, 260, ry, safe(l.getLotNumber()));
                    text(cs, PDType1Font.HELVETICA, 9, 360, ry, money(l.getQuantity()));
                    text(cs, PDType1Font.HELVETICA, 9, 410, ry, money(l.getUnitPrice()));
                    text(cs, PDType1Font.HELVETICA, 9, 470, ry, money(l.getDiscountAmount()));
                    text(cs, PDType1Font.HELVETICA, 9, 520, ry, money(l.getLineTotal()));
                    ry -= 14;
                    if (ry < 180) {
                        break; // single-page safety guard
                    }
                }

                // Totals
                line(cs, 360, ry - 2, width - 50, ry - 2);
                ry -= 18;
                text(cs, PDType1Font.HELVETICA, 10, 400, ry, "Subtotal:");
                text(cs, PDType1Font.HELVETICA, 10, 500, ry, "$ " + money(ctx.getSubtotal()));
                ry -= 14;
                text(cs, PDType1Font.HELVETICA, 10, 400, ry, "Descuentos:");
                text(cs, PDType1Font.HELVETICA, 10, 500, ry, "$ " + money(ctx.getTotalDiscount()));
                ry -= 14;
                text(cs, PDType1Font.HELVETICA, 10, 400, ry, "IVA:");
                text(cs, PDType1Font.HELVETICA, 10, 500, ry, "$ " + money(ctx.getTotalTax()));
                ry -= 18;
                text(cs, PDType1Font.HELVETICA_BOLD, 13, 400, ry, "TOTAL:");
                text(cs, PDType1Font.HELVETICA_BOLD, 13, 500, ry, "$ " + money(ctx.getTotalAmount()));

                // CUFE + QR
                float fy = 150;
                line(cs, 50, fy + 20, width - 50, fy + 20);
                text(cs, PDType1Font.HELVETICA_BOLD, 10, 50, fy, "CUFE (simulado):");
                text(cs, PDType1Font.HELVETICA, 8, 50, fy - 12, trim(safe(ctx.getCufe()), 80));
                text(cs, PDType1Font.HELVETICA, 8, 50, fy - 30,
                        "Representacion grafica de factura electronica. ERP Frescura Total.");

                try {
                    BufferedImage qr = QrCodeGenerator.generate(
                            ctx.getQrContent() != null ? ctx.getQrContent() : safe(ctx.getCufe()), 150);
                    PDImageXObject qrImg = LosslessFactory.createFromImage(doc, qr);
                    cs.drawImage(qrImg, width - 170, fy - 40, 110, 110);
                } catch (Exception ex) {
                    text(cs, PDType1Font.HELVETICA, 9, width - 170, fy, "[QR no disponible]");
                }
            }

            doc.save(out.toFile());
        }
        return out;
    }

    private void text(PDPageContentStream cs, PDType1Font font, float size,
                      float x, float y, String value) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(value));
        cs.endText();
    }

    private void line(PDPageContentStream cs, float x1, float y1, float x2, float y2)
            throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private static String sanitize(String s) {
        if (s == null) {
            return "";
        }
        // PDFBox Helvetica (WinAnsi) cannot encode some unicode chars; normalize.
        return s.replace("…", "...")
                .replaceAll("[^\\x00-\\xFF]", "?");
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String trim(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "...";
    }

    private static String money(BigDecimal v) {
        return v == null ? "0.00" : v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
