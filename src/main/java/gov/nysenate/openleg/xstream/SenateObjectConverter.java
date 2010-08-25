package gov.nysenate.openleg.xstream;

import gov.nysenate.openleg.model.SenateObject;

import java.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.mapper.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.TreeMarshaller;

/**
 * 
 * @author graylin
 *
 *	Converter to handle some of the issues that came up when serializing
 *	the SenateObjects using the built in XStreamConverters. This converter
 *	implementation will handle anything implementing SenateObject and
 *	should support all of the available XStream Annotations.
 *
 *	Additionally, having our own converter will allow us to integrate any
 *	annotations that we want into the XML output and customize the effects
 *	that they have.
 *	
 */
public class SenateObjectConverter implements Converter {

	private Mapper mapper;
	private boolean isJson;
	
	public SenateObjectConverter(Mapper mapper,boolean isJson) {
		this.mapper = mapper;
		this.isJson = isJson;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
					
		//Get the FieldAliasMapper, responsible for XStreamOmitField and XStreamAlias mark up
		FieldAliasingMapper aliasMapper = (FieldAliasingMapper) this.mapper.lookupMapperOfType(FieldAliasingMapper.class);
		
		//Get the AttributeMapper, responsible for XStreamAsAttribute mark up
		AttributeMapper attributeMapper = (AttributeMapper) this.mapper.lookupMapperOfType(AttributeMapper.class);
		
		//Get the LocalConversionMapper, responsible for XStreamConverter mark up
		LocalConversionMapper conversionMapper = (LocalConversionMapper) this.mapper.lookupMapperOfType(LocalConversionMapper.class);
		
		SenateObject obj = (SenateObject) value;
		Class c = obj.getClass();
		
		List<Field> attributes = new ArrayList();
		List<Field> children = new ArrayList();
		for (Field f: c.getDeclaredFields()) {
			if (f.getName().startsWith("jdo") == false && 			//Exclude fields declared by JDO
				Modifier.isStatic(f.getModifiers()) == false &&		//Exclude static fields
				aliasMapper.shouldSerializeMember(c, f.getName()))	//Exclude fields marked by XStreamOmitField
			{
				//Ignore attributes for json, add the annotations for xml
				if (this.isJson == false && attributeMapper.shouldLookForSingleValueConverter(f.getName(), f.getType(), c)==true)
					attributes.add(f);
				else
					children.add(f);
			}
		}
		
		for (Field f: attributes) {
			
			//Get value of the field, even if private
			Object fieldValue = getValue(f,obj);
			
			//Get field name, gets the XStreamAlias if they've used it
			String alias = aliasMapper.serializedMember(c, f.getName());
			
			//Get the value from XStreamConverter if they've used it
			SingleValueConverter converter = conversionMapper.getConverterFromAttribute(c, f.getName(),f.getClass());
			writeAttribute(alias,fieldValue,writer,context,converter);
		}
		
		for (Field f: children) {
						
			//Get value of the field, even if private
			Object fieldValue = getValue(f,obj);
			
			//Get field name, gets the XStreamAlias if they've used it
			String alias = aliasMapper.serializedMember(c, f.getName());
			
			//Get the XStreamCollectionAlias annotation if they've used it
			XStreamCollectionAlias annotation = f.getAnnotation(XStreamCollectionAlias.class);
															
			//Get the value from XStreamConverter if they've used it
			Converter converter = conversionMapper.getLocalConverter(c, f.getName());
			
			//Get the (result of the) XStreamImplicit annotation if they've used it
			Mapper.ImplicitCollectionMapping implicitMapper = this.mapper.getImplicitCollectionDefForFieldName(c, f.getName());
			
			
			if(annotation != null) {
				writeCollectionAliased(annotation,fieldValue,writer,context,converter);
			}
			else if (implicitMapper!=null) {
				String fieldName = implicitMapper.getItemFieldName();
				if (fieldName!=null)
					alias = fieldName;
				writeImplicit(alias,fieldValue,writer,context,converter);
			}
			else {
				writeNode(alias,fieldValue,writer,context,converter);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeCollectionAliased(XStreamCollectionAlias annotation, Object field, HierarchicalStreamWriter writer, MarshallingContext context, Converter converter) {
		Collection collection = (Collection) field;
		writer.startNode(annotation.node());
		if(collection !=null) {
			for (Object obj: collection) {
				writeNode(annotation.value(),obj,writer,context,converter);
			}
		}
		
		writer.endNode();
	}
	
	@SuppressWarnings("unchecked")
	private void writeImplicit(String itemName, Object field, HierarchicalStreamWriter writer, MarshallingContext context, Converter converter) {
		Collection collection = (Collection) field;
		if(collection !=null) {
			for (Object obj : collection) {
				writeNode(itemName,obj,writer,context,converter);
			}
		}
	}
	
	private void writeNode(String alias, Object value, HierarchicalStreamWriter writer, MarshallingContext context, Converter converter) {
		writer.startNode(alias);
		if (value!=null) {
			try {
				if (converter != null) {
					if (converter.canConvert(value.getClass())) {
						converter.marshal(value, writer, context);
					}
					else {
						throw new ConversionException("Cannot convert "+value.getClass()+" with converter "+converter.getClass());
					}
				}
				else {
					context.convertAnother(value);
				}
			}
			catch (TreeMarshaller.CircularReferenceException e) {
				writer.setValue("Circular Referencing");
			}
		}
		else {
//			writer.setValue("null");
		}
		writer.endNode();
	}
	
	private void writeAttribute(String alias, Object value, HierarchicalStreamWriter writer, MarshallingContext context, SingleValueConverter converter) {
		if (converter != null) {
			if (converter.canConvert(value.getClass())) {
				writer.addAttribute(alias, converter.toString(value));	
			}
			else {
				throw new ConversionException("Cannot convert "+value.getClass()+" with SingleValueConverter "+converter.getClass());
			}
		}
		else {
			writer.addAttribute(alias, (value != null) ? value.toString():"");
		}
	}
	
	private Object getValue(Field f,Object obj) {
		boolean old = f.isAccessible();
		f.setAccessible(true);
		try {
			return f.get(obj);
		}
		catch (IllegalArgumentException e) {
			return "IllegalArgumentException";
		}
		catch (IllegalAccessException e) {
			return "IllegalAccessException";
		}
		finally {
			f.setAccessible(old);
		}
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		// TODO Auto-generated method stub
		return SenateObject.class.isAssignableFrom(clazz);
	}

}
