package com.breitling.jexpr.booleaneengine;

import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import com.breitling.jexpr.JExpr;
import com.breitling.jexpr.JExprException;

/*
** Boolean Engine Grammer
** ----------------------
** Engine           := <expression> 
** 
** expression       := ( <expression> ) | ( <expression> <boolean-operator> <expression> ) | <base>  
**
** base             := <expr> | <expr> <operator> <expr>
*
** boolean-operator := NOP | AND | OR | XOR | EQV
**
** operator         := NOP | == | != | < | <= | > | >= 
**
** expr             := JExpr
**
*/

public class JExprBooleanEngine
{
    private enum Operator {NOP, EQUALS, NOTEQUALS, LESSTHAN, LESSTHANEQUALS, GREATERTHAN, GREATERTHANEQUALS};
    private enum BooleanOperator {NOP, AND, OR, XOR, EQV};
    
    private JExpr          expressionProcessor;
    private Stack<String>  peekTokens;
    
    private static HashMap<String,TreeNode> expressionTreeCache = new HashMap<String,TreeNode>();

//  CONSTRUCTORS    
    
    private JExprBooleanEngine(final Object o)
    {
        peekTokens = new Stack<String>();
        
        expressionProcessor = JExpr.create(o);
    }
    
//  PUBLIC CREATE METHODS
    
    public static JExprBooleanEngine create()
    {
        return new JExprBooleanEngine(null);
    }
    
    public static JExprBooleanEngine create(Object o)
    {
        return new JExprBooleanEngine(o);
    }
    
//  PUBLIC METHODS
    
    public Boolean evaluate(final String expression) throws JExprException
    {
        boolean retVal;
        
        try 
        {
            retVal = (boolean) execute(parse(expression));
        }
        catch (Throwable e) 
        {
            StringBuffer err = new StringBuffer();
            
            err.append("Error with expression [");
            err.append(expression);
            err.append("] : ");
            err.append(e.toString().substring(36));
            
            throw new JExprException(err.toString());
        }
        
        return retVal;
    }
    
//  PUBLIC SUPPORT METHODS 
       
    public void setObject(final Object object)
    {
        expressionProcessor.setObject(object);
    }

    public JExpr getExpresionProcessor()
    {
        return this.expressionProcessor;
    }
    
//  PRIVATE METHODS
    
//
//****************************************************************************************
//**                                                                                    **    
//**                      JExprBooleanEngine Execution Engine                           **
//**                                                                                    **  
//****************************************************************************************
//                
    private Boolean execute(final TreeNode node) throws JExprException
    {
        if (node == null)
        {
            return Boolean.TRUE;
        }
        else
        if (node instanceof LeafNode)
        {
            return execute(((LeafNode) node).node);
        }
        else
        {
            return execute(execute(node.n1), node.booleanOperator, execute(node.n2));
        }
    }
    
    private boolean execute(final ExprNode node) throws JExprException
    {
        return execute(node.lhs, node.operator, node.rhs);
    }
    
    private boolean execute(final String lhs, final Operator op, final String rhs) throws JExprException
    {
        boolean rc = false;
        String  r1 = this.expressionProcessor.execute(lhs);
        
        if (op == Operator.NOP)
            rc = Boolean.valueOf(r1);

        if (rhs != null)
        {
            String r2 = this.expressionProcessor.execute(rhs);
            
            switch (op)
            {
            case EQUALS:
                 rc = r1.equals(r2);
                 break;
                 
            case NOTEQUALS:
                 rc = ! r1.equals(r2);
                 break;
                 
            case LESSTHAN:
                 rc = lessThan(r1, r2);
                 break;
                 
            case LESSTHANEQUALS:
                 rc = lessThan(r1, r2) || r1.equals(r2);
                 break;
                 
            case GREATERTHAN:
                 rc = greaterThan(r1, r2);
                 break;
                 
            case GREATERTHANEQUALS:
                 rc =  greaterThan(r1, r2) || r1.equals(r2);
                 break;
              
            case NOP:
                 break;
            }
        }
        
        return rc;
    }
        
    private boolean execute(final Boolean r1, final BooleanOperator op, final Boolean r2)
    {
        switch (op)
        {
        case AND:
             return r1 && r2;
                 
        case OR:
             return r1 || r2;
             
        case XOR:
             return ((r1 == false && r2 == true) || (r1 == true && r2 == false));
             
        case EQV:
             return (r1 == false && r2 == false) || (r1 == true && r2 == true);
             
        case NOP:
             return r1;
             
        default:
             return false;
        }
    }
    
    private boolean lessThan(final String r1, final String r2) throws JExprException
    {
        try
        {
            return Integer.decode(r1).intValue() < Integer.decode(r2).intValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new JExprException("bad number for numberic operator");
        }
    }
    
    private boolean greaterThan(final String r1, final String r2) throws JExprException
    {
        try
        {
            return Integer.decode(r1).intValue() > Integer.decode(r2).intValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new JExprException("bad number for numberic operator");
        }
    }
    
//
//****************************************************************************************
//**                                                                                    **    
//**                       JExprBooleanEngine Parser                                    **
//**                                                                                    **  
//****************************************************************************************
//              
    
    private TreeNode parse(final String expression) throws JExprException
    {
        TreeNode tree = null;
        
        synchronized(expressionTreeCache)
        {
            tree = expressionTreeCache.get(expression);
        }
        
        if (tree == null)
        {
            tree = parse_expression_proxy(expression);
            expressionTreeCache.put(expression, tree);
        }
        
        return tree;
    }
    
