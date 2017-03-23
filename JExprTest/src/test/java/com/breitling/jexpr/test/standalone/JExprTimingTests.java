package com.breitling.jexpr.test.standalone;

import java.util.HashMap;


public class JExprTimingTests extends JExprBaseTester
{
    public static void main(String[] args)
    {
//        JExprTimingTests tt = new JExprTimingTests();
//        
//        long    startTime;
//        long    estimatedTime;
//        
//        try
//        {
//        //  TEST 1
//            JExpr   x1;
//
//            startTime = System.nanoTime();
//            x1 = new JExpr(); 
//            estimatedTime = System.nanoTime() - startTime;
//            
//            System.out.println("First instantiation took " + ((double)(estimatedTime/1000000.0)) + " msecs.");
//            
//        //  TEST 1A
//            startTime = System.nanoTime();
//            JExpr x2 = new JExpr();
//            JExpr x3 = new JExpr();
//            estimatedTime = System.nanoTime() - startTime;
//            
//            System.out.println("2 instantiations took " + ((double)(estimatedTime/1000000.0)) + " msecs.");
//            
//        //  TEST 2
//            JExpr xcode = new JExpr(tt.new TestObject());
//            
//            startTime = System.nanoTime();
//            String now = xcode.execute("{now+':'+this.description}");
//            estimatedTime = System.nanoTime() - startTime;
//            
//            System.out.println("1 execute took " + ((double)(estimatedTime/1000000.0)) + " msecs.");
//            
//        //  TEST 3
//            
//            JExpr [] engines = new JExpr [1000];
//            TestObject to = tt.new TestObject();
//            
//            startTime = System.nanoTime();
//            for (int n = 0; n < 1000; n++)
//            {
//                engines[n] = new JExpr(to);
//                assertionNoPrint(engines[n], "{this.x}", "2");
//            }
//            estimatedTime = System.nanoTime() - startTime;
//            
//            System.out.println("1000 instantiations and executes took " + ((double)(estimatedTime/1000000.0)) + " msecs.");           
//        }
//        catch (JExprException xce)
//        {
//            System.out.println("Error: " + xce.toString());
//        }
//        catch (Exception e)
//        {
//            System.out.println("Error: assertion failed!");
//            e.printStackTrace();
//        }
    }
    
    public class TestObject
    {
        private String  x;
        private String  y;
        private HashMap<String,String> metadata = new HashMap<String,String>();
        
        public TestObject()
        {
            x = "2";
            y = "8";
            
            metadata.put("description", "this is a level instance test object.");
        }
        
        public String getX() { return x; }
        public String getY() { return y; }
        
        public TestObject getParent()
        {
            return this;
        }
        
        public String getDescription()
        {
            return this.metadata.get("description");
        }
    }
}
