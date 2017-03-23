package com.breitling.jexpr.test.standalone;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;
import com.breitling.jexpr.test.standalone.Calculator;

 
public class JExprTester extends JExprBaseTester
{
    public static void main(String[] args)
    {
        String  now;
        String  expr;
        String  timestamp;
        
        try
        {
            JExpr   expression = JExpr.create(); 

            assertion(expression, "{7}", "7");
            assertion(expression, "{'this is a test'}", "this is a test");
            
            expression.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
            
            assertion(expression, "{Calculator.add('1.0','1.0')}", "2.0");
            assertion(expression, "{Calculator.add(1.0,1.0)}", "2.0");
            assertion(expression, "{Calculator.add(\"1.0\",Calculator.multiply(\"2.0\",\"5.0\"))}", "11.0");
            assertion(expression, "{Calculator.add('1.0',Calculator.add('1.0',Calculator.add('1.0','1.0')))}", "4.0");
            
            assertionException(expression, "{Calculator.subtract('10.0',)}");
            assertionException(expression, "{Calculator.subtracts('10.0','8.0')}");
            assertionException(expression, "{Calculator.subtract('10.0','8.0'");
            
            expression = JExpr.create(new Calculator());
            
            expression.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
            
            assertion(expression, "{Calculator.add('1.0','3.0')}", "4.0");
            assertion(expression, "{Calculator.add(\"1.0\",Calculator.multiply(\"2.0\",\"5.0\"))}", "11.0");
            assertion(expression, "{this.add('1.0','1.0')}", "2.0");
            assertion(expression, "{this.add(1.0,1.0)}", "2.0");
            assertion(expression, "{this.add(1,1)}", "2");
            assertion(expression, "{this.divide(4,2)}", "2");
            assertion(expression, "{this.divide(5,2)}", "2");
            
            expression.setObject(new StringBuffer());
            
            System.out.println(" ");
            
            assertion(expression, "{this.append('this ').append('is ').append('a ').append('test.')}", "this is a test.");
            assertion(expression, "{this.append(' what is this?')}", "this is a test. what is this?");
            
            expression.setObject(new StringBuffer());
            
            expr = "{this.append('2.0 + 2.0 = ').append(Calculator.add('2.0','2.0')).append('.')}";
            
            System.out.println(" ");
            
            assertion(expression, expr, "2.0 + 2.0 = 4.0." );
            assertion(expression, "{'2.0 + 2.0 = '+Calculator.add('2.0',Calculator.add('1.0','1.0'))+'.'}", "2.0 + 2.0 = 4.0.");
            
            expression.registerClass("BigInteger", "java.math.BigInteger");
//          xcode.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");

            expression.setObject(new BigInteger("-123"));
            
            assertion(expression, "{BigInteger.abs()}", "123");
            
            now = expression.execute("{now}");
            
            assertion(expression, "{now}", now);
            assertion(expression, "{now+' is '+now}", now + " is " + now);
            assertion(expression, "{now+'-'+BigInteger.abs()}", now + "-123");
            
            assertionException(expression, "{now,now}");
            
            timestamp = expression.execute("{timestamp}");
            
            assertion(expression, "{timestamp}", timestamp);
            assertion(expression, "{timestamp+' is '+timestamp}", timestamp + " is " + timestamp);

            expression.registerClass("Math", "java.lang.Math");
            expression.setDateFormat("MM/dd/yyyy hh:mm:ssZ");

            now = expression.execute("{now}");
            
            assertion(expression, "{now}", now);
            assertion(expression, "{'PS='+Math.min('12','45')}", "PS=12");
            assertion(expression, "{'PS='+Math.min(12,45)}", "PS=12");
            
            expression.setObject(new StringBuffer());
            
            assertion(expression, "{'Y'+this.ensureCapacity('100')+'X'}", "YX");
            assertion(expression, "{..NOP()}", "");
            assertion(expression, "{..NOP(this.append('_this'),this.append('works'))+now+this}", now + "_thisworks");

            assertion(expression, "{..NOP('Art_Request.br_cat,Art_Request.br_product,Art_Request.br_size!PFGNum')}", "");
            assertion(expression, "{x.NOP('Art_Request.br_cat,Art_Request.br_product,Art_Request.br_size!PFGNum')}", "");

            List<String> list = new ArrayList<String>();

            expression.setObject(list);
            
            list.add("Test");
            list.add("Works");
            list.add(expression.execute("{now}"));
            
            expression.registerKeyWord("user", "alex");
            expression.registerKeyWord("today", "{now}");
            expression.registerKeyWord("expression", "{Calculator.add(\"1.0\",Calculator.multiply(\"2.0\",\"5.0\"))}");
            
            now = expression.execute("{now}");
            
            assertion(expression, "{user}", "alex");
            assertion(expression, "{today}", now);
            assertion(expression, "{expression}", "11.0");
            
        // TESTING
            
            JExprTester t = new JExprTester();
            
            expression.setObject(t.new Instance());
            
            assertionString(expression.execute("{this.id.substring('25')}"), "d0d00be");
            assertionString(expression.execute("{this.id.substring(25)+56}"), "d0d00be56");
            
        //  SUPPORT CLASS TESTING
            
            String test = "ThisIsATest";
            
            expression.setObject(test);
            
            assertionString(expression.execute("{x.EitherOr(this.contains('A'),'YES','NO')}"), "YES");
            assertionString(expression.execute("{x.EitherOr(this.contains('R'),'YES','NO')}"), "NO");
            
            assertionString(expression.execute("{x.CharAt(this,0)}"), "T");
            assertionString(expression.execute("{x.CharAt(this,1)}"), "h");
            assertionString(expression.execute("{x.CharAt(this,10)}"), "t");
            
            assertionString(expression.execute("{x.Substring(this,4)}"), "IsATest");
            assertionString(expression.execute("{x.Substring(this,4,7)}"), "IsA");
        }
        catch (JExprException xce)
        {
            System.out.println("Error: " + xce.toString());
        }
        catch (Exception e)
        {
            System.out.println("Error: assertion failed!");
            e.printStackTrace();
        }
    }

//  INTERNAL CLASS USED TO TEST
    
    public class Instance
    {
        public String    id;
        
        public Instance()
        {
            this.id = "2c988827175a33a901175b1d9d0d00be";
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}
