package com.breitling.jexpr.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.breitling.jexpr.JExprException;
import com.breitling.jexpr.JExprUtils;

public class TestJExprUtils 
{
    private static String [] expressions = 
    {
        "{this.prettyID.value.substring(5)}",
        "{this.getComboBoxValue('photograph')}",
        "{this.br_privateLabel+' '+this.br_cat+' '+this.br_size+' '+this.br_product}",
        "{this.descript2+' '+this.descriptVal2}",
        "{this.holdCancel}",
        "{this.Id.Id.substring(26)}",
        "{timestamp}",
        "{this.imageID+'_'+this.parent.pageNr+'_'+this.shotNumber}",
        "{this.TwistPreflight+'-'+this.TwistPreflightLvl1+'-'+this.TwistOutputFormat+'-'+this.TwistDestination+'-'+this.TwistVectorizeFonts+'-'+this.TwistGCRProfile+'-'+this.TwistExtension}",
        "{now}",
        "{this.parent.parent.Job Number+'-'+this.sequentialNumber}",
        "{this.sku+this.getMetaDataValue('photograph').value.charAt(0)+this.year+this.seasonal+this.modelShot}",
        "{xm.add(this.x,this.y)}",
        "{y.z(x.y(this.getMetaDataValue('Remote Work Site')))}",
        "{String.format('%02d',  this.x, this.y, this.parent.z, this.that)}"
    };
    
//  INITIALIZATION AND TEAR DOWN
    
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
    @SuppressWarnings({"unchecked"})
    public void testJExpr_ParseXCodeForAttributes_ValidValues() throws JExprException
    {
        int n = 0;
        Collection<String> [] results = new Collection [expressions.length];
        
        for (String expr : expressions)
            results[n++] = JExprUtils.ParseForAttributes(expr);
        
        assertEquals(1, results[0].size());
        assertEquals("[prettyID]", results[0].toString());
        
        assertEquals(1, results[1].size());        
        assertEquals("[photograph]", results[1].toString());
        
        assertEquals(4, results[2].size());
        assertEquals("[br_privateLabel, br_cat, br_size, br_product]", results[2].toString());
        
        assertEquals(2, results[3].size());
        assertEquals("[descript2, descriptVal2]", results[3].toString());
        
        assertEquals(1, results[4].size());
        assertEquals("[holdCancel]", results[4].toString());
        
        assertEquals(1, results[5].size());
        assertEquals("[Id]", results[5].toString());
        
        assertEquals(0, results[6].size());
        
        assertEquals(2, results[7].size());
        assertEquals("[imageID, shotNumber]", results[7].toString());
        
        assertEquals(7, results[8].size());
        assertEquals("[TwistPreflight, TwistPreflightLvl1, TwistOutputFormat, TwistDestination, TwistVectorizeFonts, TwistGCRProfile, TwistExtension]", results[8].toString());
        
        assertEquals(0, results[9].size());
        
        assertEquals(1, results[10].size());
        assertEquals("[sequentialNumber]", results[10].toString());
        
        assertEquals(5, results[11].size());
        assertEquals("[sku, photograph, year, seasonal, modelShot]", results[11].toString());
        
        assertEquals(2, results[12].size());
        assertEquals("[x, y]", results[12].toString());
        
           assertEquals(1, results[13].size());
        assertEquals("[Remote Work Site]", results[13].toString());
        
        assertEquals(3, results[14].size());
        assertEquals("[x, y, that]", results[14].toString());
    }
}
