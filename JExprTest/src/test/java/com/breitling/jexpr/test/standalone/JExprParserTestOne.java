package com.breitling.jexpr.test.standalone;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;


public class JExprParserTestOne
{
    public static void main(String[] args) throws InterruptedException
    {
        String  expr = "{xm.divide(xm.doubleValue(this.area),xm.intValue(this.regionScale))}";
        
        try
        {
            JExpr expression = JExpr.create(expr);
            
            System.out.print(expr);
                     
            expression.setDateFormat("MM/dd/yyyy hh:mm:ssZ");
            
            String now = expression.execute("{now}");

            Thread.sleep(5000);
            
            System.out.println("Then = " + now + " Now = " + expression.execute("{now}"));
            System.out.println("Then = " + now + " Now = " + expression.execute("{now}"));
            
            Thread.sleep(1000);
            
            System.out.println("Then = " + now + " Now = " + expression.execute("{now}"));
        }
        catch (JExprException xce)
        {
            System.out.println(xce.toString());
        }
    }
}
