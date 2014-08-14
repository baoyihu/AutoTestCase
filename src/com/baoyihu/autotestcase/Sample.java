package com.baoyihu.autotestcase;

public class Sample
{
    
    private int inner = 0;
    
    public Sample(int par)
    {
        inner = par;
    }
    
    public int plus(int input2)
    {
        
        return inner + input2;
    }
    
    public char upper(char input)
    {
        
        return (char)(input + 2);
    }
    
    public String stringUpper(String input)
    {
        
        return input.toUpperCase();
    }
    
    public Integer plus1(int input2)
    {
        
        return inner + input2;
    }
    
    public int plus2(Integer input2)
    {
        
        return inner + input2;
    }
    
    public Character char1(char cc)
    {
        return (char)(cc + 1);
    }
    
    public char char2(Character cc)
    {
        return (char)(cc + 1);
    }
    
    public Boolean bool1(boolean in)
    {
        return !in;
    }
    
    public boolean bool2(Boolean in)
    {
        return !(in);
    }
    
    public short short1(Short in)
    {
        return (short)(in + 1);
    }
    
    public Short short2(short in)
    {
        return in = 1;
    }
}
