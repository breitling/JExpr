package com.breitling.jexpr.booleanengine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.breitling.jexpr.JExprException;
import com.breitling.jexpr.booleaneengine.JEBE;
import com.breitling.jexpr.booleaneengine.JExprBooleanEngine;

public class TestJExprBooleanEngine
{
    @Test
    public void testJEBE_SimpleEqualsExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("this")).evaluate("{this} == {'this'}"));
    }

    @Test
    public void testJEBE_BadEqualsExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create(new String("this")).evaluate("{this} == {'not this'}"));
    }

    @Test
    public void testJEBE_SimpleNotEqualsExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("this")).evaluate("{this} != {'not this'}"));
    }

    @Test
    public void testJEBE_BadNotEqualsExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create(new String("this")).evaluate("{this} != {'this'}"));
    }

    @Test
    public void testJEBE_SimpleEqualsExpressionWithParens_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("this")).evaluate("({this} == {'this'})"));
    }

    @Test
    public void testJEBE_SimpleEqualsExpressionWithManyParens_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("this")).evaluate("(({this} == {'this'}))"));
    }

    @Test
    public void testJEBE_SimpleAndExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("({7} == {7}) AND ({8} != {9})"));    
    }

    @Test
    public void testJEBE_BadAndExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create().evaluate("({7} == {7}) AND ({8} == {9})"));    
    }

    @Test
    public void testJEBE_SimpleOrExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("({7} == {7}) OR ({8} != {9})"));    
    }

    @Test
    public void testJEBE_BadOrExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create().evaluate("({7} == {8}) OR ({8} == {9})"));    
    }

    @Test
    public void testJEBE_ComplexExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("({7} == {7}) AND (({8} != {9}) OR ({2} != {2}))"));    
    }

    @Test
    public void testJEBE_BadComplexExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create().evaluate("({7} == {7}) AND (({8} != {8}) OR ({2} != {2}))"));    
    }

    @Test
    public void testJEBE_LessThan_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{7} < {9}"));
    }

    @Test
    public void testJEBE_GreaterThan_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{111111111} > {9}"));
    }

    @Test
    public void testJEBE_LessThanEqual_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{20} <= {20}"));
        assertTrue(JExprBooleanEngine.create().evaluate("{20} <= {21}"));
    }

    @Test
    public void testJEBE_GreaterThanEqual_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{111111111} >= {111111111}"));
        assertTrue(JExprBooleanEngine.create().evaluate("{111111112} >= {111111111}"));
    }

    @Test
    public void testJEBE_StringLessThanEqual_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("7")).evaluate("{Integer.valueOf(this)} <= {Integer.valueOf(this)}"));
    }

    @Test
    public void testJEBE_IsBlank_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("")).evaluate("{this} == {''}"));
        assertTrue(JExprBooleanEngine.create(new String("")).evaluate("{this.length()} == {0}"));
    }

    @Test
    public void testJEBE_SetObject_True() throws JExprException
    {
        JExprBooleanEngine engine = JExprBooleanEngine.create();
    
        engine.setObject(new StringBuilder());
    
        assertTrue(engine.evaluate("{this.append('this ').append('is ').append('a ').append('test.')} == {'this is a test.'}"));
    
        engine.getExpresionProcessor().setObject("3");
    
        assertTrue(engine.evaluate("{this} == {3}"));
    }

    @Test
    public void testJEBE_SimpleXorExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("({2} == {2}) XOR ({3} == {4})"));
    }

    @Test
    public void testJEBE_BadXorExpression_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create().evaluate("({2} == {2}) XOR ({3} == {3})"));
        assertFalse(JExprBooleanEngine.create().evaluate("({2} != {2}) XOR ({3} != {3})"));
    }

    @Test
    public void testJEBE_SimpleEQVExpression_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("({2} == {3}) EQV ({3} == {4})"));
        assertTrue(JExprBooleanEngine.create().evaluate("({2} == {2}) EQV ({3} == {3})"));
    }

    @Test
    public void testJEBE_CachingTime_Times() throws JExprException
    {
        long time1 = System.nanoTime();
    
        assertTrue(JExprBooleanEngine.create().evaluate("({1} == {1}) AND (({100} != {101}) OR ({200} != {200}))"));    

        long time2 = System.nanoTime();
    
        assertTrue(JExprBooleanEngine.create().evaluate("({1} == {1}) AND (({100} != {101}) OR ({200} != {200}))"));    
    
        long time3 = System.nanoTime();
    
        System.out.println(time2 - time1);
        System.out.println(time3 - time2);
    }

    @Test
    public void testJEBE_StartsWith_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create(new String("JEBE is fast!")).evaluate("({this.startsWith('JEBE')}) AND ({this.endsWith('!')})"));
    }

    @Test
    public void testJEBE_Matches_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{x.Match('JEBE is good','^JEBE(.*)d','#1')} == {' is goo'}"));
        assertTrue(JExprBooleanEngine.create(new String("JEBE is good")).evaluate("{..Match(this,'^JEBE(.*)d','#1')} == {' is goo'}"));
    }

    @Test
    public void testJEBE_FindBadExpressions_ShouldNotFail()
    {
        testBadExpression("");
        testBadExpression("8}");
        testBadExpression("{8");
        testBadExpression("{7}{8}");
        testBadExpression("({7} == {7}) AND {8} = {9");
        testBadExpression("{true}");
        testBadExpression("{7} < {'z'}");
        testBadExpression("{7} > {'z'}");
        testBadExpression("({7} < {10}) ANDD ({'true'})");
    }

    @Test
    public void testJEBE_True_True() throws JExprException
    {
        assertTrue(JExprBooleanEngine.create().evaluate("{'true'}"));
    }

    @Test
    public void testJEBE_False_False() throws JExprException
    {
        assertFalse(JExprBooleanEngine.create().evaluate("{'false'}"));
    }

    @Test
    public void testJEBE_JEBE_True() throws JExprException
    {
        assertTrue(JEBE.evaluate("{10} == {10}"));
        assertTrue(JEBE.evaluate("this", "{this} == {'this'}"));
    }

    @Test
    public void testJEBE_SetObjectFirst_True() throws JExprException
    {
        JEBE.setObject("this");
    
        assertTrue(JEBE.evaluate("{this} == {'this'}"));
        assertTrue(JEBE.evaluate("{this.matches('t.*s')}"));
    
        JEBE.setObject("that");
    
        assertTrue(JEBE.evaluate("{this.matches('(t..t)')}"));
        assertTrue(JEBE.evaluate("{this.compareToIgnoreCase('THAT')} == {0}"));
    }

//  PRIVATE METHODS

    private void testBadExpression(final String expression)
    {
        try
        {
            JExprBooleanEngine.create().evaluate(expression);
            fail("failed: should not get here!");
        }
        catch (JExprException e)
        {
            assertEquals("com.breitling.jexpr.JExprException", e.toString().substring(0,34));
        }
    }
}
