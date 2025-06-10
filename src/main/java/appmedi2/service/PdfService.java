package appmedi2.service;

import appmedi2.entity.Solicitud;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    // Colores personalizados
    private static final BaseColor AZUL_PRINCIPAL = new BaseColor(37, 99, 235);
    private static final BaseColor AZUL_SECUNDARIO = new BaseColor(59, 130, 246);
    private static final BaseColor VERDE_EXITO = new BaseColor(16, 185, 129);
    private static final BaseColor ROJO_ERROR = new BaseColor(239, 68, 68);
    private static final BaseColor AMARILLO_PENDIENTE = new BaseColor(245, 158, 11);
    private static final BaseColor GRIS_CLARO = new BaseColor(248, 250, 252);
    private static final BaseColor GRIS_MEDIO = new BaseColor(148, 163, 184);
    private static final BaseColor BLANCO = BaseColor.WHITE;

    public ByteArrayInputStream generarHistorialPdf(List<Solicitud> solicitudes) {
        Document document = new Document(PageSize.A4, 40, 40, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Agregar eventos para header y footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);

            document.open();

            // === ENCABEZADO PRINCIPAL ===
            agregarEncabezadoPrincipal(document);

            // === INFORMACIÓN DEL REPORTE ===
            agregarInformacionReporte(document, solicitudes.size());

            // === ESTADÍSTICAS RÁPIDAS ===
            agregarEstadisticas(document, solicitudes);

            // === TABLA DE DATOS ===
            if (!solicitudes.isEmpty()) {
                agregarTablaDatos(document, solicitudes);
            } else {
                agregarMensajeSinDatos(document);
            }

            // === PIE DE PÁGINA CON INFORMACIÓN ADICIONAL ===
            agregarPieInformacion(document);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void agregarEncabezadoPrincipal(Document document) throws DocumentException {
        // Crear tabla para el encabezado con logo y título
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4});

        // Celda del "logo" (icono de medicamento)
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(AZUL_PRINCIPAL);
        logoCell.setPadding(15);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font iconFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BLANCO);
        Paragraph iconPara = new Paragraph("Rx", iconFont);
        iconPara.setAlignment(Element.ALIGN_CENTER);
        logoCell.addElement(iconPara);
        headerTable.addCell(logoCell);

        // Celda del título
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setBackgroundColor(AZUL_SECUNDARIO);
        titleCell.setPadding(15);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BLANCO);
        Paragraph title = new Paragraph("SISTEMA DE MEDICAMENTOS POR VOZ", titleFont);
        title.setAlignment(Element.ALIGN_LEFT);
        titleCell.addElement(title);

        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BLANCO);
        Paragraph subtitle = new Paragraph("Historial de Solicitudes de Medicamentos", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_LEFT);
        subtitle.setSpacingBefore(5);
        titleCell.addElement(subtitle);

        headerTable.addCell(titleCell);
        document.add(headerTable);
        document.add(new Paragraph(" ")); // Espacio
    }

    private void agregarInformacionReporte(Document document, int totalSolicitudes) throws DocumentException {
        // Tabla de información del reporte
        PdfPTable infoTable = new PdfPTable(3);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{2, 2, 2});
        infoTable.setSpacingBefore(10);

        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font infoValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL_PRINCIPAL);

        // Fecha de generación
        PdfPCell fechaCell = crearCeldaInfo("Fecha de Generacion:",
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                infoFont, infoValueFont);
        infoTable.addCell(fechaCell);

        // Total de registros
        PdfPCell totalCell = crearCeldaInfo("Total de Solicitudes:",
                String.valueOf(totalSolicitudes), infoFont, infoValueFont);
        infoTable.addCell(totalCell);

        // Período
        PdfPCell periodoCell = crearCeldaInfo("Periodo:",
                "Historico Completo", infoFont, infoValueFont);
        infoTable.addCell(periodoCell);

        document.add(infoTable);
        document.add(new Paragraph(" ")); // Espacio
    }

    private PdfPCell crearCeldaInfo(String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(GRIS_MEDIO);
        cell.setBackgroundColor(GRIS_CLARO);
        cell.setPadding(10);

        Paragraph labelPara = new Paragraph(label, labelFont);
        Paragraph valuePara = new Paragraph(value, valueFont);
        valuePara.setSpacingBefore(3);

        cell.addElement(labelPara);
        cell.addElement(valuePara);
        return cell;
    }

    private void agregarEstadisticas(Document document, List<Solicitud> solicitudes) throws DocumentException {
        // Calcular estadísticas
        long completadas = solicitudes.stream().filter(s -> "COMPLETADA".equals(s.getEstado().toString())).count();
        long pendientes = solicitudes.stream().filter(s -> "PENDIENTE".equals(s.getEstado().toString())).count();
        long canceladas = solicitudes.stream().filter(s -> "CANCELADA".equals(s.getEstado().toString())).count();

        // Título de estadísticas
        Font statsTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, AZUL_PRINCIPAL);
        Paragraph statsTitlePara = new Paragraph("RESUMEN ESTADISTICO", statsTitle);
        statsTitlePara.setSpacingBefore(15);
        statsTitlePara.setSpacingAfter(10);
        document.add(statsTitlePara);

        // Tabla de estadísticas
        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        statsTable.setWidths(new float[]{1, 1, 1, 1});

        // Total
        PdfPCell totalStatsCell = crearCeldaEstadistica("TOTAL", String.valueOf(solicitudes.size()), AZUL_PRINCIPAL);
        statsTable.addCell(totalStatsCell);

        // Completadas
        PdfPCell completadasCell = crearCeldaEstadistica("COMPLETADAS", String.valueOf(completadas), VERDE_EXITO);
        statsTable.addCell(completadasCell);

        // Pendientes
        PdfPCell pendientesCell = crearCeldaEstadistica("PENDIENTES", String.valueOf(pendientes), AMARILLO_PENDIENTE);
        statsTable.addCell(pendientesCell);

        // Canceladas
        PdfPCell canceladasCell = crearCeldaEstadistica("CANCELADAS", String.valueOf(canceladas), ROJO_ERROR);
        statsTable.addCell(canceladasCell);

        document.add(statsTable);
        document.add(new Paragraph(" ")); // Espacio
    }

    private PdfPCell crearCeldaEstadistica(String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(color);
        cell.setPadding(15);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BLANCO);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BLANCO);

        Paragraph labelPara = new Paragraph(label, labelFont);
        labelPara.setAlignment(Element.ALIGN_CENTER);

        Paragraph valuePara = new Paragraph(value, valueFont);
        valuePara.setAlignment(Element.ALIGN_CENTER);
        valuePara.setSpacingBefore(5);

        cell.addElement(labelPara);
        cell.addElement(valuePara);
        return cell;
    }

    private void agregarTablaDatos(Document document, List<Solicitud> solicitudes) throws DocumentException {
        // Título de la tabla
        Font tableTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, AZUL_PRINCIPAL);
        Paragraph tableTitlePara = new Paragraph("DETALLE DE SOLICITUDES", tableTitle);
        tableTitlePara.setSpacingBefore(20);
        tableTitlePara.setSpacingAfter(10);
        document.add(tableTitlePara);

        // Crear tabla principal
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2.5f, 1.8f, 1.2f, 2.7f});

        // Encabezados con estilo
        String[] headers = {"ID", "Medicamento", "Fecha", "Estado", "Texto Reconocido"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLANCO);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(AZUL_PRINCIPAL);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(12);
            cell.setPhrase(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        // Datos con colores alternados
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy\nHH:mm");
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font dataBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

        for (int i = 0; i < solicitudes.size(); i++) {
            Solicitud solicitud = solicitudes.get(i);
            BaseColor rowColor = (i % 2 == 0) ? BLANCO : GRIS_CLARO;

            // ID
            PdfPCell idCell = crearCeldaDato(String.valueOf(solicitud.getId()), dataFont, rowColor, Element.ALIGN_CENTER);
            table.addCell(idCell);

            // Medicamento
            PdfPCell medCell = crearCeldaDato(solicitud.getMedicamento().getNombre(), dataBoldFont, rowColor, Element.ALIGN_LEFT);
            table.addCell(medCell);

            // Fecha
            PdfPCell fechaCell = crearCeldaDato(solicitud.getFechaSolicitud().format(formatter), dataFont, rowColor, Element.ALIGN_CENTER);
            table.addCell(fechaCell);

            // Estado con color específico
            PdfPCell estadoCell = crearCeldaEstado(solicitud.getEstado().toString(), dataFont);
            table.addCell(estadoCell);

            // Texto Reconocido
            String textoReconocido = solicitud.getTextoReconocido();
            if (textoReconocido != null && textoReconocido.length() > 45) {
                textoReconocido = textoReconocido.substring(0, 42) + "...";
            }
            PdfPCell textoCell = crearCeldaDato(textoReconocido != null ? textoReconocido : "N/A", dataFont, rowColor, Element.ALIGN_LEFT);
            table.addCell(textoCell);
        }

        document.add(table);
    }

    private void agregarMensajeSinDatos(Document document) throws DocumentException {
        Font messageFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, GRIS_MEDIO);
        Paragraph message = new Paragraph("No hay solicitudes registradas en el sistema.", messageFont);
        message.setAlignment(Element.ALIGN_CENTER);
        message.setSpacingBefore(50);
        message.setSpacingAfter(50);
        document.add(message);
    }

    private PdfPCell crearCeldaDato(String texto, Font font, BaseColor backgroundColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(GRIS_MEDIO);
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell crearCeldaEstado(String estado, Font font) {
        BaseColor backgroundColor;
        BaseColor borderColor;
        BaseColor textColor = BLANCO;

        switch (estado.toUpperCase()) {
            case "COMPLETADA":
                backgroundColor = VERDE_EXITO;
                borderColor = new BaseColor(5, 150, 105);
                break;
            case "PENDIENTE":
                backgroundColor = AMARILLO_PENDIENTE;
                borderColor = new BaseColor(217, 119, 6);
                break;
            case "CANCELADA":
                backgroundColor = ROJO_ERROR;
                borderColor = new BaseColor(185, 28, 28);
                break;
            default:
                backgroundColor = GRIS_MEDIO;
                borderColor = new BaseColor(100, 116, 139);
        }

        Font estadoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, textColor);
        PdfPCell cell = new PdfPCell(new Phrase(estado, estadoFont));
        cell.setBackgroundColor(backgroundColor);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(borderColor);
        cell.setBorderWidth(1.5f);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private void agregarPieInformacion(Document document) throws DocumentException {
        document.add(new Paragraph(" ")); // Espacio

        // Tabla de información adicional
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(20);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(GRIS_CLARO);
        footerCell.setBorder(Rectangle.BOX);
        footerCell.setBorderColor(GRIS_MEDIO);
        footerCell.setPadding(15);

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
        Font footerBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_PRINCIPAL);

        Paragraph footerTitle = new Paragraph("INFORMACION DEL SISTEMA", footerBoldFont);
        footerTitle.setSpacingAfter(8);
        footerCell.addElement(footerTitle);

        Paragraph footerInfo = new Paragraph(
                "• Este reporte fue generado automaticamente por el Sistema de Medicamentos por Voz\n" +
                        "• Disenado especialmente para personas con discapacidad visual\n" +
                        "• Todas las solicitudes son procesadas mediante reconocimiento de voz\n" +
                        "• Para soporte tecnico, contacte al administrador del sistema", footerFont);
        footerCell.addElement(footerInfo);

        footerTable.addCell(footerCell);
        document.add(footerTable);
    }

    // Clase para manejar header y footer en cada página - CORREGIDA
    class HeaderFooterPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                // Obtener las dimensiones de la página
                Rectangle pageSize = document.getPageSize();
                float pageWidth = pageSize.getWidth() - document.leftMargin() - document.rightMargin();

                // Footer en cada página
                PdfPTable footerTable = new PdfPTable(3);
                footerTable.setTotalWidth(pageWidth); // Establecer ancho total explícitamente
                footerTable.setWidths(new float[]{1, 1, 1});

                Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, GRIS_MEDIO);

                // Fecha
                PdfPCell dateCell = new PdfPCell(new Phrase("Generado: " +
                        java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), footerFont));
                dateCell.setBorder(Rectangle.NO_BORDER);
                dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                dateCell.setPadding(5);
                footerTable.addCell(dateCell);

                // Sistema
                PdfPCell systemCell = new PdfPCell(new Phrase("Sistema de Medicamentos por Voz", footerFont));
                systemCell.setBorder(Rectangle.NO_BORDER);
                systemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                systemCell.setPadding(5);
                footerTable.addCell(systemCell);

                // Número de página
                PdfPCell pageCell = new PdfPCell(new Phrase("Pagina " + writer.getPageNumber(), footerFont));
                pageCell.setBorder(Rectangle.NO_BORDER);
                pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pageCell.setPadding(5);
                footerTable.addCell(pageCell);

                // Posicionar el footer con coordenadas absolutas
                float footerY = document.bottomMargin() - 20;
                footerTable.writeSelectedRows(0, -1, document.leftMargin(), footerY, writer.getDirectContent());

            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }
}