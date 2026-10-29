package com.frescuratotal.erp.common.invoice;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

/**
 * Renders a visual electronic invoice as a PNG image using Java2D.
 *
 * <p>The QR code produced with ZXing is composited onto the image.</p>
 */
public class PngInvoiceGenerator implements InvoiceDocumentGenerator {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 1120;
    private static final Color BRAND = new Color(26, 115, 60);
    private static final Color LIGHT = new Color(235, 245, 238);
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public Path generate(InvoiceContext ctx, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            draw(g, ctx);
        } finally {
            g.dispose();
        }
        Path out = outputDir.resolve(ctx.getInvoiceNumber() + ".png");
        ImageIO.write(img, "png", out.toFile());
        return out;
    }

    private void draw(Graphics2D g, InvoiceContext ctx) {
        Font title = new Font("SansSerif", Font.BOLD, 26);
        Font h = new Font("SansSerif", Font.BOLD, 14);
        Font normal = new Font("SansSerif", Font.PLAIN, 13);
        Font small = new Font("SansSerif", Font.PLAIN, 11);

        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Header band
        g.setColor(BRAND);
        g.fillRect(0, 0, WIDTH, 90);
        g.setColor(Color.WHITE);
        g.setFont(title);
        g.drawString(safe(ctx.getCompanyName()), 30, 45);
        g.setFont(small);
        g.drawString("NIT: " + safe(ctx.getCompanyNit())
                + "   |   " + safe(ctx.getCompanyAddress())
                + ", " + safe(ctx.getCompanyCity()), 30, 68);
        g.drawString("Tel: " + safe(ctx.getCompanyPhone())
                + "   |   " + safe(ctx.getCompanyEmail()), 30, 82);

        // Invoice title box
        g.setColor(LIGHT);
        g.fillRect(500, 100, 270, 80);
        g.setColor(BRAND);
        g.setFont(h);
        g.drawString("FACTURA ELECTRONICA DE VENTA", 510, 122);
        g.setColor(Color.DARK_GRAY);
        g.setFont(normal);
        g.drawString("No. " + safe(ctx.getInvoiceNumber()), 510, 145);
        g.drawString("Venta: " + safe(ctx.getSaleNumber()), 510, 163);
        g.drawString("Fecha: "
                + (ctx.getIssueDate() != null ? ctx.getIssueDate().format(DTF) : "-"), 510, 180);

        // Customer box
        int y = 120;
        g.setColor(BRAND);
        g.setFont(h);
        g.drawString("CLIENTE", 30, y);
        g.setColor(Color.DARK_GRAY);
        g.setFont(normal);
        y += 22;
        g.drawString("Nombre: " + safe(ctx.getCustomerName()), 30, y);
        y += 18;
        g.drawString("Documento: " + safe(ctx.getCustomerDocument()), 30, y);
        y += 18;
        g.drawString("Direccion: " + safe(ctx.getCustomerAddress()), 30, y);
        y += 18;
        g.drawString("Telefono: " + safe(ctx.getCustomerPhone()), 30, y);

        // Table header
        int tableTop = 215;
        g.setColor(BRAND);
        g.fillRect(30, tableTop, WIDTH - 60, 26);
        g.setColor(Color.WHITE);
        g.setFont(h);
        g.drawString("Producto", 40, tableTop + 18);
        g.drawString("Lote", 300, tableTop + 18);
        g.drawString("Cant.", 440, tableTop + 18);
        g.drawString("V. Unit", 510, tableTop + 18);
        g.drawString("Desc.", 600, tableTop + 18);
        g.drawString("Total", 690, tableTop + 18);

        // Table rows
        g.setFont(small);
        int rowY = tableTop + 26;
        boolean alt = false;
        for (InvoiceContext.Line line : ctx.getLines()) {
            if (alt) {
                g.setColor(LIGHT);
                g.fillRect(30, rowY, WIDTH - 60, 22);
            }
            alt = !alt;
            g.setColor(Color.DARK_GRAY);
            g.drawString(trim(safe(line.getProductName()), 34), 40, rowY + 15);
            g.drawString(safe(line.getLotNumber()), 300, rowY + 15);
            g.drawString(money(line.getQuantity()), 440, rowY + 15);
            g.drawString(money(line.getUnitPrice()), 510, rowY + 15);
            g.drawString(money(line.getDiscountAmount()), 600, rowY + 15);
            g.drawString(money(line.getLineTotal()), 690, rowY + 15);
            rowY += 22;
        }

        // Totals box
        int tY = rowY + 30;
        g.setColor(Color.DARK_GRAY);
        g.setFont(normal);
        g.drawString("Subtotal:", 560, tY);
        g.drawString("$ " + money(ctx.getSubtotal()), 690, tY);
        tY += 20;
        g.drawString("Descuentos:", 560, tY);
        g.drawString("$ " + money(ctx.getTotalDiscount()), 690, tY);
        tY += 20;
        g.drawString("IVA:", 560, tY);
        g.drawString("$ " + money(ctx.getTotalTax()), 690, tY);
        tY += 26;
        g.setColor(BRAND);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("TOTAL:", 560, tY);
        g.drawString("$ " + money(ctx.getTotalAmount()), 690, tY);

        // CUFE + QR
        int footY = Math.max(tY + 50, HEIGHT - 190);
        g.setColor(Color.GRAY);
        g.drawLine(30, footY - 20, WIDTH - 30, footY - 20);
        g.setColor(Color.DARK_GRAY);
        g.setFont(h);
        g.drawString("CUFE (simulado):", 30, footY);
        g.setFont(small);
        g.drawString(trim(safe(ctx.getCufe()), 70), 30, footY + 18);
        g.setFont(small);
        g.drawString("Representacion grafica de factura electronica.", 30, footY + 44);
        g.drawString("Documento generado por ERP Frescura Total.", 30, footY + 60);

        // QR image
        try {
            BufferedImage qr = QrCodeGenerator.generate(
                    ctx.getQrContent() != null ? ctx.getQrContent() : safe(ctx.getCufe()), 140);
            g.drawImage(qr, WIDTH - 180, footY - 10, 140, 140, null);
            g.drawString("Escanee para validar", WIDTH - 180, footY + 145);
        } catch (Exception ex) {
            g.drawString("[QR no disponible]", WIDTH - 180, footY + 60);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String trim(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String money(BigDecimal v) {
        return v == null ? "0.00" : v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
