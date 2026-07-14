package com.journal.model;

/**
 * 期刊常量类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   定义大黑山大学学报的四个分版期刊名称常量：工学版、理学版、
 *   文科版、生物医学版，并提供期刊名称合法性校验方法。
 */
public class Journal {

    /** 工学版 */
    public static final String ENGINEERING = "工学版";

    /** 理学版 */
    public static final String SCIENCE = "理学版";

    /** 文科版 */
    public static final String LIBERAL_ARTS = "文科版";

    /** 生物医学版 */
    public static final String BIOMEDICAL = "生物医学版";

    /**
     * 私有构造器，禁止实例化常量类
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    private Journal() {
    }

    /**
     * 校验传入的期刊名称是否为合法的期刊分版
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param name 待校验的期刊名称
     * @return 若期刊名称合法返回 true，否则返回 false
     */
    public static boolean isValid(String name) {
        if (name == null) {
            return false;
        }
        return ENGINEERING.equals(name)
                || SCIENCE.equals(name)
                || LIBERAL_ARTS.equals(name)
                || BIOMEDICAL.equals(name);
    }
}
