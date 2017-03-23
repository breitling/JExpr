package com.breitling.jexpr;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class JExprDateSupport
{
    private static JExprDateSupport instance = null;
    private static SimpleDateFormat  dateFormat;
    
    static
    {
        dateFormat = new SimpleDateFormat("MM/dd/yy");        
    }
    
//  CONSTRUCTOR
    
    private JExprDateSupport()
    {
    }
    
    public static JExprDateSupport getInstance()
    {
        if (instance == null)
            instance = new JExprDateSupport();
        
        return instance;
    }
    
    private static String recalculate_date(String oldDate, String pattern, int offset, int offsetType) throws JExprException
    {
        String   newDate = oldDate;
        Calendar calendar = new GregorianCalendar();
        
        try
        {
            if (pattern != null)
                dateFormat.applyPattern(pattern);
            
            if (oldDate.equals("now") == false)
                calendar.setTime(dateFormat.parse(oldDate));
            
            calendar.add(offsetType, offset);
            newDate = dateFormat.format(calendar.getTime());
            
            dateFormat.applyPattern("MM/dd/yy");
        }
        catch (ParseException pe)
        {
            throw new JExprException("Unable to parse date in JExpr Date Support class: " + pe.toString());
        }
        catch (Exception e)
        {
            throw new JExprException("General failure in JExpr Date Support class: " + e.toString());
        }
        
        return newDate;
    }
    
//  PUBLIC METHODS
    
    public static String plusDays(String oldDate, String pattern, int offset) throws JExprException
    {
        return recalculate_date(oldDate, pattern, offset, Calendar.DAY_OF_MONTH);
    }
    
    public static String plusDays(String oldDate, String pattern, String offset) throws JExprException
    {
        return plusDays(oldDate, pattern, Integer.parseInt(offset));
    }
    
    public static String plusDays(String oldDate, int offset) throws JExprException
    {
        return plusDays(oldDate, null, offset);
    }
    
    public static String plusDays(String oldDate, String offset) throws JExprException
    {
        return plusDays(oldDate, null, Integer.parseInt(offset));
    }
    
    
    public static String plusHours(String oldDate, String pattern, int offset) throws JExprException
    {
        return recalculate_date(oldDate, pattern, offset, Calendar.HOUR_OF_DAY);
    }
    
    public static String plusHours(String oldDate, String pattern, String offset) throws JExprException
    {
        return plusHours(oldDate, pattern, Integer.parseInt(offset));
    }
    
    public static String plusHours(String oldDate, int offset) throws JExprException
    {
        return plusHours(oldDate, null, offset);
    }
    
    public static String plusHours(String oldDate, String offset) throws JExprException
    {
        return plusHours(oldDate, null, Integer.parseInt(offset));
    }
}
