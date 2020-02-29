package com.frms.lexer;

import com.frms.lexer.language.JavaScript;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 测试类。
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/23 22:01(ydt)
 */
public class Test {
    public static void main(String[] args)
    {
        
        StringBuffer sb = new StringBuffer("private void scanText(StringBuffer sb)\n" +
                                           "    {\n" +
                                           "        Module module = null;\n" +
                                           "        String string;\n" +
                                           "        \n" +
                                           "        char c;\n" +
                                           "        switch (sb.length())\n" +
                                           "        {\n");
        
        String[] text = new String[TAG.JAVA_KEYWORD.length] ;
        int index = 0;
        boolean w = true, w1 = false;
        for(int i =0; i < 13; i++)
        {
            w = true;
            w1 = false;
            
            for(int j = 0; j < text.length; j ++)
            {
                
                
                if(TAG.JAVA_KEYWORD[j].length() == i)
                {
                    if(w)
                    {
                        sb.append("            case " + i + " :\n" +
                                  "                switch (sb.charAt(0))\n" +
                                  "                {\n");
                        w = false;
                        w1 = true;
                    }
                    
                    sb.append("                    case '"+TAG.JAVA_KEYWORD[j].charAt(0)+"' :\n");
                    sb.append("                        if(sb.indexOf(\"" + TAG.JAVA_KEYWORD[j] + "\") == 0)\n" +
                              "                        {\n" +
                              "                            module = new Module(TAG.KEYWORD, "+i+");\n" +
                              "                        }\n" +
                              "                        break;\n");
                    
                }
                
                if(w1)
                {
                    w1 = false;
                    sb.append("                 }\n" +
                              "            break;\n");
                }
                
            }
        }
        
        sb.append("}\n" +
                  "        \n" +
                  "        if(module == null)\n" +
                  "        {\n" +
                  "            // System.out.println(\"error = \"+sb);\n" +
                  "            module = new Module(TAG.UNKNOW, sb.length());\n" +
                  "        }\n" +
                  "        \n"
                  + "        mModules.add(module);\n" +
                  "}");
        System.out.println(sb.toString());
    }
}
