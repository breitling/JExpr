package com.breitling.jexpr.booleaneengine;

import com.breitling.jexpr.JExprException;

public class JEBE 
{
    private static final JExprBooleanEngine engine = JExprBooleanEngine.create();
    
    public static Boolean evaluate(final String expression) throws JExprException
    {
        return engine.evaluate(expression);
    }
    
    public static Boolean evaluate(final Object object, final String expression) throws JExprException
    {
        engine.setObject(object);        
        return engine.evaluate(expression);
    }
    
    public static void setObject(final Object object)
    {
        engine.setObject(object);
    }
}
