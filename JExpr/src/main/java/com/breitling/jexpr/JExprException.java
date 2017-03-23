package com.breitling.jexpr;

public class JExprException extends Exception
{
    private static final long serialVersionUID = 895114122756604774L;

    
    public JExprException(String message)
    {
        super(message);
    }
    
    public JExprException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public JExprException(Throwable cause) 
    {
        super(cause);
    }
}
