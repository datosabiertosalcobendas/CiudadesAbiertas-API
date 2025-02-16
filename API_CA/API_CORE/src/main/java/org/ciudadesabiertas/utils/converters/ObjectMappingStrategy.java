/**
 * Copyright 2019 Ayuntamiento de A Coruña, Ayuntamiento de Madrid, Ayuntamiento de Santiago de Compostela, Ayuntamiento de Zaragoza, Entidad Pública Empresarial Red.es
 * 
 * This file is part of the Open Cities API, developed within the "Ciudades Abiertas" project (https://ciudadesabiertas.es/).
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package org.ciudadesabiertas.utils.converters;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jena.atlas.logging.Log;
import org.ciudadesabiertas.utils.Util;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 * @param <T>
 */
public class ObjectMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

	@Override
	public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

		//Este array es para tener en cuenta los campos que pueden o no existir (x e y)
		//Siempre en último lugar
		List<Integer> positionsToDelete=new ArrayList<Integer>();
		final int numColumns = getAnnotatedFields(bean);	
		super.setColumnMapping(new String[numColumns]);		
		
		if (!isAnnotationDriven() || numColumns == -1) {
			return super.generateHeader(bean);
		}

		String[] header = new String[numColumns];		

		BeanField<T> beanField;
		int contadorColumnsheader=0;
		for (int i = 0; i <= numColumns; i++) {
			beanField = findField(i);
			if (beanField!=null && isFieldAnnotated(beanField.getField())) {
								
				String columnHeaderName = "columna_"+i;
				header[contadorColumnsheader] = columnHeaderName;
				contadorColumnsheader++;
			
			}
		}
		
		for (Integer position:positionsToDelete)
		{
			header = ArrayUtils.removeElement(header, position.intValue());
		}
	
		if (positionsToDelete.size()>0)
		{
			header = Arrays.copyOf(header, header.length - positionsToDelete.size());
		}
		
		return header;
	}
	
	
	@Override
    public String[] transmuteBean(T bean) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        List<String> transmutedBean=new ArrayList<String>(); 
        //array
        if (bean instanceof Object[])
        {
        	Object[] campos=(Object[]) bean;
 	        int numColumns = campos.length;        
 	        for (int i=0;i<numColumns;i++)
 	        { 	        
 	        	transmutedBean.add(CSVConverter.checkTypesAndReturnString(campos[i]));
 	        } 
        }
        //un solo objecto
        else if (bean instanceof Object)
        {
        	transmutedBean.add(CSVConverter.checkTypesAndReturnString(bean));

        }
        return transmutedBean.toArray(new String[0]);
    }

    private List<String> writeWithReflection(T bean, int numColumns)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        BeanField<T> firstBeanField, subsequentBeanField;
        Object firstIndex, subsequentIndex;
        List<String> contents = new ArrayList<>(numColumns > 0 ? numColumns : 0);
        
        for(int i = 0; i < numColumns;) {
            
            // Determine the first value
            firstBeanField = findField(i);            
            firstIndex = chooseMultivaluedFieldIndexFromHeaderIndex(i);
            String[] fields = firstBeanField != null
                    ? firstBeanField.write(bean, firstIndex)
                    : ArrayUtils.EMPTY_STRING_ARRAY;
            
            if(fields.length == 0) {
                
                // Write the only value   
            	//CMG: El primer valor de la cabecera es el ignore Anotation no debe incluirse            	
            	if (i>0)
                contents.add(StringUtils.EMPTY);
                i++; // Advance the index
            }
            else {
                String field_aux=fields[0];
                if (field_aux!=null) {
                	field_aux=field_aux.replace("\"","'");
                }else {
                	field_aux="";
                }
                
                contents.add(StringUtils.defaultString(field_aux));
                
                	
                
                
                // Now write the rest
                // We must make certain that we don't write more fields
                // than we have columns of the correct type to cover them
                int j = 1;
                int displacedIndex = i+j;
                subsequentBeanField = findField(displacedIndex);                
                subsequentIndex = chooseMultivaluedFieldIndexFromHeaderIndex(displacedIndex);
                while(j < fields.length
                        && displacedIndex < numColumns
                        && Objects.equals(firstBeanField, subsequentBeanField)
                        && Objects.equals(firstIndex, subsequentIndex)) {
                    // This field still has a header, so add it
                    contents.add(StringUtils.defaultString(fields[j]));
                    
                    // Prepare for the next loop through
                    displacedIndex = i + (++j);
                    subsequentBeanField = findField(displacedIndex);
                    subsequentIndex = chooseMultivaluedFieldIndexFromHeaderIndex(displacedIndex);
                }
                
                i = displacedIndex; // Advance the index
                
                // And here's where we fill in any fields that are missing to
                // cover the number of columns of the same type
                if(i < numColumns) {
                    subsequentBeanField = findField(i);
                    subsequentIndex = chooseMultivaluedFieldIndexFromHeaderIndex(i);
                    while(Objects.equals(firstBeanField, subsequentBeanField)
                            && Objects.equals(firstIndex, subsequentIndex)
                            && i < numColumns) {
                        contents.add(StringUtils.EMPTY);
                        subsequentBeanField = findField(++i);
                        subsequentIndex = chooseMultivaluedFieldIndexFromHeaderIndex(i);
                    }
                }
            }
            
        }
        return contents;
    }
	
    
    private List<String> writeWithIntrospection(T bean, int numColumns) {
        PropertyDescriptor desc;
        List<String> contents = new ArrayList<>(numColumns > 0 ? numColumns : 0);
        for(int i = 0; i < numColumns; i++) {
            try {
                desc = findDescriptor(i);
                Object o = desc != null ? desc.getReadMethod().invoke(bean, (Object[]) null) : null;
                contents.add(Objects.toString(o, ""));
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                CsvBeanIntrospectionException csve = new CsvBeanIntrospectionException(
                        bean, null, ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("error.introspecting.beans"));
                csve.initCause(e);
                throw csve;
            }
        }
        return contents;
    }
//	@Override
//    protected Map<String, PropertyDescriptor> loadDescriptorMap() throws IntrospectionException {
//		Map<String, PropertyDescriptor> map = new HashMap<>(); 
//		map = super.loadDescriptorMap();
//		if (map!=null) {
//			map.remove("ID");
//		}
//        return map;
//    }

	private int getAnnotatedFields(T bean) {
		
		return (int) Arrays.stream(FieldUtils.getAllFields(bean.getClass())).filter(this::isFieldAnnotated).count();
	}

	private boolean isFieldAnnotated(Field f) {
		boolean isFAnnotated = f.isAnnotationPresent(CsvBindByName.class) || f.isAnnotationPresent(CsvCustomBindByName.class);
		return isFAnnotated;
	}

	private String extractHeaderName(final BeanField beanField) {
		if (beanField == null || beanField.getField() == null) {
			return StringUtils.EMPTY;
		}

		Field field = beanField.getField();

		if (field.getDeclaredAnnotationsByType(CsvBindByName.class).length != 0) {
			final CsvBindByName bindByNameAnnotation = field.getDeclaredAnnotationsByType(CsvBindByName.class)[0];
			return bindByNameAnnotation.column();
		}

		if (field.getDeclaredAnnotationsByType(CsvCustomBindByName.class).length != 0) {
			final CsvCustomBindByName bindByNameAnnotation = field
					.getDeclaredAnnotationsByType(CsvCustomBindByName.class)[0];
			return bindByNameAnnotation.column();
		}

		return StringUtils.EMPTY;
	}
    
	
	
}