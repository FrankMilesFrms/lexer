/*
 * Copyright Frms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