    private TreeNode parse_expression_proxy(final String expression) throws JExprException
    {
        peekTokens.removeAllElements();
        
        StringTokenizer st = new StringTokenizer(expression, "{}()", true);

        TreeNode node = parseExpression(st);
        
        if (! getNextToken(st).isEmpty())
            throw new JExprException("expression not completely parsed");

        return node;
    }
    
    private TreeNode parseExpression(final StringTokenizer st) throws JExprException
    {
        String   t;
        TreeNode node = null;
        
        if ((t = getNextToken(st)).equals("("))
        {
            node = parseExpression(st);
            
            if (! getNextToken(st).equals(")"))
                throw new JExprException("missing ')'");

            if (! peekToken(st).isEmpty()  && ! peekToken(st).equals(")"))
            {
                node.booleanOperator = parseBooleanOperator(st);
                node.n2 = parseExpression(st);
            }
        }
        else
        if (t.equals("{"))
        {
            node = new TreeNode();
            
            pushToken("{");
            
            node.n1 = new LeafNode(parseBase(st));
        }
        else
        {
            throw new JExprException("bad expression.");
        }
        
        return node;
    }
    
    private ExprNode parseBase(final StringTokenizer st) throws JExprException
    {
        String   rhs = null;
        String   lhs = parseJExpr(st);
        Operator op  = parseOperator(st);
        
        if (op != Operator.NOP)
            rhs = parseJExpr(st);
        
        return new ExprNode(lhs, op, rhs);
    }
    
    private String parseJExpr(final StringTokenizer st) throws JExprException
    {
        if (! getNextToken(st).equals("{"))
            throw new JExprException("bad JExpr found, missing {");
        
        String s;
        StringBuilder sb = new StringBuilder();
        
        while (true)
        {
            s = getNextToken(st);
            
            if (s.isEmpty())
                throw new JExprException("bad JExpr found, missing }");
            
            if (s.equals("}"))
                break;
                    
            sb.append(s);
        }
        
        String expr = "{" + sb.toString() + "}"; 
                
        return expr;
    }
    
    private Operator parseOperator(final StringTokenizer st) throws JExprException
    {
        String sop = getNextToken(st);
        Operator op = Operator.NOP;
        
        switch (sop.trim())
        {
        case "==":
             op = Operator.EQUALS;
             break;
        
        case "!=":
             op = Operator.NOTEQUALS;
             break;
            
        case "<":
             op = Operator.LESSTHAN;
             break;
            
        case "<=":
             op = Operator.LESSTHANEQUALS;
             break;
            
        case ">":
             op = Operator.GREATERTHAN;
             break;
        
        case ">=":
             op = Operator.GREATERTHANEQUALS;
             break;
            
        case "":
             break;
             
        default:
             pushToken(sop);
             break;
        }

        return op;
    }
    
    private BooleanOperator parseBooleanOperator(final StringTokenizer st) throws JExprException
    {
        String sop = getNextToken(st);
        BooleanOperator op = BooleanOperator.NOP;
        
        switch (sop.trim())
        {
        case "AND":
             op = BooleanOperator.AND;
             break;
         
        case "OR":
             op = BooleanOperator.OR;
             break;

        case "XOR":
             op = BooleanOperator.XOR;
             break;

        case "EQV":
             op = BooleanOperator.EQV;
             break;

        default:
             throw new JExprException("bad boolean operator found");
        }
        
        return op;
    }
//
//****************************************************************************************
//**                                                                                    **    
//**                       JExprBooleanEngine Tokenizer                                 **
//**                                                                                    **  
//****************************************************************************************
//            
      private String getNextToken(final StringTokenizer st) throws JExprException
      {
          String token;
          
          if (!peekTokens.empty())
              token = popToken();
          else
          if (st.hasMoreTokens())
              token = st.nextToken();
          else
              token = "";
          
          return token;
      }
      
      private String peekToken(final StringTokenizer st) throws JExprException
      {
          String token;
          
          if (!peekTokens.empty())
          {
              token = peekTokens.peek();
          }
          else
          if (st.hasMoreTokens())
          {
              token = getNextToken(st);
              pushToken(token);
          }
          else
          {
              token = "";
          }
          
          return token;
      }
      
      private String popToken()
      {
          return peekTokens.pop();
      }
      
      private void pushToken(final String token)
      {
          peekTokens.push(token);
      }      
//
//****************************************************************************************
//**                                                                                    **    
//**                         JExprBooleanEngine Nodes                                   **
//**                                                                                    **  
//****************************************************************************************
//          
    private class ExprNode 
    {
        String    lhs;
        String    rhs;
        Operator  operator;
        
        public ExprNode(final String lhs, final Operator operator, final String rhs)
        {
            this.lhs = lhs;
            this.operator = operator;
            this.rhs = rhs;
        }
    }

    private class LeafNode extends TreeNode
    {
        public ExprNode node;
        
        public LeafNode(final ExprNode node)
        {
            this.node = node;
            
            this.n1 = null;
            this.booleanOperator = BooleanOperator.NOP;
            this.n2 = null;
        }
    }
    
    private class TreeNode
    {
        public TreeNode    n1;
        public TreeNode    n2;
        public BooleanOperator  booleanOperator;   // AND or OR or NOP
        
        public TreeNode()
        {
            booleanOperator = BooleanOperator.NOP;
        }
    }
}
