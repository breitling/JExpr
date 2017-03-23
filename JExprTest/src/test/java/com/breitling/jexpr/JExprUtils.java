package com.breitling.jexpr;

import java.util.Collection;
import java.util.ArrayList;

import com.breitling.jexpr.JExpr.AppendNode;
import com.breitling.jexpr.JExpr.ExprNode;
import com.breitling.jexpr.JExpr.JExprNode;

public class JExprUtils 
{
    public static Collection<String> ParseForAttributes(final String expr)
    {
        ArrayList<String> attributes = new ArrayList<String>();
           
        try
        {
            JExpr xcode = JExpr.create(This.create());      // A bit of a kludge but JExpr "this" must be something
            JExprNode  tree = xcode.parse(expr);
        
            examineTree(attributes, tree);
        }
        catch (JExprException e)
        {
        }
        
        return attributes;
    }

//  LOCAL METHOD
    
    private static void examineTree(final ArrayList<String> attributes, final JExprNode node) throws JExprException
    {
        AppendNode localNode = (AppendNode) node;
        
        for (JExprNode n : localNode.nodes)
            if (n instanceof ExprNode)
                doExprNode(attributes, (ExprNode) n);
    }
    
    private static void doExprNode(final ArrayList<String> attributes, final ExprNode node) throws JExprException
    {
        ExprNode localNode = node;
        
        do
        {
            if (localNode.original.startsWith("get"))
            {
                JExprNode n = localNode.nodes.get(0);
                
                if (n instanceof JExpr.StringNode)
                    attributes.add((String)((JExpr.StringNode) n).value);
            }
            else
            if ((localNode.klass.equals(JExpr.THISCLASSPLACEHOLDER) || localNode.klass.equals("this")) && 
                localNode.original.equals("parent") == false)
            {
                attributes.add(localNode.original);
            }
            
            for (JExprNode n : localNode.nodes)
            {
                if (n instanceof ExprNode)
                    doExprNode(attributes, (ExprNode) n);
            }
        }
        while ((localNode = localNode.nextExprNode) != null);
    }
    
//  INNER CLASS USED FOR PARSING EXPRESSIONS
    
    private static class This 
    {
        public static This create() 
        {
            return new This();
        }
    }
}
