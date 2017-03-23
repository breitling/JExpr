package com.breitling.jexpr.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;
import com.breitling.jexpr.JExprSupport;
import com.breitling.jexpr.test.standalone.Calculator;


public class TestJExpr 
{
//  INITIALIZATION and TEAR DOWN
    
    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }
    
    @After
    public void tearDown() throws Exception
    {
    }

//  TEST CASSES
    
    @Test
    public void testJExpr_CanInstantiate_NotNull()
    {
        JExpr jexpr = JExpr.create();
        
        assertNotNull(jexpr);
    }
    
    @Test
    public void testJExpr_IsValidExpression_True()
    {
        assertTrue(JExpr.containsJExpr("{now}"));
    }
    
    @Test
    public void  testJExpr_IsValidExpressionWithExtraSpaces_True()
    {
        assertTrue(JExpr.containsJExpr("   {this.parent.name}   "));
    }
    
    @Test
    public void testJExpr_BadExpressionMissingLeadingBracket_False()
    {
        assertFalse(JExpr.containsJExpr("this}"));
    }
    
    @Test
    public void testJExpr_BadExpressionMissingTrailingBracket_False()
    {
        assertFalse(JExpr.containsJExpr("{this"));
    }
    
    @Test
    public void testJExpr_BadExpressionCharsAtEnd_False()
    {
        assertFalse(JExpr.containsJExpr("{now}?"));
    }
    
    @Test
    public void textJExpr_BadExpressionCharsAtFront_False()
    {
        assertFalse(JExpr.containsJExpr("x{now}"));
    }
    
    @Test
    public void testJExpr_SetObject_ValidString() throws JExprException
    {
        JExpr jexpr = JExpr.create();
        
        assertNotNull(jexpr);
        
        jexpr.setObject(new StringBuffer());
        
        assertEquals(jexpr.execute("{this.append('this ').append('is ').append('a ').append('test.')}"), "this is a test.");
        assertEquals(jexpr.execute("{this.append(' was ist los?')}"), "this is a test. was ist los?");
        
        jexpr.setObject(new StringBuffer());
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        String expr = "{this.append('2.0 + 2.0 = ').append(Calculator.add('2.0','2.0')).append('.')}";
            
        assertEquals(jexpr.execute(expr), "2.0 + 2.0 = 4.0.");
    }
    
    @Test
    public void testJExpr_RegisterClass_ExpressionsEvaluate() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Calculator());
        
        assertNotNull(jexpr);
        
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        assertEquals(jexpr.execute("{Calculator.add('1.0','1.0')}"), "2.0");
        assertEquals(jexpr.execute("{Calculator.add(1.0,1.0)}"), "2.0");
        assertEquals(jexpr.execute("{Calculator.add(\"1.0\",Calculator.multiply(\"2.0\",\"5.0\"))}"), "11.0");
        assertEquals(jexpr.execute("{Calculator.add('1.0',Calculator.add('1.0',Calculator.add('1.0','1.0')))}"), "4.0");
        
        assertEquals(jexpr.execute("{Calculator.add('1.0','3.0')}"), "4.0");
        assertEquals(jexpr.execute("{Calculator.add(\"1.0\",Calculator.multiply(\"2.0\",\"5.0\"))}"), "11.0");
        
        assertEquals(jexpr.execute("{this.add('1.0','1.0')}"), "2.0");
        assertEquals(jexpr.execute("{this.add(1.0,1.0)}"), "2.0");
        assertEquals(jexpr.execute("{this.add(1,1)}"), "2");
        assertEquals(jexpr.execute("{this.divide(4,2)}"), "2");
        assertEquals(jexpr.execute("{this.divide(5,2)}"), "2");
    }
    
    @Test
    public void testJExpr_RegisterClassAgain_ThrowsException() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Calculator());
        
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        try
        {
            jexpr.registerClass("Calculator", "com.breitling.already.registered");
            fail("should not get here");
        }
        catch (JExprException e)
        {
            assertEquals("com.breitling.jexpr.JExprException: Calculator already used to register a class", e.toString());
        }
    }
    
    @Test
    public void testJExpr_UsingABooleanObject_ValidValues() throws JExprException
    {
        Boolean b = true;
        
        JExpr jexpr = JExpr.create(b);
        
        assertNotNull(jexpr);
        assertEquals(jexpr.execute("{this}"),"true");
        
        b = false;
        
        assertEquals(jexpr.execute("{this}"),"true");
        
        jexpr.setObject(b);
        
        assertEquals(jexpr.execute("{this}"),"false");
    }
    
    @Test
    public void testJExpr_BadExpressions_ThrowsException() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Calculator());
        
        assertNotNull(jexpr);
        
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        try 
        {
                jexpr.execute("{Calculator.subtract('10.0',)}");
                fail("should not get here!");
        }
        catch (JExprException e)
        {
                String msg = e.toString().substring(0, 102);
                assertEquals("com.breitling.jexpr.JExprException: Error with expression [{Calculator.subtract('10.0',)}] with object", msg);
        }
        
        try
        {        
                jexpr.execute("{Calculator.subtracts('10.0','8.0')}");
                fail("should not get here!");
        }
        catch (JExprException e)
        {
                String msg = e.toString().substring(0, 108);
                assertEquals("com.breitling.jexpr.JExprException: Error with expression [{Calculator.subtracts('10.0','8.0')}] with object", msg);
        }
        
        try
        {
                jexpr.execute("{Calculator.subtract('10.0','8.0'");
                fail("should not get here!");
        }
        catch (JExprException e)
        {
                String msg = e.toString().substring(0, 105);
                assertEquals("com.breitling.jexpr.JExprException: Error with expression [{Calculator.subtract('10.0','8.0'] with object", msg);
        }
    }
    
    @Test
    public void testJExpr_RegisterKeyWords_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Calculator());
        
        assertNotNull(jexpr);
        
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        jexpr.registerKeyWord("user", "alex");
        jexpr.registerKeyWord("today", "{now}");
        jexpr.registerKeyWord("expression", "{Calculator.add(1.0,Calculator.multiply(2.0,5.0))}");
        
        String now = jexpr.execute("{now}");
        
        assertEquals(jexpr.execute("{user}"), "alex");
        assertEquals(jexpr.execute("{today}"), now);
        assertEquals(jexpr.execute("{expression}"), "11.0");
    }
    
    @Test
    public void testJExpr_RegisterKeyWordsAndExecute_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Calculator());
        
        assertNotNull(jexpr);
        
        jexpr.registerKeyWord("TODAY", "{now}");
        jexpr.registerKeyWordAndExecute("today", "{now}");
        
        JExprSupport.Sleep(2000);
        
        String now = jexpr.execute("{now}");
        
        assertEquals(jexpr.execute("{TODAY}"), now);
        assertNotSame(jexpr.execute("{today}"), now);
    }
    
    @Test
    public void testJExpr_ExpressionArguments_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create(new TestObject());
        
        assertEquals(jexpr.execute("{this.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.id.substring(25)+56}"), "d0d00be56");
        assertEquals(jexpr.execute("{this.id.substring(25)+'56'}"), "d0d00be56");
        
        assertEquals(jexpr.execute("{this.parent.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.parent.parent.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.parent.parent.parent.id.substring('25')}"), "d0d00be");
        
        assertEquals(jexpr.execute("{this.getParent().id.substring('30')}"), "be");
    }
    
    @Test
    public void testJExpr_BadExpressionArguments_ThrowsException()
    {
        JExpr jexpr = JExpr.create(new TestObject());
        
        try
        {
                jexpr.execute("{this.id.substring('34')}");
                fail("should not get here!");
        }
        catch (JExprException e)
        {
                String msg = e.toString().substring(0,97);
                assertEquals("com.breitling.jexpr.JExprException: Error with expression [{this.id.substring('34')}] with object", msg);
        }
    }
    
    @Test
    public void testJExpr_ExpressionExecution_ValidValue() throws JExprException
    {
        JExpr jexpr = JExpr.create(new TestObject());
        
        assertEquals(jexpr.execute("{String.format('%02d',Integer.valueOf(this.page))}"), "01");
    }
    
    @Test
    public void testJExpr_JExprSupportLibrary_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create();
        
        assertNotNull(jexpr);
        assertEquals(jexpr.execute("{x.NOP()}"), "");
        
        String now = jexpr.execute("{now}");
        
        try
        {
                assertEquals(jexpr.execute("{..NOP(this.append('_this'),this.append('works'))+now+this}"), now + "_thisworks");
        }
        catch (JExprException e)
        {
                String msg = e.toString().substring(0, 131);
                assertEquals("com.breitling.jexpr.JExprException: Error with expression [{..NOP(this.append('_this'),this.append('works'))+now+this}] with object", msg);
//              assertEquals(e.toString(), "com.breitling.jexpr.JExprException: JExpr object never initialized so 'this.<method>' is not valid.");
        }
 
        jexpr.setObject(new StringBuilder());
        
        assertEquals(jexpr.execute("{..NOP(this.append('_this'),this.append('works'))+now+this}"), now + "_thisworks");
        assertEquals(jexpr.execute("{x.NOP(this.append('_this'),this.append('works'))+now+this}"), now + "_thisworks_thisworks");
    }
    
    @Test
    public void testJExpr_JExprDateSupportLibrary_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create();
        
        jexpr.setDateFormat(new SimpleDateFormat("MMMM"));
        
        assertEquals(jexpr.execute("{xd.plusDays('2/1/09',2)}"),  "02/03/09");
        assertEquals(jexpr.execute("{xd.plusDays('2/1/09',30)}"), "03/03/09");
        
        assertEquals(jexpr.execute("{xd.plusDays('2/1/09','2')}"),  "02/03/09");
        assertEquals(jexpr.execute("{xd.plusDays('2/1/09','30')}"), "03/03/09");
        
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1','yyyy/MM/dd',2)}"),  "2009/02/03");
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1','yyyy/MM/dd',30)}"), "2009/03/03");
        
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1','yyyy/MM/dd','2')}"),  "2009/02/03");
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1','yyyy/MM/dd','30')}"), "2009/03/03");
        
        assertEquals(jexpr.execute("{xd.plusDays('1/1/09',365)}"),  "01/01/10");
        assertEquals(jexpr.execute("{xd.plusDays('1/1/09',10000)}"),"05/19/36");
        
        assertEquals(jexpr.execute("{xd.plusDays('1/1/09','365')}"),  "01/01/10");
        assertEquals(jexpr.execute("{xd.plusDays('1/1/09','10000')}"),"05/19/36");
        
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/3','yyyy/MM/dd',xm.subtract(0,2))}"),  "2009/02/01");
        
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1 13:59:59','yyyy/MM/dd HH:mm:ss',2)}"),  "2009/02/03 13:59:59");
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1 13:59','yyyy/MM/dd HH:mm',2)}"),  "2009/02/03 13:59");
        assertEquals(jexpr.execute("{xd.plusDays('2009/2/1 13','yyyy/MM/dd HH','2')}"),  "2009/02/03 13");

        assertEquals(jexpr.execute("{xd.plusHours('2009/2/1 13:59:59','yyyy/MM/dd HH:mm:ss',2)}"),  "2009/02/01 15:59:59");
        assertEquals(jexpr.execute("{xd.plusHours('2009/2/1 13:59','yyyy/MM/dd HH:mm',20)}"),  "2009/02/02 09:59");
        assertEquals(jexpr.execute("{xd.plusHours('2009/2/1 13','yyyy/MM/dd HH','48')}"),  "2009/02/03 13");
        assertEquals(jexpr.execute("{xd.plusHours('2009/2/1 13','yyyy/MM/dd HH','50')}"),  "2009/02/03 15");
        
        assertEquals(jexpr.execute("{xd.plusHours('2/1/09',2)}"),  "02/01/09");
        assertEquals(jexpr.execute("{xd.plusHours('2/1/09',24)}"), "02/02/09");
    }
    
    @Test
    public void testJExpr_JExprMathSupportLibrary_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create();
        
     // ADDITION TESTS
        
        assertEquals(jexpr.execute("{xm.add('1','8')}"), "9");            
        assertEquals(jexpr.execute("{xm.add('1.0','8.0')}"), "9.0");
        assertEquals(jexpr.execute("{xm.add('1','8.0')}"), "9");
        assertEquals(jexpr.execute("{xm.add('1.0','8')}"), "9.0");
        
        assertEquals(jexpr.execute("{xm.add('1','3.489')}"), "4");       
        assertEquals(jexpr.execute("{xm.add('1','3.789')}"), "5");       
        
    // SUBTRACTION TESTS
        
        assertEquals(jexpr.execute("{xm.subtract('4','2')}"), "2");            
        assertEquals(jexpr.execute("{xm.subtract('4.0','2.0')}"), "2.0");
        assertEquals(jexpr.execute("{xm.subtract('4','2.0')}"), "2");
        assertEquals(jexpr.execute("{xm.subtract('4.0','2')}"), "2.0");
        
        assertEquals(jexpr.execute("{xm.subtract('3','9')}"), "-6");
        
    // MULTIPLICATION TESTS
        
        assertEquals(jexpr.execute("{xm.multiply('4','2')}"), "8");            
        assertEquals(jexpr.execute("{xm.multiply('4.0','2.0')}"), "8.0");
        assertEquals(jexpr.execute("{xm.multiply('4','2.0')}"), "8");
        assertEquals(jexpr.execute("{xm.multiply('4.0','2')}"), "8.0");

        jexpr.setObject(new TestObject());
        
        assertEquals(jexpr.execute("{xm.multiply(this.width,this.height)}"), "24");
        assertEquals(jexpr.execute("{xm.multiply(this.parent.width,this.height)}"), "24");
        assertEquals(jexpr.execute("{xm.multiply(this.parent.parent.parent.width,this.height)}"), "24");
        
        assertEquals(jexpr.execute("{this.area}"), "24");
        assertEquals(jexpr.execute("{xm.subtract(this.area,xm.multiply(this.width,this.height))}"), "0");
        
    // DIVISION TESTS
        
        assertEquals(jexpr.execute("{xm.divide('4','2')}"), "2");            
        assertEquals(jexpr.execute("{xm.divide('4.0','2.0')}"), "2.0");
        assertEquals(jexpr.execute("{xm.divide('4','2.0')}"), "2");
        assertEquals(jexpr.execute("{xm.divide('4.0','2')}"), "2.0");
        
        assertEquals(jexpr.execute("{xm.divide('2','4')}"), "0");
        assertEquals(jexpr.execute("{xm.divide('2','4.0')}"), "0");
        assertEquals(jexpr.execute("{xm.divide('10','11')}"), "0");
        
        assertEquals(jexpr.execute("{xm.divide('2.0','4.0')}"), "0.5");
        assertEquals(jexpr.execute("{xm.divide('2.0','4')}"), "0.5");
        assertEquals(jexpr.execute("{xm.divide('10.0','11.0')}"), "0.9090909090909091");
        assertEquals(jexpr.execute("{xm.divide('10.0','11')}"), "0.9090909090909091");
        
     // OTHER TESTS
        
        assertEquals(jexpr.execute("{xm.intValue('5.0')}"), "5");
        assertEquals(jexpr.execute("{xm.doubleValue('5')}"), "5.0");
        
        assertEquals(jexpr.execute("{xm.divide(xm.doubleValue(this.area),this.regionScale)}"), "12.0");
        assertEquals(jexpr.execute("{xm.divide(xm.intValue(this.regionScale),'1')}"), "2");
        assertEquals(jexpr.execute("{xm.multiply('12.0',xm.intValue(this.regionScale))}"), "24.0");
    }

