package com.baoyihu.autotestcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Utills
{
    public static final String SPLITE_LINE = "\r\n";
    
    public static final int SPLITE_LENTH = 2;
    
    private static final char SPLITE = '\\';
    
    public static final String TAB_STRING = "    ";
    
    public static boolean createDirs(String dir)
    {
        File file = new File(dir);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return true;
    }
    
    public enum MyColor
    {
        RED, BLUE, BLACK
    }
    
    public static boolean deleteDirIfEmpty(String dir)
    {
        File file = new File(dir);
        if (file.exists() && file.list().length == 0)
        {
            if (file.delete())
            {
                String parent = dir.substring(0, dir.lastIndexOf(SPLITE));
                return deleteDirIfEmpty(parent);
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
    
    public static boolean copyFile(String source, String dest)
    {
        String buffer = readFile(source);
        return writeFiles(dest, buffer);
        
    }
    
    public static boolean writeFiles(String name, String buffer)
    {
        String dir = name.substring(0, name.lastIndexOf(SPLITE));
        createDirs(dir);
        File file = new File(name);
        if (buffer == null)
        {
            file.delete();
            return true;
        }
        OutputStreamWriter writer = null;
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            writer.write(buffer);
            writer.flush();
            return true;
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
        }
        return false;
    }
    
    public static String readFile(String name)
    {
        File file = new File(name);
        if (!file.exists())
            return null;
        
        InputStreamReader reader = null;
        try
        {
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            char[] buffer = new char[1024];
            int count = 0;
            StringBuilder builder = new StringBuilder();
            while ((count = reader.read(buffer, 0, 1024)) > 0)
            {
                builder.append(buffer, 0, count);
            }
            return builder.toString();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
        }
        return null;
    }
    
    public static List<String> findAllClassPath(String dirPath, String[] typeArray)
    {
        List<String> ret = new ArrayList<String>();
        
        if (dirPath == null)
            return ret;
        
        if (!dirPath.endsWith("\\"))
        {
            dirPath = dirPath + SPLITE;
        }
        
        File dirFile = new File(dirPath);
        if (dirFile.exists())
        {
            String[] filesInDir = dirFile.list();
            for (String tempString : filesInDir)
            {
                File tempFile = new File(dirPath + tempString);
                if (tempFile.isDirectory())
                {
                    ret.addAll(findAllClassPath(dirPath + tempString + SPLITE, typeArray));
                }
                else
                {
                    if (endWithIgnoreCase(tempString, typeArray))
                    {
                        ret.add(dirPath + tempString);
                    }
                }
            }
        }
        
        return ret;
    }
    
    private static boolean endWithIgnoreCase(String str, String[] typeArray)
    {
        if (typeArray == null || typeArray.length == 0 || str == null)
        {
            return false;
        }
        for (String flag : typeArray)
        {
            int len = flag.length();
            if (str.length() >= len)
            {
                String temp = str.substring(str.length() - len);
                boolean ret = temp.equalsIgnoreCase(flag);
                if (ret)
                {
                    return true;
                }
            }
        }
        return false;
        
    }
    
    public static void addResourceFrom(String source, String dest)
    {
        String[] typeArray = new String[] {".9.png", ".png", ".bmp", "jpg", "gif"};
        List<String> sourceList = Utills.findAllClassPath(source, typeArray);
        List<String> destList = Utills.findAllClassPath(dest, typeArray);
        for (String temp : sourceList)
        {
            String targetStr = temp.replace(source, dest);
            if (!destList.contains(targetStr))
            {
                copyFile(temp, targetStr);
            }
        }
    }
    
    public static void removeUnusedResource(String dir)
    {
        List<String> pictureList = Utills.findAllClassPath(dir, new String[] {".9.png", ".png", ".bmp", "jpg", "gif"});
        Map<String, List<String>> pictureMap = new HashMap<String, List<String>>();
        for (String temp : pictureList)
        {
            String name = temp.substring(temp.lastIndexOf('\\') + 1);
            name = name.substring(0, name.indexOf('.'));
            if (name != null)
            {
                if (!pictureMap.containsKey(name))
                {
                    pictureMap.put(name, new ArrayList<String>());
                }
                pictureMap.get(name).add(temp);
            }
        }
        
        List<String> classPaths = Utills.findAllClassPath(dir, new String[] {".java"});
        List<String> xmlPaths = Utills.findAllClassPath(dir, new String[] {".xml"});
        filterMapWithFile(pictureMap, "R.drawable.", classPaths);
        filterMapWithFile(pictureMap, "R.raw.", classPaths);
        filterMapWithFile(pictureMap, "@drawable/", xmlPaths);
        filterMapWithFile(pictureMap, "@raw/", xmlPaths);
        for (Entry<String, List<String>> entry : pictureMap.entrySet())
        {
            for (String filePath : entry.getValue())
            {
                new File(filePath).delete();
                System.out.println("deal end :" + filePath + " Removed!");
            }
        }
        System.out.println("Remove Unused Resource finished!!!!");
    }
    
    private static void filterMapWithFile(Map<String, List<String>> pictureMap, String preFix, List<String> classPaths)
    {
        for (String temp : classPaths)
        {
            String source = Utills.readFile(temp);
            Iterator<Map.Entry<String, List<String>>> it = pictureMap.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<String, List<String>> entry = it.next();
                String flag = preFix + entry.getKey();
                if (source.contains(flag))
                {
                    it.remove();
                }
            }
        }
    }
    
    public static String getSimpleName(String in)
    {
        if (in == null)
        {
            return null;
        }
        int index = in.lastIndexOf('.');
        
        if (index < 0)
        {
            return null;
        }
        return in.substring(index + 1);
        
    }
    
    public static String generatePropertyParentList(String type, List<String> checkedClass,
        Map<String, String> classParentMapping)
    {
        StringBuilder builder = new StringBuilder();
        String tabString = TAB_STRING;
        String extendsFrom = "extends from : ";
        while (type != null)
        {
            builder.append(tabString + extendsFrom + type + SPLITE_LINE);
            if (checkedClass.contains(type))
            {
                //TODO do we only need to add check here?                
                return builder.toString();
            }
            type = classParentMapping.get(type);
            tabString += TAB_STRING;
        }
        return "";
    }
    
    public static String getRandomString(Random random)
    {
        char[] buffer = new char[10];
        for (int jLoop = 0; jLoop < buffer.length; jLoop++)
        {
            buffer[jLoop] = getRandomChar(random);
        }
        return String.valueOf(buffer);
    }
    
    public static char getRandomChar(Random random)
    {
        int d = random.nextInt(57);
        while (d <= 31 && d >= 26)
        {
            d = random.nextInt(57);
        }
        return (char)('A' + d);
    }
    
    public static String getCaptitor(String input)
    {
        String ret = null;
        
        if (input.length() > 1)
        {
            ret = input.substring(0, 1).toUpperCase() + input.substring(1);
        }
        else
        {
            ret = input;
        }
        return ret;
    }
    
}
