package com.vinodh.springbatch;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

public class Test {

	public static void main(String[] args) {
		
		String ss ="T1h Vinodh i1s iVin1odhs Vinodh Ku1mar Thimmi@gmail.com "; //"Hello@ti.com 123 asd2df df dsfsd edf@s.das" ;//"T1h Vinodh i1s iVin1odhs Vinodh Ku1mar Thimm1i@gmail.com ";
		String s2= "s123dfdfd123sdfsd123fsdfd123";
		String emailRegex= "\\b[a-zA-Z]+(@)[a-zA-Z]+(.)[a-zA-Z]+\\b";
		String nRegex = "1(?=23)";
		
		Matcher matcher= Pattern.compile(nRegex).matcher(s2);
		Matcher matcher1= Pattern.compile(emailRegex).matcher(ss);
		while(matcher1.find()) {
			 System.out.println(">>>>>>>>");
		}
		 
	}
}
