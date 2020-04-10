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

package com.frms.lexer.language;

import com.frms.lexer.TAG;
import com.frms.lexer.Token;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 *     语法关键字使用基于 Rnino Mozilla 1.8.0 版本。
 * </p>
 * <p>
 *     注意，这只是粗略编译，并将 [/][*] 和 [*][/] 统一看成注释标志。并非精准优化。
 * </p>
 * 项目名称 ： app
 * @author  ： Frms, 3505826836@qq.com
 * 创建时间 ：2020/2/25 10:09(ydt)
 */
public class JavaScript extends Token
{
    private int length; // 全文长度，他可以为[0, mChars.length]
    private char[] mChars;
    private int[] mRowStartCount;
    private int[] mModulesRowsStart;
    private int index; // 全文引索
    
    private int symbolLength = 0;
    private int line;
    private int lineEnd;
    
    private StringBuffer log;
    private ArrayList<Module> mModules;
    
    /**
     * 是否启用DOC注释高亮，并且他会优化注释结构，不再以 * / 以前文本视为注释。
     */
    private boolean useDocAnnotation = false;
    
    private static int lengthSymbol = 0;
    private static char peek, npeek;
    private static int indexSign;
    private static boolean hasEnd = false; // 是否处于完整符号中
    private static StringBuffer text;
    
