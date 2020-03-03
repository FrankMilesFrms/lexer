package com.frms.lexer;

import com.frms.lexer.language.Java;
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
    public static void main1(String[] args)
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
    
    private static String text = "function dp(ctx, dips){ return Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);} \n" +
                                 "\n" + "\n" + "ctx.runOnUiThread(new Runnable({ \n" +
                                 "run: function(){ \n" + "\n" + "// 仅能在CreateJS测试过，其他运行平台请自行测试！\n" +
                                 "ctx.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,\n" +
                                 "\t\t\t\t\t\t\t\t  android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);\t\n" +
                                 "\n" + "\n" + "var postion = [0, 0, 0, 0];\n" + "var pop;\n" +
                                 "var button = new Button(ctx);\n" + "button.setText(\"标题\");\n" +
                                 "button.setOnTouchListener(new android.view.View.OnTouchListener({onTouch:function (v, e) {\n" +
                                 "        switch (e.getAction()) {\n" + "          case 0:\n" +
                                 "            postion[0] = e.getRawX();\n" +
                                 "            postion[1] = e.getRawY();\n" +
                                 "            break;\n" + "          case 2:\n" +
                                 "            postion[2] += e.getRawX() - postion[0];\n" +
                                 "            postion[3] += e.getRawY() - postion[1];\n" +
                                 "            postion[0] = e.getRawX();\n" +
                                 "            postion[1] = e.getRawY();\n" +
                                 "            pop.update(postion[2], postion[3], -2 , -2);\n" +
                                 "            break;\n" + "        }\n" +
                                 "        return !true;\n" + "    }}));\n";
    private static int startLine = 1;
    private static int start = 0;
    private static int end = 0;
    private static int endLine ;
    private static int[] rowStart;
    
    public static void main(String[] args)
    {
        JavaScript javaScript = new JavaScript();
        set();
        
        javaScript.set(text, rowStart, text.length());
        javaScript.token(start, startLine, end, endLine);
        
        System.out.println(javaScript.get().toString());
    }
    
    
    public static void set()
    {
        if(text == null || text.length() == 0)return;
        
        end = text.length() -1;
        char[] chars = text.toCharArray();
        rowStart = new int[end + 1];
        int l = 1, i = 0;
        
        for(char c : chars)
        {
           i++;
            if(c == TAG.EOL)
            {
                rowStart[++l] =  i;
            }
        }
    }
}
