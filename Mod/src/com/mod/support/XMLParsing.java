package com.mod.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.AnnotationMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XMLParsing {

	private static final XmlFriendlyNameCoder XML_FRIENDLY_REPLACER = new XmlFriendlyNameCoder("_", "_");
	private static final XStream X_STREAM = new XStream(new XppDriver(XML_FRIENDLY_REPLACER)){
		
		@Override
        protected MapperWrapper wrapMapper(MapperWrapper next)
        {
            AnnotationMapper mapper = new AnnotationMapper(next,(ConverterRegistry) this.getConverterLookup(),getClassLoader(), this.getReflectionProvider(), new JVM());

            return new MapperWrapper(mapper)
            {

                @Override
                public boolean shouldSerializeMember(Class definedIn, String fieldName)
                {
                    return definedIn != Object.class ? super.shouldSerializeMember(definedIn, fieldName) : false;
                }
                
                
            };
        } 
	};
	
	static{
		X_STREAM.processAnnotations(ConfigData.class);
	}
	
	
	public static final String getXML(Object data){
		return X_STREAM.toXML(data);
	}

	public static final Object getObject(String xml){
		return X_STREAM.fromXML(xml);
	}
	
	public static ConfigData readAppConfig(String location){
		BufferedReader reader =  null;
		StringBuilder builder = new StringBuilder();
		ConfigData configData = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(location)),"UTF-8"));
			
			String str = null;
			while((str=reader.readLine())!=null){
				if(!str.contains("#comment")){
					builder.append(str);
				}
			}
			str=null;
			reader.close();
			
			configData = (ConfigData)getObject(builder.toString());
			
			
			//readPropertiesFile(location);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			System.out.println("Could not find config files in location :"+location);
			return null;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		} catch (Exception e){
			e.printStackTrace();
				try {
					if(reader!=null){
						reader.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			return null;		
		}
		return configData;
	}
	
	
	public static void main(String[] args) {
		ConfigData data = readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/App.config");
		System.out.println();
	}

}
