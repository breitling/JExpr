package com.breitling.jexpr.test.standalone;

import java.text.SimpleDateFormat;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;


public class JExprDateTester extends JExprBaseTester
{
    public static void main(String[] args)
    {
        JExpr  expression = JExpr.create(); 
        
        try
        {
            expression.setDateFormat(new SimpleDateFormat("MMMM"));
            
            System.out.println("Month = " + expression.execute("{now}"));
            
         // TEST THE XCODE DATE SUPPORT CLASS
            
            assertion(expression, "{xd.plusDays('2/1/09',2)}",  "02/03/09");
            assertion(expression, "{xd.plusDays('2/1/09',30)}", "03/03/09");
            
            assertion(expression, "{xd.plusDays('2/1/09','2')}",  "02/03/09");
            assertion(expression, "{xd.plusDays('2/1/09','30')}", "03/03/09");
            
            assertion(expression, "{xd.plusDays('2009/2/1','yyyy/MM/dd',2)}",  "2009/02/03");
            assertion(expression, "{xd.plusDays('2009/2/1','yyyy/MM/dd',30)}", "2009/03/03");
            
            assertion(expression, "{xd.plusDays('2009/2/1','yyyy/MM/dd','2')}",  "2009/02/03");
            assertion(expression, "{xd.plusDays('2009/2/1','yyyy/MM/dd','30')}", "2009/03/03");
            
            assertion(expression, "{xd.plusDays('1/1/09',365)}",  "01/01/10");
            assertion(expression, "{xd.plusDays('1/1/09',10000)}","05/19/36");
            
            assertion(expression, "{xd.plusDays('1/1/09','365')}",  "01/01/10");
            assertion(expression, "{xd.plusDays('1/1/09','10000')}","05/19/36");
            
            assertion(expression, "{xd.plusDays('2009/2/1 13:59:59','yyyy/MM/dd HH:mm:ss',2)}",  "2009/02/03 13:59:59");
            assertion(expression, "{xd.plusDays('2009/2/1 13:59','yyyy/MM/dd HH:mm',2)}",  "2009/02/03 13:59");
            assertion(expression, "{xd.plusDays('2009/2/1 13','yyyy/MM/dd HH','2')}",  "2009/02/03 13");

            assertion(expression, "{xd.plusHours('2009/2/1 13:59:59','yyyy/MM/dd HH:mm:ss',2)}",  "2009/02/01 15:59:59");
            assertion(expression, "{xd.plusHours('2009/2/1 13:59','yyyy/MM/dd HH:mm',20)}",  "2009/02/02 09:59");
            assertion(expression, "{xd.plusHours('2009/2/1 13','yyyy/MM/dd HH','48')}",  "2009/02/03 13");
            assertion(expression, "{xd.plusHours('2009/2/1 13','yyyy/MM/dd HH','50')}",  "2009/02/03 15");
            
            assertion(expression, "{xd.plusHours('2/1/09',2)}",  "02/01/09");
            assertion(expression, "{xd.plusHours('2/1/09',24)}", "02/02/09");
            
        //  TEST now FEATURE
            
            System.out.println(expression.execute("{xd.plusDays('now',2)}"));
            System.out.println(expression.execute("{xd.plusHours('now','yyyy/MM/dd HH:mm',2)}"));
            
        //  OTHER TESTS            
        }
        catch (JExprException xce)
        {
            System.out.println("Error: " + xce.toString());
            xce.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println("Error: assertion failed!");
            e.printStackTrace();
        }
    }
}
