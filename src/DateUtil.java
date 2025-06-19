import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class DateUtil {
    // 将中文日期字符串转换为 java.sql.Date
    public static java.sql.Date convertToSqlDate(String chineseDate) {
        if (chineseDate == null || chineseDate.trim().isEmpty()) {
            return null;
        }
        
        // 尝试不同格式的日期解析
        SimpleDateFormat[] formats = {
            new SimpleDateFormat("yyyy年MM月dd日"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyyMMdd")
        };

        for (SimpleDateFormat sdf : formats) {
            try {
                Date date = sdf.parse(chineseDate);
                return new java.sql.Date(date.getTime());
            } catch (ParseException ignored) {
                // 尝试下一个格式
            }
        }

        JOptionPane.showMessageDialog(null, 
            "无效的日期格式: " + chineseDate + 
            "\n请使用格式: YYYY年MM月DD日 或 YYYY-MM-DD", 
            "日期格式错误", 
            JOptionPane.ERROR_MESSAGE);
        return null;
    }

    // 将 java.sql.Date 转换为中文格式字符串
    public static String convertToChineseDate(java.sql.Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    // 验证并转换日期字符串
    public static java.sql.Date parseDate(String dateStr) {
        return convertToSqlDate(dateStr);
    }
    
    // 新增方法：直接从年月日数字创建SQL日期
    public static java.sql.Date createFromComponents(int year, int month, int day) {
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        try {
            return java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
