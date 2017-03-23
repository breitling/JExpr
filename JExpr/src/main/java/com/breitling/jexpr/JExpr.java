package com.breitling.jexpr;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/*
** JExpr Grammer
** -------------
** JExpr       := {<expr>}
**
** <expr>      := <subexpr> | <subexpr> + <expr>
**
** <subexpr>   := <classexpr> | this | "<string>" | '<string>' | now | timestamp | <number> | <null>
**
** <classexpr> := <classname>.<method> | this.<method> 
**
** <method>    := <name>(<commaexpr>) | <method>.<name>(<commaexpr>) | <name> | <method>.<name>
**
** <commaexpr> := <subexpr> | <subexpr>,<subexpr>
**
** <class>     := <Any known/registered Java Class>
**
** <name>      := <Any public method of <class>>
**
** <string>    := a-Z, A-Z, 0-9, and special characters
**
** <number>    := <integer> | <float>
**
** <integer>   := [0-9][0-9]*
**
** <float>     := [0-9][0-9.]*
**
** <null>      := 
*/

/*
** NOTE:
** 
** EXPRESSIONS ARE NOW CACHED AND ALL EXECUTION RELATED PROCESSING MUST OCCUR IN THE EXECUTION PART OF THE PROCESS.
**
*/

public class JExpr
{
    private Object                  object;
    
    private Stack<String>           peekTokens;
    
    private SimpleDateFormat        dateFormat;
    private SimpleDateFormat        timeStampFormat;
    
    private HashMap<String,String>  classMap;
    private HashMap<String,String>  keyWords;
    
    private static HashMap<String,JExprNode> expressionTreeCache = new HashMap<String,JExprNode>();
    
    protected static final String THISCLASSPLACEHOLDER = "CLASS<this>";
    
//  CONSTRUCTORS
    
    private JExpr()
    {
        object = null;
        peekTokens = new Stack<String>();
        classMap = new HashMap<String,String>();
        keyWords = new HashMap<String,String>();
        
        dateFormat = new SimpleDateFormat("MMddyy");                       // "MM/dd/yyyy hh:mm:ssZ"
        timeStampFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        
        try
        {
            registerClass(".",  "com.breitling.jexpr.JExprSupport");       // Special access to JExprSupport class
            registerClass("x",  "com.breitling.jexpr.JExprSupport");       // Special access to JExprSupport class
            registerClass("xm", "com.breitling.jexpr.JExprMathSupport");   // Special math class
            registerClass("xd", "com.breitling.jexpr.JExprDateSupport");   // Special date class
            
            registerClass("String", "java.lang.String");                   // java.lang support 
            registerClass("Integer", "java.lang.Integer");
            registerClass("Short", "java.lang.Short");
            registerClass("Long", "java.lang.Long");
            registerClass("StringBuffer", "java.lang.StringBuffer");
            registerClass("Boolean", "java.lang.Boolean");
        }
        catch (JExprException jee)
        {
        }
    }
    
    private JExpr(Object object)
    {
        this();
        this.object = object;
    }

//  PUBLIC CREATE METHODS
    
    public static JExpr create()
    {
        return new JExpr();
    }
    
    public static JExpr create(Object object)
    {
        return new JExpr(object);
    }
    
//  PUBLIC METHODS
    
    public String execute(Object o, String expression) throws JExprException
    {
        this.object = o;
        
        return (String) execute(parse(expression));
    }
    
    public String execute(String expression) throws JExprException
    {
        String retVal = "";
        
        try 
        {
            retVal = (String) execute(parse(expression));
        }
        catch (Throwable e) 
        {
            StringBuffer err = new StringBuffer();
            
            err.append("Error with expression [");
            err.append(expression);
            err.append("] with object => ");
            err.append(this.object);
            err.append(" ===> ");
            err.append(e.toString().substring(36));
            
            throw new JExprException(err.toString());
        }
        
        return retVal;
    }
    