    @Override
    public void token(int start, int startLine, int end, int endLine)
    {
        lengthSymbol = 0;
        mModules.clear();
        line = startLine;
        lineEnd = Math.min(mRowStartCount.length -1, endLine);
        index = start;
        
        length = Math.min(end, length);
        
        run :
        for(; index < length; index++)
        {
            peek = mChars[index];
            main : switch (peek)
            {
                
                case '/':
                    
                    npeek = next();
                    
                    if(npeek == '/')
                    {
                        addSymbol();
    
                        indexSign = index - 2;
    
                        while (hasNext())
                        {
                            if( next() == TAG.EOF )
                            {
                                mModules.add(new Module(TAG.ZHUSHI_LINE, index - indexSign - 1));
                                break main;
                            }
                            else if(mChars[index] == TAG.EOL)
                            {
            
                                mModules.add(new Module(TAG.ZHUSHI_LINE, index - indexSign - 1));
                                mModules.add(new Module(TAG.COMMON, 1));
                                line++;
                                break main;
                            }
                        }
    
    
                    }
                    else if(npeek == '*')
                    {
                        addSymbol();
                        lengthSymbol = 2;
                        
    
                        hasEnd = true;
    
                        // 这里不把 Tag.EOL 算做 注释的一部分，会被独立开来。
                        if(hasNext())
                        {
                            if(mChars[1 + index] == '*' && hasNext() && mChars[index + 2] != '/')
                            {
                                cyc : while (hasNext())
                                {
                                    npeek = mChars[++index];
                                    lengthSymbol ++;
        
                                    if(npeek == TAG.EOL)
                                    {
            
                                        if(lengthSymbol > 1)mModules.add(new Module(TAG.ZHUSHI_DOC, lengthSymbol - 1));
                                        mModules.add(new Module(TAG.COMMON, 1));
                                        line++;
                                        lengthSymbol = 0;
                                    } else
                                    if(npeek == TAG.EOF)
                                    {
                                        if(lengthSymbol > 1)
                                            mModules.add(new Module(TAG.ZHUSHI_DOC, lengthSymbol - 1));
                                        break cyc;
                                    } else
                                    if(npeek == '*')
                                    {
                                        if(next() == '/')
                                        {
                                            hasEnd = true;
                                            mModules.add(new Module(TAG.ZHUSHI_DOC, lengthSymbol + 1));
                                            lengthSymbol = 0;
                                            break cyc;
                                        } else
                                        {
                                            last();
                                        }
            
                                    }
        
        
                                }
                            } else
                            {
                                cyc : while (hasNext())
                                {
                                    npeek = mChars[++index];
                                    lengthSymbol ++;
        
                                    if(npeek == TAG.EOL)
                                    {
            
                                        if(lengthSymbol > 1)mModules.add(new Module(TAG.ZHUSHI_BLOCK, lengthSymbol - 1));
                                        mModules.add(new Module(TAG.COMMON, 1));
                                        line++;
                                        lengthSymbol = 0;
                                    } else
                                    if(npeek == TAG.EOF)
                                    {
                                        if(lengthSymbol > 1)
                                            mModules.add(new Module(TAG.ZHUSHI_BLOCK, lengthSymbol - 1));
                                        break cyc;
                                    } else
                                    if(npeek == '*')
                                    {
                                        if(next() == '/')
                                        {
                                            hasEnd = true;
                                            mModules.add(new Module(TAG.ZHUSHI_BLOCK, lengthSymbol + 1));
                                            lengthSymbol = 0;
                                            break cyc;
                                        } else
                                        {
                                            last();
                                        }
            
                                    }
        
        
                                }
                            }
                        }
                        
                        
    
    
    
                        if(hasEnd)
                        {
                            log.append( "\n 错误： 不能发现完整注释结尾！");
                        }
                    }
                    else {
                        // 为了处理多个符号而优化, 这里不直接调用 addSymbol()。
                        symbolLength++;
                        addSymbol();
                        last();
                    }
                    
                    break;
                    
                case '"':
                    
                    addSymbol();
                    
                    hasEnd = true;
                    
                    lengthSymbol = 1;
                    
                    cyc : while (hasNext())
                    {
                        npeek = mChars[++index];
                        
                        // 暂时不涉及。
                        //if (npeek == '\\')
                        //{
                        //    if(next() == )
                        //} else
    
                        lengthSymbol++;
                        
                        if(npeek == TAG.EOL)
                        {
                            mModules.add(new Module(TAG.STRING, lengthSymbol - 1));
                            mModules.add(new Module(TAG.COMMON, 1));
                            
                            line++;
                            lengthSymbol = 0;
                            break cyc;
                        }
                        else if(npeek == TAG.EOF)
                        {
                            if(lengthSymbol > 1)mModules.add(new Module(TAG.STRING, lengthSymbol - 1));
                            break cyc;
                        }
                        else if(npeek == '"')
                        {
                            hasEnd = true;
                            mModules.add(new Module(TAG.STRING, lengthSymbol));
                            break  cyc;
                        }
                        
                        
                    }
                    
                    
                    if(hasEnd)
                    {
                        log.append("\n错误：没有发现 \" 的结尾符");
                        log.append("\n(Error : can not find END-STRING before EOL.)");
                    }
                    break;
                case '\'':
                    
                    addSymbol();
                    
                    hasEnd = true;
                    
                    lengthSymbol = 1;
                    
                    cyc : while (hasNext())
                    {
                        npeek = mChars[++index];
    
                        // 暂时不涉及。
                        //if (npeek == '\\')
                        //{
                        //    if(next() == )
                        //} else
                        
                        lengthSymbol++;
                        
                        if(npeek == TAG.EOL)
                        {
                            mModules.add(new Module(TAG.STRING, lengthSymbol - 1));
                            mModules.add(new Module(TAG.COMMON, 1));
                            line++;
                            lengthSymbol = 0;
                            break cyc;
                        }
                        else if(npeek == TAG.EOF)
                        {
                            if(lengthSymbol > 1)mModules.add(new Module(TAG.STRING, lengthSymbol - 1));
                            break cyc;
                        }
                        else if(npeek == '\'')
                        {
                            hasEnd = true;
                            mModules.add(new Module(TAG.STRING, lengthSymbol));
                            break  cyc;
                        }
    
                        
                    }
            
                    
                    if(hasEnd)
                    {
                        log.append("\n错误：没有发现 ' 的结尾符");
                        log.append("\n(Error : can not find END-CHAR before EOL.)");
                    }
                    break;
                case '~':
                case '!':
                case '&':
                case '%':
                case '^':
                case '(':
                case ')':
                case '-':
                case '+':
                case '{':
                case '}':
                case '[':
                case ']':
                case ';':
                case ':':
                case '\\':
                case '|':
                case '<':
                case '>':
                case ',':
                case '.':
                case '?':
                case '=':
                //case TAG.EOF:
                case '\t':
                case ' ':
                    symbolLength++;
                    break;
                case TAG.EOL:
                    
                    addSymbol();
                    
                    mModules.add(new Module(TAG.COMMON, 1));
                    
                    line++;
                    break;
                case '*':
                    if(next() == '/')
                    {
                        
                        addSymbol();
                        boolean cache = true;
                        int inMode = TAG.UNKNOW;
                        
                        indexSign = mModules.size();
                        Module module;
    
//                        if(useDocAnnotation)
//                        {
//                            int ci;
//
//                            annotation :
//                            for(ci = index; ci > 0; ci--)
//                            {
//                                if(mChars[ci] == '*')
//                                {
//                                    if(ci > 1 && mChars[ci - 1] == '/')
//                                    {
//                                        if(ci < index && mChars[ci + 1] == '*')
//                                        {
//                                            inMode = TAG.ZHUSHI_DOC;
//                                        } else
//                                        {
//                                            inMode = TAG.ZHUSHI_BLOCK;
//                                        }
//                                        break annotation;
//                                    }
//                                }
//                            }
//
//                            if(inMode == TAG.UNKNOW)break;
//
//                            zhushi :
//                            for(int i = indexSign; i > 0; )
//                            {
//                                module = mModules.get(--i);
//
//                                if(module.getFirst() != TAG.COMMON)
//                                {
//                                    if(module.getFirst() == TAG.ZHUSHI_BLOCK
//                                    || module.getFirst() == TAG.ZHUSHI_DOC)
//                                    {
//                                        break zhushi;
//                                    }
//
//                                    if(inMode == TAG.ZHUSHI_DOC)
//                                    {
//                                        cache = false;
//                                        i++;
//
//                                        while (i < indexSign)
//                                        {
//                                            module = mModules.get(i);
//
//                                            if(module.getFirst() != TAG.COMMON)
//                                            {
//                                                module.setFirst(TAG.ZHUSHI_DOC);
//                                                mModules.set(i, module);
//                                            }
//                                            i++;
//                                        }
//
//                                        mModules.add(new Module(TAG.ZHUSHI_DOC, 2));
//
//                                        break zhushi;
//                                    } else
//                                    {
//
//                                    }
//                                    module.setFirst(TAG.ZHUSHI_BLOCK);
//                                    mModules.set(i, module);
//                                }
//                            }
//
//                            if(cache)
//                            {
//                                mModules.add(new Module(TAG.ZHUSHI_BLOCK, 2));
//                            }
//
//
//
//                        }
//                        else
                        {
                            zhushi :
                            for(int i = indexSign; i > 0; )
                            {
                                module = mModules.get(--i);
        
                                if(module.getFirst() != TAG.COMMON)
                                {
                                    if(module.getFirst() == TAG.ZHUSHI_BLOCK)
                                    {
                                        break zhushi;
                                    }
                                    else
                                    if(module.getFirst() == TAG.ZHUSHI_DOC)
                                    {
                                        cache = false;
                                        i++;
                
                                        while (i < indexSign)
                                        {
                                            module = mModules.get(i);
                    
                                            if(module.getFirst() != TAG.COMMON)
                                            {
                                                module.setFirst(TAG.ZHUSHI_DOC);
                                                mModules.set(i, module);
                                            }
                                            i++;
                                        }
                
                                        mModules.add(new Module(TAG.ZHUSHI_DOC, 2));
                
                                        break zhushi;
                                    }
                                    module.setFirst(TAG.ZHUSHI_BLOCK);
                                    mModules.set(i, module);
                                }
                            }
    
                            if(cache)
                            {
                                mModules.add(new Module(TAG.ZHUSHI_BLOCK, 2));
                            }
    
                        }
                        
                        
                        
                        
                        
                    } else
                    {
                        symbolLength++;
                        addSymbol();
                        last();
                    }
                    break;
                case TAG.EOF:
                    addSymbol();
                    break main;
                default:
                    
                    addSymbol();
                    
                    // 处理数字。
                    
                    if(Character.isDigit(peek))
                    {
                        indexSign = index - 1;
                        cyc : while (hasNext())
                        {
                            if(!isDigit(mChars[++index]))
                            {
                                index--;
                                break cyc;
                            }
                        }
                        
                        //System.out.println("数字长度="+ (index - indexSign));
                        mModules.add(new Module(TAG.INTENGER, index - indexSign));
                
                    }
                    else
                        if(Character.isJavaIdentifierPart(peek))
                    {
                        
                        text.setLength(0);
                        text.append(peek);
                        
                        cyc : while (hasNext())
                        {
                            npeek = mChars[++index];
                            text.append(npeek);
                    
                            if(!Character.isJavaIdentifierPart(npeek))
                            {
                                text.deleteCharAt(text.length() - 1);
                                index--;
                                break cyc;
                            }
                        }
                        // 扫描文本并记录
                        scanText(text);
                
                        //System.out.println("String长度="+ text.length()+"|"+text.toString());
                    }
                    else {
                        mModules.add(new Module(TAG.UNKNOW, 1));
                    }
            }
    
        }
    }
    
