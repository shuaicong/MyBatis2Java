package com.mybatis.mybatis2java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  自动生成MyBatis实体映射XML文件、Mapper

 */
public class MyBatis2SpringBoot {
	
	private final String author = "alter";
 
    private final String type_char = "char";
 
    private final String type_date = "date";
 
    private final String type_timestamp = "timestamp";
 
    private final String type_int = "int";
 
    private final String type_bigint = "bigint";
 
    private final String type_text = "text";
 
    private final String type_bit = "bit";
 
    private final String type_decimal = "decimal";
 
    private final String type_blob = "blob";
    
    private final String type_double = "double";
    
    private final String type_float = "float";
 
 
    private final String moduleName = "alter"; // 对应模块名称（根据自己模块做相应调整!!!务必修改^_^）
 
    private final String bean_path = "d:/entity_model";
 
    private final String mapper_path = "d:/entity_dao";
    
    private final String service_path = "d:/entity_service";
    
    private final String service_impl_path = "d:/entity_service/impl";
 
    private final String xml_path = "d:/entity_dao/xml";
 
    private final String bean_package = "com." + moduleName + ".model";
 
    private final String mapper_package = "com." + moduleName + ".dao";
    
    private final String service_package = "com." + moduleName + ".service";
    
    private final String service_package_impl = "com." + moduleName + ".service.impl";
    
    private final String driverName = "com.mysql.jdbc.Driver";
 
    private final String user = "root";
 
    private final String password = "root";
 
    private final String url = "jdbc:mysql://localhost:3306/renren-security-boot?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";
 
    private String tableName = null;
 
    private String beanName = null;
 
    private String mapperName = null;
    
    private String serviceName = null;
    
    private String serviceNameImpl = null;
 
    private Connection conn = null;
    
