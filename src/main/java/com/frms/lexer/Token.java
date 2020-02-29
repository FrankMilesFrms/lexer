package com.frms.lexer;

import java.util.ArrayList;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/25 10:02(ydt)
 */
public abstract class Token
{
    /**
     * 设置要分析的文本
     * @param str
     * @param lineStart Tag.EOL 后一个位置 的集合
     */
    public abstract void set(String str, int[] lineStart, int length);
    
    /**
     * 设置要分析的文本
     * @param chars
     * @param lineStart Tag.EOL 后一个位置 的集合
     */
    public abstract void set(char[] chars, int[] lineStart, int length);
    
    /**
     * 语言分析
     * @param start
     * @param end
     */
    public abstract void token(int start, int startLine, int end, int endLine);
}
