package pq.jdev.b001.bookstore.export;

import java.io.IOException;
import java.util.List;
 
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pq.jdev.b001.bookstore.cart.model.Order;
 
public class OrderExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Order> listOrders;
     
    public OrderExcelExporter(List<Order> listOrders) {
        this.listOrders = listOrders;
        workbook = new XSSFWorkbook();
    }
 
 
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Orders");
         
        Row row = sheet.createRow(0);
         
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
         
        createCell(row, 0, "Order Num", style);      
        createCell(row, 1, "Order Date", style);       
        createCell(row, 2, "CustomerName", style);    
        createCell(row, 3, "CustomerAddress", style);
        createCell(row, 4, "CustomerEmail", style);
        createCell(row, 5, "CustomerPhone", style);
        createCell(row, 6, "Amount", style);
         
    }
     
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
     
    private void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        for (Order order : listOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
             
            createCell(row, columnCount++, order.getOrderNum(), style);
            createCell(row, columnCount++, order.getOrderDate().toString(), style);
            createCell(row, columnCount++, order.getCustomerName(), style);
            createCell(row, columnCount++, order.getCustomerAddress(), style);
            createCell(row, columnCount++, order.getCustomerEmail(), style);
            createCell(row, columnCount++, order.getCustomerPhone(), style);
            createCell(row, columnCount++, String.valueOf(order.getAmount()), style);
             
        }
    }
     
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
         
        outputStream.close();
         
    }
}