    public Boolean cacheExpression(String expression) throws JExprException
    {
        JExprNode tree = this.parse(expression);
        
        if (tree != null)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
    
    public static synchronized void ResetJExprCache()
    {
        expressionTreeCache = new HashMap<String,JExprNode>();
    }
    
//  PUBLIC SUPPORT METHODS 
    
    public void registerClass(String name, String path) throws JExprException
    {
        if (classMap.get(name) != null)
            throw new JExprException(name + " already used to register a class");
        
        classMap.put(name, path);
    }
    
    public void registerKeyWord(String key, String value)
    {
        keyWords.put(key, value);
    }
    
    public void registerKeyWordAndExecute(String key, String value) throws JExprException
    {
        keyWords.put(key, execute(value));
    }
    
    public void setDateFormat(SimpleDateFormat sdf)
    {
        this.dateFormat = sdf;
    }
    
    public void setDateFormat(String pattern)
    {
        this.dateFormat.applyPattern(pattern);
    }
    
    public void setTimeStampFormat(SimpleDateFormat sdf)
    {
        this.timeStampFormat = sdf;
    }
    
    public void setTimeStampFormat(String pattern)
    {
        this.timeStampFormat.applyPattern(pattern);
    }
       
    public void setObject(Object object)
    {
        this.object = object;
    }

//  STATIC METHODS
    
    public static final Boolean containsJExpr(final String s)
    {
        if (s == null)
            return false;

        String s2 = s.trim();
        
        return (s2.startsWith("{") && s2.endsWith("}"));
    }
    
//
//****************************************************************************************
//**                                                                                    **    
//**                                JExpr Execution Engine                              **
//**                                                                                    **  
//****************************************************************************************
//   
    private Object execute(JExprNode tree) throws JExprException
    {
        Object returnValue;
        
        if (tree == null)
            throw new IllegalArgumentException("tree cannot be null.");

        try 
        {
            if (tree instanceof AppendNode)
            {
                returnValue = executeAppendNode((AppendNode) tree);
            }
            else
            if (tree instanceof ExprNode)
            {
                returnValue = executeExprNode((ExprNode) tree);
            }
            else
            {
                returnValue = tree.value;
            }
        }
        catch (Throwable e)
        {
            String message = e.toString();
            
            if (message.startsWith("com.breitling.jexpr.JExprException:"))
                message = message.substring(36);
            
            throw new JExprException("Expression failure: " + message);
        }

        return returnValue.toString();
    }

    private String executeAppendNode(AppendNode a) throws JExprException
    {
        String value = null;
        
        Object [] args = narrow(a.nodes);
     
        for (int n = 0; n < args.length; n++)
        {
            if (args[n] instanceof ExprNode)
                args[n] = executeExprNode((ExprNode) args[n]);
        }
        
        if (args.length > 1)
        {
            StringBuffer sb = new StringBuffer();
            
            for (int n = 0; n < args.length; n++)
                if (args[n] != null)
                    sb.append(args[n].toString());
                         
            value = sb.toString();
        }
        else
        {
            if (args[0] != null)
                value =  args[0].toString();
            else
                value = "";
        }
        
        return value;
    }

    private Object executeExprNode(ExprNode e) throws JExprException
    {
        Object retval = null;
        Object o = this.object;

        try
        {
            ExprNode prev = null;
            
            do
            {
                ExprNode localExprNode = (ExprNode) e.clone();
                
                if (localExprNode.klass.equals(THISCLASSPLACEHOLDER))
                {
                    localExprNode.klass = o.getClass().getName();
                }
                else
                if (localExprNode.klass.equals("&"))
                {
                    if (prev == null)
                        throw new JExprException("Internal Error.");
                    
                    if (retval == null)
                        throw new JExprException("Chained method failed: " + prev.klass + "."  + prev.method);

                    localExprNode.klass = retval.getClass().getName();
                    o  = retval;
                }
                
                localExprNode.args  = narrow(localExprNode.nodes);
                localExprNode.types = getTypes(localExprNode.args);

                // Find any child ExprNodes and execute them

                for (int n = 0; n < localExprNode.args.length; n++)
                {
                    if (localExprNode.args[n] instanceof ExprNode)
                    {
                        localExprNode.args[n] = executeExprNode((ExprNode) localExprNode.args[n]);
                        localExprNode.types[n] = getType(localExprNode.args[n]);
                    }
                }

                retval = getMethod(localExprNode).invoke(o, localExprNode.args);
                prev = localExprNode;
            }
            while ((e = e.nextExprNode) != null);
        }
        catch (ClassNotFoundException cnfe ) 
        {
            throw new JExprException("Can't find class '" + e.klass + "'.");
        }
        catch (NoSuchMethodException nsme ) 
        {
            throw new JExprException("Can't find method '" + e.method + "' with this signature in '" + e.klass+ "'.");
        }
        catch (IllegalAccessException iae) 
        {
            throw new JExprException("Not allowed to call method '" + e.method + "' in '" + e.klass+ "'.");
        } 
        catch (InvocationTargetException ite) 
        {
            Throwable t = ite.getCause();
            
            if (t != null)
                formatException(t, e, o);
            else
                throw new JExprException("Expression error: " + ite.toString());
        }
        catch (Exception ex)
        {
            throw new JExprException("Internal Error: " + ex.toString());
        }

        return retval;
    }
    
    private void formatException(Throwable t, ExprNode e, Object o) throws JExprException 
    {
        if (t.getClass().getName().equals(StringIndexOutOfBoundsException.class.getName())) 
        {
            String subStrLength = "0";
            
            if (e.args != null && e.args.length > 0)
                subStrLength = (e.args.length == 2 ? e.args[1] : e.args[0]).toString();

            throw new JExprException("JExpr attempted to get a substring of length " + subStrLength + 
                                     " within the attribute '" + (o) + "' but there were only '" + (o != null ? o.toString().length() : "") + 
                                     "' characters available at the time.", t);
        } 
        else 
        {
            throw new JExprException("Exception while executing expression: " + t.toString());
        }        
    }
    
    protected Class<?> getClassName(ExprNode e) throws ClassNotFoundException
    {
        Class<?> klass;
        
        e.klass = e.klass.trim();
        
        try
        {
            klass = Class.forName(e.klass);
        }
        catch (ClassNotFoundException cnfe)
        {
            String registeredName = classMap.get(e.klass);
            
            if (registeredName != null)
            {
                klass = Class.forName(registeredName);
                e.klass = registeredName;
            }
            else
            {
                klass = Class.forName("com.breitling.jexpr." + e.klass);    // ????
            }
        }
        
        return klass; 
    }
    
    protected Method getMethod(ExprNode e) throws ClassNotFoundException, NoSuchMethodException
    {
        Class<?> klass = getClassName(e);
        
    //  A BIT OF A KLUDGE, BUT THERE SEEMS TO BE SOME SPECIAL CASES...
        
        if (e.klass.equals("com.breitling.jexpr.JExprSupport") && e.method.equals("NOP"))
        {
            e.args = null;
            e.types = null;
        }
        else
        if (e.klass.equals("java.lang.String"))
        {
            if (e.method.equals("equals"))
                e.types[0] = Object.class;
            else
            if (e.method.equals("contains"))
                e.types[0] = CharSequence.class;
            else
            if (e.method.equals("format"))
            {
                e.types[1] = Array.newInstance(Object.class,0).getClass();
                
                Object a = Array.newInstance(Object.class, e.args.length-1);
                
                for (int n = 0; n < e.args.length-1; n++)
                    Array.set(a, n, e.args[n+1]);
                
                e.args[1] = a;
            }
        }
        
    //  OK LETS SEE IF 
        return klass.getMethod(e.method, e.types);
    }
    
    private Class<?> [] getTypes(Object [] args)
    {
        Class<?> [] types = new Class[args.length];
        
        for (int i = 0; i < args.length; ++i) 
            types[i] = getType(args[i]);

        return types;
    }
    
    private Class<?> getType(Object o)
    {
        Class<?> k = o.getClass();
        
     // Convert wrapper types (like Double) to primitive types (like double)

        if (k == Double.class)
            k = double.class;
        else
        if (k == Integer.class)
            k = int.class;
        else
        if (k == Boolean.class)
            k = boolean.class;
        
        return k;
    }
    
    private Object [] narrow(List<JExprNode> args)
    {
        int  n = 0;
        Object [] narrowed = new Object[args.size()];
        
        for (JExprNode node : args)
        {
            if (node instanceof StringNode)
                narrowed[n++] = narrow((String) node.value);
            else
            if (node instanceof ThisNode)
                narrowed[n++] = (object != null ? object : node.value);  // should always be object
            else
            if (node instanceof NullNode)
                narrowed[n++] = null;
            else
            if (node instanceof ExprNode)
                narrowed[n++] = node;
            else
            if (node instanceof KeywordNode)
            {
                String value = (String) node.value;
                
                if (JExpr.containsJExpr(value))
                {
                    try
                    {
                        narrowed[n++] = execute(value);
                    }
                    catch (JExprException xce)
                    {
                        narrowed[n++] = "[ERROR: " + value + " failed to execute!]";
                    }
                }
                else
                {
                    narrowed[n++] = value;
                }
            }
        }
        
        return narrowed;
    }
    
    private Object narrow(String argstring) 
    {
        if (argstring.startsWith("DATE:now"))
        {
            return this.dateFormat.format(new Date());
        }
        else
        if (argstring.startsWith("DATE:timestamp"))
        {
            return this.timeStampFormat.format(new Date());
        }
        
        try
        {
            return Integer.valueOf(argstring);
        } 
        catch( NumberFormatException nfe )
        {
        }
        
        try
        {
            return Double.valueOf( argstring );
        }
        catch( NumberFormatException nfe ) 
        {
        }
        
        if (argstring.equalsIgnoreCase("true")) 
        {
            return Boolean.TRUE;
        }
        else 
        if (argstring.equalsIgnoreCase("false"))
        {
            return Boolean.FALSE;
        }

        return argstring;
    }
    
//
//****************************************************************************************
//**                                                                                    **    
//**                                JExpr Parsing Engine                                **
//**                                                                                    **  
//****************************************************************************************
//
    protected JExprNode parse(String expression) throws JExprException
    {
        JExprNode tree = null;
        
        synchronized(expressionTreeCache)
        {
            tree = expressionTreeCache.get(expression);
        }
        
        if (tree == null)
        {
            tree = parseExpression(expression);
            expressionTreeCache.put(expression, tree);
        }
        
        return tree;
    }
    
    protected JExprNode parseExpression(String expression) throws JExprException
    {
        JExprNode node;
        
        peekTokens.removeAllElements();
        
        StringTokenizer st = new StringTokenizer(expression, "{}.,+()'\"", true);
                
        if (!getNextToken(st).equals("{"))
            throw new JExprException("Expression does not start with {.");
        
        node = parseExpr(st);
        node.expression = expression;
        
        if (!getNextToken(st).equals("}"))
            throw new JExprException("Expression does not end with }.");

        return node;
    }
    
    private JExprNode parseExpr(StringTokenizer st) throws JExprException
    {
        String t;
        AppendNode node = new AppendNode();
        
        do
        {
            parseSubExpr(st, node);
        }
        while ((t = getNextToken(st)).equals("+"));
        
        if (!t.equals("}"))
            throw new JExprException("Badly formed expression.");
        
        pushToken(t);
            
        return node;
    }
    
    private ExprNode parseClassExpr(StringTokenizer st) throws JExprException
    {
        String klass = getNextToken(st);
        
        if (!getNextToken(st).equals("."))
            throw new JExprException("Expression is improperly formed class.method expression.");
        
        if (klass.equals("this"))
        {
            if (object == null)
                throw new JExprException("JExpr object never initialized so 'this.<method>' is not valid.");
            
            klass = THISCLASSPLACEHOLDER;      // Postpone until execution instead of this -> object.getClass().getName();
        }

        ExprNode node = new ExprNode(klass);
        
        return parseMethod(st, node);
    }
    
    private ExprNode parseMethod(StringTokenizer st, ExprNode node) throws JExprException
    {
        String t;
        String method = getNextToken(st);

        if (!peekToken(st).equals("("))
        {
            node.original = method;
            node.method = constructMethodName(method);
        }
        else
        {
            node.original = node.method = method;
        }

        do
        {
            t = getNextToken(st);

            if (t.equals(")") || t.equals("}") || t.equals("+") || t.equals(","))
            {
                pushToken(t);
            }
            else
            if (t.equals("."))
            {
                node.nextExprNode = parseMethod(st, new ExprNode("&"));
            }
            else
            if (t.equals("("))
            {
                do
                {
                    parseSubExpr(st, node);
                }                
                while (peekToken(st).equals(","));
                                    
                if (!getNextToken(st).equals(")"))
                    throw new JExprException("Expression has improperly formed argument list.");
            }
            else
            {
                throw new JExprException("Expression has improperly formed argument list.");
            }
        }
        while (peekToken(st).equals("."));

        return node;
    }
    
    private void parseSubExpr(StringTokenizer st, ControlNode node) throws JExprException
    {
        boolean  foundComma = false;
        String   t = getNextToken(st);
        
        if (t.equals(","))
        {
            foundComma = true;
            t = getNextToken(st).trim();
        }
        
        if (t.equals(")") || t.equals("+"))
        {
            if (foundComma)
                throw new JExprException("Missing expression after a comma.");
            
            pushToken(t);
        }
        else
        if (t.equals("this"))
        {
            String isDot = peekToken(st);

            if (isDot.equals("."))
            {
                pushToken(t);
                node.add(parseClassExpr(st));
            }
            else
            {
                if (object == null)
                    throw new JExprException("JExpr object never initialized so 'this' is not a valid argument.");

                node.add(new ThisNode(object));
            }
        }
        else
        if (t.equals("now"))
        {
            node.add(new StringNode("DATE:now"));
        }
        else
        if (t.equals("timestamp"))
        {
            node.add(new StringNode("DATE:timestamp"));
        }
        else
        if (t.equals("\"") || t.equals("'"))
        {
            String delimeter = t;
            StringBuffer sb = new StringBuffer();

            while ((t = getNextToken(st)).equals(delimeter) == false)
                sb.append(t);

            node.add(new StringNode(sb.toString()));
        }
        else
        if (t.equals("."))
        {
            pushToken(t);
            node.add(parseClassExpr(st));
        }
        else
        if (Character.isDigit(t.charAt(0)))
        {
            int n = 0;
            int len = t.length();
            StringBuilder sb = new StringBuilder();

            while (n < len && Character.isDigit(t.charAt(n)))
                n++;

            if (n == len && peekToken(st).equals("."))
            {
                sb.append(t).append(getNextToken(st));
                
                n = 0;
                t = getNextToken(st);
                len = t.length();
                
                while (n < len && Character.isDigit(t.charAt(n)))
                    n++;
            }

            sb.append(t);  // the substring is not necessary if we throw that exception - substring(0,n));
            
            if (n < len)
                throw new JExprException("Bad number expression: " + sb.toString());

            node.add(new StringNode(sb.toString()));
        }
        else
        {
            if (keyWords.containsKey(t))
            {
                node.add(new KeywordNode(keyWords.get(t)));
            }
            else
            {
                pushToken(t);
                node.add(parseClassExpr(st));
            }
        }
    }

    private String constructMethodName(String method)
    {
        return "get" + method.substring(0,1).toUpperCase() + method.substring(1);
    }
    
//
//****************************************************************************************
//**                                                                                    **    
//**                                    JExpr Tokenizer                                 **
//**                                                                                    **  
//****************************************************************************************
//   
       
    private String getNextToken(StringTokenizer st) throws JExprException
    {
        String token;
        
        if (!peekTokens.empty())
            token = popToken();
        else
        if (st.hasMoreTokens())
            token = st.nextToken();
        else
            throw new JExprException("EOF on expression!");
        
        return token;
    }
    
    private String peekToken(StringTokenizer st) throws JExprException
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
            throw new JExprException("EOF on expression!");
        }
        
