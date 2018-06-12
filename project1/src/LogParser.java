import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class LogParser 
{
	 
	public static void main(String [] args)
	{
		
		String filePath = "/Users/KimberlyQuintana/Desktop/CS122B/apache-tomcat-8.5.29/wtpwebapps/project1/log.txt";
		
		File file = new File(filePath);
		
		try
		{
			Scanner sc = new Scanner(file);
			int count = 0, TJTotal = 0, TSTotal = 0;
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String [] values = line.split(",");
				int TJ = Integer.parseInt(values[0]), TS = Integer.parseInt(values[1]);
				
				count++;
				TJTotal+= TJ;
				TSTotal+= TS;
			}
			
			System.out.println("Average TJ = " + TJTotal/count + "ns");
			System.out.println("Average TS = " + TSTotal/count + "ns");
		}
		catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	
}
