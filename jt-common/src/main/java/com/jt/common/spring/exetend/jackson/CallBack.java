package com.jt.common.spring.exetend.jackson;
import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;
//专门负责进行json转换，支持jsonp的跨域
public class CallBack extends MappingJackson2HttpMessageConverter{
	//成员变量
	//在springmvc-config.xml文件中，配置了<property name="callbackName">
	//那么就能够获取all?请求中的函数名
	
	private String callbackName;
	//专门进行json包装的方法
	//该方法由程序员手动编写
	//该方法由springmvc自动调用
	@Override
	protected void writeInternal(Object object, Type arg1, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub
				// 从threadLocal中获取当前的Request对象
		        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
		                .currentRequestAttributes()).getRequest();
		        String callbackParam = request.getParameter(callbackName);
		        System.out.println("==========="+callbackParam);  
		        System.out.println("asda111111sdasddasd");
		        if (StringUtils.isEmpty(callbackParam)) {
		            // 没有找到callback参数，直接返回json数据
		            super.writeInternal(object, outputMessage);
		        } else {
		            JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
		            try {
		            	//将对象转换为json串，然后用回调方法包括起来
		                String result = callbackParam + "(" + super.getObjectMapper().writeValueAsString(object)
		                        + ");";
		                System.out.println("result======"+result);
		                IOUtils.write(result, outputMessage.getBody(), encoding.getJavaName());
		            } catch (JsonProcessingException ex) {
		                throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
		            }
		        }

	}
	public String getCallbackName() {
		return callbackName;
	}

	public void setCallbackName(String callbackName) {
		this.callbackName = callbackName;
	}
	
}
