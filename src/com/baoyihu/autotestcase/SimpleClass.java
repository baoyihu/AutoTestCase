package com.baoyihu.autotestcase;

import java.util.Random;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

public class SimpleClass
{
    public static Random rand = new Random();
    
    public String innerName;
    
    public String wrapName;
    
    public Class<?> classPrimitive;
    
    public Class<?> classWrap;
    
    public Code code;
    
    public SimpleClass()
    {
        init();
    }
    
    public void init()
    {
        
    }
    
    public RandomObject getRandomObject()
    {
        return null;
    }
    
    public static final SimpleClass INT = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "int";
            wrapName = "Integer";
            classPrimitive = int.class;
            classWrap = Integer.class;
            code = PrimitiveType.INT;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = rand.nextInt();
            ret.name = "int" + ret.count;
            ret.print = "int " + ret.name + "= " + ret.object.toString() + ";";
            return ret;
        }
    };
    
    public static final SimpleClass CHAR = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "char";
            wrapName = "Character";
            classPrimitive = char.class;
            classWrap = Character.class;
            code = PrimitiveType.CHAR;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = Utills.getRandomChar(rand);
            ret.name = "char" + ret.count;
            return ret;
            
        }
    };
    
    public static final SimpleClass BOOLEAN = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "boolean";
            wrapName = "Boolean";
            classPrimitive = boolean.class;
            classWrap = Boolean.class;
            code = PrimitiveType.BOOLEAN;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = rand.nextBoolean();
            ret.name = "boolean" + ret.count;
            return ret;
            
        }
    };
    
    public static final SimpleClass SHORT = new SimpleClass()
    {
        
        @Override
        public void init()
        {
            innerName = "short";
            wrapName = "Short";
            classPrimitive = short.class;
            classWrap = Short.class;
            code = PrimitiveType.SHORT;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = (short)rand.nextInt(Short.MAX_VALUE);
            ret.name = "short" + ret.count;
            return ret;
        }
    };
    
    public static final SimpleClass LONG = new SimpleClass()
    {
        
        @Override
        public void init()
        {
            innerName = "long";
            wrapName = "Long";
            classPrimitive = long.class;
            classWrap = Long.class;
            code = PrimitiveType.LONG;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = rand.nextLong();
            ret.name = "long" + ret.count;
            return ret;
        }
    };
    
    public static final SimpleClass FLOAT = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "float";
            wrapName = "Float";
            classPrimitive = float.class;
            classWrap = Float.class;
            code = PrimitiveType.FLOAT;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = rand.nextFloat();
            ret.name = "float" + ret.count;
            return ret;
        }
    };
    
    public static final SimpleClass DOUBLE = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "double";
            wrapName = "Double";
            classPrimitive = double.class;
            classWrap = Double.class;
            code = PrimitiveType.DOUBLE;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = rand.nextDouble();
            ret.name = "double" + ret.count;
            return ret;
        }
    };
    
    public static final SimpleClass BYTE = new SimpleClass()
    {
        @Override
        public void init()
        {
            innerName = "byte";
            wrapName = "Byte";
            classPrimitive = byte.class;
            classWrap = Byte.class;
            code = PrimitiveType.BYTE;
        }
        
        @Override
        public RandomObject getRandomObject()
        {
            RandomObject ret = new RandomObject();
            ret.object = (byte)rand.nextInt(Byte.MAX_VALUE);
            ret.name = "byte" + ret.count;
            return ret;
        }
    };
    
    static SimpleClass[] array = new SimpleClass[] {SimpleClass.INT, SimpleClass.CHAR, SimpleClass.BOOLEAN,
        SimpleClass.SHORT, SimpleClass.LONG, SimpleClass.FLOAT, SimpleClass.DOUBLE, SimpleClass.BYTE};
    
    public static SimpleClass findByName(String input)
    {
        for (SimpleClass temp : array)
        {
            if (temp.wrapName.equals(input) || temp.innerName.equals(input))
            {
                return temp;
            }
        }
        return null;
    }
    
    public static SimpleClass findByClass(Class<?> input)
    {
        for (SimpleClass temp : array)
        {
            if (temp.classPrimitive == input || temp.classWrap == input)
            {
                return temp;
            }
        }
        return null;
    }
    
    public static SimpleClass findByCode(Code input)
    {
        for (SimpleClass temp : array)
        {
            if (temp.code == input)
            {
                return temp;
            }
        }
        return null;
    }
    
}