//  THIS TEST MUST BE THE LAST TEST RUN FOR IT TO TEST THE CACHE...
    
    @Test
    public void testJExpr_CachingExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create(new TestObject());
        
        assertNotNull(jexpr);
        
        assertTrue(jexpr.cacheExpression("{this.id.substring('29')}"));
        assertTrue(jexpr.cacheExpression("{this.id.substring('29')}"));
        assertTrue(jexpr.cacheExpression("{this.id.substring('29')}"));
        assertTrue(jexpr.cacheExpression("{this.id.substring('29')}"));
        
        assertEquals(jexpr.execute("{this.id.substring('29')}"), "0be");
        assertEquals(jexpr.execute("{this.id.substring('29')}"), "0be");

        assertEquals(jexpr.execute("{this.id.substring(25).substring(4)}"), "0be");
        assertEquals(jexpr.execute("{this.id.substring(25).substring(4)}"), "0be");

        jexpr.setObject(new StringBuffer());
        jexpr.registerClass("Calculator", "com.breitling.jexpr.test.standalone.Calculator");
        
        assertEquals(jexpr.execute("{this.append('this ').append('is ').append('a ').append('test.')}"), "this is a test.");
        
        jexpr.setObject(new StringBuffer());
        assertEquals(jexpr.execute("{this.append('2.0 + 2.0 = ').append(Calculator.add('2.0','2.0')).append('.')}"), "2.0 + 2.0 = 4.0.");
        
        jexpr = JExpr.create(new Calculator());
        
        assertEquals(jexpr.execute("{this.divide(5,2)}"), "2");
        
        jexpr = JExpr.create(new TestObject());
        
        assertEquals(jexpr.execute("{this.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.id.substring(25)+56}"), "d0d00be56");
        assertEquals(jexpr.execute("{this.id.substring(25)+'56'}"), "d0d00be56");
        
        assertEquals(jexpr.execute("{this.parent.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.parent.parent.id.substring('25')}"), "d0d00be");
        assertEquals(jexpr.execute("{this.parent.parent.parent.id.substring('25')}"), "d0d00be");
        
        assertEquals(jexpr.execute("{this.getParent().id.substring('30')}"), "be");
        
        jexpr = JExpr.create(new Calculator());
        
        assertNotNull(jexpr);
        
        jexpr.registerKeyWord("TODAY", "{now}");
        jexpr.registerKeyWordAndExecute("today", "{now}");
        
        JExprSupport.Sleep(2000);
        
        String now = jexpr.execute("{now}");
        
        assertEquals(jexpr.execute("{TODAY}"), now);
        assertNotSame(jexpr.execute("{today}"), now);
        
        jexpr.registerKeyWord("TIME", "{timestamp}");
        jexpr.registerKeyWordAndExecute("time", "{timestamp}");
        
        JExprSupport.Sleep(2000);
        
        String time = jexpr.execute("{timestamp}");
        
        assertEquals(jexpr.execute("{TIME}"), time);
        assertNotSame(jexpr.execute("{time}"), time);    
    }
    
    @Test
    public void testJExpr_SplitExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create("this:is:a:test:of:Split");
        
        assertEquals(jexpr.execute("{x.Split(this,':',0)}"), "this");
        assertEquals(jexpr.execute("{x.Split(this,':',1)}"), "is");
        assertEquals(jexpr.execute("{x.Split(this,':',2)}"), "a");
        assertEquals(jexpr.execute("{x.Split(this,':',3)}"), "test");
        assertEquals(jexpr.execute("{x.Split(this,':',4)}"), "of");
        assertEquals(jexpr.execute("{x.Split(this,':',5)}"), "Split");
        
        assertEquals(jexpr.execute("{x.Split(this,'[st].',0)}"), "");
        assertEquals(jexpr.execute("{x.Split(this,'[st].',1)}"), "i");
        assertEquals(jexpr.execute("{x.Split(this,'[st].',2)}"), "i");
        assertEquals(jexpr.execute("{x.Split(this,'[st].',3)}"), "a:");
        
        jexpr.setObject("10/28/1956 11:12");
        
        assertEquals(jexpr.execute("{x.Split(this,' ',0)}"), "10/28/1956");
        assertEquals(jexpr.execute("{x.Split(this,' ',1)}"), "11:12");
    }
    
    @Test
    public void testJExpr_SplitsWithExprExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create("this:is:a:test:of:Split");
        
        assertEquals(jexpr.execute("{..Split(this,':','#1')}"), "this");
        assertEquals(jexpr.execute("{..Split(this,':','#6')}"), "Split");
        assertEquals(jexpr.execute("{..Split(this,':','#6 #2 good!')}"), "Split is good!");
    }

    @Test
    public void testJExpr_MatchExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create("image1(12345)");
        
        assertEquals(jexpr.execute("{x.Match(this,'(.*)\\((.*)\\)','#1')}"), "image1");
        assertEquals(jexpr.execute("{x.Match(this,'(.*)\\((.*)\\)','#2')}"), "12345");
        assertEquals(jexpr.execute("{x.Match(this,'(.*)\\((.*)\\)','#1.x.#2')}"), "image1.x.12345");
        assertEquals(jexpr.execute("{x.Match(this,'(.*)\\((.*)\\)','#0')}"), "image1(12345)");
    }
    
    @Test
    public void testJExpr_BackSlashMatchExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create("image1(12345)");
        
        assertEquals(jexpr.execute("{x.Match(this,'(.*)\\\\((.*)\\\\)','#1')}"), "image1");
    }
    
    @Test
    public void testJExpr_GroupExpressions_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create("image1(12345)");
        
        assertEquals(jexpr.execute("{..Group(this,'(.*)\\((.*)\\)','#1')}"), "image1");
        assertEquals(jexpr.execute("{..Group(this,'(.*)\\((.*)\\)','#2')}"), "12345");
        assertEquals(jexpr.execute("{..Group(this,'(.*)\\((.*)\\)','#1.x.#2')}"), "image1.x.12345");
        assertEquals(jexpr.execute("{..Group(this,'(.*)\\((.*)\\)','#0')}"), "image1(12345)");
        
        jexpr.setObject("1956-10-27 11:12");
        
        assertEquals(jexpr.execute("{..Group(this.substring(0,10),'([0-9]+)-([0-9]+)-([0-9]+)','#2/#3/#1')}"),"10/27/1956");
    }
    
    @Test
    public void testJExpr_UnregisteredClasses_ValidValues() throws JExprException
    {
        JExpr jexpr = JExpr.create(new Date());
        
        try 
        {
            jexpr.execute("{DateFormat.getInstance().format(this)}");
            fail("should not get here!");
        }
        catch (JExprException e)
        {
            assertTrue(e.toString().contains("Expression failure: Can't find class 'DateFormat'."));
        }
        
        jexpr.registerClass("DF", "java.text.DateFormat");
        jexpr.setDateFormat("M/d/yy h:mm a");
        jexpr.setObject(new Date());
        
        String now = jexpr.execute("{now}");
        
        assertEquals(jexpr.execute("{DF.getInstance().format(this)}"), now);
    }
    
//  PRIVATE CLASS TO SUPPOR THE TEST CASES
    
    public class TestObject
    {
        public String  area;
        public String  width;
        public String  height;
        public String  regionScale;
        public String  id;
        public String  page;
        
        public TestObject()
        {
            area = "24";
            width = "8";
            height = "3";
            regionScale = "2.0";
            page = "1";
            
            id = "2c988827175a33a901175b1d9d0d00be";
        }
        
        public TestObject getParent()
        {
            return this;
        }
        
        public String getId() { return id; }
        
        public String getArea() { return area; }
        public String getHeight() { return height; }
        public String getWidth() { return width; }
        public String getRegionScale() { return regionScale; }
        public String getPage() { return page; }
    }
}
