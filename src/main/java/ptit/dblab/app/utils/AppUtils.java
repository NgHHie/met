package ptit.dblab.app.utils;


import ptit.dblab.app.entity.TableCreated;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AppUtils {
    private final static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    public static LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null; // Handle cases where either date or time is null, return null or throw an error
        }
        return date.atTime(time);
    }

    public static boolean isOpenTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end);
    }

    public static String[] convertToFormattedArrayTableUses(List<TableCreated> tableUses) {
        AtomicInteger counter = new AtomicInteger(1); // Start counter at 1

        // Convert each TableDetail to "nameTable: number priority"
        List<String> formattedList = tableUses.stream()
                .map(detail -> detail.getTableNameWithPrefix() + ": " + counter.getAndIncrement())
                .toList();

        return formattedList.toArray(new String[0]);
    }

    public static String getCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return ""; // Return an empty string if the cell is null or blank
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue()); // For numeric, return as string
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return ""; // Return empty string for other cell types
        }
    }
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return dateTime.format(formatter);
    }

    public static String getClientIp(HttpServletRequest request) {
        if(Objects.isNull(request)) return "system-retry";
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }

    public static String getFullName(String firstName, String lastName) {
        String fullName =(lastName != null ? lastName : "")+ (firstName != null ? " "+firstName : "");
        return fullName.trim();
    }
}
