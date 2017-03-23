package com.breitling.jexpr.test.standalone;

import com.breitling.jexpr.JExpr;

public abstract class JExprBaseTester
{
    public JExprBaseTester()
    {
    }
    
    public static void assertion(JExpr expression, String a, String b) throws Exception
    {
        String r = expression.execute(a);
        
        if (! r.equals(b))
            throw new Exception("Assertion failed: " + r + " != " + b);
        
        System.out.println(r);
    }
    
    public static void assertionNoPrint(JExpr expression, String a, String b) throws Exception
    {
        String r = expression.execute(a);
        
        if (! r.equals(b))
            throw new Exception("Assertion failed: " + r + " != " + b);
    }
    
    public static void notAssertion(JExpr expression, String a, String b) throws Exception
    {
        String r = expression.execute(a);
        
        if (r.equals(b))
            throw new Exception("Not Assertion failed: " + r + " = " + b);
        
        System.out.println(r);
    }
    
    public static void assertionString(String a, String b) throws Exception
    {
        if (! a.equals(b))
            throw new Exception("Assertion failed: " + a + " != " + b);
        
        System.out.println(a);
    }
    
    public static void assertionException(JExpr expression, String a) throws Exception
    {
        boolean threwException = false;
        
        try 
        {
            expression.execute(a);
        }
        catch (Exception e)
        {
            System.out.println("Threw: " + e.toString());
            threwException = true;
        }
        
        if (!threwException)
            throw new Exception("Assertion Exception failed: " + a + " did not throw an exception");
    }
}