        return token;
    }
    
    private String popToken()
    {
        return peekTokens.pop();
    }
    
    private void pushToken(String token)
    {
        peekTokens.push(token);
    }
    
//
//****************************************************************************************
//**                                                                                    **    
//**                                      JExpr Nodes                                   **
//**                                                                                    **  
//****************************************************************************************
//   
    protected abstract class JExprNode
    {
        public Object  value;
        public String  expression;
    }
    
    protected class ControlNode extends JExprNode
    {
        public ExprNode     nextExprNode;
        public Object []    args;
        public Class<?> []  types;
        public List<JExprNode>  nodes;
        
        public ControlNode()
        {
            this.nodes = new ArrayList<JExprNode>(1);
            this.nextExprNode = null;
            
            this.args = null;
            this.types = null;

            this.value = null;
        }
        
        public void add(JExprNode node)
        {
            nodes.add(node);
        }
    }
    
    protected class AppendNode extends ControlNode
    {
        public AppendNode()
        {
            super();
        }
    }
    
    protected class ExprNode extends ControlNode implements Cloneable 
    {
        public String       klass;
        public String       method;
        public String       original;
        
        public ExprNode()
        {
            super();
            
            this.klass = "";
            this.method = "";
            this.original = null;
        }
        
        public ExprNode(String klass)
        {
            this();
            this.klass = klass;
        }
        
        public ExprNode(String klass, String method)
        {
            this();
            this.klass = klass;
            this.method = method;
        }
        
        public ExprNode(ExprNode that)
        {
            this.klass = that.klass;
            this.method = that.method;
            this.nodes = that.nodes;
            this.nextExprNode = that.nextExprNode;
            this.original = that.original;
            this.value = that.value;
            this.expression = that.expression;
        }
        
        public Object clone()
        {
            return new ExprNode(this);
        }
    }
    
    protected class ThisNode extends JExprNode
    {
        public ThisNode(Object o)
        {
            value = o;
        }
    }
    
    protected class StringNode extends JExprNode
    {
        public StringNode(String str)
        {
            value = str;
        }
    }
    
    protected class KeywordNode extends JExprNode
    {
        public KeywordNode(String str)
        {
            value = str;
        }
    }
    
    protected class NullNode extends JExprNode
    {
        public NullNode()
        {
            value = null;
        }
    }
}