    public JavaScript()
    {
        text = new StringBuffer();
        log = new StringBuffer();
        log.append(new Date());
        line = 1;
        mModules = new ArrayList<>();
    
        TAG.ZHUSHI_DOC = TAG.ZHUSHI_BLOCK;
    }
    
    @Override
    public void set(String str, int[] lineStart, int length)
    {
        set(str.toCharArray(), lineStart, length);
    }
    
    @Override
    public void set(char[] chars, int[] lineStart, int length)
    {
        this.length = length;
        mChars = chars;
        mRowStartCount = lineStart;
        mModulesRowsStart = new int[chars.length + 2];
    }
    
    public ArrayList<Module> get()
    {
        return mModules;
    }
    
    /**
     * 查找行开头处于的位置。
     * @param line 要找到的行位置
     * @param module 当前所在Module位置，必须在行位置之前
     * @return
     */
    public Module find(int line, Module module)
    {
        if(line == 1)return mModules.get(0);
        
        int start = line >= mRowStartCount.length? length : mRowStartCount[line];
        
        int counts = module.position;
        
        int np = module.modulePosition;
        
        if(start >= counts)
        {
            while (np + 1 < mModules.size())
            {
                if(counts > start)break;
        
                module = mModules.get(++np);
                counts = module.position;
            }
        } else
        {
            while (np > 0)
            {
                if(counts <= start)
                {
                    module = mModules.get(np+ 1);
                    break;
                }
                
                module = mModules.get(--np);
                counts = module.position;
                
                
            }
        }
        
        
        return module;
    }
    
