package com.cuipengfei.ssh.sshdemo;

import com.cuipengfei.ssh.sshdemo.client.SSHClient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by cuipengfei on 16-12-27.
 */
public class App {
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        /**
         *商品中心web服务
         */
        map.put("192.168.30.212", "BC-JYGOODS-WEB");
        map.put("192.168.31.212", "BC-JYGOODS-WEB");

        map.put("192.168.30.211", "BS-JYGOODS-PRODUCT");
        map.put("192.168.31.211", "BS-JYGOODS-PRODUCT");
        map.put("192.168.32.211", "BS-JYGOODS-PRODUCT");
        map.put("192.168.33.211", "BS-JYGOODS-PRODUCT");

        SSHClient sshClient=new SSHClient();
        try {
            sshClient.init();
            String filePath = "/home/cuipengfei/商品basic服务统计.xlsx";
            FileInputStream fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);
            Sheet sheet = wb.getSheetAt(0);
            int index = sheet.getFirstRowNum();
            int count = sheet.getLastRowNum();
            Row row;
            Cell cell;
            String url;
            while (index < count) {
                row = sheet.getRow(index + 1);
                if (row == null || row.getCell(1) == null) {
                    break;
                }
                url = row.getCell(2).getStringCellValue();
                String cmd="cd /data/logs&&grep '"+url+"' access_log.2016-* > ~/aaa&&cat ~/aaa | cut -d ' ' -f 1 | sort | uniq -c | sort -nr | awk '{print$0 }' | head -n 10 | less";
                String resultStr=sshClient.excute(cmd);
                cell = row.createCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(resultStr);
                index++;
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            FileOutputStream outputStream = new FileOutputStream(filePath);
            wb.write(outputStream);
            outputStream.flush();
            if (outputStream != null) {
                outputStream.close();
            }
            sshClient.destroy();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
