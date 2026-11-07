package com.parking.model;

/**
 * Kích thước ô đỗ và phân cấp phương tiện.
 * Áp dụng Enums trong Java (Slide 04, 05).
 * Thứ tự phân cấp: SMALL (0) < MEDIUM (1) < LARGE (2).
 */
public enum SpotSize {
    SMALL(1, "Nhỏ (Xe máy)"),
    MEDIUM(2, "Vừa (Ô tô)"),
    LARGE(3, "Lớn (Xe tải/Khách)");

    private final int level;
    private final String displayName;

    SpotSize(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Kiểm tra xem kích thước ô đỗ hiện tại có đủ lớn để chứa kích thước yêu cầu hay không.
     * Quy tắc: Ô đỗ có kích thước >= kích thước phương tiện yêu cầu.
     * 
     * @param required Kích thước tối thiểu phương tiện yêu cầu
     * @return true nếu vừa, ngược lại false
     */
    public boolean canFit(SpotSize required) {
        if (required == null) {
            return false;
        }
        return this.ordinal() >= required.ordinal();
    }
}
