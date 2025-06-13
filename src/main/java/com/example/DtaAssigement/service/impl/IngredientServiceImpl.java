package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.ImportErrorDTO;
import com.example.DtaAssigement.dto.ImportResultDTO;
import com.example.DtaAssigement.dto.IngredientDTO;
import com.example.DtaAssigement.dto.IngredientStockUpdateDTO;
import com.example.DtaAssigement.ennum.TransactionType;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.Ingredient;
import com.example.DtaAssigement.entity.IngredientHistory;
import com.example.DtaAssigement.mapper.IngredientMapper;
import com.example.DtaAssigement.repository.*;
import com.example.DtaAssigement.service.IngredientService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final BranchRepository branchRepo;
    private final IngredientHistoryRepository ingredientHistoryRepo;

    @Override
    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public Optional<Ingredient> getById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Override
    public IngredientDTO create(IngredientDTO ingredientDTO) {
        // Tìm branch từ branchId trong DTO
        Branch branch = branchRepo.findByName(ingredientDTO.getBranchName())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + ingredientDTO.getBranchName()));


        Ingredient ingredient = IngredientMapper.toEntity(ingredientDTO, branch);
        ingredientRepository.save(ingredient);

        //3. Tạo bản ghi lịch sử nhập
        IngredientHistory history = IngredientHistory.builder()
                .ingredient(ingredient)
                .transactionType(TransactionType.IMPORT)
                .quantity(ingredient.getQuantityInStock())
                .transactionDate(LocalDateTime.now())
                .priceAtTransaction(ingredient.getImportPrice())
                .quantityAfterTransaction(ingredient.getQuantityInStock())
                .note("nhập vào kho")
                .build();
        ingredientHistoryRepo.save(history);

        return IngredientMapper.toDTO(ingredient);
    }


    @Override
    public IngredientDTO updateStock(Long id, IngredientStockUpdateDTO dto) {
        return ingredientRepository.findById(id)
                .map(ingredient -> {

                    double oldQty = ingredient.getQuantityInStock();
                    double diff = dto.getQuantityChange();

                    double newQty = oldQty + diff;
                    if (newQty < 0) {
                        throw new RuntimeException("Không thể xuất kho vượt quá số lượng tồn");
                    }

                    ingredient.setQuantityInStock(newQty);
                    ingredient.setImportPrice(dto.getImportPrice());

                    // Lưu ingredient trước để đảm bảo id không thay đổi
                    ingredientRepository.save(ingredient);

                    // Nếu có thay đổi số lượng (diff != 0), tạo history
                    if (diff != 0) {
                        TransactionType type = diff > 0
                                ? TransactionType.IMPORT
                                : TransactionType.EXPORT;
                        double absQty = Math.abs(diff);

                        IngredientHistory history = IngredientHistory.builder()
                                .ingredient(ingredient)
                                .transactionType(type)
                                .quantity(absQty)
                                .transactionDate(LocalDateTime.now())
                                .priceAtTransaction(dto.getImportPrice())
                                .quantityAfterTransaction(newQty)
                                .note(type == TransactionType.IMPORT
                                        ? "Nhập kho (điều chỉnh)"
                                        : "Xuất kho (điều chỉnh)")
                                .build();
                        ingredientHistoryRepo.save(history);
                    }

                    return IngredientMapper.toDTO(ingredient);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu với id: " + id));
    }

    @Override
    public void delete(Long id) {
        // 1. Lấy nguyên liệu từ DB
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu để xóa với id: " + id));

        ingredientRepository.delete(ingredient);
    }

    /**
     * Import từ file Excel.
     * - Với các dòng thành công: cập nhật database và ghi vào successList.
     * - Với các dòng lỗi: ghi nguyên raw row + thông báo lỗi vào một Workbook tạm để client tải về.
     * Cuối cùng trả về ImportResultDTO chứa số hàng thành công, số hàng lỗi, danh sách DTO thành công và 1 token để client tải file lỗi.
     */
    @Override
    public ImportResultDTO importFromExcel(MultipartFile file) {
        List<IngredientDTO> successList = new ArrayList<>();
        List<ImportErrorDTO> errorList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            // Nếu file trống (chỉ có header hoặc không có dòng nào)
            if (!rowIterator.hasNext()) {
                // Toàn bộ file trống → trả về failureCount = 1 (hoặc tuỳ bạn xử lý)
                errorList.add(new ImportErrorDTO(
                        0,
                        "",
                        "",
                        "",
                        "EMPTY_FILE",
                        "File Excel không có dữ liệu (chỉ có header hoặc trống)."
                ));
                return new ImportResultDTO(0, 1, successList, errorList);
            }

            // Đọc header rồi bỏ qua
            Row headerRow = rowIterator.next();
            int nameColIndex = 0;
            int qtyColIndex = 1;
            int priceColIndex = 2;

            // Duyệt từng dòng dữ liệu
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row == null) continue;

                String rawName = "";
                String rawQty  = "";
                String rawPrice = "";
                String code = "";
                String message = "";

                try {
                    // ----- 1) Đọc cột name -----
                    Cell cellName = row.getCell(nameColIndex);
                    if (cellName == null || cellName.getCellType() == CellType.BLANK) {
                        code = "MISSING_NAME";
                        throw new RuntimeException("Thiếu trường 'name' (ô trống).");
                    }
                    rawName = cellName.getStringCellValue().trim();
                    if (rawName.isEmpty()) {
                        code = "EMPTY_NAME";
                        throw new RuntimeException("Trường 'name' không thể để trống.");
                    }

                    // ----- 2) Đọc cột quantityChange -----
                    Cell cellQty = row.getCell(qtyColIndex);
                    double quantityChange;
                    if (cellQty == null || cellQty.getCellType() == CellType.BLANK) {
                        code = "MISSING_QUANTITY";
                        throw new RuntimeException("Thiếu trường 'quantityChange'.");
                    }
                    if (cellQty.getCellType() == CellType.NUMERIC) {
                        quantityChange = cellQty.getNumericCellValue();
                    } else if (cellQty.getCellType() == CellType.STRING) {
                        rawQty = cellQty.getStringCellValue().trim();
                        try {
                            quantityChange = Double.parseDouble(rawQty);
                        } catch (NumberFormatException e) {
                            code = "INVALID_QUANTITY";
                            throw new RuntimeException("Giá trị 'quantityChange' không hợp lệ (phải là số).");
                        }
                    } else {
                        code = "INVALID_QUANTITY_TYPE";
                        throw new RuntimeException("Giá trị 'quantityChange' phải ở dạng số.");
                    }
                    if (rawQty.isEmpty()) {
                        rawQty = String.valueOf(quantityChange);
                    }

                    // ----- 3) Đọc cột importPrice -----
                    Cell cellPrice = row.getCell(priceColIndex);
                    BigDecimal importPrice;
                    if (cellPrice == null || cellPrice.getCellType() == CellType.BLANK) {
                        code = "MISSING_PRICE";
                        throw new RuntimeException("Thiếu trường 'importPrice'.");
                    }
                    if (cellPrice.getCellType() == CellType.NUMERIC) {
                        importPrice = BigDecimal.valueOf(cellPrice.getNumericCellValue());
                    } else if (cellPrice.getCellType() == CellType.STRING) {
                        rawPrice = cellPrice.getStringCellValue().trim();
                        try {
                            importPrice = new BigDecimal(rawPrice);
                        } catch (NumberFormatException e) {
                            code = "INVALID_PRICE";
                            throw new RuntimeException("Giá trị 'importPrice' không hợp lệ (phải là số).");
                        }
                    } else {
                        code = "INVALID_PRICE_TYPE";
                        throw new RuntimeException("Giá trị 'importPrice' phải ở dạng số.");
                    }

                    // ----- 4) Business logic: tìm Ingredient theo tên -----
                    Optional<Ingredient> optIngredient = ingredientRepository.findByName(rawName);
                    Ingredient ingredient;
                    if (optIngredient.isPresent()) {
                        ingredient = optIngredient.get();
                        double oldQty = ingredient.getQuantityInStock();
                        double newQty = oldQty + quantityChange;
                        if (newQty < 0) {
                            code = "NEGATIVE_STOCK";
                            throw new RuntimeException(String.format(
                                    "Không thể giảm số lượng (oldQty=%.2f, change=%.2f) dẫn đến tồn âm.",
                                    oldQty, quantityChange));
                        }
                        ingredient.setQuantityInStock(newQty);
                        ingredient.setImportPrice(importPrice);
                        ingredientRepository.save(ingredient);

                        TransactionType type = (quantityChange >= 0)
                                ? TransactionType.IMPORT
                                : TransactionType.EXPORT;
                        String note = (quantityChange >= 0)
                                ? "Nhập vào kho"
                                : "Xuất khỏi kho";

                        // Ghi lịch sử
                        IngredientHistory history = IngredientHistory.builder()
                                .ingredient(ingredient)
                                .transactionType(type)
                                .quantity(quantityChange)
                                .transactionDate(LocalDateTime.now())
                                .priceAtTransaction(importPrice)
                                .quantityAfterTransaction(newQty)
                                .note(note)
                                .build();
                        ingredientHistoryRepo.save(history);
                    } else {
                        code = "NOT_FOUND_INGREDIENT";
                        throw new RuntimeException("Không tìm thấy nguyên liệu có tên '" + rawName + "'.");
                    }

                    // Nếu đến đây không ném exception, nghĩa là dòng này SUCCESS
                    IngredientDTO dto = IngredientMapper.toDTO(ingredient);
                    successList.add(dto);

                } catch (RuntimeException rex) {
                    // Bắt mọi lỗi, lưu vào errorList
                    message = rex.getMessage();

                    // Nếu rawName, rawQty, rawPrice vẫn rỗng, thử đọc thêm raw để user dễ debug
                    if (rawName.isEmpty()) {
                        Cell c = row.getCell(nameColIndex);
                        rawName = (c == null) ? "" :
                                (c.getCellType() == CellType.STRING ? c.getStringCellValue() : "");
                    }
                    if (rawQty.isEmpty()) {
                        Cell c = row.getCell(qtyColIndex);
                        rawQty = (c == null) ? "" :
                                (c.getCellType() == CellType.STRING ? c.getStringCellValue() :
                                        (c.getCellType() == CellType.NUMERIC ? String.valueOf(c.getNumericCellValue()) : ""));
                    }
                    if (rawPrice.isEmpty()) {
                        Cell c = row.getCell(priceColIndex);
                        rawPrice = (c == null) ? "" :
                                (c.getCellType() == CellType.STRING ? c.getStringCellValue() :
                                        (c.getCellType() == CellType.NUMERIC ? String.valueOf(c.getNumericCellValue()) : ""));
                    }
                    // Nếu code rỗng (không được set), gán code chung
                    if (code.isEmpty()) {
                        code = "UNKNOWN_ERROR";
                    }

                    errorList.add(new ImportErrorDTO(
                            row.getRowNum() + 1,
                            rawName,
                            rawQty,
                            rawPrice,
                            code,
                            message
                    ));
                    // Tiếp tục vòng lặp, không throw ra ngoài
                }
            }

        } catch (IOException ioe) {
            // Lỗi đọc file Excel → coi như 1 lỗi toàn cục
            errorList.add(new ImportErrorDTO(
                    0,
                    "",
                    "",
                    "",
                    "READ_ERROR",
                    "Lỗi khi đọc file Excel: " + ioe.getMessage()
            ));
            return new ImportResultDTO(0, errorList.size(), successList, errorList);
        }

        // Hoàn thành duyệt hết các dòng
        int successCount = successList.size();
        int failureCount = errorList.size();
        return new ImportResultDTO(successCount, failureCount, successList, errorList);
    }


}
