package com.example.accountservice.utils;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelUtils {
    public static <T> List<T> readFromExcel(  MultipartFile file, Class<T> type) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {

//            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
//                    .headerStart(1) // Bỏ qua dòng tiêu đề
//                    .build();

            // Xác định loại tệp Excel dựa trên phần mở rộng
            PoijiExcelType excelType = file.getOriginalFilename().endsWith(".xls") ?
                    PoijiExcelType.XLS : PoijiExcelType.XLSX;

            // Sử dụng phương thức fromExcel với InputStream, loại tệp và các tùy chọn
            List<T> result = Poiji.fromExcel(inputStream, excelType, type);

            System.out.println("Số dòng đọc được: " + result.size());
            return result;
        }
    }
}
