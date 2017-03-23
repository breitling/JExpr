package com.breitling.jexpr;


import java.lang.reflect.Method;


public class JExprMathSupport
{
    private static JExprMathSupport instance = null;
    
//  CONSTRUCTOR
    
    private JExprMathSupport()
    {
    }
    
    public static JExprMathSupport getInstance()
    {
        if (instance == null)
            instance = new JExprMathSupport();
        
        return instance;
    }
    
//  PUBLIC METHODS

    public static String doubleValue(int a)           { return new Double(a).toString(); }
    public static String doubleValue(Object a)        { return narrowToDouble(a).toString(); }
    public static String doubleValue(String a)        { return narrowToDouble(a).toString(); }
    
    public static String intValue(double a)           { return new Integer((int) a).toString(); }
    public static String intValue(Object a)           { return narrowToInt(a).toString(); }
    public static String intValue(String a)           { return narrowToInt(a).toString(); }
    
//  ADDITION    
    public static int    add(int a, int b)            { return (a + b); }
    public static int    add(int a, double b)         { return (a + ((int) Math.round(b))); }
    public static double add(double a, double b)      { return (a + b); }
    public static double add(double a, int b)         { return (a + ((double) b)); }
    
    public static String add(int a, String b)         { return genericMethod("add", a, b); }
    public static String add(double a, String b)      { return genericMethod("add", a, b); }
    public static String add(String a, int b)         { return genericMethod("add", a, b); }
    public static String add(String a, double b)      { return genericMethod("add", a, b); }
    public static String add(String a, String b)      { return genericMethod("add", a, b); }
    
//  SUBTRACTION
    public static int    subtract(int a, int b)       { return (a - b); }
    public static int    subtract(int a, double b)    { return (a - ((int) Math.round(b))); }
    public static double subtract(double a, double b) { return (a - b); }
    public static double subtract(double a, int b)    { return (a - ((double) b)); }
    
    public static String subtract(int a, String b)    { return genericMethod("subtract", a, b); }
    public static String subtract(double a, String b) { return genericMethod("subtract", a, b); }
    public static String subtract(String a, int b)    { return genericMethod("subtract", a, b); }
    public static String subtract(String a, double b) { return genericMethod("subtract", a, b); }   
    public static String subtract(String a, String b) { return genericMethod("subtract", a, b); }
    
//  MULTIPLICATION
    public static int    multiply(int a, int b)       { return (a * b); }
    public static int    multiply(int a, double b)    { return ((int) (a * b)); }
    public static double multiply(double a, double b) { return (a * b); }
    public static double multiply(double a, int b)    { return (a * ((double) b)); }
    
    public static String multiply(int a, String b)    { return genericMethod("multiply", a, b); }
    public static String multiply(double a, String b) { return genericMethod("multiply", a, b); }
    public static String multiply(String a, int b)    { return genericMethod("multiply", a, b); }
    public static String multiply(String a, double b) { return genericMethod("multiply", a, b); }
    public static String multiply(String a, String b) { return genericMethod("multiply", a, b); }
    
//  DIVISION
    public static int    divide(int a, int b)         { return (a / b); }
    public static int    divide(int a, double b)      { return ((int) (a / b)); }
    public static double divide(double a, double b)   { return (a / b); }
    public static double divide(double a, int b)      { return (a / ((double) b)); }
    
    public static String divide(int a, String b)      { return genericMethod("divide", a, b); }
    public static String divide(double a, String b)   { return genericMethod("divide", a, b); }
    public static String divide(String a, int b)      { return genericMethod("divide", a, b); }
    public static String divide(String a, double b)   { return genericMethod("divide", a, b); }
    public static String divide(String a, String b)   { return genericMethod("divide", a, b); }
    
//  PRIVATE METHODS

    private static String genericMethod(String methodName, Object a, Object b)
    {
        Method m;
        Object oa = a;
        Object ob = b;
        
        if (a instanceof String)
            oa = narrow((String) a);
        
        if (b instanceof String)
            ob = narrow((String) b);
        
        String r = "0";
        
        try
        {
            if (oa instanceof Integer)
            {
                if (ob instanceof Integer)
                {
                    m = JExprMathSupport.class.getMethod(methodName, int.class, int.class);
                    r = (new Integer((Integer) m.invoke(JExprMathSupport.class, ((Integer) oa).intValue(), ((Integer) ob).intValue()))).toString();
                }
                else
                if (ob instanceof Double)
                {
                    m = JExprMathSupport.class.getMethod(methodName, int.class, double.class);
                    r = (new Integer((Integer) m.invoke(JExprMathSupport.class, ((Integer) oa).intValue(), ((Double) ob).doubleValue()))).toString();
                }
            }
            else
            if (oa instanceof Double)
            {
                if (ob instanceof Integer)
                {
                    m = JExprMathSupport.class.getMethod(methodName, double.class, int.class);
                    r = (new Double((Double) m.invoke(JExprMathSupport.class, ((Double) oa).doubleValue(), ((Integer) ob).intValue()))).toString();
                }
                else
                if (ob instanceof Double)
                {
                    m = JExprMathSupport.class.getMethod(methodName, double.class, double.class);
                    r = (new Double((Double) m.invoke(JExprMathSupport.class, ((Double) oa).doubleValue(), ((Double) ob).doubleValue()))).toString();
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("What?");
        }
        
        return r;
    }
    
    private static Object narrow(String argstring) 
    {
        try
        {
            return Integer.valueOf(argstring);
        } 
        catch( NumberFormatException nfe )
        {
        }
        
        try
        {
            return Double.valueOf( argstring );
        }
        catch( NumberFormatException nfe ) 
        {
        }
        
        if (argstring.equalsIgnoreCase("true")) 
        {
            return Boolean.TRUE;
        }
        else 
        if (argstring.equalsIgnoreCase("false"))
        {
            return Boolean.FALSE;
        }

        return argstring;
    }
    
    private static Double narrowToDouble(Object arg)
    {
        Object o = arg;
        
        if (arg instanceof String)
            o = narrow((String) arg);

        if (o instanceof Integer)
            return ((Integer) o).doubleValue();
        else
        if (o instanceof Double)
            return (Double) o;
        else
            return (Double) 0.0;
    }
    
    private static Integer narrowToInt(Object arg)
    {
        Object o = arg;
        
        if (arg instanceof String)
            o = narrow((String) arg);

        if (o instanceof Integer)
            return (Integer) o;
        else
        if (o instanceof Double)
            return ((Double) o).intValue();
        else
            return (Integer) 0;
    }
}
