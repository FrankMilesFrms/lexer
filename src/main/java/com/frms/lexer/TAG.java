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

public class TAG
{
    public static final char EOL = '\n',
                             EOF = '\uFFFF';
    
    public static final int COMMON = 0,
    
                            ZHUSHI_LINE = 1,
                            ZHUSHI_BLOCK = 0x102,
                            
    
                            STRING = 2,
                            SYMBOL = 3,
                            INTENGER = 4,
                            KEYWORD = 5,
                            CONSTANT = 51,
                            UNKNOW = 0Xaa;
    
    public static int ZHUSHI_DOC = 0x103;
    public static final String[] JavaScript_keyWords =
        {
            "Infinity", "JSON", "NaN", "arguments", "break",
            
            "case", "catch", "class", "const", "continue",
            
            "debugger", "default", "delete", "do",
            
            "else", "enum", "export", "extends", "false",
            
            "finally", "for", "function", "if", "implements",
            
            "import", "in", "instanceof", "interface", "let",
            
            "new", "null", "package", "private", "protected",
            
            "public", "return", "static", "super", "switch",
            
            "this", "throw", "true", "try", "typeof",
            
            "undefined", "var", "void", "while", "with",
            
            "yield"
        };
    
    
    public static final String[] JAVA_KEYWORD =
        {
            // 这玩意好难整理
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double","else", "enum", "extends",
            "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native","new", "package",
            "private", "protected", "public", "return",
            "short","static","strictfp","super","switch",
            "synchronized", "this", "throw", "throws", "transient",
            "try","void","volatile", "while", "goto", "const",
            "strictfp", "assert", "enum","byValue", "cast", "const",
            "false", "future", "generic", "goto", "inner", "null",
            "operator","outer", "rest", "true", "var"
        };
    
    public static final String[] C_KEYWORD =
        {
            "auto", "short", "int", "long", "double",
            "char", "struct", "union", "enum", "typedef",
            "const", "unsigned", "signed", "extern", "register",
            "static", "volatile", "void ", "if", "else", "switch",
            "case", "for", "do", "while", "goto", "continue", "break",
            "default", "sizeof", "return"
        };
    
    public static final String[] nativeKeyWorld =
        {
            "break",    "delete",  "function",   "return", "typeof",
            "case",     "do",      "if",         "switch", "var",
            "catch",    "else",    "in",         "this",   "void",
            "continue", "false",   "instanceof", "throw",  "while",
            "debugger", "finally", "new",        "true",   "with",
            "default",  "for",     "null",       "try",
            
            // 只对部分做处理
            // ECMAScript 5 保留字
            "class", "enum", "super", "const", "export", "extends", "import",
            // 严格模式下的保留字
            "let", "public", "private", "yield", "implements",
            "interface", "package", "protected", "static",
            // 特殊(预存)
            "arguments", "Infinity", "undefined", "NaN", "JSON"
        };
}