    /**
     * 给出一个绝对位置，返回所在 Module;
     * @param position 绝对位置
     * @return
     */
    public Module findPosition(int position, Module module)
    {
       
        int counts = module.position;
        int np = module.modulePosition;
        
        while (np + 1 < mModules.size())
        {
        
            if(counts >= position)break;
        
            module = mModules.get(++np);
            counts = module.position;
        }
        module.modulePosition = np;
        
        return module;
    }
    
    /**
     * 查找并返回缓冲内已有的编译模块。
     * @param lineCount 行
     * @return 成功返回所在行开头位置模块，否则进行查找.
     */
    public Module findInCache(int lineCount, Module module)
    {
        if(lineCount <= line)
        {
            if(mModulesRowsStart[lineCount] < mModules.size())
                return mModules.get(mModulesRowsStart[lineCount]);
            else
                return find(line, module);
        }
        
        return null;
        
    }
    
    /**
     * 是否启用DOC注释高亮，默认关闭，对于大文本存在效率过低问题，故启用可能存在BUG。
     * @param flag
     */
    public void setUseDocAnnotation(boolean flag)
    {
        useDocAnnotation = flag;
        if(flag)
        {
            TAG.ZHUSHI_DOC = TAG.ZHUSHI_BLOCK;
            log.append("效率可能会减低\n");
        }
        else
            TAG.ZHUSHI_DOC += 1;
        
    }
    
    boolean hasNext() {return index +1 < length;}
    
    char next() {
        if(hasNext())
        {
            return mChars[++index];
        }
        return TAG.EOF;
    }
    
    char last() {
        if(index > 0)
        {
            return mChars[--index];
        }
        return TAG.EOF;
    }
    
    boolean nextLine() {
        if(line < lineEnd)
        {
            index = mRowStartCount[++line];
            return true;
        } else
        {
            index = length;
            return false;
        }
    }

    
    /**
     * 用于添加符号做优化处理。
     */
    void addSymbol()
    {
        if(symbolLength > 0)
        {
            //System.out.println("添加符号数="+symbolLength);
            mModules.add(new Module(TAG.SYMBOL, symbolLength));
            symbolLength = 0;
        }
    }
    
    boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'f')
            || (c >= 'A' && c <= 'F')
            ||  c == 'x' || c == 'X';
    }
    
    /**
     * 翻译一段文本。
     * @param sb
     */
    private void scanText(StringBuffer sb)
    {
        Module module = null;
        String string;
        
        char c;
        switch (sb.length())
        {
            case 2:
                c = sb.charAt(1);
                switch (sb.charAt(0))
                {
                    case 'd':
                        if(c == 'o')
                        {
                            module = new Module(TAG.KEYWORD, 2);
                        }
                        break;
                    case 'i':
                        if(c == 'f')
                        {
                            module = new Module(TAG.KEYWORD, 2);
                        } else if(c == 'n')
                        {
                            module = new Module(TAG.KEYWORD, 2);
                        }
                        break;
                    
                }
                break;
            case 3:
                switch (sb.charAt(0))
                {
                    case 'f':
                        if(sb.indexOf("for") == 0) {
                            module = new Module(TAG.KEYWORD, 3);
                        }
                        break;
                    case 'v':
                        if(sb.indexOf("var") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 3);
                        }
                        break;
                    case 'n':
                        if(sb.indexOf("new") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 3);
                        }
                        break;
                    case 't':
                        if(sb.indexOf("try") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 3);
                        }
                        break;
                    case 'l':
                        if(sb.indexOf("let") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 3);
                        }
                        break;
                    case  'N':
                        if(sb.indexOf("NaN") == 0)
                        {
                            module = new Module(TAG.CONSTANT, 3);
                        }
                        break;
                }
                break;
            case 4:
                switch (sb.charAt(0))
                {
                    case 'b':
                        if(sb.indexOf("break") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        }
                        break;                    case 'c':
                    if(sb.indexOf("case") == 0)
                    {
                        module = new Module(TAG.KEYWORD, 4);
                    }
                    break;
                    case 'e':
                        string = sb.toString();
                        if(string .equals( "else" ))
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        } /*else if(string == "enum")
                        {
                            mModules
                        }*/
                        break;
                    case 't':
                        string = sb.toString();
                        if(string.equals("this"))
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        }else if(string.equals("true"))
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        }
                        break;
                    case 'v':
                        if(sb.indexOf("void") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        }
                        break;
                    case 'n':
                        if(sb.indexOf("null") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 4);
                        }
                        break;
                }
                break;
            case 5:
                switch (sb.charAt(0))
                {
                    case 'b':
                        if(sb.indexOf("break") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                    case 'c':
                        string = sb.toString();
                        if(string == "catch")
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }else if(string == "const")
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                    case 'f':
                        if (sb.indexOf("false") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                    case 't':
                        if(sb.indexOf("throw") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                    case 'w':
                        if(sb.indexOf("while") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                    case 'y':
                        if(sb.indexOf("yield") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 5);
                        }
                        break;
                }
                break;
            case 6:
                switch (sb.charAt(0))
                {
                    case 'd':
                        if(sb.indexOf("delete") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 6);
                        }
                        break;
                    case 'r':
                        if(sb.indexOf("return") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 6);
                        }
                        break;
                    case 't':
                        if(sb.indexOf("typeof") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 6);
                        }
                        break;
                    case 's':
                        if(sb.indexOf("switch") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 6);
                        }
                        break;
                }
                break;
            case 7:
                switch (sb.charAt(0))
                {
                    case 'f':
                        if(sb.indexOf("finally") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 7);
                        }
                        break;
                    case 'd':
                        if(sb.indexOf("default") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 7);
                        }
                        break;
                }
                break;
            case 8:
                switch (sb.charAt(0))
                {
                    case 'f':
                        if(sb.indexOf("function") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 8);
                        }
                        break;
                    case 'c':
                        if(sb.indexOf("continue") == 0)
                        {
                            module = new Module(TAG.KEYWORD, 8);
                        }
                        break;
                    case 'I':
                        if(sb.indexOf("Infinity") == 0)
                        {
                            module = new Module(TAG.CONSTANT, 8);
                        }
                        break;
                }
                break;
        }
        
        if(module == null)
        {
            // System.out.println("error = "+sb);
            module = new Module(TAG.UNKNOW, sb.length());
        }
        
        mModules.add(module);
    }
    
    
    public class Module
    {
        private int first;
        private int second;
        public int position = 0;
        public int modulePosition;
        public int lineIn = 1;
        
        public Module(int x, int y)
        {
            first = x;
            second = y;
            
            position = y - 1;
            
            if((modulePosition = mModules.size()) > 0)
            {
                position = y + mModules.get(modulePosition - 1).position;
            }
            
            lineIn = line;
            
            if(x == TAG.COMMON)
            {
                mModulesRowsStart[line + 1] = modulePosition + 1;
            }
        }
        
        
        public final int getFirst()
        {

            return first;
        }
        
        public final int getSecond()
        {
            return second;
        }
        
        public final void setFirst(int value)
        {
            first = value;
        }
        
        public final void setSecond(int value)
        {
            second = value;
        }
        
        public String toString()
        {
            return "[type = " + first + ", " +
                   "length = " + second+ ", " +
                   "real position = "+ position+", " +
                   "module position = "+modulePosition+", " +
                   "line = "+lineIn+"]";
        }
    }
    
}
