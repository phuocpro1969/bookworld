package pq.jdev.b001.bookstore.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
 
import javax.servlet.http.HttpServletResponse;
 
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import pq.jdev.b001.bookstore.cart.model.Order;
 
 
public class OrderPDFExporter {
    private List<Order> listOrders;
     
    public OrderPDFExporter(List<Order> listOrders) {
        this.listOrders = listOrders;
    }
 
    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPadding(2);
         
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
         
        cell.setPhrase(new Phrase("Order Num", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("Order Date", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("Customer Name", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("Customer Address", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("Customer Email", font));
        table.addCell(cell);    

        cell.setPhrase(new Phrase("Customer Phone", font));
        table.addCell(cell); 

        cell.setPhrase(new Phrase("Amount", font));
        table.addCell(cell); 

    }
     
    private void writeTableData(PdfPTable table) {
        for (Order order : listOrders) {
            table.addCell(String.valueOf(order.getOrderNum()));
            table.addCell(order.getOrderDate().toString());
            table.addCell(order.getCustomerName());
            table.addCell(order.getCustomerAddress());
            table.addCell(order.getCustomerEmail());
            table.addCell(order.getCustomerPhone());
            table.addCell(String.valueOf(order.getAmount()));
        }
    }
     
    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
         
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
         
        Paragraph p = new Paragraph("List of Orders", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
         
        document.add(p);
         
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 1.5f, 2.0f, 2.5f, 2.5f, 2.5f, 1.5f});
        table.setSpacingBefore(10);
         
        writeTableHeader(table);
        writeTableData(table);
         
        document.add(table);
         
        document.close();
         
    }
}
