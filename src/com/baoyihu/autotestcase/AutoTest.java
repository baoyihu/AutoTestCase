package com.baoyihu.autotestcase;

import java.util.List;

import org.simpleframework.xml.core.PersistenceException;

public class AutoTest
{
    
    private String sourceRoot = null;
    
    private List<String> binList = null;
    
    public static void main(String[] args)
    {
        String configPath = args[0];
        if (configPath == null || configPath.isEmpty() || !configPath.toLowerCase().endsWith("xml"))
        {
            System.err.println("Config File is empty or not xml");
        }
        else
        {
            AutoTest test = new AutoTest(args[0]);
            test.doJob();
        }
    }
    
    public AutoTest(String input1)
    {
        this.sourceRoot = input1;
        String configbuffer = Utills.readFile(input1);
        try
        {
            Config config = SerializerService.fromXml(Config.class, configbuffer);
            this.binList = config.binaryList;
            this.sourceRoot = config.source;
        }
        catch (PersistenceException e)
        {
            
            e.printStackTrace();
        }
        
        if (!sourceRoot.endsWith("\\"))
        {
            sourceRoot = sourceRoot + "\\";
        }
        
    }
    
    public void doJob()
    {
        String desPath = null;
        List<String> javaPaths = Utills.findAllClassPath(sourceRoot, new String[] {"java"});
        for (String path : javaPaths)
        {
            System.out.println("sourcePath:" + path);
            ParseClass parser = new ParseClass(path, desPath, binList);
            parser.doJob();
            
        }
    }
    
}
