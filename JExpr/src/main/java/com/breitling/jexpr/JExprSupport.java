package com.breitling.jexpr;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breitling.jexpr.JExpr.AppendNode;
import com.breitling.jexpr.JExpr.ExprNode;
import com.breitling.jexpr.JExpr.JExprNode;


public class JExprSupport
{
    private String   internalResult;
    
//  CONSTRUCTOR
    
    private JExprSupport()
    {
        internalResult = "";
    }
    
//  PUBLIC METHODS
    
    public String toString()
    {
        return internalResult;
    }
    
//  STATIC METHODS
    
    public static String getString(Object o)
    {
        return o.toString();
    }
    
    public static String NOP()
    {
        return "";
    }
    
    public static void Sleep(int millis)
    {
        try
        {
            java.lang.Thread.sleep((long) millis);
        }
        catch (InterruptedException ie)
        {
        }
    }
    
    public static String CharAt(String value, int index)
    {
        if (value.length() > index)
            return new Character(value.charAt(index)).toString();
        else
            return "";
    }
    
    public static String EitherOr(Boolean conditional, String either, String or)
    {
        if (conditional)
            return either;
        else
            return or;
    }
    
    public static String EitherOr(boolean conditional, String either, String or)
    {
        if (conditional)
            return either;
        else
            return or;
    }
        
    public static String Substring(String value, int begin)
    {
        if (value.length() > begin)
            return value.substring(begin);
        else
            return "";
    }
    
    public static String Substring(String value, int begin, int end)
    {
        if (value.length() >= end)
            return value.substring(begin, end);
        else
            return "";
    }
    
    public static String Split(String value, String RE, int index)
    {
        String [] fields = value.split(RE);
        
        return fields[index];
    }
    
    public static String Split(String value, String RE, String expr)
    {
        String [] fields = value.split(RE);        
        char []   exprChars = expr.toCharArray();
        
        StringBuffer sb = new StringBuffer();
                    
        for (int n = 0; n < exprChars.length; n++)
        {
            if (exprChars[n] == '#')
            {
                int g = (int)(exprChars[++n] - '0');
                
                sb.append(fields[g-1]);
            }
            else
            {
                sb.append(exprChars[n]);
            }
        }
        
        return sb.toString();
    }
    
    public static String Group(String value, String RE, String expr)
    {
        return Match(value, RE, expr);
    }
    
    public static String Match(String value, String RE, String expr)
    {
     // *************************************************************************
     // ** BACKSLASH EXPANSION HACK! DOES THIS REALLY WORK? AND IS IT NECESSARY *
     // *************************************************************************
        if (RE.contains("\\\\"))
            RE = RE.replace("\\\\","\\");
     // *************************************************************************
        Pattern p = Pattern.compile(RE);
        Matcher m = p.matcher(value);
        
        if (m.matches())
        {
            char [] exprChars = expr.toCharArray();
            StringBuffer sb = new StringBuffer();
                        
            for (int n = 0; n < exprChars.length; n++)
            {
                if (exprChars[n] == '#')
                {
                    int g = (int)(exprChars[++n] - '0');
                    
                    sb.append(m.group(g));
                }
                else
                {
                    sb.append(exprChars[n]);
                }
            }
            
            return sb.toString();
        }
        
        return "";
    }
    
//  PROTECTED METHOD JUST FOR XCODES USE
    
    protected static void printAllMethods(JExpr that, ExprNode e)
    {
        try
        {
            Class<?> klass = that.getClassName(e);
            Method [] methods = klass.getDeclaredMethods();
            
            for (Method m : methods)
            {
                Class<?> [] types = m.getParameterTypes();
                StringBuffer sb = new StringBuffer();
              
                for (int n = 0; n < types.length; n++)
                    sb.append(types[n].getName()).append(" ");
                
                System.out.println("m = " + m.getName() + "(" + sb.toString() + ")");
            }
        }
        catch (Exception x)
        {
        }
    }
    
    protected static String printAppendNode(AppendNode node, int level)
    {
        StringBuffer sb = new StringBuffer();
        int len = node.nodes.size();
        
        for (JExprNode n : node.nodes)
        {
            if (n instanceof ExprNode)
                sb.append(printExprNode((ExprNode) n, 0));
            else
                sb.append(n.value.toString()).append("\n");
            
            if (--len > 0)
                sb.append("+\n");
        }
        
        return sb.toString();
    }
    
    protected static String printExprNode(ExprNode node, int level)
    {
        int  loopCnt = 0;
        StringBuffer sb = new StringBuffer();
        
        do
        {
            if (loopCnt > 0)
                sb.append(indent(level));
            
            sb.append(node.klass).append(".").append(node.method).append("\n");
               
            for (JExprNode n : node.nodes)
            {
                if (n instanceof ExprNode)
                    sb.append(indent(level)).append("    arg = ").append(printExprNode((ExprNode) n, level+1));
                else
                    sb.append(indent(level)).append("    arg = ").append(n.value).append("\n");
            }
            
            if (node.nextExprNode != null)
                sb.append(indent(level)).append(".\n");
            
            loopCnt++;
        }
        while ((node = node.nextExprNode) != null);
        
        return sb.toString();
    }

    private static String indent(int level)
    {
        StringBuffer sb = new StringBuffer();
        
        for (int n = 0; n < level; n++)
            sb.append("          ");
        
        return sb.toString();
    }
}
