package com.baoyihu.autotestcase;

import java.lang.annotation.Target;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "config", strict = false)
public class Config
{
    @Element(name = "source", required = true)
	String source;
    @ElementList(name = "binaries", required = true)    
    List<String> binaryList;
}