    List<String> beanList = new ArrayList<>();
 
 
    private void init() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        conn = DriverManager.getConnection(url, user, password);
    }
 
 
    /**
     *  获取所有的表
     *
     * @return
     * @throws SQLException 
     */
    private List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<String>();
        PreparedStatement pstate = conn.prepareStatement("show tables");
        ResultSet results = pstate.executeQuery();
        while ( results.next() ) {
            String tableName = results.getString(1);
            //          if ( tableName.toLowerCase().startsWith("yy_") ) {
            tables.add(tableName);
            //          }
        }
        return tables;
    }
 
 
    private void processTable( String table ) {
        StringBuffer sb = new StringBuffer(table.length());
        String tableNew = table.toLowerCase();
        String[] tables = tableNew.split("_");
        String temp = null;
        for ( int i = 0 ; i < tables.length ; i++ ) {
            temp = tables[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        beanName = sb.toString();
        mapperName = beanName + "Mapper";
        serviceName = beanName + "Service";
        serviceNameImpl = beanName + "ServiceImpl";
        beanList.add(beanName);
    }
 
 
    private String processType( String type ) {
        if ( type.indexOf(type_char) > -1 ) {
            return "String";
        } else if ( type.indexOf(type_bigint) > -1 ) {
            return "Long";
        } else if ( type.indexOf(type_int) > -1 ) {
            return "Integer";
        } else if ( type.indexOf(type_date) > -1 ) {
            return "java.util.Date";
        } else if ( type.indexOf(type_text) > -1 ) {
            return "String";
        } else if ( type.indexOf(type_timestamp) > -1 ) {
            return "java.util.Date";
        } else if ( type.indexOf(type_bit) > -1 ) {
            return "Boolean";
        } else if ( type.indexOf(type_decimal) > -1 ) {
            return "java.math.BigDecimal";
        } else if ( type.indexOf(type_blob) > -1 ) {
            return "byte[]";
        } else if ( type.indexOf(type_double) > -1 ) {
        	return "double";
        } else if ( type.indexOf(type_float) > -1 ) {
        	return "float";
        } 
        return null;
    }
 
 
    private String processField( String field ) {
        StringBuffer sb = new StringBuffer(field.length());
        //field = field.toLowerCase();
        String[] fields = field.split("_");
        String temp = null;
        sb.append(fields[0]);
        for ( int i = 1 ; i < fields.length ; i++ ) {
            temp = fields[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        return sb.toString();
    }
 
 
    /**
     *  将实体类名首字母改为小写
     *
     * @param beanName
     * @return 
     */
    private String processResultMapId( String beanName ) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }
 
 
    /**
     *  构建类上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException 
     */
    private BufferedWriter buildClassComment( BufferedWriter bw, String text ) throws IOException {
        bw.newLine();
        bw.newLine();
        bw.write("/**");
        bw.newLine();
        bw.write(" * " + text);
        bw.newLine();
        bw.write(" * @author "+author);
        bw.newLine();
        bw.write(" * @date "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        bw.newLine();
        bw.write(" **/");
        return bw;
    }
 
 
    /**
     *  构建方法上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException 
     */
    private BufferedWriter buildMethodComment( BufferedWriter bw, String text ) throws IOException {
        bw.newLine();
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * ");
        bw.newLine();
        bw.write("\t * " + text);
        bw.newLine();
        bw.write("\t * ");
        bw.newLine();
        bw.write("\t **/");
        return bw;
    }
 
 
    /**
     *  生成实体类
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException 
     */
    private void buildEntityBean( List<String> columns, List<String> types, List<String> comments, String tableComment )
        throws IOException {
        File folder = new File(bean_path);
        if ( !folder.exists() ) {
            folder.mkdir();
        }
 
        File beanFile = new File(bean_path, beanName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile),"UTF-8"));
        bw.write("package " + bean_package + ";");
        bw.newLine();
        //bw.write("import lombok.Data;");
        //      bw.write("import javax.persistence.Entity;");
        bw = buildClassComment(bw, tableComment);
        bw.newLine();
        //      bw.write("@Entity");
        //bw.write("@Data");
        //bw.newLine();
        bw.write("public class " + beanName + " {");
        bw.newLine();
        bw.newLine();
        int size = columns.size();
        for ( int i = 0 ; i < size ; i++ ) {
            bw.write("\t/**" + comments.get(i) + "**/");
            bw.newLine();
            bw.write("\tprivate " + processType(types.get(i)) + " " + processField(columns.get(i)) + ";");
            bw.newLine();
            bw.newLine();
        }
        bw.newLine();
        // 生成get 和 set方法
        String tempField = null;
        String _tempField = null;
        String tempType = null;
        for ( int i = 0 ; i < size ; i++ ) {
            tempType = processType(types.get(i));
            _tempField = processField(columns.get(i));
            tempField = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
            bw.newLine();
            //          bw.write("\tpublic void set" + tempField + "(" + tempType + " _" + _tempField + "){");
            bw.write("\tpublic void set" + tempField + "(" + tempType + " " + _tempField + "){");
            bw.newLine();
            //          bw.write("\t\tthis." + _tempField + "=_" + _tempField + ";");
            bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic " + tempType + " get" + tempField + "(){");
            bw.newLine();
            bw.write("\t\treturn this." + _tempField + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
        }
        bw.newLine();
        bw.write("}");
        bw.newLine();
        bw.flush();
        bw.close();
    }
    
    private void buildService() throws IOException {
        File folder = new File(service_path);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
 
        File mapperFile = new File(service_path, serviceName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile),"UTF-8"));
        bw.write("package " + service_package + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import " + bean_package + "." + beanName + ";");
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.write("import java.util.Map;");
        bw = buildClassComment(bw, serviceName + "业务层接口类");
        bw.newLine();
        bw.newLine();
        //      bw.write("public interface " + mapperName + " extends " + mapper_extends + "<" + beanName + "> {");
        bw.write("public interface " + serviceName + "{");
        bw.newLine();
        bw.newLine();
        // ----------定义Mapper中的方法Begin----------
        bw = buildMethodComment(bw, "查询"+beanName+"列表");
        bw.newLine();
        bw.write("\tpublic "+"List<" + beanName + ">  select"+beanName+"List (Map<String,Object> map);");
        bw.newLine();
        bw = buildMethodComment(bw, "查询"+beanName+"列表计数");
        bw.newLine();
        bw.write("\tpublic "+"int select"+beanName+"ListCount (Map<String,Object> map);");
        bw.newLine();
        bw = buildMethodComment(bw, "根据主键查询"+beanName);
        bw.newLine();
        bw.write("\tpublic " + beanName + "  select"+beanName+"ById (long id);");
        bw.newLine();
        bw = buildMethodComment(bw, "根据主键删除"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int delete"+beanName+"ById (long id);");
        bw.newLine();
        bw = buildMethodComment(bw, "添加");
        bw.newLine();
        bw.write("\tpublic " + "int insert"+beanName+"(" + beanName + " record );");
        bw.newLine();
        bw = buildMethodComment(bw, "修改"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int update"+beanName+"(Map<String,Object> map);");
        bw.newLine();
 
        // ----------定义Mapper中的方法End----------
        bw.newLine();
        bw.write("}");
        bw.flush();
        bw.close();
    }
    
    private void buildServiceImpl() throws IOException {
        File folder = new File(service_impl_path);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
 
        File mapperFile = new File(service_impl_path, serviceNameImpl + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile),"UTF-8"));
        bw.write("package " + service_package_impl + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import " + bean_package + "." + beanName + ";");
        bw.newLine();
        bw.write("import " + mapper_package + "." + mapperName + ";");
        bw.newLine();
        bw.write("import " + service_package + "." + serviceName + ";");
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.write("import java.util.Map;");
        bw.newLine();
        bw.write("import java.util.HashMap;");
        bw.newLine();
        bw.write("import org.springframework.beans.factory.annotation.Autowired;");
        bw.newLine();
        bw.write("import org.springframework.stereotype.Service;");
        bw.newLine();
        bw = buildClassComment(bw, serviceNameImpl + "业务层实现类");
        bw.newLine();
        bw.newLine();
        //      bw.write("public interface " + mapperName + " extends " + mapper_extends + "<" + beanName + "> {");
        bw.write("@Service");
        bw.newLine();
        bw.write("public class " + serviceNameImpl + " implements "+serviceName+"{");
        bw.newLine();
        bw.write("\t@Autowired");
        bw.newLine();
        bw.write("\tprivate "+mapperName+" "+lowerFirsrLetter(mapperName)+";");
        bw.newLine();
        // ----------定义Mapper中的方法Begin----------
        bw = buildMethodComment(bw, "查询"+beanName+"列表");
        bw.newLine();
        bw.write("\tpublic "+"List<" + beanName + ">  select"+beanName+"List (Map<String,Object> map){");
        bw.newLine();
        bw.write("\t\treturn "+lowerFirsrLetter(mapperName)+".select"+beanName+"(map);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw = buildMethodComment(bw, "查询"+beanName+"列表计数");
        bw.newLine();
        bw.write("\tpublic "+"int select"+beanName+"ListCount (Map<String,Object> map){");
        bw.newLine();
        bw.write("\t\treturn "+lowerFirsrLetter(mapperName)+".select"+beanName+"Count(map);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw = buildMethodComment(bw, "根据主键查询"+beanName);
        bw.newLine();
        bw.write("\tpublic " + beanName + "  select"+beanName+"ById (long id){");
        bw.newLine();
        bw.write("\t\tMap<String,Object> map = new HashMap<String,Object>();");
        bw.newLine();
        bw.write("\t\tmap.put(\"id\",id);");
        bw.newLine();
        bw.write("List<"+beanName+"> list = "+lowerFirsrLetter(mapperName)+".select"+beanName+"(map);");
        bw.newLine();
        bw.write("\t\treturn list!=null&&!list.isEmpty()?list.get(0):null;");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw = buildMethodComment(bw, "根据主键删除"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int delete"+beanName+"ById (long id){");
        bw.newLine();
        bw.write("\t\tMap<String,Object> map = new HashMap<String,Object>();");
        bw.newLine();
        bw.write("\t\tmap.put(\"id\",id);");
        bw.newLine();
        bw.write("\t\treturn "+lowerFirsrLetter(mapperName)+".delete"+beanName+"(map);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw = buildMethodComment(bw, "添加");
        bw.newLine();
        bw.write("\tpublic " + "int insert"+beanName+"(" + beanName + " record ){");
        bw.newLine();
        bw.write("\t\treturn "+lowerFirsrLetter(mapperName)+".insert"+beanName+"(record);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw = buildMethodComment(bw, "修改"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int update"+beanName+"(Map<String,Object> map){");
        bw.newLine();
        bw.write("\t\treturn "+lowerFirsrLetter(mapperName)+".update"+beanName+"(map);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        // ----------定义Mapper中的方法End----------
        bw.newLine();
        bw.write("}");
        bw.flush();
        bw.close();
    }
 
 
    /**
     *  构建Mapper文件
     *
     * @throws IOException 
     */
    private void buildMapper() throws IOException {
        File folder = new File(mapper_path);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
 
        File mapperFile = new File(mapper_path, mapperName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile),"UTF-8"));
        bw.write("package " + mapper_package + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import " + bean_package + "." + beanName + ";");
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.write("import java.util.Map;");
        bw.newLine();
        bw.write("import org.apache.ibatis.annotations.Mapper;");
        bw = buildClassComment(bw, mapperName + "数据库操作接口类");
        bw.newLine();
        bw.newLine();
        //      bw.write("public interface " + mapperName + " extends " + mapper_extends + "<" + beanName + "> {");
        bw.write("@Mapper");
        bw.newLine();
        bw.write("public interface " + mapperName + "{");
        bw.newLine();
        bw.newLine();
        // ----------定义Mapper中的方法Begin----------
        bw = buildMethodComment(bw, "查询"+beanName+"列表");
        bw.newLine();
        bw.write("\tpublic "+"List<" + beanName + ">  select"+beanName+"(Map<String,Object> map);");
        bw.newLine();
        bw = buildMethodComment(bw, "查询"+beanName+"列表计数");
        bw.newLine();
        bw.write("\tpublic "+"int select"+beanName+"Count (Map<String,Object> map);");
        bw.newLine();
        bw = buildMethodComment(bw, "删除"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int delete"+beanName+" (Map<String,Object> map);");
        bw.newLine();
        bw = buildMethodComment(bw, "添加");
        bw.newLine();
        bw.write("\tpublic " + "int insert"+beanName+"(" + beanName + " record );");
        bw.newLine();
        bw = buildMethodComment(bw, "修改"+beanName);
        bw.newLine();
        bw.write("\tpublic " + "int update"+beanName+"(Map<String,Object> map);");
        bw.newLine();
 
        // ----------定义Mapper中的方法End----------
        bw.newLine();
        bw.write("}");
        bw.flush();
        bw.close();
    }
    
    /**
     * 首字母小写
     * @return
     */
    private String lowerFirsrLetter(String word){
    	String[] split = word.split("|");
    	return split[1].toLowerCase()+word.substring(1);
    }
 
 
    /**
     *  构建实体类映射XML文件
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException 
     */
    private void buildMapperXml( List<String> columns, List<String> types, List<String> comments ) throws IOException {
        File folder = new File(xml_path);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
 
        File mapperXmlFile = new File(xml_path, beanName + ".xml");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile),"UTF-8"));
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" ");
        bw.newLine();
        bw.write("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        bw.newLine();
        bw.write("<mapper namespace=\"" + mapper_package+ "."+mapperName + "\">");
        bw.newLine();
        bw.newLine();
 
        bw.write("\t<!--实体映射-->");
        bw.newLine();
        bw.write("\t<resultMap id=\"" + this.processResultMapId(beanName) + "ResultMap\" type=\"" + bean_package+"."+beanName + "\">");
        bw.newLine();
        int size = columns.size();
        for ( int i = 0 ; i < size ; i++ ) {
            bw.write("\t\t<result property=\""
                    + this.processField(columns.get(i)) + "\" column=\"" + columns.get(i) + "\" />");
            bw.newLine();
        }
        bw.write("\t</resultMap>");
 
        bw.newLine();
        bw.newLine();
        bw.newLine();
 
        // 下面开始写SqlMapper中的方法
        // this.outputSqlMapperMethod(bw, columns, types);
        buildSQL(bw, columns, types);
 
        bw.write("</mapper>");
        bw.flush();
        bw.close();
    }
 
 
    private void buildSQL( BufferedWriter bw, List<String> columns, List<String> types ) throws IOException {
        int size = columns.size();
        // 通用结果列
        bw.write("\t<!-- 通用查询结果列-->");
        bw.newLine();
        bw.write("\t<sql id=\"Base_Column_List\">");
        bw.newLine();
 
        bw.write("\t\t");
        for ( int i = 0 ; i < size ; i++ ) {
            bw.write(columns.get(i));
            if ( i != size - 1 ) {
                bw.write(",");
            }
        }
 
        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
        bw.write("\t<sql id=\"Base_Where_Condition\">");
        bw.newLine();
 
        for ( int i = 0 ; i < size ; i++ ) {
        	if("java.util.Date".equals(processType(types.get(i)))){
        		bw.write("\t\t<if test=\"startDate!=null\">");
                bw.write("and " + columns.get(i)+" >= #{startDate}</if>");
                bw.newLine();
                bw.write("\t\t<if test=\"endDate!=null\">");
                bw.write("and " + columns.get(i)+" &lt;= #{endDate}</if>");
        	}else{
        		bw.write("\t\t<if test=\""+processField(columns.get(i))+"!=null and "+processField(columns.get(i))+"!=''\">");
                bw.write("and " + columns.get(i)+" = #{"+processField(columns.get(i))+"}</if>");
        	}
            bw.newLine();
        }
        bw.write("\t</sql>");
        bw.newLine();
 
 
        bw.write("\t<!-- 查询"+beanName+" -->");
        bw.newLine();
        bw.write("\t<select id=\"select"+beanName+"\" parameterType=\"Map\" resultMap=\""+this.processResultMapId(beanName) + "ResultMap\">");
        bw.newLine();
        bw.write("\t\t select");
        bw.newLine();
        bw.write("\t\t <include refid=\"Base_Column_List\" />");
        bw.newLine();
        bw.write("\t\t FROM " + tableName);
        bw.newLine();
        bw.write("\t\t <where>");
        bw.newLine();
        bw.write("\t\t\t <include refid=\"Base_Where_Condition\" />");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t\t<if test=\"pageSize !=null  and pageSize!='' \" >");
        bw.write("limit #{start},#{pageSize}");
        bw.write("</if>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
        bw.newLine();
        
        bw.write("\t<!-- 查询"+beanName+"计数 -->");
        bw.newLine();
        bw.write("\t<select id=\"select"+beanName+"Count\" resultType=\"int\" parameterType=\"Map\">");
        bw.newLine();
        bw.write("\t\t select");
        bw.newLine();
        bw.write("\t\t count(1)");
        bw.newLine();
        bw.write("\t\t FROM " + tableName);
        bw.newLine();
        bw.write("\t\t <where>");
        bw.newLine();
        bw.write("\t\t\t <include refid=\"Base_Where_Condition\" />");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
        bw.newLine();
        // 查询完
 
 
        bw.write("\t<!--删除-->");
        bw.newLine();
        bw.write("\t<delete id=\"delete"+beanName+"\" parameterType=\"Map\">");
        bw.newLine();
        bw.write("\t\t DELETE FROM " + tableName);
        bw.newLine();
        bw.write("\t\t <where>");
        bw.newLine();
        bw.write("\t\t\t <include refid=\"Base_Where_Condition\" />");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</delete>");
        bw.newLine();
        bw.newLine();
        // 删除完
 
 
        // 添加insert方法
        bw.write("\t<!-- 添加 -->");
        bw.newLine();
        bw.write("\t<insert id=\"insert"+beanName+"\" parameterType=\"" + bean_package+"."+beanName + "\" useGeneratedKeys=\"true\" keyProperty=\""+columns.get(0)+"\">");
        bw.newLine();
        bw.write("\t\t INSERT INTO " + tableName);
        bw.newLine();
        bw.write(" \t\t(");
        for ( int i = 1 ; i < size ; i++ ) {
            bw.write(columns.get(i));
            if ( i != size - 1 ) {
                bw.write(",");
            }
        }
        bw.write(") ");
        bw.newLine();
        bw.write("\t\t VALUES ");
        bw.newLine();
        bw.write(" \t\t(");
        for ( int i = 1 ; i < size ; i++ ) {
            bw.write("#{" + processField(columns.get(i)) + "}");
            if ( i != size - 1 ) {
                bw.write(",");
            }
        }
        bw.write(") ");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();
        // 添加insert完
 
        // 修改update方法
        bw.write("\t<!-- 修 改-->");
        bw.newLine();
        bw.write("\t<update id=\"update"+beanName+"\" parameterType=\"Map\">");
        bw.newLine();
        bw.write("\t\t UPDATE " + tableName);
        bw.newLine();
        bw.write(" \t\t <trim prefix=\"set\" suffixOverrides=\",\"> ");
        bw.newLine();
        for ( int i = 1 ; i < size ; i++ ) {
        	if("java.util.Date".equals(processType(types.get(i)))){
        		bw.write("\t\t\t<if test=\""+processField(columns.get(i))+"!=null\">");
        	}else{
        		bw.write("\t\t\t<if test=\""+processField(columns.get(i))+"!=null and "+processField(columns.get(i))+"!=''\">");
        	}
            bw.write(columns.get(i)+" = #{"+processField(columns.get(i))+"},</if>");
            bw.newLine();
        }
        bw.write(" \t\t </trim>");
        bw.newLine();
        bw.write("\t\t<where>");
        bw.newLine();
        bw.write("\t\t id=#{id}");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</update>");
        bw.newLine();
        bw.newLine();
        // update方法完毕

        bw.newLine();
        bw.newLine();
    }
 
 
    /**
     *  获取所有的数据库表注释
     *
     * @return
     * @throws SQLException 
     */
    private Map<String, String> getTableComment() throws SQLException {
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = conn.prepareStatement("show table status");
        ResultSet results = pstate.executeQuery();
        while ( results.next() ) {
            String tableName = results.getString("NAME");
            String comment = results.getString("COMMENT");
            maps.put(tableName, comment);
        }
        return maps;
    }
 
 
    public void generate() throws ClassNotFoundException, SQLException, IOException {
        init();
        String prefix = "show full fields from ";
        List<String> columns = null;
        List<String> types = null;
        List<String> comments = null;
        PreparedStatement pstate = null;
        List<String> tables = getTables();
        Map<String, String> tableComments = getTableComment();
        for ( String table : tables ) {
            columns = new ArrayList<String>();
            types = new ArrayList<String>();
            comments = new ArrayList<String>();
            pstate = conn.prepareStatement(prefix + table);
            ResultSet results = pstate.executeQuery();
            while ( results.next() ) {
                columns.add(results.getString("FIELD"));
                types.add(results.getString("TYPE"));
                comments.add(results.getString("COMMENT"));
            }
            tableName = table;
            processTable(table);
            //          this.outputBaseBean();
            String tableComment = tableComments.get(tableName);
            buildEntityBean(columns, types, comments, tableComment);
            buildMapper();
            buildService();
            buildServiceImpl();
            buildMapperXml(columns, types, comments);
        }
        buildMapperConfigXML();
        conn.close();
    }
    
    private void buildMapperConfigXML() throws IOException{
    	File folder = new File(xml_path);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
 
        File mapperXmlFile = new File(xml_path, "SqlMapConfig.xml");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile),"UTF-8"));
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write("<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD  Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">");
        bw.newLine();
        bw.write("<configuration>");
        bw.newLine();
        bw.write("\t<settings>");
        bw.newLine();
        bw.write("\t\t<setting name=\"logImpl\" value=\"SLF4J\" />");
        bw.newLine();
        bw.write("\t</settings>");
        bw.newLine();
        bw.write("\t<typeAliases>");
        bw.newLine();
        for (String bean : beanList) {
			bw.write("\t\t<typeAlias type=\""+bean_package+"."+bean+"\" alias=\""+lowerFirsrLetter(bean)+"\"/>");
			bw.newLine();
		}
        bw.write("\t</typeAliases>");
        bw.newLine();
        bw.write("\t<mappers>");
        bw.newLine();
        for (String bean : beanList) {
			bw.write("\t\t<mapper resource=\""+bean+".xml\" />");
			bw.newLine();
		}
        bw.write("\t</mappers>");
        bw.newLine();
        bw.write("</configuration>");
        bw.newLine();
        bw.flush();
        bw.close();
    }
 
 
    public static void main( String[] args ) {
        try {
            new MyBatis2SpringBoot().generate();
            // 自动打开生成文件的目录
            Runtime.getRuntime().exec("cmd /c start explorer D:\\");
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } catch ( SQLException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
