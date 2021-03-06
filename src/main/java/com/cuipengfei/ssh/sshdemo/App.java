package com.cuipengfei.ssh.sshdemo;

import com.cuipengfei.ssh.sshdemo.client.SSHClient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

/**
 * Created by cuipengfei on 16-12-27.
 */
public class App {
    public static void main(String[] args) {
        Properties prop=null;
        try {
            InputStream inStream=App.class.getClassLoader().getResourceAsStream("properties.properties");
            prop = new Properties();
            prop.load(new InputStreamReader(inStream,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if(prop==null){
            return;
        }

        SSHClient sshClient=new SSHClient();
        try {
            sshClient.init(prop.getProperty("host"),
                    Integer.parseInt(prop.getProperty("port","22")),
                    prop.getProperty("username"),
                    prop.getProperty("password")
            );
            FileInputStream fileInputStream = new FileInputStream(prop.getProperty("filePath"));
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
                cell = row.createCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String cmd="cd /data/logs&&grep '"+url+"' access_log.2016-* > ~/aaa&&cat ~/aaa | cut -d ' ' -f 1 | sort | uniq -c | sort -nr | awk '{print$0 }' | head -n 10 | less";
                Set<String> resultSet=sshClient.excute(cmd);
                StringBuffer resultStr=new StringBuffer();
                if(resultSet!=null){
                    for (String ip:resultSet) {
                        resultStr.append(ip);
                        resultStr.append("(");
                        resultStr.append(prop.getProperty(ip,ip));
                        resultStr.append(")");
                    }
                }
                cell.setCellValue(resultStr.toString());
                index++;
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            FileOutputStream outputStream = new FileOutputStream(prop.getProperty("filePath"));
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
