package com.baoyihu.autotestcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ParseClass
{
    private String filePath = null;
    
    private String testerPath = null;
    
    private List<String> binaryPath = null;
    
    private String className = null;
    
    private String classFullName = null;
    
    private final Map<String, String> importMap = new HashMap<String, String>();
    
    //   private static final char SPLIT = '\\';
    
    private URL[] urls = null;
    
    ///public class AutoTestTest extends TestCase
    
    public ParseClass(String path, String destPath, List<String> binaryPaths)
    {
        this.filePath = path;
        this.testerPath = destPath;
        this.binaryPath = binaryPaths;
    }
    
    public void doJob()
    {
        urls = new URL[binaryPath.size()];
        for (int iLoop = 0; iLoop < urls.length; iLoop++)
        {
            String uri = binaryPath.get(iLoop);
            try
            {
                urls[iLoop] = new File(uri).toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        
        CompilationUnit root = AstUtills.getRoot(filePath);
        List<TypeDeclaration> typeDeclaration = root.types();
        this.classFullName = AstUtills.getClassFullName(root);
        if (classFullName != null && classFullName.contains("."))
        {
            this.className = classFullName.substring(classFullName.lastIndexOf('.') + 1);
        }
        else
        {
            this.className = classFullName;
        }
        
        parseMainClass(typeDeclaration.get(0));// we just test the outer class;
        
    }
    
    private void parseMainClass(TypeDeclaration type)
    {
        URLClassLoader urlLoader = null;
        try
        {
            urlLoader = new URLClassLoader(urls);
            Class<?> thisClass = urlLoader.loadClass(classFullName);
            List<Method> methods = getPublicMethods(urlLoader, type, thisClass);
            String toto = printTest(urlLoader, thisClass, methods);
            StringBuilder builder = new StringBuilder();
            builder.append(getImportString());
            builder.append("public class " + className + "Test" + " extends TestCase " + Utills.SPLITE_LINE);
            builder.append("{" + Utills.SPLITE_LINE);
            builder.append(toto);
            builder.append("}" + Utills.SPLITE_LINE);
            System.out.println(builder.toString());
        }
        catch (SecurityException | ClassNotFoundException e2)
        {
            e2.printStackTrace();
        }
        finally
        {
            if (urlLoader != null)
            {
                try
                {
                    urlLoader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private String getImportString()
    {
        StringBuilder builder = new StringBuilder();
        for (String tempString : importMap.keySet())
        {
            builder.append("import " + tempString + ";" + Utills.SPLITE_LINE);
        }
        builder.append(Utills.SPLITE_LINE);
        return builder.toString();
    }
    
    private String printTest(URLClassLoader urlLoader, Class<?> thisClass, List<Method> methods)
    {
        StringBuilder builder = new StringBuilder();
        for (Method method : methods)
        {
            try
            {
                RandomObject obj = getRandomObject(thisClass, new ArrayList<String>());
                
                RandomObject[] methodParams = getRandomParameters(method.getParameterTypes(), new ArrayList<String>());
                Object[] paramArray = randomToObjectArray(methodParams);
                Object retObject = method.invoke(obj.object, paramArray);
                
                if (retObject != null)
                {
                    //------------------//
                    builder.append("public void test" + Utills.getCaptitor(method.getName()));
                    builder.append("()");
                    builder.append(Utills.SPLITE_LINE);
                    builder.append('{');
                    builder.append(Utills.SPLITE_LINE);
                    
                    //--------new Object---------//
                    builder.append(obj.print);
                    
                    //---------invoke method-----------//
                    builder.append(Utills.TAB_STRING);
                    String printString = ObjectToString(paramArray, method.getParameterTypes());
                    builder.append(method.getReturnType().getName() + " val=");
                    builder.append(className.toLowerCase() + "." + method.getName());
                    builder.append("(" + printString + ");");
                    builder.append(Utills.SPLITE_LINE);
                    
                    //-------Assert equal---------//
                    builder.append(Utills.TAB_STRING);
                    builder.append(method.getReturnType().getName() + " expected ="
                        + getcleanValue(retObject, method.getReturnType()) + ";");
                    builder.append(Utills.SPLITE_LINE);
                    builder.append(Utills.TAB_STRING);
                    builder.append("assertEquals( expected, val);");
                    builder.append(Utills.SPLITE_LINE);
                    builder.append('}');
                    builder.append(Utills.SPLITE_LINE);
                }
                builder.append(Utills.SPLITE_LINE);
                
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return builder.toString();
        
    }
    
    private String getPrimitiveClassName(Class<?> type)
    {
        StringBuilder builder = new StringBuilder();
        String typeName = null;
        
        SimpleClass simpleClass = SimpleClass.findByClass(type);
        if (simpleClass != null)
        {
            typeName = simpleClass.innerName;
        }
        
        if (typeName != null)
        {
            builder.append('(');
            builder.append(typeName);
            builder.append(')');
        }
        
        return builder.toString();
    }
    
    private String getcleanValue(Object object, Class<?> type)
    {
        StringBuilder builder = new StringBuilder();
        String format;
        if (type == Character.class || type == char.class)
        {
            format = "\'" + object + "\'";
        }
        else if (type == String.class)
        {
            format = "\"" + object + "\"";
        }
        else
        {
            format = object.toString();
        }
        
        builder.append(format);
        
        return builder.toString();
    }
    
    private String getFormalValue(Object object, Class<?> type)
    {
        StringBuilder builder = new StringBuilder();
        String format;
        if (type == Character.class || type == char.class)
        {
            format = "\'" + object + "\'";
        }
        else if (type == String.class)
        {
            format = "\"" + object + "\"";
        }
        else
        {
            format = object.toString();
        }
        
        builder.append(getPrimitiveClassName(type) + format);
        
        return builder.toString();
    }
    
    private String ObjectToString(Object[] parameters, Class<?>[] types)
    {
        StringBuilder builder = new StringBuilder();
        int iLoop = 0;
        for (Object object : parameters)
        {
            builder.append(getFormalValue(object, types[iLoop]));
            builder.append(',');
            iLoop++;
        }
        String ret = builder.toString();
        if (ret.length() > 0)
        {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }
    
    private RandomObject[] getRandomParameters(Class<?>[] parameterTypes, List<String> list)
    {
        RandomObject[] ret = new RandomObject[parameterTypes.length];
        int iLoop = 0;
        Random rand = new Random();
        for (Class<?> tempClass : parameterTypes)
        {
            SimpleClass simpleClass = SimpleClass.findByClass(tempClass);
            if (simpleClass != null)
            {
                ret[iLoop] = simpleClass.getRandomObject();
            }
            else if (tempClass == String.class)
            {
                RandomObject obj = new RandomObject();
                obj.object = Utills.getRandomString(rand);
                obj.name = "string" + obj.count;
                ret[iLoop] = obj;
            }
            else
            {
                RandomObject obj = getRandomObject(tempClass, list);
                ret[iLoop] = obj;
            }
            iLoop++;
        }
        return ret;
    }
    
    private RandomObject getRandomObject(Class<?> tempClass, List<String> list)
    {
        RandomObject ret = new RandomObject();
        Constructor<?>[] constructors = tempClass.getConstructors();
        Object obj = null;
        for (Constructor<?> con : constructors)
        {
            List<String> myList = new ArrayList<String>();
            RandomObject[] dd = getRandomParameters(con.getParameterTypes(), myList);
            try
            {
                Object[] jiji = randomToObjectArray(dd);
                obj = con.newInstance(jiji);
                if (obj != null)
                {
                    ret.object = obj;
                    ret.name = tempClass.getSimpleName().toLowerCase() + ret.count;
                    String print =
                        Utills.TAB_STRING + tempClass.getName() + " " + ret.name + " = new "
                            + tempClass.getSimpleName() + "(";
                    for (RandomObject ii : dd)
                    {
                        print += ii.name + ",";
                    }
                    if (dd.length > 0)
                    {
                        print = print.substring(0, print.length() - 1);
                        
                    }
                    print += ");";
                    
                    ret.print = Utills.TAB_STRING;
                    for (RandomObject ii : dd)
                    {
                        ret.print += ii.print + Utills.SPLITE_LINE;
                    }
                    ret.print += print + Utills.SPLITE_LINE;
                    importMap.put(tempClass.getName(), tempClass.getSimpleName());
                    break;
                }
                
            }
            catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e)
            {
                e.printStackTrace();
            }
            
        }
        return ret;
    }
    
    private Object[] randomToObjectArray(RandomObject[] dd)
    {
        Object[] jiji = new Object[dd.length];
        for (int jLoop = 0; jLoop < dd.length; jLoop++)
        {
            jiji[jLoop] = dd[jLoop].object;
        }
        return jiji;
    }
    
    private List<Method> getPublicMethods(URLClassLoader urlLoader, TypeDeclaration type, Class<?> thisClass)
        throws ClassNotFoundException
    {
        List<Method> ret = new ArrayList<Method>();
        MethodDeclaration[] methods = type.getMethods();
        for (MethodDeclaration methodDeclare : methods)
        {
            String name1 = methodDeclare.getName().getFullyQualifiedName();
            if (name1.equals(className))
            {
                continue;
            }
            boolean isPublic = Modifier.isPublic(methodDeclare.getModifiers());
            if (isPublic)
            {
                List<?> paramList = methodDeclare.parameters();
                Class<?>[] parameterClasses = TranslateNodeType(urlLoader, paramList);
                Method method;
                try
                {
                    method = thisClass.getMethod(methodDeclare.getName().getFullyQualifiedName(), parameterClasses);
                    ret.add(method);
                }
                catch (NoSuchMethodException | SecurityException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }
    
    private Class<?>[] TranslateNodeType(URLClassLoader urlLoader, List<?> paramList)
        throws ClassNotFoundException
    {
        Class<?>[] array = new Class<?>[paramList.size()];
        int iLoop = 0;
        for (Object obj : paramList)
        {
            if (obj instanceof SingleVariableDeclaration)
            {
                SingleVariableDeclaration declare = (SingleVariableDeclaration)obj;
                Type type = declare.getType();
                if (type.isPrimitiveType())
                {
                    PrimitiveType primitive = (PrimitiveType)type;
                    Code code = primitive.getPrimitiveTypeCode();
                    SimpleClass simpleClass = SimpleClass.findByCode(code);
                    if (simpleClass != null)
                    {
                        array[iLoop] = simpleClass.classPrimitive;
                    }
                }
                else if (type.isSimpleType())
                {
                    SimpleType simpleType = (SimpleType)type;
                    String typeName = simpleType.getName().getFullyQualifiedName();
                    SimpleClass simpleClass = SimpleClass.findByName(typeName);
                    if (simpleClass != null)
                    {
                        array[iLoop] = simpleClass.classWrap;
                    }
                    else if (typeName.equals("String"))
                    {
                        array[iLoop] = String.class;
                    }
                    else
                    {
                        String classFullName = typeName;
                        classFullName = AstUtills.getSimpleTypeFullName(simpleType);
                        array[iLoop] = urlLoader.loadClass(classFullName);
                    }
                }
            }
            iLoop++;
        }
        
        return array;
    }
    
}
