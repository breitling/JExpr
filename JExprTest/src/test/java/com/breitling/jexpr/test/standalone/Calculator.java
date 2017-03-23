package com.breitling.jexpr.test.standalone;

public class Calculator
{
    public Calculator()
    {
    }
    
    static public int add(int a, int b)
    {
        return a + b;
    }
    
    static public double add( double a, double b ) 
    {
        return a + b;
    }

    
    static public int subtract(int a, int b)
    {
        return a - b;
    }
    
    static public double subtract( double a, double b )
    {
        return a - b;
    }

    
    static public int multiply(int a , int b)
    {
        return a * b;
    }
    
    static public double multiply( double a, double b )
    {
        return a * b;
    }

    
    static public int divide(int a, int b)
    {
        return (int)(a / b);
    }
    
    static public double divide( double a, double b )
    {
        return a / b;
    }

    
    static public double add( double a, double b, double c )
    {
        return a + b + c;
    }
}
