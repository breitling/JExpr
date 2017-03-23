package com.breitling.jexpr.test.standalone;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;

public class JExprMathTester extends JExprBaseTester
{
    public static void main(String[] args)
    {
        JExpr  expression = JExpr.create(); 
        JExprMathTester  mt = new JExprMathTester();
        
        TestObject to = mt.new TestObject();
        
        try
        {
         // ADDITION TESTS
            
            assertion(expression, "{xm.add('1','8')}", "9");            
            assertion(expression, "{xm.add('1.0','8.0')}", "9.0");
            assertion(expression, "{xm.add('1','8.0')}", "9");
            assertion(expression, "{xm.add('1.0','8')}", "9.0");
            
            assertion(expression, "{xm.add('1','3.489')}", "4");       
            assertion(expression, "{xm.add('1','3.789')}", "5");       
            
        // SUBTRACTION TESTS
            
            assertion(expression, "{xm.subtract('4','2')}", "2");            
            assertion(expression, "{xm.subtract('4.0','2.0')}", "2.0");
            assertion(expression, "{xm.subtract('4','2.0')}", "2");
            assertion(expression, "{xm.subtract('4.0','2')}", "2.0");
            
            assertion(expression, "{xm.subtract('3','9')}", "-6");
            
        // MULTIPLICATION TESTS
            
            assertion(expression, "{xm.multiply('4','2')}", "8");            
            assertion(expression, "{xm.multiply('4.0','2.0')}", "8.0");
            assertion(expression, "{xm.multiply('4','2.0')}", "8");
            assertion(expression, "{xm.multiply('4.0','2')}", "8.0");

            expression.setObject(to);
            
            assertion(expression, "{xm.multiply(this.width,this.height)}", "24");
            assertion(expression, "{xm.multiply(this.parent.width,this.height)}", "24");
            assertion(expression, "{xm.multiply(this.parent.parent.parent.width,this.height)}", "24");
            
            assertion(expression, "{this.area}", "24");
            assertion(expression, "{xm.subtract(this.area,xm.multiply(this.width,this.height))}", "0");
            
        // DIVISION TESTS
            
            assertion(expression, "{xm.divide('4','2')}", "2");            
            assertion(expression, "{xm.divide('4.0','2.0')}", "2.0");
            assertion(expression, "{xm.divide('4','2.0')}", "2");
            assertion(expression, "{xm.divide('4.0','2')}", "2.0");
            
            assertion(expression, "{xm.divide('2','4')}", "0");
            assertion(expression, "{xm.divide('2','4.0')}", "0");
            assertion(expression, "{xm.divide('10','11')}", "0");
            
            assertion(expression, "{xm.divide('2.0','4.0')}", "0.5");
            assertion(expression, "{xm.divide('2.0','4')}", "0.5");
            assertion(expression, "{xm.divide('10.0','11.0')}", "0.9090909090909091");
            assertion(expression, "{xm.divide('10.0','11')}", "0.9090909090909091");
            
        // OTHER TESTS
            
            assertion(expression, "{xm.intValue('5.0')}", "5");
            assertion(expression, "{xm.doubleValue('5')}", "5.0");
            
            assertion(expression, "{xm.divide(xm.doubleValue(this.area),this.regionScale)}", "12.0");
            assertion(expression, "{xm.divide(xm.intValue(this.regionScale),'1')}", "2");
            assertion(expression, "{xm.multiply('12.0',xm.intValue(this.regionScale))}", "24.0");
            
//          assertion(xcode, "{}", "");
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
    
    public class TestObject
    {
        public String  area;
        public String  width;
        public String  height;
        public String  regionScale;
        
        
        public TestObject()
        {
            area = "24";
            width = "8";
            height = "3";
            regionScale = "2.0";
        }
        
        public TestObject getParent()
        {
            return this;
        }
        
        public String getArea() { return area; }
        public String getHeight() { return height; }
        public String getWidth() { return width; }
        public String getRegionScale() { return regionScale; }
    }
}
